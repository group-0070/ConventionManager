package event_system;


import java.time.LocalDateTime;
import java.util.List;

public interface IEventController {

    /**
     * Adds an event to the event list only if it's possible
     * @param eventType     EventType          Contains the Event Type
     * @param eventCapacity int                Contains the capacity of the event
     * @param eventID       String             Contains the Event ID
     * @param startTime     LocalDateTime      Contains the start time of the event
     * @param endTime       LocalDateTime      Contains the end time of the event
     * @param roomID        String             Contains the room ID
     * @param speakerIDs    List<String>       A list of speaker IDs
     * @param attendeeIDs   List<String>       A list of attendee IDs
     * @return              EventPrompt        Returns appropriate EventPrompt corresponding to the check result
     *
     */
    EventPrompt addEvent(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                               LocalDateTime endTime, String roomID, List<String> speakerIDs,
                               List<String> attendeeIDs);

    /**
     * Updates the csv file for event list.
     * @return true if the file has been successfully saved
     */
    boolean save();

    /**
     * Method to read events from the database
     * @return  boolean  True if the events have successfully been read from the database
     */
    boolean load();

    /**
     * Return the List of Events that user for userID are signed up for
     * @param userID  String                Id of the user/speaker to check
     * @return        List<String>          A list of events the user is attending/giving talks in
     */
    List<String> getEventsForUser(String userID);

    /**
     * Return a list of events a user is a part of
     * @return  List<String>  Contains the list of events a user is a part of
     */
    List<String> getEventsForUser();

    /**
     * Deletes all events with the given event ID
     * @param eventID  String   Contains the event ID
     * @return         boolean  True if at least one event if the id eventID has been cancelled
     */
    boolean cancelEventByID(String eventID);

    /**
     * Deletes all events with the given event ID
     * @param eventType  EventType   Contains the event type
     * @return           boolean     True if at least one event of type eventType has been cancelled
     */
    boolean cancelEventsByType(EventType eventType);

    /**
     * Deletes all events that have # of attendees > maxAttendees
     * @param maxAttendees  int      The maximum # of attendees an event can have
     * @return              boolean  True if at least 1 event with # of attendees > max attendees has been deleted
     */
    boolean cancelEventsBySize(int maxAttendees, boolean atLeast);

    /**
     * Method to get a nested list of events in list form
     * @return  List<List<List<String>>>  A list where each element is a nested list in the form [[eventType],
     *                                    [eventCap], [eventID], [startTime], [endTime], [roomID], [speakers],
     *                                    [attendees]]
     */
    List<List<List<String>>> getListEvents();

    /**
     * Changes the event capacity to a new value if the following three things are satisfied:
     * 1.The event exists
     * 2.The new eventCapacity is smaller than or equal to roomCapacity
     * 3.The new eventCapacity is larger than the current number of attendees and speakers in total
     * @param newCapacity  int      Contains the new event capacity
     * @param eventID      String   Contains the event ID of the event that's capacity must be changed
     * @param roomID       String   The room ID.
     * @return             boolean  True if the event capacity has been changed successfully
     */
    boolean modifyEventCapacity(String eventID, String roomID, int newCapacity);

    /**
     * Sign up the userID for the given eventID.
     * @param   userID       The id of the user who is signing up.
     * @param   eventID      The id of the event.
     * @return  EventPrompt  USER_DOUBLE_SIGNUP: if the user has already sign up before for the event
     *                       EVENT_FULL: if the event is already full
     *                       EVENT_DNE: if the event is invalid(i.e does not exist)
     *                       ATTENDEE_DNE: if the user is invalid(i.e does not exist)
     *                       SIGNUP_SUCCESS: if sign up successful
     */
    EventPrompt signUp(String userID, String eventID);
    // Overload with currently login User
    EventPrompt signUp(String eventID);

    /**
     * Cancels sign up for an event.
     * @param userID The id of the user who is cancelling.
     * @param eventID The id of the event.
     * @return True if the eventID is valid
     */
    EventPrompt cancelSignUp(String userID, String eventID);
    // Overload with currently login User
    EventPrompt cancelSignUp(String eventID);

}
