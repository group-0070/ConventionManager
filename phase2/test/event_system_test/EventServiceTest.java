package event_system_test;

import event_system.EventService;
import event_system.EventServiceEngine;
import event_system.EventType;
import org.junit.Before;
import org.junit.Test;
import user_system.UserService;
import user_system.UserServiceEngine;
import user_system.UserType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static org.junit.Assert.*;

public class EventServiceTest {

    private final EventService es = new EventServiceEngine();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime t1 = LocalDateTime.parse("2001-03-30 10:10", format);
    private final LocalDateTime t2 = LocalDateTime.parse("2001-03-30 11:10", format);
    private final LocalDateTime t3 = LocalDateTime.parse("2001-03-30 12:00", format);
    private final LocalDateTime t4 = LocalDateTime.parse("2001-03-30 12:10", format);
    private final LocalDateTime t5 = LocalDateTime.parse("2001-03-30 13:00", format);
    private final LocalDateTime t6 = LocalDateTime.parse("2001-03-30 13:30", format);
    private final LocalDateTime t7 = LocalDateTime.parse("2001-03-30 10:30", format);
    private final LocalDateTime t8 = LocalDateTime.parse("2001-03-30 12:30", format);
    private final UserService userService = new UserServiceEngine();

    @Before
    public void Setup() {
        userService.addUser("aHelen", "passHelen", UserType.ATTENDEE);
        userService.addUser("aLeo", "passLeo", UserType.ATTENDEE);
    }

    private String toString(List<String> listIDs){
        StringBuilder stringIds= new StringBuilder();
        if(listIDs.size()>0){
            for (String ID:listIDs){
                stringIds.append(ID+'|');
            }
        }
        //removing the last '|' if the StringBuilder is not empty
        if (stringIds.length()>0){
            stringIds.setLength(stringIds.length() - 1);
        }
        return stringIds.toString();
    }

    /**
     * Convert the nested List into a HashMap.
     * @param nestedList List<List<String>> A nested 2-D List containing all info of one event.
     */
    private HashMap<String,String> listToMap(List<List<String>> nestedList){
        HashMap<String,String> map=new HashMap<>();
        map.put("eventType",nestedList.get(0).get(0));
        map.put("eventCapacity",nestedList.get(1).get(0));
        map.put("eventID",nestedList.get(2).get(0));
        map.put("startTime",nestedList.get(3).get(0));
        map.put("endTime",nestedList.get(4).get(0));
        map.put("roomID",nestedList.get(5).get(0));
        map.put("speakerIDs",toString(nestedList.get(6)));
        map.put("attendeeIDs",toString(nestedList.get(7)));
        return map;
    }

    /**
     * Takes the input parameter and make it a HashMap instead.
     */
    private HashMap<String,String> toMap(EventType eventType,int eventCapacity,String eventID,
                                         LocalDateTime startTime,LocalDateTime endTime,String roomID,
                                         List<String> speakerIDs, List<String> attendeeIDs){
        HashMap<String,String> map = new HashMap<>();
        map.put("eventType",eventType.toString());
        map.put("eventCapacity",Integer.toString(eventCapacity));
        map.put("eventID",eventID);
        map.put("startTime", startTime.format(format));
        map.put("endTime",endTime.format(format));
        map.put("roomID",roomID);
        if(attendeeIDs.size()!=0){
            map.put("attendeeIDs",toString(attendeeIDs));
        } else{
            map.put("attendeeIDs","");
        }
        if(speakerIDs.size()!=0){
            map.put("speakerIDs",toString(speakerIDs));
        } else{
            map.put("speakerIDs","");
        }
        return map;
    }

