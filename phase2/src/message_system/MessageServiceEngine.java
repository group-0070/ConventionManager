package message_system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use case class for sending messages to other users
 * Empty constructor
 */
public class MessageServiceEngine implements MessageService {
    private List<Message> messageList = new ArrayList<>();

    /**
     * Add existing message into the system from the file
     *
     * @param messageId  UUID            the id of the message
     * @param info       String          the information of the message
     * @param senderId   String          the id name of the sender
     * @param receiverId String          the id name of the receiver
     * @param time       LocalDateTime   the time recorded on the external file
     * @param status     MessageStatus   the status of the message
     */
    public void addMessage(UUID messageId, String info, String senderId, String receiverId, LocalDateTime time, MessageStatus status) {
        Message messageToAdd = new Message(messageId, info, senderId, receiverId, time, status);
        messageList.add(messageToAdd);
    }

    /**
     * Send a message
     *
     * @param info       String         the information of the message
     * @param senderId   String         the id name of the sender
     * @param receiverId String         the id name of the receiver
     */
    public void addMessage(String info, String senderId, String receiverId) {
        Message messageToSend = new Message(UUID.randomUUID(), info, senderId, receiverId, LocalDateTime.now(), MessageStatus.UNREAD);
        messageList.add(messageToSend);
    }

    /**
     * Send multiple messages to a number of people in the system
     *
     * @param info        String      the information of the message
     * @param senderId    String      the id name of the sender
     * @param receiverIds String      a list of id name of the receivers
     */
    public void multiMessage(String info, String senderId, List<String> receiverIds) {
        for (String receiver : receiverIds) {
            Message messageToMulticast = new Message(UUID.randomUUID(), info, senderId, receiver, LocalDateTime.now(), MessageStatus.UNREAD);
            messageList.add(messageToMulticast);
        }
    }

    /**
     * Get a list of messages information that this userId has association with (a history log of messages)
     * @param  userId   String    the related userId whose messages are shown
     * @return List<List<String>> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<List<String>> showMessage(String userId) {
        List<List<String>> res = new ArrayList<>();
        for (Message m : this.messageList) {
            // dictionary might be better per message?
            if (m.getSenderID().equals(userId) || m.getReceiverID().equals(userId)) {
                res.add(m.toStrings());
            }
        }
        return res;
    }

    /**
     * Get a list of messages information that has the status
     * @param status    MessageStatus   the status of the message
     * @return List<List<String>> a list of messages with its information such as the id of the sender, time, etc.
     */
    public List<List<String>> getGivenStatusMessages(MessageStatus status) {
        List<List<String>> res = new ArrayList<>();
        for (Message m : this.messageList) {
            if (m.getStatus().equals(status))
                res.add(m.toStrings());}
        return res;
    }

    /**
     * Return the conversation between user1 and user2
     * @param userId1   String  the username of user1
     * @param userId2   String  the username of user2
     * @return List<String> a list of messages between two users, user1 and user2, with its information
     * such as the id of the sender and receiver, content, and time.
     */
    public List<List<String>>showConversation(String userId1, String userId2) {
        List<List<String>> res = new ArrayList<>();
        for (Message m : this.messageList) {
            if ((m.getReceiverID().equals(userId1) && m.getSenderID().equals(userId2))
                    || m.getSenderID().equals(userId1) && m.getReceiverID().equals(userId2)) {
                res.add(m.toStrings());
            }
        }
        return res;
    }

    /**
     * Check if this message exists in the system with its id.
     *
     * @param messageId     UUID        the Id of the message
     * @return              boolean     True if this message id exists, else False
     */
    public boolean messageIdExists(UUID messageId){
        return (getMessage(messageId) != null);
    }

    /**
     * Update the status of a message.
     *
     * @param messageId     UUID            the Id of the message
     * @param newStatus     MessageStatus   the new status of the message, could be UNREAD, READ, or ARCHIVE
     */
    public void changeMessageStatus(UUID messageId, MessageStatus newStatus){
        Message m = getMessage(messageId);
        if (m != null){
            m.setStatus(newStatus);
        }
    }

    /**
     * Delete a message
     * @param messageId     UUID            the Id of the message
     */
    public void deleteMessage(UUID messageId){
        this.messageList.removeIf(m -> m.getMessageID().equals(messageId));
    }

    /**
     * Get a list of all messages (in list of string representation) in messageList
     * @return List<List<String>>   a list of messages in string representation in messageList
     */
    public List<List<String>> getMessageInfo(){
        List<List<String>> res = new ArrayList<>();
        for (Message m: messageList){
            res.add(m.toStrings());
        }
        return res;
    }

    /*
    * Helper function:
    * Locate the message in the messageList based on the messageID
    * @para UUID    the id of the message
     */
    private Message getMessage(UUID messageID){
        for (Message m : this.messageList) {
            if (m.getMessageID().equals(messageID)) {
                return m;
            }
        }
        return null;
    }

}