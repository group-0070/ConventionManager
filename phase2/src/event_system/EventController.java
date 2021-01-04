package event_system;

import user_system.UserService;
import user_system.UserType;
import room_system.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Controller class for adding events.
 */

public class EventController implements IEventController{

    private final EventService event_service;
    private final EventDatabaseReadWriter data_provider;
    private final UserService user_service;
    private final RoomService room_service;

    /**
     * Initialize a EventController
     * @param eventListFileName The file name of the event list.
     * @param userService       The userService.
     * @param eventService      The eventService.
     */
    public EventController(String eventListFileName, UserService userService,
                           EventService eventService, RoomService roomService){
        event_service = eventService;
        data_provider = new EventDatabaseReadWriter(event_service, eventListFileName);
        this.user_service = userService;
        this.room_service = roomService;
    }

    /**
     * Method to read events from the database
     * @return  boolean  True if the events have successfully been read from the database
     */
    public boolean load(){
        return data_provider.read();
    }

    /**
     * Return the List of Events that user for userID are signed up for
     * @param userID  String                Id of the user/speaker to check
     * @return        List<String>          A list of events the user is attending/giving talks in
     */
    @Override
    public List<String> getEventsForUser(String userID) {
        List<String> speakers = user_service.getListOfIDsByType(UserType.SPEAKER);
        List<String> attendees = user_service.getListOfIDsByType(UserType.ATTENDEE);
        List<String> events = new ArrayList<>();
        if (speakers.contains(userID)) {
            events = event_service.getEventsByUserType(UserType.SPEAKER, userID);
        }
        else if (attendees.contains(userID)) {
            events = event_service.getEventsByUserType(UserType.ATTENDEE, userID);
        }
        return events;
    }

    /**
     * Return a list of events a user is a part of
     * @return  List<String>  Contains the list of events a user is a part of
     */
    @Override
    public List<String> getEventsForUser(){
        return getEventsForUser(user_service.getCurrentUserID());
    }


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
    @Override
    public EventPrompt addEvent(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                                      LocalDateTime endTime, String roomID, List<String> speakerIDs,
                                      List<String> attendeeIDs) {
        //Make the eventId/roomId be trimmed
        String trimRoomID = roomID.trim();
        String trimEventID = eventID.trim();
        if (event_service.isDoubleBookingRoom(trimRoomID, startTime, endTime)){
            return EventPrompt.DOUBLE_BOOK_ROOM; // trying to book a room at the same time as existing events
        }
        else if(event_service.isEventExist(trimEventID)){
            return EventPrompt.EVENT_ALREADY_EXIST;
        }
        else if (!room_service.isRoomExist(trimRoomID)){
            return EventPrompt.ROOM_DNE;
        }
        else if (room_service.isExceedingRoomCapacity(attendeeIDs.size() + speakerIDs.size(), trimRoomID)){
            return EventPrompt.ATTENDEE_OVERLOAD;
        }
        else if (!room_service.isValidEventCapacity(eventCapacity, trimRoomID)){
            return EventPrompt.INVALID_EVENT_CAPACITY;
        }
        else if (!event_service.areValidTimes(startTime, endTime)){
            return EventPrompt.INVALID_TIME_SELECTION; // the startTime and endTime combination chosen is invalid
        }
        else if (!user_service.getListOfIDsByType(UserType.SPEAKER).containsAll(speakerIDs)){
            return EventPrompt.SPEAKER_DNE; // trying to book an event with non-existing speakers
        }
        else if (event_service.isDoubleBookingSpeaker(speakerIDs, startTime, endTime)){
            return EventPrompt.DOUBLE_BOOK_SPEAKER; // you are trying to double book a speaker
        }
        else if (!user_service.getListOfIDsByType(UserType.ATTENDEE).containsAll(attendeeIDs)){
            return EventPrompt.ATTENDEE_DNE; // you are trying to add attendees that don't exist in the system
        }
        else if (room_service.isExceedingRoomCapacity(attendeeIDs.size(),trimRoomID)){
            return EventPrompt.EXCEEDS_ROOM_CAPACITY;
            // the number of attendees is exceeding the room capacity
        }
        else if (attendeeIDs.size() + speakerIDs.size() > eventCapacity){
            return EventPrompt.INVALID_EVENT_CAPACITY; // the event capacity is took small but the room size is valid
        }
        else if (!isValidNumSpeakers(eventType, speakerIDs)){
            return EventPrompt.NUM_SPEAKERS_MISMATCH; // the number of speaker does not correspond to the event ype
        }
        else if (event_service.isSpeakerDuplicate(speakerIDs)){
            return EventPrompt.SAME_SPEAKER_ADDED;
        }
        else{
            event_service.addEvent(eventType, eventCapacity, trimEventID, startTime, endTime, trimRoomID, speakerIDs,
                    attendeeIDs);
            return EventPrompt.EVENT_ADDED; // you have added the event successfully
        }
    }


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
    @Override
    public EventPrompt signUp(String userID, String eventID) {
        if (event_service.isSignUpBefore(eventID,userID)){
            return EventPrompt.USER_DOUBLE_SIGNUP; // the user has already sign up before for the event
        }
        else if (event_service.isEventFull(eventID)){
            return EventPrompt.EVENT_FULL; // the event is already full
        }
        else if (!event_service.isEventExist(eventID)){
            return EventPrompt.EVENT_DNE; // the event does not exist
        }
        else if(!user_service.getListOfIDsByType(UserType.ATTENDEE).contains(userID)){
            return EventPrompt.ATTENDEE_DNE; // the attendee is invalid(i.e doesn't exist)
        }
        else{
            event_service.addUserToEvent(userID,eventID);
            return EventPrompt.SIGNUP_SUCCESS; // successful sign up!
        }
    }

