package event_system_test;

import event_system.*;
import room_system.RoomController;
import room_system.RoomService;
import room_system.RoomServiceEngine;
import user_system.UserService;
import user_system.UserServiceEngine;
import org.junit.Before;
import org.junit.Test;
import user_system.UserType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private final String eventListFileName="jdbc:sqlite:assets/UserDataTest.db";
    private final String roomListFileName = "jdbc:sqlite:assets/UserDataTest.db";
    private EventDatabaseReadWriter testProvider;
    private EventService testEventService;
    private UserService userService = new UserServiceEngine();
    private IEventController testEventController;
    private RoomService testRoomService;
    private RoomController testRoomController;

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


    @Before
    public void Setup() {
        this.testEventService = new EventServiceEngine();
        this.testRoomService = new RoomServiceEngine();
        this.testEventController =new EventController(eventListFileName, userService,
                this.testEventService,testRoomService);
        this.testRoomController = new RoomController(roomListFileName,testRoomService);
        userService.addUser("sNancy","passNancy", UserType.SPEAKER);
        userService.addUser("sLily","passLily", UserType.SPEAKER);
        userService.addUser("aHelen","passHelen", UserType.ATTENDEE);
        userService.addUser("aLeo", "passLeo", UserType.ATTENDEE);
        userService.addUser("aJan", "passJan", UserType.ATTENDEE);
        userService.addUser("sJan","passJan", UserType.ATTENDEE);
        userService.addUser("sHelen","passLily", UserType.SPEAKER);
        testRoomService.addRoom("BA1007", 100);
        testRoomService.addRoom("HL205", 50);
        testRoomService.addRoom("Somewhere in HW", 20);
        testRoomService.addRoom("Online", 200);
        testRoomService.addRoom("PG101", 15);
        testRoomService.addRoom("PG1201", 10);
        testRoomService.addRoom("Somewhere", 20);
    }

    @Test
    public void addEventMultipleSpeakers() {
        // Multiple speakers and one attendee
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");
        speakerIds1.add("sLily");


        // Speaker not in the list
        List<String> speakerIds2=new ArrayList<>();
        speakerIds2.add("sJan");
        speakerIds2.add("sSeyon");



        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 3,"CSC207",t1,t2,
                "BA1007",
                speakerIds1,new ArrayList<>()), EventPrompt.EVENT_ADDED);
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSCB07",t4,t5,
                "HL205", speakerIds2,new ArrayList<>()), EventPrompt.SPEAKER_DNE);
    }

    @Test
    public void addEventSingleSpeaker() {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");

        List<String> speakerIds2=new ArrayList<>();
        speakerIds2.add("sLeo");

        assertEquals(this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 10, "MAT224",t2,t3,
                "Somewhere in HW",speakerIds1,new ArrayList<>()), EventPrompt.EVENT_ADDED);
        assertEquals(this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 10,"STA302",t6,t7,
                "Online", speakerIds2,new ArrayList<>()), EventPrompt.SPEAKER_DNE);
    }

    @Test
    public void addExistingEvent(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        List<String> s2=new ArrayList<>();
        s2.add("sNancy");
        s2.add("sLily");

        assertEquals(EventPrompt.EVENT_ADDED,testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT,
                10,"CSC207",t1,t3,"Somewhere in HW",s1,new ArrayList<>()));
        assertEquals(EventPrompt.EVENT_ALREADY_EXIST,testEventController.addEvent(EventType.NO_SPEAKER_EVENT,
                5,"CSC207",t3,t4,"Online",new ArrayList<>(),new ArrayList<>()));
    }

    @Test
    public void addEventInvalidRoom(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        List<String> s2=new ArrayList<>();
        s2.add("sNancy");
        s2.add("sLily");

        assertEquals(EventPrompt.ROOM_DNE,testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT,
                10,"CSC207",t1,t3,"Invalid Room",s1,new ArrayList<>()));
    }

    /**
     * Test the case where the same speaker is attending two events at the same time
     */
    @Test
    public void addEventSpeakerTimeConflict(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        List<String> s2=new ArrayList<>();
        s2.add("sNancy");
        s2.add("sLily");

        assertEquals(EventPrompt.EVENT_ADDED,testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT,
                10,"CSC207",t1,t3,"Somewhere in HW",s1,new ArrayList<>()));
        assertEquals(EventPrompt.DOUBLE_BOOK_SPEAKER,testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT,
                5,"CSCB07",t2,t3,"Online",s2,new ArrayList<>()));
    }

    /**
     * This test checks if the event type matches the num of speakers
     */
    @Test
    public void addEventTypeMatch(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        List<String> s2=new ArrayList<>();
        s2.add("sLily");
        s2.add("sHelen");

        assertEquals(EventPrompt.EVENT_ADDED,testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT,
                10,"CSC207",t1,t3,"Somewhere in HW",s1,new ArrayList<>()));
        assertEquals(EventPrompt.NUM_SPEAKERS_MISMATCH,
                testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 5,
                        "CSCB07",t2,t3,"Online",s2,new ArrayList<>()));
    }


    @Test
    public void signUpToEmptyEvent() {
        String id1=userService.getListOfIDsByType(UserType.ATTENDEE).get(0);
        List<String> speakers = new ArrayList<>();
        speakers.add("SNancy");
        Enum<EventPrompt> result = this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,
                "MATH137",t5,t6, "PG101", speakers,new ArrayList<>());
        userService.setCurrentUser(id1);
        assertEquals(this.testEventController.signUp("MATH137"), EventPrompt.EVENT_DNE);
    }


    @Test
    public void signUpToFullEvent() {
        List<String> speakers = new ArrayList<>();
        speakers.add("SNancy");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 2,
                "MATH 137", t5, t6,"PG101", speakers,new ArrayList<>()), EventPrompt.SPEAKER_DNE);
        userService.setCurrentUser("aJan");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.EVENT_DNE);
        userService.setCurrentUser("aHelen");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.EVENT_DNE);
        userService.setCurrentUser("aLeo");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.EVENT_DNE);
    }

    @Test
    public void signUpToInvalidEvent() {
        userService.setCurrentUser("aLeo");
        assertEquals(this.testEventController.signUp("Invalid"), EventPrompt.EVENT_DNE);
    }

    @Test
    public void signUpWithInvalidUser() {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");
        this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2, "MAT137", t5, t6,
                "PG101", speakerIds1, new ArrayList<>());
        List<String> att = testEventService.getUsersForEvent("MAT137");
        userService.setCurrentUser("Invalid");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.ATTENDEE_DNE);
        assertEquals(att,new ArrayList<>());
    }

    @Test
    public void signUpTwice() {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");

        this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"MAT137", t5, t6,
                "PG101", speakerIds1, new ArrayList<>());
        List<String> att = this.testEventService.getUsersForEvent("MAT137");
        assertEquals(att, new ArrayList<>());
        userService.setCurrentUser("aLeo");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.SIGNUP_SUCCESS);
        List<String> att_list = new ArrayList<>();
        att_list.add("aLeo");
        assertEquals(att, att_list);
        userService.setCurrentUser("aLeo");
        assertEquals(this.testEventController.signUp("MAT137"), EventPrompt.USER_DOUBLE_SIGNUP);
    }

    @Test
    public void cancelSignupExistingEvent() {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");

        String id1=userService.getListOfIDsByType(UserType.ATTENDEE).get(0);

        assertEquals(EventPrompt.EVENT_ADDED, this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"MAT137",t5,t6,"PG101",
                speakerIds1, new ArrayList<>()));
        userService.setCurrentUser(id1);
        assertEquals(EventPrompt.SIGNUP_SUCCESS, this.testEventController.signUp("MAT137"));
        assertEquals(EventPrompt.CANCEL_SUCCESS, this.testEventController.cancelSignUp("MAT137"));
        HashMap<String,String> actual0 = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected0 = toMap(EventType.SINGLE_SPEAKER_EVENT, 2,"MAT137",t5,t6,"PG101",
                speakerIds1, new ArrayList<>());
        assertEquals(expected0, actual0);
    }

    @Test
    public void cancelSignupMissingEvent() {
        String user = userService.getListOfIDsByType(UserType.ATTENDEE).get(0);
        userService.setCurrentUser(user);
        assertEquals(EventPrompt.EVENT_DNE,this.testEventController.cancelSignUp("BOB165"));
    }

    @Test
    public void cancelSignupToEventNotSignedUp() {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sHelen");

        this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2, "MAT1037",t5,t6,
                "PG1201", speakerIds1, new ArrayList<>());
        userService.setCurrentUser("aLeo");
        Enum<EventPrompt> result = this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,
                "MAT1037",t5,t6,"PG1201", speakerIds1, new ArrayList<>());
        assertEquals(EventPrompt.ATTENDEE_NOT_IN_EVENT,
                this.testEventController.cancelSignUp("MAT1037"));
    }

    @Test
    public void cancelSignupWithInvalidUser () {
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sLeo");

        this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2, "MAT137",t5,t6,"PG101"
                ,speakerIds1, new ArrayList<>());
        userService.setCurrentUser("Invalid User");
        this.testEventController.cancelSignUp("MAT137");
        assertEquals(new ArrayList<>(),
                this.testEventService.getUsersForEvent("MAT137"));
    }

    @Test
    public void testGetEventsForUser(){
        List<String> speakerIds1=new ArrayList<>();
        speakerIds1.add("sNancy");

        Enum<EventPrompt> result = this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,
                "MAT137", t5, t6,"PG101", speakerIds1, new ArrayList<>());
        userService.setCurrentUser("aJan");
        userService.setCurrentUser("aLeo");
        this.testEventController.signUp("MAT137");
        userService.setCurrentUser("aLeo");
        List<String> att_event_signed_up = testEventController.getEventsForUser();
        List<String> event_list_expected = new ArrayList<>();
        event_list_expected.add("MAT137");
        assertEquals(event_list_expected, att_event_signed_up);
        userService.setCurrentUser("sNancy");
        List<String> spk_event_signed_up = testEventController.getEventsForUser();
        assertEquals(event_list_expected, spk_event_signed_up);
    }

    @Test
    public void doubleBookingRoom(){
        this.testEventController.addEvent(EventType.NO_SPEAKER_EVENT, 2, "CSC207",t1,t3,"HL205"
                ,new ArrayList<>(), new ArrayList<>());
        assertEquals(EventPrompt.DOUBLE_BOOK_ROOM,
                this.testEventController.addEvent(EventType.NO_SPEAKER_EVENT, 2, "CSC236",t2,t4,
                        "HL205", new ArrayList<>(),new ArrayList<>()));
    }

    @Test
    public void doubleBookingSpeaker(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        List<String> s2=new ArrayList<>();
        s2.add("sNancy");
        s2.add("sHelen");

        this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "CSC207",t1,t3,
                "Somewhere",s1, new ArrayList<>());
        assertEquals(EventPrompt.DOUBLE_BOOK_SPEAKER,
                this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 2,"CSC236",t1,t3,
                        "BA1007",s2, new ArrayList<>()));
    }

    @Test
    public void cancelEventByID(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 10, "Hack The Valley",t5,t6,
                "Somewhere",s1, new ArrayList<>());
        List<String> events1 = new ArrayList<>();
        HashMap<String,String> actual = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected = toMap(EventType.MULTI_SPEAKER_EVENT, 10, "Hack The Valley",t5,t6,
                "Somewhere",s1, new ArrayList<>());
        assertEquals(expected,actual);

        assertTrue(this.testEventController.cancelEventByID("Hack The Valley"));
        assertEquals(0,testEventService.getListEvents().size());
    }

    @Test
    public void cancelEventsByType(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        List<String> s2=new ArrayList<>();
        s2.add("sLily");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 20,
                "Hack The Valley",t5,t6,"Somewhere",s1, new ArrayList<>()),
                EventPrompt.EVENT_ADDED);
        assertEquals(this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 40,
                "Vaccination Event",t5,t6,"HL205",s2, new ArrayList<>()), EventPrompt.EVENT_ADDED);
        HashMap<String,String> actual0 = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected0 = toMap(EventType.MULTI_SPEAKER_EVENT, 20,
                "Hack The Valley",t5,t6,"Somewhere",s1, new ArrayList<>());
        assertEquals(expected0,actual0);
        HashMap<String,String> actual1 = listToMap(testEventService.getListEvents().get(1));
        HashMap<String,String> expected1 = toMap(EventType.SINGLE_SPEAKER_EVENT, 40,
                "Vaccination Event",t5,t6,"HL205",s2, new ArrayList<>());
        assertEquals(expected1,actual1);

        assertTrue(this.testEventController.cancelEventsByType(EventType.SINGLE_SPEAKER_EVENT));
        assertEquals(1,testEventService.getListEvents().size());
    }

    @Test
    public void cancelEventsBySize(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        List<String> s2=new ArrayList<>();
        s2.add("sLily");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 15,
                "Hack The Valley",t5,t6,"Somewhere",s1, new ArrayList<>()), EventPrompt.EVENT_ADDED);
        assertEquals(this.testEventController.addEvent(EventType.SINGLE_SPEAKER_EVENT, 40,
                "UofT Event",t3,t4,"HL205",s2, new ArrayList<>()), EventPrompt.EVENT_ADDED);
        userService.setCurrentUser("aJan");
        this.testEventController.signUp("Hack The Valley");
        userService.setCurrentUser("aLeo");
        this.testEventController.signUp("Hack The Valley");
        userService.setCurrentUser("aHelen");
        this.testEventController.signUp("Hack The Valley");
        userService.setCurrentUser("aLeo");
        this.testEventController.signUp("UofT Event");

        List<String> a1 = new ArrayList<>();
        a1.add("aJan");
        a1.add("aLeo");
        a1.add("aHelen");
        List<String> a2 = new ArrayList<>();
        a2.add("aLeo");
        HashMap<String,String> actual = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected = toMap(EventType.MULTI_SPEAKER_EVENT, 15,
                "Hack The Valley",t5,t6,"Somewhere",s1, a1);
        assertEquals(expected,actual);
        HashMap<String,String> actual1 = listToMap(testEventService.getListEvents().get(1));
        HashMap<String,String> expected1 = toMap(EventType.SINGLE_SPEAKER_EVENT, 40,
                "UofT Event",t3,t4,"HL205",s2, a2);
        assertEquals(expected1,actual1);
        assertEquals(2,testEventService.getListEvents().size());

        assertTrue(this.testEventController.cancelEventsBySize(2, true));
        HashMap<String,String> actual2 = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected2 = toMap(EventType.SINGLE_SPEAKER_EVENT, 40,
                "UofT Event",t3,t4,"HL205",s2, a2);
        assertEquals(expected2,actual2);
        assertEquals(1,testEventService.getListEvents().size());
    }


    @Test
    public void modifyEventCapacityExisting(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        List<String> a1=new ArrayList<>();
        a1.add("aJan");
        a1.add("aLeo");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 15,
                "Hack The Valley",t5,t6,"Somewhere",s1, a1), EventPrompt.EVENT_ADDED);
        assertTrue(this.testEventController.modifyEventCapacity("Hack The Valley",
                "Somewhere", 4));
        assertFalse(this.testEventController.modifyEventCapacity("Hack The Valley","Somewhere",
                1));
    }

    @Test
    public void modifyEventCapacityDNE(){
        assertFalse(this.testEventController.modifyEventCapacity("Non-existing event",
                "Somewhere",10));
    }

    /**
     * This checks the condition where the new event capacity is exceeding the room capacity.
     */
    @Test
    public void modifyEventCapacityWithRoomExplodes(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        List<String> a1=new ArrayList<>();
        a1.add("aJan");
        a1.add("aLeo");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 15,
                "Hack The Valley",t5,t6,"Somewhere",s1, a1), EventPrompt.EVENT_ADDED);
        assertTrue(this.testEventController.modifyEventCapacity("Hack The Valley",
                "Somewhere",15));
        assertFalse(this.testEventController.modifyEventCapacity("Hack The Valley",
                "Somewhere",30));
    }

    /**
     * This checks the condition where the new event capacity is smaller thant the current
     * number of attendees and speakers in total
     */
    @Test
    public void modifyEventCapacityWithAttendeeExplodes(){
        List<String> s1=new ArrayList<>();
        s1.add("sNancy");
        s1.add("sHelen");
        List<String> a1=new ArrayList<>();
        a1.add("aJan");
        a1.add("aLeo");
        assertEquals(this.testEventController.addEvent(EventType.MULTI_SPEAKER_EVENT, 15,
                "Hack The Valley",t5,t6,"Somewhere",s1, a1), EventPrompt.EVENT_ADDED);
        assertTrue(this.testEventController.modifyEventCapacity("Hack The Valley",
                "Somewhere", 6));
        assertFalse(this.testEventController.modifyEventCapacity("Hack The Valley",
                "Somewhere", 3));
    }
}
