package event_system;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Use case class for storing and adding events.
 */
public class EventServiceEngine implements EventService {

    private final ArrayList<Event> event_list = new ArrayList<>();
    private final int attendee_capacity = 2;
    /**
     * Adds am event to event_service
     * @param eventID        String              Contains the event ID
     * @param startTime      LocalDateTime       Contains the start time of the event
     * @param endTime        LocalDateTime       Contains the end time of the event
     * @param roomID         String              Contains the room ID
     * @param speakersID     ArrayList<String>   Contains a list of speaker IDs
     * @return               boolean             True if the event has been successfully added, false otherwise
     */
    @Override
    public boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                            ArrayList<String> speakersID){
        Event eventToAdd = new Event(eventID, startTime, endTime, roomID, speakersID);
        if (verifyEvent(eventToAdd)){
            event_list.add(eventToAdd);
            return true;
        }
        return false;
    }

    /**
     * Adds am event to event_service, with overloaded parameter attendeeIDs
     * @param eventID        String              Contains the event ID
     * @param startTime      LocalDateTime       Contains the start time of the event
     * @param endTime        LocalDateTime       Contains the end time of the event
     * @param roomID         String              Contains the room ID
     * @param speakersID     ArrayList<String>   Contains a list of speaker IDs
     * @param attendeeIDs    ArrayList<String>   Contains a list of attendee IDs
     * @return               boolean             True if the event has been successfully added, false otherwise
     */
    @Override
    public boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                            ArrayList<String> speakersID, ArrayList<String> attendeeIDs){
        Event eventToAdd = new Event(eventID, startTime, endTime, roomID, speakersID, attendeeIDs);
        if (verifyEvent(eventToAdd)){
            event_list.add(eventToAdd);
            return true;
        }
        return false;
    }

    // soon-to-be deprecated method
    @Override
    public boolean addEvent(String eventID, LocalDateTime startTime, LocalDateTime endTime, String roomID,
                            String speakerID){
        // temp. fix for now
        ArrayList<String> speaker_ids = new ArrayList<>();
        speaker_ids.add(speakerID);
        Event eventToAdd = new Event(eventID, startTime, endTime, roomID, speaker_ids);
        if (verifyEvent(eventToAdd)){
            event_list.add(eventToAdd);
            return true;
        }
        return false;
    }

    /**
     * Adds user to the event only if there's space.
     * @param  userID  String   The User's ID.
     * @param  eventID String   The Event's ID.
     * @return         boolean  True if the user gets added.
     */
    @Override
    public boolean addUserToEvent(String userID, String eventID){
        for (Event event: event_list){
            if (eventID.equals(event.getEventID()) && signUpAuthorization(event, userID)){
                event.getAttendeeIDs().add(userID);
                return true;
            }
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
        for (Event event: event_list) {
            if (event.getEventID().equals(eventID) && event.getAttendeeIDs().contains(userID)) {
                event.getAttendeeIDs().remove(userID);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a String version of every event.
     * @return  ArrayList<String>   Contains string version of each event.
     */
    @Override
    public ArrayList<String> getStringEvents() {
        ArrayList<String> res = new ArrayList<>();
        for (Event event: event_list){
            res.add(event.toString());
        }
        return res;
    }

    /**
     * Returns a String version of every event with changes to display.
     * @return ArrayList<String> List of String version of events to display.
     */
    @Override
    public ArrayList<String> getDisplayStringEvents() {
        ArrayList<String> res = new ArrayList<>();
        for (Event event: event_list){
            res.add(event.toString().replaceAll("ε", "N/A"));
        }
        return res;
    }

    /**
     * Return a list of all attendees for all events
     * @return   ArrayList<String>  A list of attendee IDs
     */
    @Override
    public ArrayList<String> getListOfAttendeesId() {
        ArrayList<String> allAttendees = new ArrayList<>();
        for (Event event: event_list){
            for (String attendee: event.getAttendeeIDs()){
                if (!allAttendees.contains(attendee)){ // prevents attendees that have signed up for multiple events
                    allAttendees.add(attendee);        // from being added into allAttendees list > 1 time
                }
            }
        }
        return allAttendees;
    }

    /***
     * Return a list of all speaker IDs the the event_list
     * @return   ArrayList<String>  The list of all speakers in event_service
     */
    @Override
    // Same as above but for getSpeakerIDs method.
    public ArrayList<String> getListOfSpeakersId() {
        ArrayList<String> allSpeakers = new ArrayList<>();
        ArrayList<String> eventSpeakerList;
        for (Event event: event_list){
            eventSpeakerList = event.getSpeakerIDs();
            for (String id: eventSpeakerList){
                if (!allSpeakers.contains(id)){ // prevent duplicates from being added to allSpeakers
                    allSpeakers.add(id);
                }
            }
        }
        return allSpeakers;
    }

    /**
     * Return the ArrayList of users that are in the given event
     * @param eventID  String               The id of the given event
     * @return         ArrayList<String>    A list of user ids in the given event id
     */
    @Override
    public ArrayList<String> getUsersForEvent(String eventID) {
        ArrayList<String> users = new ArrayList<>();
        for (Event event: event_list) {
            if (event.getEventID() .equals(eventID)) {
                users = event.getAttendeeIDs();
            }
        }
        return users;
    }

    /**
     * Return the ArrayList of users that are in the given event
     * @param userID  String               The id of the given user
     * @return        ArrayList<String>    A list of event ids that the attendee id has signed up for
     */
    @Override
    public ArrayList<String> getEventsForAttendee(String userID) {
        ArrayList<String> events = new ArrayList<>();
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
     * @return           ArrayList<String>   Contains all eventIDs which speakerID is in
     */
    @Override
    public ArrayList<String> getEventsBySpeaker(String speakerID) {
        ArrayList<String> events_by_speaker = new ArrayList<>();
        for (Event event: event_list) {
            if (event.getSpeakerIDs().contains(speakerID)) {
                events_by_speaker.add(event.getEventID());
            }
        }
        return events_by_speaker;
    }

    /**
     * Checks if the event to be added does not conflict with currently existing events
     * @param  eventToVerify  Event     The event that
     * @return                Boolean   True if the event does not have any conflicts
     */
    private boolean verifyEvent(Event eventToVerify){
        // check if start is before endtime, there is at least 1 speaker and there are at most attendee_capacity
        if (eventToVerify.getStartTime().isBefore(eventToVerify.getEndTime()) &&
                eventToVerify.getSpeakerIDs().size() >= 1 &&
                eventToVerify.getAttendeeIDs().size() <= attendee_capacity) {
            LocalDateTime[] eventToVerifyTimes = {eventToVerify.getStartTime(), eventToVerify.getEndTime()};
            for (String speakerID : eventToVerify.getSpeakerIDs()) {
                for (Event event : event_list) {
                    LocalDateTime[] eventTimes = {event.getStartTime(), event.getEndTime()};
                    if (((eventToVerify.getRoomID().equals(event.getRoomID()) ||
                            event.getSpeakerIDs().contains(speakerID)) &&
                            isTimeConflict(eventTimes, eventToVerifyTimes)) ||
                            event.getEventID().equals(eventToVerify.getEventID())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if there is a time conflict between 2 LocalDateTime variables
     * @param  t1      LocalDateTime[]  An array of LocalDateTime variables
     * @param  t2      LocalDateTime[]  An array of LocalDateTime variables
     * @return         boolean          True if there is a time conflict
     */
    private boolean isTimeConflict(LocalDateTime[] t1, LocalDateTime[] t2){
        return !((t1[1].equals(t2[0]) || t1[1].isBefore(t2[0])) || (t2[1].equals(t1[0]) || t2[1].isBefore(t1[0])));
    }

    /**
     * Returns true if a user is permitted to sign up for an event
     * @param  eventToSignUp   Event    The event the user wishes to
     * @param  userId          String   The user ID
     * @return                 boolean  True if the user can sign up for the event
     */
    private boolean signUpAuthorization(Event eventToSignUp,String userId){
        boolean result=true;
        ArrayList<String> userIdList=new ArrayList<>();
        for (Event event: event_list){
            if (event == eventToSignUp){
                userIdList = event.getAttendeeIDs();
            }
        }
        if (userIdList.contains(userId)) {
            result = false;
        }
        return result && eventToSignUp.getAttendeeIDs().size() <= attendee_capacity;

    }


}
