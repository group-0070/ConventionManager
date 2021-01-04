package messaging_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 A entity class that stores the information of a message
 */
public class Message {
    private final String info;
    private final String senderId;
    private final String receiverId;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime time;

    /**
     * Creates a new instance of message.
     * @param info the text info of the message
     * @param senderId  the userid of the sender
     * @param receiverId  the userid of the receiver
     */
    public Message(String info, String senderId, String receiverId){
        this.info = info;
        this.senderId = senderId;
        this.receiverId = receiverId;
        time = LocalDateTime.now();
    }

    public Message(String info, String senderId, String receiverId, String timestamp) {
        this.info = info;
        this.senderId = senderId;
        this.receiverId = receiverId;
        // From String to Date (cited source):
        // https://www.java67.com/2016/04/how-to-convert-string-to-localdatetime-in-java8-example.html
        this.time = LocalDateTime.parse(timestamp, formatter);
    }

    /**
     * Returns the text information of the message.
     * @return String text information of the message
     */
    public String getInfo() {
        return info;
    }

    /**
     * Returns the userid of the message sender.
     * @return String userid of the message sender
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Returns the userid of the message receiver.
     * @return String userid of the message receiver
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * Returns the creation time of the message.
     * @return LocalDateTime the time of the message
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Return a String form of the private variable time.
     * @return value of the private variable timeString
     */
    public String getTimeString() {
        return formatter.format(time);
    }

    /**
     * Return a string representation of Message object.
     * @return a String representation of Message object in the form of
     * senderId (timestamp): info @receiverId
     */
    @Override
    public String toString() {
        return getSenderId() + " (" + getTimeString() + "): " + getInfo()
                + " @" + getReceiverId();
    }
}
