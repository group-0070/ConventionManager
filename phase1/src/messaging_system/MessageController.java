package messaging_system;

import event_system.EventService;
import login_system.ILoginController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 A Controller class for sending, multicasting, and receiving messages
 */
public class MessageController implements IMessageController {
    private final MessageService messageService = new MessageServiceEngine();
    private final IMessageDataProvider messageDataProvider;
    private final MessageAuthorization messageAuthorizer;
    private final EventService eventService;
    private final ILoginController loginController;
    private final String userId;

    /**
     * Initialize MessageController.
     */
    public MessageController(ILoginController loginController, EventService eventService, String messageListFileName) {
        this.messageDataProvider = new MessageFileDataProvider(messageListFileName, messageService);
        this.messageAuthorizer = new MessageAuthorizer(loginController);
        this.eventService = eventService;
        this.loginController = loginController;
        userId = loginController.getCurrentUserId();
        messageDataProvider.read();
    }

    /**
     * Check if this userID exist in the system or not
     *
     * @param receiverId String    potential receiver ID for a message
     * @return boolean   true if this ID exists in the system, false otherwise
     */
    public boolean idExist(String receiverId) {
        return this.loginController.userExistsInList(receiverId);
    }

    /**
     * Send a message
     *
     * @param info       String      the information of the message
     * @param receiverId String      the id name of the receiver
     * @return boolean     true if message is successfully sent, otherwise false
     */
    public boolean sendMessage(String info, String receiverId) {
        if (idExist(receiverId) && messageAuthorizer.canMessage(receiverId)) {
            this.messageService.sendMessage(info, this.userId, receiverId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Send multiple messages to attendees or speakers in the system, from an organizer account
     *
     * @param info         String  the information of the message
     * @param receiverType String  the type of the receivers, either all "Speaker" or "Attendee" in the system
     * @return Boolean  true if it is authorized to message, false otherwise
     */
    public boolean multicastMessage(String info, String receiverType) {
        if (this.messageAuthorizer.canMulticast()) {
            ArrayList<String> receiverIds;
            // userType is Organizer
            if (receiverType.equals("Speaker")) {
                receiverIds = loginController.getUserService().getListOfSpeakerId();
            } else if (receiverType.equals("Attendee")){
                receiverIds = loginController.getUserService().getListOfAttendeeId();
            } else { // No receiver type other than "Speaker" or "Attendee"
                return false;
            }
            this.messageService.multicastMessage(info, this.userId, receiverIds);
            return true;
        } else {
            return false;
        }
    }

    /**
     * send a message to attendee in all the events that a speaker is assigned to
     *
     * @param info String  the text information of the message
     * @return Boolean  true if it is authorized to message, false otherwise
     */
    public boolean speakerMulticastMessage(String info) {
        if (this.messageAuthorizer.canMulticast()) {
            ArrayList<String> receiverIds = new ArrayList<>();
            ArrayList<String> events = eventService.getEventsBySpeaker(userId);
            for (String eventID : events) {
                receiverIds.addAll(eventService.getUsersForEvent(eventID));
            }
            // Cited resource about stream()
            // https://stackoverflow.com/questions/26152000/how-to-get-unique-values-in-list
//            this.messageService.multicastMessage(info, this.userId, receiverIds);
            ArrayList<String> filtered = (ArrayList<String>) receiverIds.stream().distinct().collect(Collectors.toList());
            this.messageService.multicastMessage(info, this.userId, filtered);
            return true;
        } else {
            return false;
        }
    }

    /**
     * send a message to all attendees in events based on selection of the speaker who is assigned to the events
     *
     * @param info     String      the text information of the message
     * @param eventIDs String      the event names separated by ";" to message to their attendees
     * @return Boolean     true if authorized to message or the event name is correct, false otherwise
     */
    public boolean speakerMulticastMessageForEvents(String info, String eventIDs) {
        if (this.messageAuthorizer.canMulticast()) {
            ArrayList<String> events = eventService.getEventsBySpeaker(userId);
            String[] sendMessageEvents = eventIDs.split(",");
            // clean the spaces
            for (int i = 0; i < sendMessageEvents.length; i++)
                sendMessageEvents[i] = sendMessageEvents[i].trim();

            boolean flag = true; //Detect if there is a invalid event input
            for (String event : sendMessageEvents) {
                if (events.contains(event)) { // the speaker is assigned to this event
                    ArrayList<String> receiverIds = eventService.getUsersForEvent(event);
                    this.messageService.multicastMessage("#"+event+": "+info, this.userId, receiverIds);
                } else {
                    flag = false;
                }
            }
            return flag;
        } else {
            return false;
        }
    }

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     *
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<String> showMessage(String userId) {
        return this.messageService.showMessage(userId);
    }

    // This is a method that we will use for Phase 2. Not dead code. :)
    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     *
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<String> showConversation(String userId1, String userId2) {
        return this.messageService.showConversation(userId1, userId2);
    }

    /**
     * Persist new messages to the disk
     */
    public void save() {
        this.messageDataProvider.write();
    }
}