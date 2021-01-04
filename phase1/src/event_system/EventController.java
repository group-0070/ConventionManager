package event_system;

import login_system.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 * Controller class for adding events.
 */

public class EventController implements IEventController{

    private final EventService event_service;
    private final IEventDataProvider data_provider;
    private final UserService user_service;

    /**
     * Initialize a EventController
     * @param eventListFileName
     * @param userService
     */
    public EventController(String eventListFileName, UserService userService){
        event_service = new EventServiceEngine();
        data_provider = new EventDataProvider(event_service, eventListFileName);
        this.user_service = userService;
    }

    /**
     * Getter for EventService field
     * @return  event_service  EventService  Contains the user service
     */
    @Override
    public EventService getEventService() {
        return event_service;
    }


    /**
     * Return the ArrayList of Events that user for userID are signed up for
     * @param userID  String                Id of the user/speaker to check
     * @return        ArrayList<String>     A list of events the user is attending/giving talks in
     */
    @Override
    public ArrayList<String> getEventsForUser(String userID) {
        ArrayList<String> speakers = user_service.getListOfSpeakerId();
        ArrayList<String> users = user_service.getListOfAttendeeId();
        ArrayList<String> events = new ArrayList<>();
        if (speakers.contains(userID)) {
            events = event_service.getEventsBySpeaker(userID);
        }
        else if (users.contains(userID)) {
            events = event_service.getEventsForAttendee(userID);
        }
        return events;
    }


    /**
     * Adds event to the list only if it's possible.
     * @param  eventID      String        Contains the Event ID.
     * @param  startTime    LocalDateTime Start of the event.
     * @param  endTime      LocalDateTime End of the event.
     * @param  roomID       String        Contains the Room ID.
     * @param  speakerIDs   ArrayList<String> Contains the Speaker ID.
     * @return              boolean       True if the event has been added.
     */
    // Verify whether the users in speakerIDs and attendeeIDs exist in the UserService.
    @Override
    public boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                            ArrayList<String> speakerIDs) {
        ArrayList<String> speakers = user_service.getListOfSpeakerId();
        //ArrayList<Boolean> onRecord = new ArrayList<Boolean>();
        for (String speaker : speakerIDs) {
            if (!speakers.contains(speaker)) {
                return false;
            }
        }
        return event_service.addEvent(eventID, startTime, endTime, roomID, speakerIDs);

    }

    /**
     * Adds event to the list only if it's possible.
     * @param  eventID      String        Contains the Event ID.
     * @param  startTime    LocalDateTime Start of the event.
     * @param  endTime      LocalDateTime End of the event.
     * @param  roomID       String        Contains the Room ID.
     * @param  speakerID    String        Contains the Speaker ID.
     * @return              boolean       True if the event has been added.
     */
    @Override
    public boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                            String speakerID){
        ArrayList<String> speakers = user_service.getListOfSpeakerId();
        if (!speakers.contains(speakerID)) {
            return false;
        }
        return event_service.addEvent(eventID, startTime, endTime, roomID, speakerID);
    }

    /**
     * Sign up the userID for the given eventID.
     * @param userID The id of the user who is signing up.
     * @param eventID The id of the event.
     * @return True if
     */
    @Override
    public boolean signUp(String userID, String eventID) {
        // verify if userID in UserService
        ArrayList<String> users = user_service.getListOfAttendeeId();
        // System.out.println(users.toString());
        for (String user: users){
            if (userID.equals(user)){
                return event_service.addUserToEvent(userID,eventID);
            }
        }
        return false;
    }

    /**
     * Removed the userID for the given eventID if they are signed up
     * @param userID The id of the user who is signing up.
     * @param eventID The id of the event.
     * @return True if
     */
    @Override
    public boolean cancelSignUp(String userID, String eventID) {
        ArrayList<String> users = user_service.getListOfAttendeeId();
        for (String user: users) {
            if (userID.equals(user)) {
                return event_service.removeUserFromEvent(userID, eventID);
            }
        }
        return false;
    }

    /**
     * Updates the csv file for event list.
     * @return boolean  True if the the data has been written to a file successfully
     */
    @Override
    public boolean saveEvents(){ return data_provider.write(); }

    /**
     * Read events from the file.
     * @return Why
     */
    @Override
    public boolean readEvents(){ return data_provider.read(); }

    /**
     * Gets list of events in display format (replacing empty char's with N/A.
     * @return ArrayList<String> String of events to display.
     */
    @Override
    public ArrayList<String> getDisplayStringEvents() {
        return event_service.getDisplayStringEvents();
    }

}