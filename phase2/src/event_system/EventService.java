package event_system;

import user_system.UserType;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    /**
     * Adds am event to event_service, with overloaded parameter attendeeIDs
     * @param eventType      EventType           Contains the type of the event
     * @param eventCapacity  Int                 Contains the capacity of the event
     * @param eventID        String              Contains the event ID
     * @param startTime      LocalDateTime       Contains the start time of the event
     * @param endTime        LocalDateTime       Contains the end time of the event
     * @param roomID         String              Contains the room ID
     * @param speakerIDs     List<String>        Contains a list of speaker IDs
     * @param attendeeIDs    List<String>        Contains a list of attendee IDs
     */
    void addEvent(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                  LocalDateTime endTime, String roomID, List<String> speakerIDs, List<String> attendeeIDs);


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
     * Method to get a nested List of events
     * @return  List<List<List<String>>>  An List containing events Lists
     */
    List<List<List<String>>> getListEvents();

    /**
     * Return the List of users that are in the given event
     * @param eventID  String               The id of the given event
     * @return         List<String>    A list of user ids in the given event id
     */
    List<String> getUsersForEvent(String eventID);

    /**
     * Return the List of users that are in the given event
     * @param userID  String               The id of the given user
     * @return        List<String>    A list of event ids that the user id has signed up for
     */
    List<String> getEventsForAttendee(String userID);

    /**
     * Return a list of events a speaker is part of
     * @param speakerID  String              Contains the speaker's ID
     * @return           List<String>   Contains all eventIDs which speakerID is in
     */
    List<String> getEventsBySpeaker(String speakerID);

    /**
     * Return a list of events the user is a part of
     * @param userType  UserType  Contains the user type
     * @param userID    String          Contains the user ID
     * @return                          A list of events the user is a part of
     */
    List<String> getEventsByUserType(UserType userType, String userID);

    /**
     * Checks if a room is being booked more than once in a specific time period
     * @param room_id  String         Contains the room id
     * @param start    LocalDateTime  Contains the start time
     * @param end      LocalDateTime  Contains the end time
     * @return         Boolean        True if the room is being double booked
     */
    boolean isDoubleBookingRoom(String room_id, LocalDateTime start, LocalDateTime end);

    /**
     * Checks if a speaker is being booked in two separate events at the same time
     * @param speakers   List<String>  Contains a list of speakers
     * @param start      LocalDateTime      Contains the start time
     * @param end        LocalDateTime      Contains the end time
     * @return           Boolean            True if the speaker is being double booked
     */
    boolean isDoubleBookingSpeaker(List<String> speakers, LocalDateTime start, LocalDateTime end);

    /**
     * Returns true if the user has already signed up before for the given event
     * @param eventId String The eventId of the event.
     * @param userId  String The userId of the user.
     */
    boolean isSignUpBefore(String eventId,String userId);

    /**
     * Method to check if the event is full
     * @param eventId  String  Contains the event ID
     * @return         boolean True if the event is full, false otherwise or if the eventID is not valid
     */
    boolean isEventFull(String eventId);

    /**
     * Checks if the event exists
     * @param eventId  String   Contains the event ID
     * @return         boolean  True if the event exists
     */
    boolean isEventExist(String eventId);

    /**
     * Checks if the start time is after the end time
     * @param  t1      LocalDateTime  The start time
     * @param  t2      LocalDateTime  The end time
     * @return         boolean          True if the time is valid
     */
    boolean areValidTimes(LocalDateTime t1, LocalDateTime t2);


    /**
     * Cancels an event using the event ID
     * @param eventID  String   Contains the event ID
     * @return         boolean  True if the event has been successfully deleted, False if the event DNE
     */
    boolean cancelEventByID(String eventID);

    /**
     * Cancels all events of a specific type
     * @param eventType  EventType  Contains the event type
     * @return           boolean          True if at least 1 event of type eventType has been deleted
     */
    boolean cancelEventsByType(EventType eventType);

    /**
     * Cancels all events with the number of attendees == numAttendees or >= numAttendees
     * @param numAttendees  int      Contains the maximum number of attendees all events are allowed to have
     * @param atLeast       boolean  Whether the comparison should >= or ==.
     * @return              boolean  True if at least 1 event with attendance > maxAttendees has been removed
     */
    boolean cancelEventsBySize(int maxAttendees, boolean atLeast);

    /**
     * Changes the event capacity to a new value if valid
     * @param newCapacity  int      Contains the new event capacity
     * @param eventID      String   Contains the event ID of the event that's capacity must be changed
     * @return             boolean  True if the event capacity has been changed successfully
     */
    boolean changeEventCapacity(String eventID, int newCapacity);

    /**
     * Returns the total number of attendees and speakers.
     * @param eventID     String    The Id of the event.
     */
    int getNumAttendance(String eventID);

    /**
     * Method to check if a speaker is being added twice to the same event
     * @param speakerList  List<String>  Contains the speaker IDs for the event
     * @return             boolean       True if the speakers are not distinct
     */
    boolean isSpeakerDuplicate(List<String> speakerList);
}
