package message_system;

/**
 * Enum Class for the indexes of message representation
 */
public enum MessageIndex {
    MESSAGE_ID(0),
    SENDER(1),
    RECEIVER(2),
    MESSAGE_INFO(3),
    TIME(4),
    STATUS(5);

    int value;

    MessageIndex(int value){
        this.value=value;
    }

    public int getValue(){
        return this.value;
    }
}
