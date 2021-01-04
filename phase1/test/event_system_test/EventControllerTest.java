package event_system_test;

import event_system.EventController;
import event_system.IEventController;
import event_system.IEventDataProvider;
import login_system.UserService;
import login_system.UserServiceEngine;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class EventControllerTest {
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime t1 = LocalDateTime.parse("2001-03-30 10:10", format);
    private final LocalDateTime t2 = LocalDateTime.parse("2001-03-30 11:10", format);
    private final LocalDateTime t3 = LocalDateTime.parse("2001-03-30 12:00", format);
    private final LocalDateTime t4 = LocalDateTime.parse("2001-03-30 12:10", format);
    private final LocalDateTime t5 = LocalDateTime.parse("2001-03-30 13:00", format);
    private final LocalDateTime t6 = LocalDateTime.parse("2001-03-30 13:30", format);
    private final LocalDateTime t7 = LocalDateTime.parse("2001-03-30 14:00", format);

    private final String eventListFileName="./assets/events.csv";
    private IEventDataProvider testProvider;
//    private EventService testEventService;
    private UserService userService = new UserServiceEngine();
    private IEventController testController;

    @Before
    public void Setup() {
//        File eventListFile = new File(this.eventListFileName);
//        if (eventListFile.exists()) {
//            eventListFile.delete();
//        }

//        this.testProvider=new EventDataProvider(this.testEventService, eventListFileName);
        this.testController=new EventController(eventListFileName, userService);
        userService.addUser("sNancy","passNancy");
        userService.addUser("sLily","passLily");
        userService.addUser("aHelen","passHelen");
        userService.addUser("aLeo", "passLeo");
        userService.addUser("aJan", "passJan");
        userService.addUser("sJan","passJan");
    }

    @Test
    public void addEventMultipleSpeakers() {
        // Multiple speakers and one attendee
        ArrayList<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");
        speakerIds1.add("sLily");


        // Speaker not in the list
        ArrayList<String> speakerIds2=new ArrayList<>();
        speakerIds2.add("sJan");
        speakerIds2.add("sSeyon");


        assertTrue(this.testController.addEvent("CSC207",t1,t2,"BA1007",speakerIds1));
        assertFalse(this.testController.addEvent("CSCB07",t4,t5,"HL205",speakerIds2));
    }

    @Test
    public void addEventSingleSpeaker() {
        assertTrue(this.testController.addEvent("MAT224",t2,t3,"Somewhere in HW","sNancy"));
        assertFalse(this.testController.addEvent("STA302",t6,t7,"Online","sLeo"));
        // boolean result=testController.addEvent("STA302",t3,t4,"Online","Jan");
    }


    @Test
    public void signUpToEmptyEvent() {
        String id1=userService.getListOfAttendeeId().get(0);
        this.testController.addEvent("MAT137",t5,t6,"PG101","sNancy");
        assertTrue(this.testController.signUp(id1,"MAT137"));
//        this.testController.signUp(id1,"MAT137");
    }


    @Test
    public void signUpToFullEvent() {
        assertTrue(this.testController.addEvent("MAT137", t5, t6, "PG101", "sNancy"));
        assertTrue(this.testController.signUp("aJan", "MAT137"));
        assertTrue(this.testController.signUp("aHelen", "MAT137"));

        assertTrue(this.testController.signUp("aLeo", "MAT137"));
    }

    @Test
    public void signUpToInvalidEvent() {
        assertFalse(this.testController.signUp("aLeo", "Invalid"));
//        assertTrue(this.testController.getEventService().getStringEvents().equals(new ArrayList<Event>()));
    }

    @Test
    public void signUpWithInvalidUser() {
        this.testController.addEvent("MAT137", t5, t6, "PG101", "sNancy");
        ArrayList<String> att = testController.getEventService().getUsersForEvent("MAT137");
        assertFalse(this.testController.signUp("Invalid", "MAT137"));
        assertEquals(att,new ArrayList<>());
    }

    @Test
    public void signUpTwice() {
        this.testController.addEvent("MAT137", t5, t6, "PG101", "sNancy");
        ArrayList<String> att = testController.getEventService().getUsersForEvent("MAT137");
        assertEquals(att, new ArrayList<String>());
        assertTrue(this.testController.signUp("aLeo", "MAT137"));
//        ArrayList<String> updated_att = testController.getEventService().getUsersForEvent("MAT137");
        ArrayList<String> att_list = new ArrayList<>();
        att_list.add("aLeo");
        assertEquals(att, att_list);
        assertFalse(this.testController.signUp("aLeo", "MAT137"));
    }

    @Test
    public void cancelSignupExistingEvent() {
        String id1=userService.getListOfAttendeeId().get(0);
        this.testController.addEvent("MAT137",t5,t6,"PG101","sNancy");
        assertTrue(this.testController.signUp(id1,"MAT137"));
        assertTrue(this.testController.cancelSignUp(id1, "MAT137"));
    }

    @Test
    public void cancelSignupMissingEvent() {
        // cancel signup for an event that does not exist
        String user = userService.getListOfAttendeeId().get(0);
        assertFalse(this.testController.cancelSignUp(user, "BOB165"));

        // cancel signup for an event that does exist, but user is not signed up for // moved to below test
        // assertFalse(this.testController.cancelSignUp(user, "CSC236"));
    }

    @Test
    public void cancelSignupToEventNotSignedUp() {
        // try to cancel signup for an event the user did not sign up to
        this.testController.addEvent("MAT137",t5,t6,"PG101","sHelen");
        assertFalse(this.testController.cancelSignUp("aLeo", "MAT137"));
    }

    @Test
    public void cancelSignupWithInvalidUser () {
        this.testController.addEvent("MAT137",t5,t6,"PG101","sLeo");
        assertFalse(this.testController.cancelSignUp("Invalid User", "MAT137"));
        assertEquals(new ArrayList<>(),
                     this.testController.getEventService().getUsersForEvent("MAT137"));
    }

    @Test
    public void testGetEventsForUser(){
        this.testController.addEvent("MAT137", t5, t6, "PG101", "sNancy");
        this.testController.signUp("aLeo", "MAT137");
        ArrayList<String> att_event_signed_up = testController.getEventsForUser("aLeo");
        ArrayList<String> event_list_expected = new ArrayList<>();
        event_list_expected.add("MAT137");
        assertEquals(event_list_expected, att_event_signed_up);
        ArrayList<String> spk_event_signed_up = testController.getEventsForUser("sNancy");
        assertEquals(event_list_expected, spk_event_signed_up);
    }

//    @Test
//    public void testSaveEvents(){
//        // check if saveEvents method performs its function
//        this.testController.saveEvents();
//        try{
//            BufferedReader br = new BufferedReader(new FileReader(eventListFileName));
//            String line = null;
//            ArrayList<String> events = this.testController.getStringEvents();
//            int i=0;
//            while ((line = br.readLine()) != null || i < events.size() - 1)
//            {
//                assertEquals(line, events.get(i));
//                i++;
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
