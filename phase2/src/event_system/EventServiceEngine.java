package event_system;

import user_system.UserType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/**
 * Use case class for storing and adding events.
 */
public class EventServiceEngine implements EventService {

    private final List<Event> event_list = new ArrayList<>();

    /**
     * Method to get a nested List of events
     * @return  List<List<List<String>>>  An List containing events Lists
     */
    @Override
    public List<List<List<String>>> getListEvents(){
        List<List<List<String>>> res = new ArrayList<>();
        for (Event event: event_list){
            res.add(event.toEventArray());
        }
        return res;
    }


    /**
     * Adds am event to event_service, with overloaded parameter attendeeIDs
     * @param eventType      EventType           Contains the type of the event
     * @param eventCapacity  Int                 Contains the capacity of the event
     * @param eventID        String              Contains the event ID
     * @param startTime      LocalDateTime       Contains the start time of the event
     * @param endTime        LocalDateTime       Contains the end time of the event
     * @param roomID         String              Contains the room ID
     * @param speakersID     List<String>        Contains a list of speaker IDs
     * @param attendeeIDs    List<String>        Contains a list of attendee IDs
     */
    @Override
    public void addEvent(EventType eventType, int eventCapacity, String eventID, LocalDateTime startTime,
                         LocalDateTime endTime, String roomID, List<String> speakersID,
                         List<String> attendeeIDs){
        Event eventToAdd = new Event(eventType, eventCapacity, eventID, startTime, endTime, roomID, speakersID,
                attendeeIDs);
        event_list.add(eventToAdd);
    }

    /**
     * Cancels an event using the event ID
     * @param eventID  String   Contains the event ID
     * @return         boolean  True if the event has been successfully deleted, False if the event DNE
     */
    public boolean cancelEventByID(String eventID){
        Event event = searchEvent(eventID);
        if (event!=null){
            event_list.remove(event);
            return true;
        }
        return false;
    }

    /**
     * Cancels all events of a specific type
     * @param eventType  Enum<EventType>  Contains the event type
     * @return           boolean          True if at least 1 event of type eventType has been deleted
     */
    public boolean cancelEventsByType(EventType eventType){
        int count = 0;
        Iterator<Event> iterator = event_list.iterator();
        while (iterator.hasNext()){
            Event event = iterator.next();
            if (event.getEventType().equals(eventType)){
                iterator.remove();
                count++;
            }
        }
        return count > 0;
    }

