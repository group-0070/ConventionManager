package event_system;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface EventService {
    /**
     * Adds event to the list only if it's possible.
     * @param  startTime  LocalDateTime Start of the event.
     * @param  endTime    LocalDateTime End of the event.
     * @param  roomID     String        Contains the Room ID.
     * @param  speakerIDs  String        Contains the Speaker ID.
     * @return            boolean       True if the event has been added.
     */
    boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                     ArrayList<String> speakerIDs);
    boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                     ArrayList<String> speakerIDs, ArrayList<String> attendeeIDs);

    boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                     String speakerID);

    /**
     * Adds user to the event only if there's space.
     * @param  userID  String  The User's ID.
     * @param  eventID String  The Event's ID.
     * @return         boolean True if the user gets added.
     */
    boolean addUserToEvent(String userID, String eventID);

    /**
     * Removes given user from given event if they are signed up for it
     * @param  userID  String  The User's ID.
     * @param  eventID String  The Event's ID.
     * @return         boolean True if the user gets removed
     */
    boolean removeUserFromEvent(String userID, String eventID);

    /**
     * Returns a String version of every event.
     * @return ArrayList<String>    List of String version of events.
     */
    ArrayList<String> getStringEvents();

    /**
     * Returns a String version of every event with changes to display.
     * @return ArrayList<String>    List of String version of events to display.
     */
    ArrayList<String> getDisplayStringEvents();

    /**
     * Return the ArrayList of users that are in the given event
     * @param eventID  String               The id of the given event
     * @return         ArrayList<String>    A list of user ids in the given event id
     */
    ArrayList<String> getUsersForEvent(String eventID);

    /**
     * Return the ArrayList of users that are in the given event
     * @param userID  String               The id of the given user
     * @return        ArrayList<String>    A list of event ids that the user id has signed up for
     */
    ArrayList<String> getEventsForAttendee(String userID);

    /**
     * Return a list of events a speaker is part of
     * @param speakerID  String              Contains the speaker's ID
     * @return           ArrayList<String>   Contains all eventIDs which speakerID is in
     */
    ArrayList<String> getEventsBySpeaker(String speakerID);

    /**
     * Return a list of all attendees for all events
     * @return   ArrayList<String>  A list of attendee IDs
     */
    ArrayList<String> getListOfAttendeesId();

    /***
     * Return a list of all speaker IDs the the event_list
     * @return   ArrayList<String>  The list of all speakers in event_service
     */
    ArrayList<String> getListOfSpeakersId();
}