    @Test
    public void addMultipleEvent() {
        List<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        List<String> a0 = new ArrayList<>();
        a0.add("Lily");
        a0.add("Helen");
        List<String> s1 = new ArrayList<>();
        s1.add("");
        List<String> a1 = new ArrayList<>();
        List<String> s2 = new ArrayList<>();
        s2.add("Leo");
        List<String> a2 = new ArrayList<>();
        a2.add("Jan");
        a2.add("Seyon");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2, "CSC207", t1, t2, "Somewhere in Bahen",
                s0, a0);
        es.addEvent(EventType.NO_SPEAKER_EVENT, 2, "", t4, t5, "", s1, a1);
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "CSCB07", t5, t6, "Somewhere in IC",
                s2, a2);

        HashMap<String,String> actual0 = listToMap(es.getListEvents().get(0));
        HashMap<String,String> expected0 = toMap(EventType.SINGLE_SPEAKER_EVENT, 2, "CSC207", t1, t2, "Somewhere in Bahen",
                s0, a0);
        assertEquals(expected0,actual0);
        HashMap<String,String> actual1 = listToMap(es.getListEvents().get(1));
        HashMap<String,String> expected1 = toMap(EventType.NO_SPEAKER_EVENT, 2, "", t4, t5, "", s1, a1);
        assertEquals(expected1,actual1);
        HashMap<String,String> actual2 = listToMap(es.getListEvents().get(2));
        HashMap<String,String> expected2 = toMap(EventType.SINGLE_SPEAKER_EVENT, 3, "CSCB07", t5, t6, "Somewhere in IC",
                s2, a2);
        assertEquals(expected2,actual2);
    }


    @Test
    public void addUserToEvent() {
        List<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC207",t1,t2,"HL205",s1,
                new ArrayList<>());
        assertTrue(es.addUserToEvent("aHelen","CSC207"));
        assertFalse(es.addUserToEvent("aJan","CSCB07"));
    }

    @Test
    public void addUserToFullEvent() {
        List<String> speakers = new ArrayList<>();
        speakers.add("sSeyon");
        List<String> attendees = new ArrayList<>();
        attendees.add("aJan");
        attendees.add("aHassan");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,  "MATH 136",t5,t6,"HL205",speakers,
                attendees);
        //Try to add a user to an event with > with > 2 attendees (exceeds maximum capacity)
        assertFalse(es.addUserToEvent("aNancy", "MATH136"));
    }

    @Test
    public void addUserToInvalidEvent() {
        List<String> speakerIds=new ArrayList<>();
        speakerIds.add("sJan");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 5, "MATB24", t5, t6, "HL205",
                speakerIds,new ArrayList<>());
        assertFalse(es.addUserToEvent("aLeo", "MATA22"));
        assertEquals(es.getEventsForAttendee("aLeo"), new ArrayList<String>());
    }

    @Test
    public void removeUserFromEvent() {
        List<String> attendeeList=new ArrayList<>();
        List<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        attendeeList.add("aHelen");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 4, "MAT224",t2,t3,"BA1007",s1,
                attendeeList);
        assertTrue(es.removeUserFromEvent("aHelen","MAT224"));
    }


    @Test
    public void getUsersForEvent() {
        List<String> a1=new ArrayList<>();
        a1.add("aSeyon");
        a1.add("aJan");
        List<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2, "CSC207",t1,t2,"HL205",s1,a1);
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC263",t4,t5,"SS206",s1,
                new ArrayList<>());
        assertEquals(es.getUsersForEvent("CSC207"),a1);
        assertEquals(es.getUsersForEvent("CSC263"),new ArrayList<>());
    }

    @Test
    public void getEventsForUser() {
        List<String> a1=new ArrayList<>();
        a1.add("aSeyon");
        a1.add("aJan");
        List<String> a2=new ArrayList<>();
        a2.add("aSeyon");
        List<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        List<String> expected=new ArrayList<>();
        expected.add("CSC207");
        expected.add("CSC263");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC207",t1,t2,"HL205",s1,a1);
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC263",t4,t5,"SS206",s1,a2);
        assertEquals(es.getEventsForAttendee("aSeyon"),expected);
        assertEquals(es.getEventsForAttendee("aLily"),new ArrayList<>());
    }

    @Test
    public void getEventsBySpeaker() {
        List<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        List<String> expected=new ArrayList<>();
        expected.add("CSC207");
        expected.add("CSC258");
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC207",t1,t2,"HL205",s1,
                new ArrayList<>());
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 4,"CSC258",t2,t3,"BA2007",s1,
                new ArrayList<>());
        assertEquals(es.getEventsByUserType(UserType.SPEAKER, "sSeyon"),expected);
        assertEquals(es.getEventsByUserType(UserType.ATTENDEE,"aNancy"),new ArrayList<>());
    }


    @Test
    public void isDoubleBookingRoom(){
        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 4, "CSC207",t1,t2,"HL205",
                new ArrayList<>(), new ArrayList<>());
        assertTrue(es.isDoubleBookingRoom("HL205", t7, t3));
        assertFalse(es.isDoubleBookingRoom("HL205", t5, t6));

        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,  "CSC236",t4,t5,"Somewhere",
                new ArrayList<>(),new ArrayList<>());
        assertTrue(es.isDoubleBookingRoom("Somewhere", t7, t8));
        assertFalse(es.isDoubleBookingRoom("Somewhere", t5, t6));
    }

    @Test
    public void isDoubleBookingSpeaker(){
        List<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        List<String> s2=new ArrayList<>();
        s2.add("sSeyon");
        s2.add("sHelen");
        List<String> s3=new ArrayList<>();
        s3.add("sJan");

        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "CSC207",t1,t2,"HL205",
                s1,new ArrayList<>());
        assertTrue(es.isDoubleBookingSpeaker(s2, t7, t3));
        assertFalse(es.isDoubleBookingSpeaker(s3, t5, t6));

        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 4, "CSC236",t4,t5,"Somewhere",s1,
                new ArrayList<>());
        assertTrue(es.isDoubleBookingSpeaker(s2, t7, t8));
        assertFalse(es.isDoubleBookingSpeaker(s2, t5, t6));

    }

    /**
     * Cancel all events.
     */
    @Test
    public void cancelEventBySize0(){
        List<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        List<String> s2=new ArrayList<>();
        s2.add("sSeyon");
        s2.add("sHelen");
        List<String> s3=new ArrayList<>();
        List<String> a3=new ArrayList<>();
        a3.add("aSeyon");

        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "CSC207",t1,t2,"HL205",
                s1,new ArrayList<>());
        es.addEvent(EventType.MULTI_SPEAKER_EVENT,5,"MAT224",t3,t4,"Bahen",
                s2,new ArrayList<>());
        es.addEvent(EventType.NO_SPEAKER_EVENT,6,"Party",t5,t6,"Party Room",
                s3,a3);
        assertTrue(es.cancelEventsBySize(0, true));

        assertEquals(0,es.getListEvents().size());
    }

    /**
     * Cancel events with the number of attendees greater than the maxAttendees given.
     */
    @Test
    public void cancelEventsBySizeNonzero(){
        List<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        List<String> a1=new ArrayList<>();
        a1.add("aHelen");
        List<String> s2=new ArrayList<>();
        s2.add("sSeyon");
        s2.add("sHelen");
        List<String> a2=new ArrayList<>();
        a2.add("aHelen");
        a2.add("aLeo");
        List<String> s3=new ArrayList<>();
        List<String> a3=new ArrayList<>();


        es.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "CSC207",t1,t2,"HL205",
                s1,a1);
        es.addEvent(EventType.MULTI_SPEAKER_EVENT,5,"MAT224",t3,t4,"Bahen",
                s2,a2);
        es.addEvent(EventType.NO_SPEAKER_EVENT,6,"Party",t5,t6,"Party Room",
                s3,a3);

        //cancels the 2-speakers event
        assertTrue(es.cancelEventsBySize(2, true));
        assertEquals(2,es.getListEvents().size());
        HashMap<String,String> expected1 = toMap(EventType.SINGLE_SPEAKER_EVENT, 3, "CSC207",t1,t2,"HL205",
                s1,a1);
        HashMap<String,String> actual1 = listToMap(es.getListEvents().get(0));
        assertEquals(expected1,actual1);
        HashMap<String,String> expected2 = toMap(EventType.NO_SPEAKER_EVENT,6,"Party",t5,t6,"Party Room",
                s3,a3);
        HashMap<String,String> actual2 = listToMap(es.getListEvents().get(1));
        assertEquals(expected2,actual2);

        // this leaves only the non_speaker event left
        assertTrue(es.cancelEventsBySize(1, true));
        assertEquals(1,es.getListEvents().size());
        HashMap<String,String> expected3 = toMap(EventType.NO_SPEAKER_EVENT,6,"Party",t5,t6,"Party Room",
                s3,a3);
        HashMap<String,String> actual3 = listToMap(es.getListEvents().get(0));
        assertEquals(expected3,actual3);
    }

}
