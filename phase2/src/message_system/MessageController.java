package message_system;

import event_system.EventService;
import user_system.UserService;
import user_system.UserType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 A Controller class for sending, multicasting, and receiving messages
 */
public class MessageController implements IMessageController {
    private final MessageDatabaseReadWriter message_db_read_writer;
    private final MessageService message_service;
    private final EventService event_service;
    private final UserService user_service;

    /**
     * Initialize MessageController.
     * @param user_service               the use case class of login_system
     * @param event_service              the use case class of event_system
     * @param message_service            the use case class of messaging_system
     * @param messageStorageAddress      the file path for storing messages
     */
    public MessageController(String messageStorageAddress, UserService user_service, EventService event_service,
                             MessageService message_service) {
        this.message_service = message_service;
        this.event_service = event_service;
        this.user_service = user_service;
        this.message_db_read_writer = new MessageDatabaseReadWriter(this.message_service, messageStorageAddress);
    }

    /**
     * Persist new messages to the disk
     */
    public boolean load() {
        return this.message_db_read_writer.read();
    }

    /**
     * Persist new messages to the disk
     */
    public boolean save() {
        return this.message_db_read_writer.write();
    }

    /**
     * Returns a list of userIDs of users the given userType can message
     *
     * @param userType  UserType            the user type of the logged on user
     * @param user      String              the userID of the logged on user
     * @return          List<String>        list of userIDs the logged on user can message
     */
    public List<String> getListOfUsersCanMessage(UserType userType, String user) {
        List<String> allIds = new ArrayList<>();
        for (UserType uType : UserType.values()) {
            allIds.addAll(user_service.getListOfIDsByType(uType));
        }

        List<String> users = new ArrayList<>();
        for (String id : allIds) {
            if (canMessage(userType, id) && !id.equals(user)) {
                users.add(id);
            }
        }

        return users;
    }
    // Overload
    public List<String> getListOfUsersCanMessage() {
        return getListOfUsersCanMessage(user_service.getCurrentUserType(), user_service.getCurrentUserID());
    }

    /**
     * Returns a list of userIDs whose messages the logged on user can archive
     * @param userType  UserType        the user type of the logged on user
     * @param user      String          the user ID of the logged on user
     * @return          List<String>    list of userIDs whose messages the logged on user can archive
     */
    public List<String> getListOfUsersCanArchive(UserType userType, String user) {
        List<String> allIds = new ArrayList<>();
        // If the current user is admin, they won't receive and messages
        // So they do not have messages from anyone to archive
        if (userType == UserType.ADMIN) {
            return allIds;
        }
        // If the user is speaker or attendee or organizer
        for (UserType uType : UserType.values()) {
            // remove admin
            if (uType != UserType.ADMIN) {
                allIds.addAll(user_service.getListOfIDsByType(uType));
            }
        }
        // Remove user him/herself
        List<String> users = new ArrayList<>();
        for (String id : allIds) {
            if (!id.equals(user)) {
                users.add(id);
            }
        }
        return users;
    }
    // Overload
    public List<String> getListOfUsersCanArchive() {
        return getListOfUsersCanArchive(user_service.getCurrentUserType(), user_service.getCurrentUserID());
    }

    /**
     * Returns a list of message information for messages that the user can archive from a specific user
     * The message has to be sent to them, and not archived already.
     *
     * @param user  String                          the id of the user that can archive the messages
     * @param user2 String                          the id of the sender of the messages that user can archive
     * @return      List<List<String>>              list of message information that can be archived by user
     */
    @Override
    public List<List<String>> getArchivableMessagesByUser(String user, String user2) {
        List<List<String>> messages = showConversation(user, user2);
        List<List<String>> archive_messages = new ArrayList<>();
        for (List<String> message : messages) {
            if (message.get(MessageIndex.RECEIVER.getValue()).equals(user)) {
                archive_messages.add(message);
            }
        }
        return archive_messages;
    }
    // Overload
    public List<List<String>> getArchivableMessagesByUser(String user) {
        return getArchivableMessagesByUser(user_service.getCurrentUserID(), user);
    }


