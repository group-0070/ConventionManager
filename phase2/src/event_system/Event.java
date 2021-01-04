package event_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for storing the Event's data.
 */
public class Event {
    private final EventType event_type;
    private int eventCapacity;
    private final String event_id;
    private final LocalDateTime start_time;
    private final LocalDateTime end_time;
    private final String room_id;
    private final List<String> attendeeIDs;
    private final List<String> speaker_ids;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Creates a new instance of Event.
     * @param eventType   Enum<EventType>   Contains the event type.
     * @param eventID     String            Contains the Event ID.
     * @param startTime   LocalDateTime     Start of the event.
     * @param endTime     LocalDateTime     End of the event.
     * @param roomID      String            Contains the Room ID.
     * @param speakerIDs  ArrayList<String> List of speakers at the event.
     * @param attendeeIDs ArrayList<String> List of attendees at the event.
     */
    public Event(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                 LocalDateTime endTime, String roomID, List<String> speakerIDs, List<String> attendeeIDs){
        event_type = eventType;
        this.eventCapacity = eventCapacity;
        event_id = eventID;
        start_time = startTime;
        end_time = endTime;
        room_id = roomID;
        speaker_ids = speakerIDs;
        this.attendeeIDs = attendeeIDs;
    }

    /**
     * Returns the event type
     * @return  Enum<EventType>  The event type
     */
    public EventType getEventType(){
        return event_type;
    }

    /**
     * Returns the event ID.
     * @return String Event ID.
     */
    public String getEventID() {
        return event_id;
    }

    /**
     * Returns the start of the event.
     * @return DateTime Start of the event.
     */
    public LocalDateTime getStartTime() {
        return start_time;
    }

    /**
     * Returns the end of the event.
     * @return DateTime End of the event.
     */
    public LocalDateTime getEndTime() {
        return end_time;
    }

    /**
     * Returns the Room ID.
     * @return String Room ID.
     */
    public String getRoomID() {
        return room_id;
    }

    /***
     * Return a list of attendee IDs
     * @return ArrayList<String> A list of attendee IDs
     */
    public List<String> getAttendeeIDs() { return attendeeIDs; }

    /**
     * Returns the Speaker IDs list.
     * @return ArrayList<String> A list of Speaker IDs.
     */
    public List<String> getSpeakerIDs() { return speaker_ids; }

    /**
     * Returns the event capacity.
     * @return  int  Contains the event's capacity (the maximum # of people that can attend the event)
     */
    public int getEventCapacity(){
        return eventCapacity;
    }

    /**
     * Setter for event_capacity (used to set a new event capacity)
     * @param newCapacity  int  Contains the new event capacity
     */
    public void setEventCapacity(int newCapacity){
        this.eventCapacity = newCapacity;
    }

    /**
     * Converts event to a nested List containing all the event info
     * @return   List<List<String>>   A nested list in the form [[eventType], [eventCap], [eventID], [startTime],
     *                                [endTime], [roomID], [speakers], [attendees]]
     */
    public List<List<String>> toEventArray(){
        String eventTypeString = event_type.toString();
        String eventCapacity = Integer.toString(this.eventCapacity);
        String formatStartTime = start_time.format(formatter);
        String formatEndTime = end_time.format(formatter);


        List<String> eventType = addStringToList(eventTypeString);
        List<String> eventCap = addStringToList(eventCapacity);
        List<String> eventID = addStringToList(this.getEventID());
        List<String> startTime = addStringToList(formatStartTime);
        List<String> endTime = addStringToList(formatEndTime);
        List<String> roomID = addStringToList(this.getRoomID());
        List<String> speakers;
        speakers = this.getSpeakerIDs();
        List<String> attendees;
        attendees = this.getAttendeeIDs();
        List<List<String>> arrayToReturn= new ArrayList<>();
        Collections.addAll(arrayToReturn, eventType, eventCap, eventID, startTime, endTime, roomID, speakers,
                attendees);
        return arrayToReturn;
    }

    /**
     * Returns a List of String containing one of the event info
     * @param infoToAdd  List<String>  A list containing event info in a single string
     * @return           List<String>  A list containing info of each event as single strings
     */
    private List<String> addStringToList(String infoToAdd){
        List<String> eventInfoList = new ArrayList<>();
        eventInfoList.add(infoToAdd);
        return eventInfoList;
    }
}
