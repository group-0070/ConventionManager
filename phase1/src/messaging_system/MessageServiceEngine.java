package messaging_system;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case class for sending messages to other users
 * Empty constructor
 */
public class MessageServiceEngine implements MessageService {
    private final List<Message> messageList = new ArrayList<>();

    /**
     * Add existing message into the system from the file
     *
     * @param info       String      the information of the message
     * @param senderId   String      the id name of the sender
     * @param receiverId String      the id name of the receiver
     * @param time       String      the time recorded on the external file
     */
    public void addMessage(String info, String senderId, String receiverId, String time) {
        Message messageToAdd = new Message(info, senderId, receiverId, time);
        messageList.add(messageToAdd);
    }

    /**
     * Send multiple messages to a number of people in the system
     *
     * @param info        String      the information of the message
     * @param senderId    String      the id name of the sender
     * @param receiverIds String      a list of id name of the receivers
     * @return boolean     true if the messages are successfully sent, otherwise false
     */
    public boolean multicastMessage(String info, String senderId, List<String> receiverIds) {
        for (String receiver : receiverIds) {
            Message messageToMulticast = new Message(info, senderId, receiver);
            messageList.add(messageToMulticast);
        }
        return true;
    }

    /**
     * Send a message
     *
     * @param info       String      the information of the message
     * @param senderId   String      the id name of the sender
     * @param receiverId String      the id name of the receiver
     * @return boolean     true if message is successfully sent, otherwise false
     */
    public boolean sendMessage(String info, String senderId, String receiverId) {
        Message messageToSend = new Message(info, senderId, receiverId);
        messageList.add(messageToSend);
        return true;
    }

    /**
     * Get a list of all message information in the current system
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<String> getStringMessage() {
        ArrayList<String> res = new ArrayList<>();
        for (Message m : this.messageList) {
            res.add(m.toString());
        }
        return res;
    }

    /**
     * Get a list of all formatted message information in the current system in preparation to be written
     * into the data source
     *
     * @return List<String> a list of messages with its information in the order of senderId, receiverId, info, time,
     * separated by special character "ç".
     */
    public List<String> formatStringMessage() {
        ArrayList<String> res = new ArrayList<>();
        for (Message m : this.messageList) {
            res.add(m.getSenderId() + "ç" + m.getReceiverId() + "ç" + m.getInfo() + "ç" + m.getTimeString());
        }
        return res;
    }

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     *
     * @return List<String> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<String> showMessage(String userId) {
        List<String> res = new ArrayList<>();
        for (Message m : this.messageList) {
            if (m.getReceiverId().equals(userId) || m.getSenderId().equals(userId)) {
                res.add(m.toString());
            }
        }
        return res;
    }

    // This is a method we will use for Phase 2. It is not dead code :)
    /**
     * Return the conversation between user1 and user2
     * @param userId1   String  the username of user1
     * @param userId2   String  the username of user2
     * @return List<String> a list of messages between two users, user1 and user2, with its information
     * such as the id of the sender and receiver, content, and time.
     */
    public List<String> showConversation(String userId1, String userId2) {
        List<String> res = new ArrayList<>();
        for (Message m : this.messageList) {
            if ((m.getReceiverId().equals(userId1) && m.getSenderId().equals(userId2))
                    || m.getSenderId().equals(userId1) && m.getReceiverId().equals(userId2)) {
                res.add(m.toString());
            }
        }
        return res;
    }
}