    /**
     * Send a message
     *
     * @param info              String      the information of the message
     * @param receiverId        String      the id name of the receiver
     * @param senderId          String      the id name of the sender
     * @return MessagePrompt    Enum        returns corresponding message with the failing/success reasoning
     */
    public MessagePrompt sendMessage(String info, String receiverId, String senderId) {
        if (user_service.userExists(receiverId)) {
            if (isNotValidMessage(info)) {
                return MessagePrompt.INVALID_INPUT;
            }
            if (canMessage(user_service.userTypeFromUsers(senderId), receiverId)){
                message_service.addMessage(info, senderId, receiverId);
                return MessagePrompt.SEND_SUCCESS;
            } else {
                return MessagePrompt.UNAUTHORIZED;
            }
        } else {
            return MessagePrompt.USER_NOT_FOUND;
        }
    }
    // Overload
    public MessagePrompt sendMessage(String info, String receiverId) {
        return sendMessage(info, receiverId, user_service.getCurrentUserID());
    }

    /**
     * Send multiple messages to attendees or speakers in the system, from an organizer account
     *
     * @param info              String       the information of the message
     * @param receiverType      UserType     enum of the type of the receivers in the system
     * @return MessagePrompt    Enum         returns corresponding message with the failing case
     */
    public MessagePrompt multiMessage(String info, String senderId, UserType receiverType) {
        if(!canMultiMessage(user_service.userTypeFromUsers(senderId))) {
            return MessagePrompt.UNAUTHORIZED;
        }
        else if (isNotValidMessage(info)) {
            return MessagePrompt.INVALID_INPUT;
        }
        else {
            List<String> receiverIds = user_service.getListOfIDsByType(receiverType);
            this.message_service.multiMessage(info, senderId, receiverIds);
            return MessagePrompt.SEND_SUCCESS;
        }
    }
    // Overload
    public MessagePrompt multiMessage(String info, UserType receiverType) {
        return multiMessage(info, user_service.getCurrentUserID(), receiverType);
    }

    /**
     * send a message to all attendees in events based on selection of the speaker who is assigned to the events
     *
     * @param info              String      the text information of the message
     * @param senderId          String      the senderID of the message
     * @param eventIDs          String      the event names separated by "," to message to their attendees
     * @return MessagePrompt    Enum        returns corresponding message with the failing/success reasoning
     */
    public MessagePrompt speakerMultiMessageForEvents(String info, String senderId, List<String> eventIDs) {
        if (isNotValidMessage(info)) {
            return MessagePrompt.INVALID_EVENT_INPUT;
        } else if (canMultiMessage(user_service.userTypeFromUsers(senderId))) {
            List<String> events = event_service.getEventsBySpeaker(senderId);

            MessagePrompt flag = MessagePrompt.SEND_SUCCESS; //Detect if there is a invalid event input
            for (String event : eventIDs) {
                if (events.contains(event)) { // the speaker is assigned to this event
                    List<String> receiverIds = event_service.getUsersForEvent(event);
                    this.message_service.multiMessage("#" + event + ": " + info, senderId, receiverIds);
                } else {
                    flag = MessagePrompt.INVALID_EVENT_INPUT;
                }
            }
            return flag;
        } else {
            return MessagePrompt.UNAUTHORIZED;
        }
    }
    // Overload
    public MessagePrompt speakerMultiMessageForEvents(String info, List<String> eventIDs) {
        return speakerMultiMessageForEvents(info, user_service.getCurrentUserID(), eventIDs);
    }

    /**
     * Update the status of a message.
     *
     * @param messageId         UUID            the Id of the message
     * @param newStatus         MessageStatus   the new status of the message, could be UNREAD, READ, or ARCHIVE
     * @return MessagePrompt    Enum            Success if changed, otherwise corresponding warning message
     */
    public MessagePrompt changeMessageStatus(UUID messageId, MessageStatus newStatus) {
        if (message_service.messageIdExists(messageId)){
            message_service.changeMessageStatus(messageId, newStatus);
            switch (newStatus) {
                case READ:
                    return MessagePrompt.MARK_STATUS_READ_SUCCESS;
                case UNREAD:
                    return MessagePrompt.MARK_STATUS_UNREAD_SUCCESS;
                case ARCHIVE:
                    return MessagePrompt.MARK_STATUS_ARCHIVE_SUCCESS;
                default:
                    return MessagePrompt.CHANGE_STATUS_SUCCESS;
            }
        } else {
            return MessagePrompt.MESSAGE_DOES_NOT_EXIST;
        }
    }

    /**
     * Delete a message given the message ID
     * @param   messageId         UUID        the Id of the message
     * @return  MessagePrompt     Enum        Success if deleted, otherwise corresponding warning message
     */
    public MessagePrompt deleteMessage(UUID messageId) {
        if (message_service.messageIdExists(messageId)){
            message_service.deleteMessage(messageId);
            return MessagePrompt.DELETE_SUCCESS;
        } else {
            return MessagePrompt.MESSAGE_DOES_NOT_EXIST;
        }
    }

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     *
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    @Override
    public List<List<String>> showMessage(String userId) {
        return this.message_service.showMessage(userId);
    }
    // Overload
    public List<List<String>> showMessage() {
        return showMessage(user_service.getCurrentUserID());
    }


