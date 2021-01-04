package message_system;

import user_system.UserType;

import java.util.List;
import java.util.UUID;

/**
 A Controller interfaces for MessageController.
 */
public interface IMessageController {

    /**
     * Returns a list of userIDs of users the given userType can message
     *
     * @param userType  UserType            the user type of the logged on user
     * @param user      String              the userID of the logged on user
     * @return          List<String>        list of userIDs the logged on user can message
     */
    List<String> getListOfUsersCanMessage(UserType userType, String user);
    // Overload for user who login
    List<String> getListOfUsersCanMessage();


    /**
     * Returns a list of userIDs whose messages the logged on user can archive
     * @param userType  UserType        the user type of the logged on user
     * @param user      String          the user ID of the logged on user
     * @return          List<String>    list of userIDs whose messages the logged on user can archive
     */
    List<String> getListOfUsersCanArchive(UserType userType, String user);
    // Overload for user who login
    List<String> getListOfUsersCanArchive();

    /**
     * Get a list of messages this userId received from another userId, excluding archived messages.
     * @param userId        String      the userID of the logged on user
     * @param senderId      String      the senderID of the messages
     * @return List<List<String>>       a list of messages with its information such as the iad of the sender, time, etc.
     */
    List<List<String>> showReceivedMessage(String userId, String senderId);

    /**
     * Returns a list of message information for messages that the user can archive from a specific user
     * The message has to be sent to them, and not archived already.
     * @param user  String                   the id of the user that can archive the messages
     * @param user2 String                   the id of the sender of the messages that user can archive
     * @return      List<List<String>>       list of message information that can be archived by user
     */
    List<List<String>> getArchivableMessagesByUser(String user, String user2);
    // Overload for user who login
    List<List<String>> getArchivableMessagesByUser(String user);

    /**
     * Send a message to another user.
     * @param info       the content of the message
     * @param receiverId the ID of the message receiver
     * @param senderId   the id of the sender
     * @return whether or not the message is sent successfully
     */
    MessagePrompt sendMessage(String info, String receiverId, String senderId);
    // Overload for user who login
    MessagePrompt sendMessage(String info, String receiverId);

    /**
     * Send the same piece of message to all the users of a particular type simultaneously
     * @param info         the content of the message
     * @param senderId     the id of the sender
     * @param receiverType the type of users that would receive the message
     * @return whether or not the message has been successfully sent
     */
    MessagePrompt multiMessage(String info, String senderId, UserType receiverType);
    // Overload for user who login
    MessagePrompt multiMessage(String info, UserType receiverType);

    /**
     * Show all the messages that is related to a user (either sender of receiver) in the system
     * @param userID the related user
     * @return a list of messages that is related to the user
     */
    List<List<String>> showMessage(String userID);
    // Overload for user who login
    List<List<String>> showMessage();

    /**
     * Save all the messages the user has sent to persistent on disk
     * @return boolean  whether or not the saving process is successful.
     */
    boolean save();

    /**
     * Load all the messages that the user has from disk.
     * @return boolean  whether or not the loading process is successful.
     */
    boolean load();

    /**
     * Show the conversation between two specific users
     * @param userId1 one of two related users
     * @param userId2 the other of two related users
     * @return a list of messages between two related users (like conversation)
     */
    List<List<String>> showConversation(String userId1, String userId2);
    // Overload
    List<List<String>> showConversation(String userId);

    /**
     * Speaker sends the same message to all the attendees that are registered in his/her one
     * specific event or several specific events
     * @param info     the content of the message
     * @param senderId the id of the sender
     * @param eventIDs a list of eventsIDs whose attendees are receivers of this message
     * @return whether or not the message is sent successfully
     */
    MessagePrompt speakerMultiMessageForEvents(String info, String senderId, List<String> eventIDs);
    // Overload
    MessagePrompt speakerMultiMessageForEvents(String info, List<String> eventIDs);

    /**
     * Update the status of a message.
     * @param messageId UUID            the Id of the message
     * @param newStatus MessageStatus   the new status of the message, could be UNREAD, READ, or ARCHIVE
     */
    MessagePrompt changeMessageStatus(UUID messageId, MessageStatus newStatus);

    /**
     * Delete a message
     * @param messageId     UUID        the Id of the message
     */
    MessagePrompt deleteMessage(UUID messageId);

    /**
     * Get a list of all messages (in list of string representation) in the system (for ADMIN user)
     * @return List<List<String>>   a list of all messages in the system
     */
    List<List<String>> getAllMessagesInSystem();

    /**
     * Get a list of all messages (in list of string representation) that has a certain status for a user
     * @param status    the status of messages
     * @param userID    the userID related to the messages
     * @return List<List<String>>   a list of all messages in the system
     */
    List<List<String>> getGivenStatusMessagesByUser(MessageStatus status, String userID);
    // Overload
    List<List<String>> getGivenStatusMessagesByUser(MessageStatus status);
}
