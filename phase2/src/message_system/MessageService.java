package message_system;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    /**
     * Send a message
     * @param info          String          the information of the message
     * @param senderId      String          the id name of the sender
     * @param receiverId    String          the id name of the receiver
     */
    void addMessage(String info, String senderId, String receiverId);

    /**
     * Send multiple messages to a number of people in the system
     * @param info          String      the information of the message
     * @param senderId      String      the id name of the sender
     * @param receiverIds   String      a list of id name of the receivers
     */
    void multiMessage(String info, String senderId, List<String> receiverIds);

    /**
     * Add existing message into the system from the file
     * @param messageId     UUID            the id of the message
     * @param info          String          the information of the message
     * @param senderId      String          the id name of the sender
     * @param receiverId    String          the id name of the receiver
     * @param time          LocalDateTime   the time recorded on the external file
     * @param status        MessageStatus   the status of the message
     */
    void addMessage(UUID messageId, String info, String senderId, String receiverId, LocalDateTime time, MessageStatus status);

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     * @param userId  the related userId whose messages are shown
     * @return List<List<String>> a list of messages with its information such as the id of the sender, time, etc.
     */
    List<List<String>> showMessage(String userId);

    /**
     * Return the conversation between user1 and user2
     * @param userId1   String  the username of user1
     * @param userId2   String  the username of user2
     * @return List<List<String>> a list of messages between two users, user1 and user2, with its information
     * such as the id of the sender and receiver, content, and time.
     */
    List<List<String>> showConversation(String userId1, String userId2);

    /**
     * Update the status of a message.
     * @param messageId     UUID            the Id of the message
     * @param newStatus     MessageStatus   the new status of the message, could be UNREAD, READ, or ARCHIVE
     */
    void changeMessageStatus(UUID messageId, MessageStatus newStatus);

    /**
     * Get a list of messages information that has the status
     * @param status    MessageStatus   the status of the message
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    List<List<String>> getGivenStatusMessages(MessageStatus status);

    /**
     * Check if this message exists in the system with its id.
     *
     * @param messageId     UUID        the Id of the message
     * @return              boolean     True if this message id exists, else False
     */
    boolean messageIdExists(UUID messageId);

    /**
     * Delete a message
     * @param messageId     UUID            the Id of the message
     */
    void deleteMessage(UUID messageId);

    /**
     * Get a list of all messages (in list of string representation) in messageList
     * @return List<List<String>>   a list of messages in string representation in messageList
     */
    List<List<String>> getMessageInfo();
}