    /**
     * Sign up the currently login user for the given eventID.
     * @param   eventID      The id of the event.
     * @return  EventPrompt  USER_DOUBLE_SIGNUP: if the user has already sign up before for the event
     *                       EVENT_FULL: if the event is already full
     *                       EVENT_DNE: if the event is invalid(i.e does not exist)
     *                       ATTENDEE_DNE: if the user is invalid(i.e does not exist)
     *                       SIGNUP_SUCCESS: if sign up successful
     */
    @Override
    public EventPrompt signUp(String eventID){
        return signUp(user_service.getCurrentUserID(), eventID);
    }

    /**
     * Method to get a nested list of events in list form
     * @return  List<List<List<String>>>  A list where each element is a nested list in the form [[eventType],
     *                                    [eventCap], [eventID], [startTime], [endTime], [roomID], [speakers],
     *                                    [attendees]]
     */
    @Override
    public List<List<List<String>>> getListEvents(){
        return event_service.getListEvents();
    }

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
    @Override
    public boolean modifyEventCapacity(String eventID, String roomID, int newCapacity){
        if (event_service.isEventExist(eventID) &&
                room_service.isValidEventCapacity(newCapacity,roomID) &&
                newCapacity>=event_service.getNumAttendance(eventID)){
            event_service.changeEventCapacity(eventID,newCapacity);
            return true;
        }
        return false;
    }

    /**
     * Removed the userID for the given eventID if they are signed up
     * @param   userID         The id of the user who is signing up.
     * @param   eventID        The id of the event.
     * @return  EventPrompt    EVENT_DNE if the event does not exist
     *                         ATTENDEE_DNE if the user does not exist,
     *                         ATTENDEE_NOT_IN_EVENT if the attendee is not in the event
     *                         CANCEL_SUCCESS if the user has been successfully removed from the event
     */
    @Override
    public EventPrompt cancelSignUp(String userID, String eventID) {
        if(!event_service.isEventExist(eventID)){
            return EventPrompt.EVENT_DNE;
        }
        else if (!user_service.getListOfIDsByType(UserType.ATTENDEE).contains(userID)){
            return EventPrompt.ATTENDEE_DNE;
        }
        else if(!event_service.getUsersForEvent(eventID).contains(userID)){
            return EventPrompt.ATTENDEE_NOT_IN_EVENT;
        }
        else{
            event_service.removeUserFromEvent(userID,eventID);
            return EventPrompt.CANCEL_SUCCESS;
        }
    }

    /**
     * Removes the currently login user from an event with the ID eventID
     * @param    eventID        String containing the event ID
     * @return   EventPrompt    EVENT_DNE if the event does not exist, ATTENDEE_DNE if the user does not exist,
     *                          ATTENDEE_NOT_IN_EVENT if the attendee is not in the event, or CANCEL_SUCCESS if the user
     *                          has been successfully removed from the event
     */
    @Override
    public EventPrompt cancelSignUp(String eventID){
        return cancelSignUp(user_service.getCurrentUserID(), eventID);
    }

    /**
     * Deletes all events with the given event ID
     * @param eventID  String   Contains the event ID
     * @return         boolean  True if at least one event if the id eventID has been cancelled
     */
    @Override
    public boolean cancelEventByID(String eventID){
        return event_service.cancelEventByID(eventID);
    }

    /**
     * Deletes all events with the given event ID
     * @param eventType  EventType   Contains the event type
     * @return           boolean     True if at least one event of type eventType has been cancelled
     */
    @Override
    public boolean cancelEventsByType(EventType eventType){
        return event_service.cancelEventsByType(eventType);
    }

    /**
     * Deletes all events that have # of attendees > maxAttendees
     * @param maxAttendees  int      The maximum # of attendees an event can have
     * @return              boolean  True if at least 1 event with # of attendees > max attendees has been deleted
     */
    @Override
    public boolean cancelEventsBySize(int maxAttendees, boolean atLeast){
        return event_service.cancelEventsBySize(maxAttendees, atLeast);
    }

    /**
     * Writes to external DB with updated events list before exiting program
     * @return boolean  True if the the data has been written to the DB successfully
     */
    @Override
    public boolean save(){ return data_provider.write(); }

    /**
     * Checks if the number of speakers are matching the eventType
     * @param eventType   EventType    The type pf the event.
     * @param speakerList List<String> Speaker list.
     */
    private boolean isValidNumSpeakers(EventType eventType, List<String> speakerList){
        int numSpeakers = speakerList.size();
        switch (eventType){
            case NO_SPEAKER_EVENT:
                return numSpeakers==0;
            case SINGLE_SPEAKER_EVENT:
                return numSpeakers==1;
            case MULTI_SPEAKER_EVENT:
                return numSpeakers>1;
        }
        return false;
    }

}