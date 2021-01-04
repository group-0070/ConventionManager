package messaging_system;

import java.util.List;

/**
 A Controller interfaces for MessageController.
 */
public interface IMessageController {
    /**
     * Send a message to another user.
     * @param info the content of the message
     * @param receiverId the ID of the message receiver
     * @return whether or not the message is sent successfully
     */
    boolean sendMessage(String info, String receiverId);

    /**
     * Send the same piece of message to all the users of a particular type simultaneously
     * @param info the content of the message
     * @param receiverType the type of users that would receive the message
     * @return whether or not the message has been successfully sent
     */
    boolean multicastMessage(String info, String receiverType);

    /**
     * Show all the messages that is related to a user (either sender of receiver) in the system
     * @param userID the related user
     * @return a list of messages that is related to the user
     */
    List<String> showMessage (String userID);

    /**
     * Save all the messages the user has sent to persistent on disk
     */
    void save();

    /**
     * Show the conversation between two specific users
     * @param userId1 one of two related users
     * @param userId2 the other of two related users
     * @return a list of messages between two related users (like conversation)
     */
    List<String> showConversation (String userId1, String userId2);

    /**
     * Speaker sends the same message to all the attendees that are registered in any of his/her event
     * @param info the content of the message
     * @return whether or not the message is sent successfully
     */
    boolean speakerMulticastMessage(String info);

    /**
     * Speaker sends the same message to all the attendees that are registered in his/her one
     * specific event or several specific events
     * @param info the content of the message
     * @param eventIDs a list of eventsIDs whose attendees are receivers of this message
     * @return whether or not the message is sent successfully
     */
    boolean speakerMulticastMessageForEvents(String info, String eventIDs);
    }