package message_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 A entity class that stores the information of a message
 */
public class Message {
    private final UUID message_id;
    private final String info;
    private final String sender_id;
    private final String receiver_id;
    private MessageStatus status;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime time;

    /**
     * Creates a new instance of message.
     * @param message_id     UUID            the id of the message
     * @param info          String          the text info of the message
     * @param sender_id      String          the userid of the sender
     * @param receiver_id    String          the userid of the receiver
     * @param timestamp     LocalDateTime   the time when the message is sent
     */
    public Message(UUID message_id, String info, String sender_id, String receiver_id,
                   LocalDateTime timestamp, MessageStatus status) {
        this.message_id = message_id;
        this.info = info;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        // From String to Date (cited source):
        // https://www.java67.com/2016/04/how-to-convert-string-to-localdatetime-in-java8-example.html
        this.time = timestamp;
        this.status = status;
    }

    /**
     * Returns the text information of the message.
     * @return String   text information of the message
     */
    public String getInfo() {
        return info;
    }

    /**
     * Returns the userid of the message sender.
     * @return String   userid of the message sender
     */
    public String getSenderID() {
        return sender_id;
    }

    /**
     * Returns the userid of the message receiver.
     * @return String   userid of the message receiver
     */
    public String getReceiverID() {
        return receiver_id;
    }

    /**
     * Returns the status of this message.
     * @return MessageStatus    an enum of the status, could be READ, UNREAD, or ARCHIVE.
     */
    public MessageStatus getStatus(){
        return this.status;
    }

    /**
     * Update the status of this message, could be read, unread, or delete.
     * @param newStatus MessageStatus the new status message is updated to
     */
    public void setStatus(MessageStatus newStatus){
        this.status = newStatus;
    }

    /**
     * Returns the id of the message.
     * @return UUID     the id of the message  
     */
    public UUID getMessageID() {
        return message_id;
    }

    /**
     * Returns the creation time of the message.
     * @return LocalDateTime    the time of the message
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Return a String form of the private variable time.
     * @return String   value of the private variable timeString
     */
    public String getTimeString() {
        return formatter.format(time);
    }

    /**
     * Return a List of strings representation of Message object.
     * @return List<String> representation of Message object in the form of
     * 0: UUID      message_id
     * 1: String    sender_id
     * 2: String    receiver_id
     * 3: String    info of the message
     * 4: String    time of the message
     * 5: String    status of the message
     */
    public List<String> toStrings() {
        List<String> res = new ArrayList<>();
        res.add(getMessageID().toString());
        res.add(sender_id);
        res.add(receiver_id);
        res.add(info);
        res.add(getTimeString());
        res.add(getStatus().toString());
        return res;
    }

}