    /**
     * Get a list of messages this userId received from another userId, excluding archived messages.
     * @param userId        String      the userID of the logged on user
     * @param senderId      String      the senderID of the messages
     * @return List<List<String>>       a list of messages with its information such as the iad of the sender, time, etc.
     */
    public List<List<String>> showReceivedMessage(String userId, String senderId) {
        List<List<String>> receivedMessage = new ArrayList<>();
        List<List<String>> messages = this.message_service.showMessage(userId);
        for (List<String> m : messages) {
            if (m.get(MessageIndex.RECEIVER.getValue()).equals(userId) &&
                    m.get(MessageIndex.SENDER.getValue()).equals(senderId) &&
                    !m.get(MessageIndex.STATUS.getValue()).equals("ARCHIVE")) {
                receivedMessage.add(m);
            }
        }
        return receivedMessage;
    }

    /**
     * Get a list of messages information that this userId has association with (a history log of messages).
     * Assumes userId1 is the user who is currently logged in.
     * @param userId1   String  user1 in the conversation
     * @param userId2   String  user2 in the conversation
     * @return List<String>     a list of messages with its information such as the id of the sender, time, etc.
     */
    @Override
    public List<List<String>> showConversation(String userId1, String userId2) {
        List<List<String>> res = new ArrayList<>();
        for (List<String> m : message_service.showConversation(userId1, userId2)) {
            if (!m.get(MessageIndex.STATUS.getValue()).equals("ARCHIVE")) {
                res.add(m);
            }
        }
        return res;
    }
    // Overload
    public List<List<String>> showConversation(String userID) {
        return showConversation(user_service.getCurrentUserID(), userID);
    }

    /**
     * Get a list of all messages (in list of string representation) in the system (for ADMIN user)
     * @return List<List<String>>   a list of all messages in the system
     */
    public List<List<String>> getAllMessagesInSystem(){
        List<List<String>> allMessages = new ArrayList<>();
        for (MessageStatus status : MessageStatus.values()){
            allMessages.addAll(this.message_service.getGivenStatusMessages(status));
        }
        return allMessages;
    }

    /**
     * Get a list of all messages (in list of string representation) that has a certain status for a user
     * @param status    the status of messages
     * @param userID    the userID related to the messages
     * @return List<List<String>>   a list of all messages in the system
     */
    public List<List<String>> getGivenStatusMessagesByUser(MessageStatus status, String userID){
        List<List<String>> allGivenStatusMessages = this.message_service.getGivenStatusMessages(status);
        List<List<String>> res = new ArrayList<>();
        for (List<String> m: allGivenStatusMessages) {
            if (m.get(MessageIndex.SENDER.getValue()).equals(userID) || m.get(MessageIndex.RECEIVER.getValue()).equals(userID)) {
                res.add(m);
            }
        }
        return res;
    }
    // Overload
    public List<List<String>> getGivenStatusMessagesByUser(MessageStatus status){
        return getGivenStatusMessagesByUser(status, user_service.getCurrentUserID());
    }

    /*
     * Check whether the user is allowed to send message
     * @param receiverId    String              the id for the message receiver
     * @param userType      UserType      the userType of the currently login user
     * @return              boolean             true if the user can send the message
     */
    private boolean canMessage(UserType userType, String receiverId) {
        if (user_service.userTypeFromUsers(receiverId) == UserType.ADMIN){
            return false;
        } else if (userType == UserType.ATTENDEE) {
            return user_service.userTypeFromUsers(receiverId) != UserType.ORGANIZER;
        } else return userType != UserType.ADMIN;
    }

    /*
     * check if the type of user can multimessage
     * @param userType  UserType  the userType of the currently login user
     * Note: only SPEAKER, ORGANIZER can do this
     * @return true if the multicasting is legal.
     */
    private boolean canMultiMessage (UserType userType) {
        return (userType == UserType.SPEAKER || userType == UserType.ORGANIZER);
    }

    /*
     * Checks if the given message is a valid message to send
     * I.e. length > 0, and contains at least 1 char that isn't a whitespace
     */
    private boolean isNotValidMessage(String message) {
        return (message == null || message.trim().length() == 0);
    }

}