package event_system;

/**
 * Enum Class for the indexes of event representation
 */
public enum EventIndex {
    EVENT_TYPE(0),
    EVENT_CAPACITY(1),
    EVENT_ID(2),
    START_TIME(3),
    END_TIME(4),
    ROOM_ID(5),
    SPEAKER_IDS(6),
    ATTENDEE_IDS(7);

    int value;

    EventIndex(int value){
        this.value=value;
    }

    public int getValue(){
        return this.value;
    }


}
