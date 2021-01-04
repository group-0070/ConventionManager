package event_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Entity for storing the Event's data.
 */
public class Event {

    private final String event_id;
    private final LocalDateTime start_time;
    private final LocalDateTime end_time;
    private final String room_id;
    private ArrayList<String> attendeeIDs = new ArrayList<>();
    private final ArrayList<String> speaker_ids;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Creates a new instance of Event with 0 attendees.
     * @param eventID    String            Contains the Event ID.
     * @param startTime  LocalDateTime     Start of the event.
     * @param endTime    LocalDateTime     End of the event.
     * @param roomID     String            Contains the Room ID.
     * @param speakerIDs ArrayList<String> List of speakers at the event.
     */
    public Event(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                 ArrayList<String> speakerIDs){
        event_id = eventID;
        start_time = startTime;
        end_time = endTime;
        room_id = roomID;
        speaker_ids = speakerIDs;
    }

    /**
     * Creates a new instance of Event.
     * @param eventID     String            Contains the Event ID.
     * @param startTime   LocalDateTime     Start of the event.
     * @param endTime     LocalDateTime     End of the event.
     * @param roomID      String            Contains the Room ID.
     * @param speakerIDs  ArrayList<String> List of speakers at the event.
     * @param attendeeIDs ArrayList<String> List of attendees at the event.
     */
    public Event(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                 ArrayList<String> speakerIDs, ArrayList<String> attendeeIDs){
        event_id = eventID;
        start_time = startTime;
        end_time = endTime;
        room_id = roomID;
        speaker_ids = speakerIDs;
        this.attendeeIDs = attendeeIDs;
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
    public ArrayList<String> getAttendeeIDs() { return attendeeIDs; }

    /**
     * Returns the Speaker IDs list.
     * @return ArrayList<String> A list of Speaker IDs.
     */
    public ArrayList<String> getSpeakerIDs() { return speaker_ids; }

    /**
     * Converts event to a row in a csv file (i.e. name,startTime,endTime,room.speakers,attendees)
     * @return String csv-formatted row for the event.
     */
    @Override
    // Save List of Attendee IDs and Speaker IDs
    public String toString(){
        String formatStartTime = start_time.format(formatter);
        String formatEndTime = end_time.format(formatter);

        String attendeeString = listToString(this.attendeeIDs);
        String speakerString = listToString(this.speaker_ids);

        return this.getEventID() + "," + formatStartTime + "," + formatEndTime + "," + this.getRoomID()
                                + "," + speakerString + "," + attendeeString;
    }

    /**
     * Convert an ArrayList of strings into a single string
     * @param listedIDs   ArrayList<String>  Contains a list of strings
     * @return            String             The string representation of the ArrayList
     */
    private String listToString(ArrayList<String> listedIDs){
        if (listedIDs.size() > 0) {
            StringBuilder builder1 = new StringBuilder();
            String l;
            for (String s : listedIDs) {
                l = s + "|";
                builder1.append(l);
            }
            String res = builder1.toString();
            if (res.length() > 1) {
                res = res.substring(0, res.length() - 1);
            }
            return res;
        }
        return "ε"; // empty character
    }
}