    /**
     * Cancels all events with the number of attendees == numAttendees or >= numAttendees
     * @param numAttendees  int      Contains the maximum number of attendees all events are allowed to have
     * @param atLeast       boolean  Whether the comparison should >= or ==.
     * @return              boolean  True if at least 1 event with attendance > maxAttendees has been removed
     */
    public boolean cancelEventsBySize(int numAttendees, boolean atLeast){
        int count = 0;
        if (numAttendees < 0) return false;
        Iterator<Event> iterator = event_list.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if ((atLeast && event.getAttendeeIDs().size() >= numAttendees) ||
                    (!atLeast && event.getAttendeeIDs().size() == numAttendees)) {
                iterator.remove();
                count++;
            }
        }
        return count > 0;
    }

    /**
     * Adds user to the event only if there's space.
     * @param  userID  String   The User's ID.
     * @param  eventID String   The Event's ID.
     * @return         boolean  True if the user gets added.
     */
    @Override
    public boolean addUserToEvent(String userID, String eventID){
        Event event = searchEvent(eventID);
        if (event!=null){
            event.getAttendeeIDs().add(userID);
            return true;
        }
        return false;
    }

    /**
     * Removes given user from given event if they are signed up for it
     * @param  userID  String  The User's ID.
     * @param  eventID String  The Event's ID.
     * @return         boolean True if the user gets removed
     */
    @Override
    public boolean removeUserFromEvent(String userID, String eventID) {
        Event event =searchEvent(eventID);
        if (event!=null){
            event.getAttendeeIDs().remove(userID);
            return true;
        }
        return false;
    }


    /**
     * Return the List of users that are in the given event
     * @param eventID  String          The id of the given event
     * @return         List<String>    A list of user ids in the given event id
     */
    @Override
    public List<String> getUsersForEvent(String eventID) {
        List<String> users = new ArrayList<>();
        Event event = searchEvent(eventID);
        if (event!=null){
            users = event.getAttendeeIDs();
            if (users.size() == 1 && users.contains("")){
                users.clear();
                return users;
            }
        }
        return users;
    }

    /**
     * Return the List of users that are in the given event
     * @param userID  String                The id of the given user
     * @return        List<String>          A list of event ids that the attendee id has signed up for
     */
    @Override
    public List<String> getEventsForAttendee(String userID) {
        List<String> events = new ArrayList<>();
        for (Event event: event_list) {
            if (event.getAttendeeIDs().contains(userID)) {
                events.add(event.getEventID());
            }
        }
        return events;
    }


    /**
     * Return a list of events a speaker is part of
     * @param speakerID  String              Contains the speaker's ID
     * @return           List<String>        Contains all eventIDs which speakerID is in
     */
    @Override
    public List<String> getEventsBySpeaker(String speakerID) {
        List<String> events_by_speaker = new ArrayList<>();
        for (Event event: event_list) {
            if (event.getSpeakerIDs().contains(speakerID)) {
                events_by_speaker.add(event.getEventID());
            }
        }
        return events_by_speaker;
    }

    /**
     * Return a list of events the user is a part of
     * @param userType  UserType        Contains the user type
     * @param userID    String          Contains the user ID
     * @return                          A list of events the user is a part of
     */
    public List<String> getEventsByUserType(UserType userType, String userID){
        List<String> events = new ArrayList<>();
        if ((UserType.ATTENDEE).equals(userType)) {
            for (Event event : event_list) {
                if (event.getAttendeeIDs().contains(userID)) {
                    events.add(event.getEventID());
                }
            }
            return events;
        }
        else if ((UserType.SPEAKER).equals(userType)){
            for (Event event: event_list) {
                if (event.getSpeakerIDs().contains(userID)) {
                    events.add(event.getEventID());
                }
            }
            return events;
        }
        else{
            return events;
        }
    }


    /**
     * Returns true if the user has already signed up before for the given event
     * @param eventId String    The eventId of the event.
     * @param userId  String    The userId of the user.
     */
    @Override
    public boolean isSignUpBefore(String eventId,String userId){
        boolean result=false;
        List<String> userIdList=new ArrayList<>();
        Event event = searchEvent(eventId);
        if (event!=null){
            userIdList = event.getAttendeeIDs();
        }
        if (userIdList.contains(userId)) {
            result = true;
        }
        return result;
    }

    /**
     * Method to check if the event is full
     * @param eventID  String  Contains the event ID
     * @return         boolean True if the event is full, false otherwise or if the eventID is not valid
     */
    @Override
    public boolean isEventFull(String eventID){
        Event event = searchEvent(eventID);
        if(event!=null){
            int numAttendees = event.getAttendeeIDs().size();
            int numSpeakers = event.getSpeakerIDs().size();
            return numAttendees + numSpeakers >= event.getEventCapacity();
        }
        return false;
    }

    /**
     * Checks if a room is being booked more than once in a specific time period
     * @param room_id  String         Contains the room id
     * @param start    LocalDateTime  Contains the start time
     * @param end      LocalDateTime  Contains the end time
     * @return         Boolean        True if the room is being double booked
     */
    public boolean isDoubleBookingRoom(String room_id, LocalDateTime start, LocalDateTime end){
        LocalDateTime[] eventToVerifyTimes = {start, end};
        for (Event event: event_list){
            LocalDateTime[] eventTimes = {event.getStartTime(),event.getEndTime()};
            if (event.getRoomID().equals(room_id) && isTimeConflict(eventTimes,eventToVerifyTimes)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a speaker is being booked in two separate events at the same time
     * @param speakers   List<String>  Contains a list of speakers
     * @param start      LocalDateTime      Contains the start time
     * @param end        LocalDateTime      Contains the end time
     * @return           Boolean            True if the speaker is being double booked
     */
    public boolean isDoubleBookingSpeaker(List<String> speakers, LocalDateTime start, LocalDateTime end){
        LocalDateTime[] eventToVerifyTimes = {start, end};
        for (Event event: event_list){
            LocalDateTime[] eventTimes = {event.getStartTime(),event.getEndTime()};
            for (String speaker: speakers){
                if (event.getSpeakerIDs().contains(speaker) && isTimeConflict(eventTimes,eventToVerifyTimes)){
                        return true; // you are trying to double book a speaker in the speakers list
                }
            }
        }
        return false; // you can book these speakers for your event
    }

    /**
     * Checks if the event exists
     * @param eventID  String   Contains the event ID
     * @return         boolean  True if the event exists
     */
    public boolean isEventExist(String eventID){
        Event event = searchEvent(eventID);
        return event!=null;
    }

    /**
     * Checks if the start time is after the end time
     * @param  t1      LocalDateTime  The start time
     * @param  t2      LocalDateTime  The end time
     * @return         boolean          True if the time is valid
     */
    public boolean areValidTimes(LocalDateTime t1, LocalDateTime t2){
        return t1.isBefore(t2);
    }

    /**
     * Changes the event capacity only if the event exists.
     * @param newCapacity  int      Contains the new event capacity
     * @param eventID      String   Contains the event ID of the event that's capacity must be changed
     * @return             boolean  True if the event capacity has been changed successfully
     */
    @Override
    public boolean changeEventCapacity(String eventID, int newCapacity){
        Event event = searchEvent(eventID);
        if (event != null){
            event.setEventCapacity(newCapacity);
            return true;
        }
        return false;
    }


    /**
     * Return the total number of attendees and speakers of a given event.
     * @param eventID String The Id of the event.
     */
    @Override
    public int getNumAttendance(String eventID){
        Event event = searchEvent(eventID);
        int numOfAttendee = event.getAttendeeIDs().size();
        int numOfSpeakers = event.getSpeakerIDs().size();
        return numOfAttendee+numOfSpeakers;
    }

    /**
     * Method to check if a speaker is being added twice to the same event
     * @param speakerList  List<String>  Contains the speaker IDs for the event
     * @return             boolean       True if the speakers are not distinct
     */
    @Override
    public boolean isSpeakerDuplicate(List<String> speakerList){
        List<String> distinctSpk = new ArrayList<>();
        for (String speaker: speakerList){
            if (!distinctSpk.contains(speaker)){
                distinctSpk.add(speaker);
            }
        }
        return !(distinctSpk.equals(speakerList));
    }

    /**
     * Checks if there is a time conflict between 2 LocalDateTime variables
     * @param  t1      LocalDateTime[]  An array of LocalDateTime variables
     * @param  t2      LocalDateTime[]  An array of LocalDateTime variables
     * @return         boolean          True if there is a time conflict
     */
    private boolean isTimeConflict(LocalDateTime[] t1, LocalDateTime[] t2){
        boolean result;
        // the starting time of the first event is before starting time of the second event
        if(t1[0].isBefore(t2[0])){
            result=t2[0].isBefore(t1[1]);
        }
        // the starting time of the first event is after starting time of the second event
        else{
            result=t1[0].isBefore(t2[1]);
        }
        return result;
    }

    /**
     * Return the event by its ID. If event DNE, then return null.
     * @param eventID   String    The ID of the event.
     */
    private Event searchEvent(String eventID){
        for (Event event:event_list){
            if (event.getEventID().equals(eventID)){
                return event;
            }
        }
        return null;
    }

}