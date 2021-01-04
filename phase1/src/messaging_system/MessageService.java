package messaging_system;

import java.util.List;

public interface MessageService {
    /**
     * Send a message
     * @param info          String      the information of the message
     * @param senderId      String      the id name of the sender
     * @param receiverId    String      the id name of the receiver
     * @return              boolean     true if message is successfully sent, otherwise false
     */
    boolean sendMessage(String info, String senderId, String receiverId);

    /**
     * Send multiple messages to a number of people in the system
     * @param info          String      the information of the message
     * @param senderId      String      the id name of the sender
     * @param receiverIds   String      a list of id name of the receivers
     * @return              boolean     true if the messages are successfully sent, otherwise false
     */
    boolean multicastMessage(String info, String senderId, List<String> receiverIds);

    /**
     * Add existing message into the system from the file
     * @param info          String      the information of the message
     * @param senderId      String      the id name of the sender
     * @param receiverId    String      the id name of the receiver
     * @param time          String      the time recorded on the external file
     */
    void addMessage(String info, String senderId, String receiverId, String time);

    /**
     * Get a list of all message information in the current system
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    List<String> getStringMessage();

    /**
     * Get a list of all formatted message information in the current system in preparation to be written
     * into the data source
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.,
     * separated by some special character.
     */
    List<String> formatStringMessage();

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    List<String> showMessage(String userId);

    /**
     * Return the conversation between user1 and user2
     * @param userId1   String  the username of user1
     * @param userId2   String  the username of user2
     * @return List<String> a list of messages between two users, user1 and user2, with its information
     * such as the id of the sender and receiver, content, and time.
     */
    List<String> showConversation(String userId1, String userId2);
}
