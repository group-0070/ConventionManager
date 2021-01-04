package event_system_test;

import event_system.EventService;
import event_system.EventServiceEngine;
import login_system.UserService;
import login_system.UserServiceEngine;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    private final UserService userService = new UserServiceEngine();

    @Before
    public void Setup() {
        userService.addUser("aHelen", "passHelen");
        userService.addUser("aLeo", "passLeo");
    }

    @Test
    public void addEvent() {
        ArrayList<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        ArrayList<String> a0 = new ArrayList<>();
        a0.add("Lily");
        a0.add("Helen");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("");
        ArrayList<String> a1 = new ArrayList<>();
        ArrayList<String> s2 = new ArrayList<>();
        s2.add("Leo");
        ArrayList<String> a2 = new ArrayList<>();
        a2.add("Jan");
        a2.add("Seyon");
        es.addEvent("CSC207", t1, t2, "Somewhere in Bahen", s0, a0);
        es.addEvent("", t4, t5, "", s1, a1);
        es.addEvent("CSCB07", t5, t6, "Somewhere in IC", s2, a2);
        String ex0 = "CSC207,2001-03-30 10:10,2001-03-30 11:10,Somewhere in Bahen,Nancy,Lily|Helen";
        String ex1 = ",2001-03-30 12:10,2001-03-30 13:00,,|,ε";
        String ex2 = "CSCB07,2001-03-30 13:00,2001-03-30 13:30,Somewhere in IC,Leo,Jan|Seyon";
        assertEquals(es.getStringEvents().get(0), ex0);
        assertEquals(es.getStringEvents().get(1), ex1);
        assertEquals(es.getStringEvents().get(2), ex2);
    }

    @Test
    public void doubleBookSpeaker() {
        ArrayList<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        s0.add("Lily");
        s0.add("");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Leo");
        s1.add("Nancy");
        ArrayList<String> s2 = new ArrayList<>();
        s2.add("Leo");
        s2.add("Helen");
        s2.add("");
        assertTrue(es.addEvent("Meeting Room", t1, t3, "Zoom", s0, new ArrayList<>()));
        assertFalse(es.addEvent("The 2nd Meeting Room", t2, t4, "", s1, new ArrayList<>()));
        assertFalse(es.addEvent("The 3rd Meeting Room", t1, t3, "", s2, new ArrayList<>()));
        assertEquals(es.getStringEvents().size(), 1);
    }

    @Test
    public void doubleBookRooms() {
        ArrayList<String> s0 = new ArrayList<>();
        s0.add("Lucia");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Leo");
        assertTrue(es.addEvent("EventA", t1, t3, "IC404", s0, new ArrayList<>()));
        assertFalse(es.addEvent("EventB", t2, t4, "IC404", s1, new ArrayList<>()));
        assertEquals(es.getStringEvents().size(), 1);
    }

    @Test
    public void addEventTimeConflict() {
        ArrayList<String> speakers = new ArrayList<>();
        speakers.add("sBob");
        speakers.add("sJoe");
        es.addEvent("MAT666",t1,t2,"HL205",speakers, new ArrayList<>());
        //trying to book different event at the same time and room as an existing event
        assertFalse(es.addEvent("MATH 136",t1,t2,"HL205",speakers, new ArrayList<>()));
        //book an event with same details as above, but in a different time block
        assertTrue(es.addEvent("MATH 136",t3,t4,"HL205",speakers, new ArrayList<>()));
    }

    @Test
    public void addUserToEvent() {
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent("CSC207",t1,t2,"HL205",s1,new ArrayList<>());
        assertTrue(es.addUserToEvent("aHelen","CSC207"));
        assertFalse(es.addUserToEvent("aJan","CSCB07"));
    }

    @Test
    public void addUserToFullEvent() {
        ArrayList<String> speakers = new ArrayList<>();
        speakers.add("sSeyon");
        ArrayList<String> attendees = new ArrayList<>();
        attendees.add("aJan");
        attendees.add("aHassan");
        es.addEvent("MATH 136",t5,t6,"HL205",speakers, attendees);
        //Try to add a user to an event with > with > 2 attendees (exceeds maximum capacity)
        assertFalse(es.addUserToEvent("aNancy", "MATH136"));
    }

    @Test
    public void addUserToInvalidEvent() {
        es.addEvent("MATB24", t5, t6, "HL205", "sJan");
        assertFalse(es.addUserToEvent("aLeo", "MATA22"));
        assertEquals(es.getEventsForAttendee("aLeo"), new ArrayList<String>());
    }

    @Test
    public void removeUserFromEvent() {
        ArrayList<String> attendeeList=new ArrayList<>();
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        attendeeList.add("aHelen");
        es.addEvent("MAT224",t2,t3,"BA1007",s1,attendeeList);
        assertTrue(es.removeUserFromEvent("aHelen","MAT224"));
        assertFalse(es.removeUserFromEvent("aJan","MAT224"));
        assertFalse(es.removeUserFromEvent("aSeyon","MAT137"));
    }

    @Test
    public void getStringEvents() {
        assertEquals(es.getStringEvents().size(),0);
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent("CSC207",t1,t2,"HL205",s1,new ArrayList<>());
        es.addEvent("CSC236",t2,t3,"SS207",s1,new ArrayList<>());
        assertEquals(es.getStringEvents().size(),2);
        ArrayList<String> ListOfEvents= new ArrayList<>();
        ListOfEvents.add("CSC207,2001-03-30 10:10,2001-03-30 11:10,HL205,sNancy,ε");
        ListOfEvents.add("CSC236,2001-03-30 11:10,2001-03-30 12:00,SS207,sNancy,ε");
        assertEquals(es.getStringEvents(),ListOfEvents);
    }

    @Test
    public void getListOfAttendeesId() {
        ArrayList<String> a1=new ArrayList<>();
        a1.add("aHelen");
        ArrayList<String> a2=new ArrayList<>();
        a2.add("aJan");
        a2.add("aLeo");
        ArrayList<String> a3=new ArrayList<>();
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent("CSC207",t1,t2,"HL205",s1,a1);
        es.addEvent("CSC236",t2,t3,"SS207",s1,a2);
        es.addEvent("CSC263",t4,t5,"SS206",s1,a3);

        ArrayList<String> allAttendees=new ArrayList<>();
        allAttendees.add("aHelen");
        allAttendees.add("aJan");
        allAttendees.add("aLeo");
        assertEquals(es.getListOfAttendeesId(),allAttendees);
    }

    @Test
    public void getListOfSpeakersId() {
        ArrayList<String> a1=new ArrayList<>();
        a1.add("sHelen");
        ArrayList<String> a2=new ArrayList<>();
        a2.add("sJan");
        ArrayList<String> a3=new ArrayList<>();
        a3.add("sJan");
        es.addEvent("CSC207",t1,t2,"HL205",a1,new ArrayList<>());
        es.addEvent("CSC236",t2,t3,"SS207",a2,new ArrayList<>());
        es.addEvent("CSC263",t4,t5,"SS206",a3,new ArrayList<>());

        ArrayList<String> expected= new ArrayList<>();
        expected.add("sHelen");
        expected.add("sJan");

        ArrayList<String> allSpeakers=new ArrayList<>(expected);

        assertEquals(es.getListOfSpeakersId(),allSpeakers);
    }

    @Test
    public void getUsersForEvent() {
        ArrayList<String> a1=new ArrayList<>();
        a1.add("aSeyon");
        a1.add("aJan");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        es.addEvent("CSC207",t1,t2,"HL205",s1,a1);
        es.addEvent("CSC263",t4,t5,"SS206",s1,new ArrayList<>());
        assertEquals(es.getUsersForEvent("CSC207"),a1);
        assertEquals(es.getUsersForEvent("CSC263"),new ArrayList<>());
    }

    @Test
    public void getEventsForUser() {
        ArrayList<String> a1=new ArrayList<>();
        a1.add("aSeyon");
        a1.add("aJan");
        ArrayList<String> a2=new ArrayList<>();
        a2.add("aSeyon");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("sNancy");
        ArrayList<String> expected=new ArrayList<>();
        expected.add("CSC207");
        expected.add("CSC263");
        es.addEvent("CSC207",t1,t2,"HL205",s1,a1);
        es.addEvent("CSC263",t4,t5,"SS206",s1,a2);
        assertEquals(es.getEventsForAttendee("aSeyon"),expected);
        assertEquals(es.getEventsForAttendee("aLily"),new ArrayList<>());
    }

    @Test
    public void getEventsBySpeaker() {
        ArrayList<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        ArrayList<String> expected=new ArrayList<>();
        expected.add("CSC207");
        expected.add("CSC258");
        es.addEvent("CSC207",t1,t2,"HL205",s1,new ArrayList<>());
        es.addEvent("CSC258",t2,t3,"BA2007",s1,new ArrayList<>());
        assertEquals(es.getEventsBySpeaker("sSeyon"),expected);
        assertEquals(es.getEventsBySpeaker("aNancy"),new ArrayList<>());
    }

    @Test
    public void testGetDisplayStringEvents(){
        ArrayList<String> s1=new ArrayList<>();
        s1.add("sSeyon");
        es.addEvent("CSC207",t1,t2,"HL205",s1,new ArrayList<>());
        ArrayList<String> actual = es.getDisplayStringEvents();
        ArrayList<String> expected =new ArrayList<>();
        String event = "CSC207,2001-03-30 10:10,2001-03-30 11:10,HL205,sSeyon,N/A";
        expected.add(event);
        assertEquals(expected, actual);
    }
}
