package event_system;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IEventController {
    /**
     * Adds event to the list only if it's possible.
     * @param  eventID      String        Contains the Event ID.
     * @param  startTime    LocalDateTime Start of the event.
     * @param  endTime      LocalDateTime End of the event.
     * @param  roomID       String        Contains the Room ID.
     * @param  speakerIDs   ArrayList<String>        Contains the Speaker IDs.
     * @return              boolean       True if the event has been added.
     */
    boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                     ArrayList<String> speakerIDs);
    boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                     String speakerIDs);
    /**
     * Updates the csv file for event list.
     * @return true if the file has been successfully saved
     */
    boolean saveEvents();

    /**
     * Updates the csv file for event list.
     * @return true if the file has been successfully read
     */
    boolean readEvents();

    /**
     * Getter for EventService field
     * @return  event_service  EventService  Contains the user service
     */
    EventService getEventService();

    /**
     *
     * @param userID  String                Id of the user/speaker to check
     * @return        ArrayList<String>     A list of events the user is attending/giving talks in
     */
    ArrayList<String> getEventsForUser(String userID);

    /**
     * Sign Up for an event.
     * @param userID The id of the user who is signing up.
     * @param eventID The id of the event.
     * @return True if the eventID is valid
     */
    boolean signUp(String userID, String eventID);

    /**
     * Cancels sign up for an event.
     * @param userID The id of the user who is cancelling.
     * @param eventID The id of the event.
     * @return True if the eventID is valid
     */
    boolean cancelSignUp(String userID, String eventID);

    /**
     * Gets list of events in display format (replacing empty char's with N/A.
     * @return ArrayList<String> String of events to display.
     */
    ArrayList<String> getDisplayStringEvents();
}
