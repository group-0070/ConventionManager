package messaging_system_test;

import event_system.EventService;
import event_system.EventServiceEngine;
import login_system.*;
import messaging_system.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageControllerTest {
    private final String test_file_name = "assets/testMessages.txt";
    private final String login_file_name = "assets/testLogin.txt";


    private ILoginController testLoginController = new LoginController(login_file_name,
            "auser1", "pass");
    private EventService testEventService = new EventServiceEngine();
    private IMessageController testMessageController = new MessageController(testLoginController, testEventService,
            test_file_name);
    private ILoginController testLoginController2 = new LoginController(login_file_name,
            "org1", "pass");
    private IMessageController testMessageController2 = new MessageController(testLoginController2, testEventService,
            test_file_name);
    private ILoginController testLoginController3 = new LoginController(login_file_name,
            "speaker1", "pass");
    private IMessageController testMessageController3 = new MessageController(testLoginController3, testEventService,
            test_file_name);

    @Before
    public void Setup() {
        testLoginController.getUserService().addUser("auser1", "pass");
        testLoginController.getUserService().addUser("att1", "pass");
        testLoginController.getUserService().addUser("speaker1", "pass");
        testLoginController.getUserService().addUser("org1", "pass");
        testLoginController.getUserService().addUser("att2", "pass");
        testLoginController.getUserService().addUser("att3", "pass");
        testLoginController.getUserService().addUser("speaker2", "pass");
    }

    @Test
    public void atestSendMessageToNonExistingUser() {
        assertFalse(testMessageController.sendMessage("send to non-existing user", "someRandomPerson"));
    }

    @Test
    public void btestSendMessageToOneself() {
        assertTrue(testMessageController.sendMessage("send to oneself", "auser1"));
        assertEquals(1, testMessageController.showMessage("auser1").size());
    }

    @Test
    public void ctestSendMessageToExistingUser() {
        assertTrue(testMessageController.sendMessage("send to existing user", "att1"));
    }

    @Test
    public void dtestSendMultipleMessages() {
        assertTrue(testMessageController.sendMessage("send multiple messages 1", "att2"));
        assertTrue(testMessageController.sendMessage("send multiple messages 2", "att3"));
        assertTrue(testMessageController.sendMessage("send multiple messages 3", "att2"));
        assertTrue(testMessageController.sendMessage("send multiple messages 4", "att2"));
        assertTrue(testMessageController.sendMessage("send multiple messages 5", "att2"));
        assertEquals(5, testMessageController.showMessage(testLoginController.getCurrentUserId()).size());
    }

    @Test
    public void etestOrganizerLegalMulticast() {
        // Message all attendees
        assertTrue(testMessageController2.multicastMessage("organizer legal multicast", "Attendee"));
        assertEquals(4, testMessageController2.showMessage("org1").size());
        assertEquals(1, testMessageController2.showMessage("att2").size());
        assertEquals(0, testMessageController2.showMessage("speaker1").size());

        // Message all speakers
        assertTrue(testMessageController2.multicastMessage("organizer legal multicast", "Speaker"));
        assertEquals(6, testMessageController2.showMessage("org1").size());
        assertEquals(1, testMessageController2.showMessage("att1").size());
        assertEquals(1, testMessageController2.showMessage("speaker1").size());
    }

    @Test
    public void fatestSpeakerLegalMulticast() {
        // No messages sent when no attendee/speaker registers for any event
        assertTrue(testMessageController3.speakerMulticastMessage("speaker legal multicast"));
        assertEquals(0, testMessageController3.showMessage("att2").size());
        assertEquals(0, testMessageController3.showMessage("speaker1").size());

        // Add an event, its speaker and attendee
        // Lists of attendees and speakers for an Event
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);
        assertTrue(testMessageController3.speakerMulticastMessage("speaker legal multicast"));
        assertEquals(2, testMessageController3.showMessage("speaker1").size());
        assertEquals(1, testMessageController3.showMessage("att2").size());
        assertEquals(1, testMessageController3.showMessage("att1").size());
        assertEquals(0, testMessageController3.showMessage("att3").size());
    }

    @Test
    public void fbtestSpeakerMulticastWithMultipleEvents() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        speakerList.add("speaker1");
        testEventService.addEvent("Event2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        testMessageController3.speakerMulticastMessage("speaker message");
        assertEquals(2, testMessageController3.showMessage("speaker1").size());
    }

    @Test
    public void gtestIlegalMulticast() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        assertFalse(testMessageController.multicastMessage("illegal multicast", "Attendee"));
        assertEquals(0, testMessageController.showMessage(testLoginController.getCurrentUserId()).size());
        assertEquals(0, testMessageController.showMessage("att2").size());
        assertEquals(0, testMessageController.showMessage(testLoginController.getCurrentUserId()).size());
    }

    @Test
    public void htestOrganizerMultipleEventMulticast() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList1 = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList1.add("speaker1");
        testEventService.addEvent("Event1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room1",
                speakerList1, attendeeList);

        ArrayList<String> speakerList2 = new ArrayList<>();
        speakerList2.add("speaker2");

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att2");
        attendeeList2.add("att3");
        testEventService.addEvent("Event2", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                "Room2", speakerList2, attendeeList2);

        assertTrue(testMessageController2.multicastMessage("multicast attendee", "Attendee"));
        assertEquals(4, testMessageController2.showMessage(testLoginController2.getCurrentUserId()).size());
        assertEquals(1, testMessageController2.showMessage("att1").size());
        assertEquals(1, testMessageController2.showMessage("att2").size());
        assertEquals(1, testMessageController2.showMessage("att3").size());
        assertEquals(1, testMessageController2.showMessage("auser1").size());
        assertEquals(0, testMessageController2.showMessage("speaker1").size());
        assertEquals(0, testMessageController2.showMessage("speaker2").size());

        assertTrue(testMessageController2.multicastMessage("multicast Speaker", "Speaker"));
        assertEquals(6, testMessageController2.showMessage(testLoginController2.getCurrentUserId()).size());
        assertEquals(1, testMessageController2.showMessage("att1").size());
        assertEquals(1, testMessageController2.showMessage("att2").size());
        assertEquals(1, testMessageController2.showMessage("att3").size());
        assertEquals(1, testMessageController2.showMessage("speaker1").size());
        assertEquals(1, testMessageController2.showMessage("speaker2").size());
    }

    @Test
    public void uutestSaveMessagesMulticast() {
        // 4 messages
        testMessageController2.multicastMessage("org1 multicast attendee", "Attendee");
        // 2 message
        testMessageController2.multicastMessage("org1 multicast speaker", "Speaker");
        System.out.println(testMessageController2.showMessage("org1"));
        // 6 messages in total
        // Save the message into the file
        testMessageController2.save();
    }

    @Test
    public void uvtestSaveMessagesSent() {
        // 1 message
        testMessageController.sendMessage("auser1 send att1", "att1");
        // 1 message
        testMessageController.sendMessage("auser1 send att2", "att2");
        // 1 message
        testMessageController.sendMessage("auser1 send att3", "att3");

        // 3 messages in total.
        // Save the message into the file
        testMessageController.save();
        // file now has 9 messages
    }

    @Test
    public void uwtestOverwrite() {
        // check that there is no double writing
        testMessageController.save();
        testMessageController2.save();
    }

    @Test
    public void vvtestShowMessages() {
        System.out.println(testMessageController2.showMessage("org1"));
        System.out.println(testMessageController2.showMessage("att1"));
        System.out.println(testMessageController2.showMessage("att2"));
        System.out.println(testMessageController2.showMessage("att3"));

        System.out.println(testMessageController.showMessage("org1"));
        System.out.println(testMessageController.showMessage("att1"));
        System.out.println(testMessageController.showMessage("att2"));
        System.out.println(testMessageController.showMessage("att3"));
    }

    @Test
    public void vwtestShowMessagesAfterNewMessages() {
        assertEquals(2, testMessageController2.showMessage("att3").size());

        // Send a new message
        testMessageController2.sendMessage("some new message", "att3");

        assertEquals(3, testMessageController2.showMessage("att3").size());

        assertEquals(2, testMessageController.showMessage("att3").size());
        testMessageController2.save();
        // 10 messages in total
    }

    @Test
    public void vxtestShowMessagesAfterNewMessagesCont() {
        System.out.println(testMessageController.showMessage("att3"));
        assertEquals(3, testMessageController.showMessage("att3").size());

        File file = new File(test_file_name);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void wwtestShowConversation() {
        testMessageController.sendMessage("att1 to org1", "org1");
        // att1 1 messages, auser1, att2, att3 one messages, org1 4 messages
        testMessageController2.multicastMessage("org1 multicast", "Attendee");

        assertEquals(0, testMessageController.showConversation("att1", "org1").size());
        assertEquals(1, testMessageController2.showConversation("att1", "org1").size());
        assertEquals(1, testMessageController2.showConversation("org1", "att1").size());
        assertEquals(1, testMessageController2.showConversation("att3", "org1").size());

        // org send a new message: this should succeed
        testMessageController2.sendMessage("new from org1 to att3", "att3");
        assertEquals(2, testMessageController2.showConversation("att3", "org1").size());
        assertEquals(2, testMessageController2.showConversation("org1", "att3").size());
        assertEquals(1, testMessageController2.showConversation("org1", "att2").size());
        assertEquals(0, testMessageController.showConversation("att3", "org1").size());
        assertEquals(0, testMessageController.showConversation("org1", "att3").size());

        // save the file
        testMessageController2.save();

        // att1 2 messages
        // org 5 messages
    }

    @Test
    // The following is a test case related to ShowConversation: ShowConversation is being used in Phase 2
    // It is not dead code. :)
    public void wxtestShowConversationCont() {
        assertEquals(2, testMessageController.showConversation("att3", "org1").size());
        assertEquals(2, testMessageController.showConversation("org1", "att3").size());

        File file = new File(test_file_name);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    // The following is a test case related to ShowConversation: ShowConversation is being used in Phase 2
    // It is not dead code. :)
    public void wytestShowConversationWithNonExisitingUser() {
        assertEquals(0,
                testMessageController.showConversation(testLoginController.getCurrentUserId(), "some user").size());
        assertEquals(0,
                testMessageController2.showConversation(testLoginController.getCurrentUserId(), "some user").size());
    }

    @Test
    public void xxtestSpeakerMulticastMessage() {
        testMessageController3.speakerMulticastMessage("speaker Multicast");
        assertEquals(0, testMessageController3.showMessage("speaker1").size());

        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);
        testMessageController3.speakerMulticastMessage("speaker multicast");
        assertEquals(2, testMessageController3.showMessage("speaker1").size());
    }

    @Test
    public void xytestSpeakerMulticastMessageForNonExistingEvents() {
        assertFalse(testMessageController3.speakerMulticastMessageForEvents("speaker Multicast", "Some Event"));
    }

    @Test
    public void xztestSpeakerMulticastMessageForOneSingleEvent() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att3");
        testEventService.addEvent("Event2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        testMessageController3.speakerMulticastMessageForEvents("speaker multicast", "Event");
        assertEquals(2, testMessageController3.showMessage("speaker1").size());
        testMessageController3.speakerMulticastMessageForEvents("another speaker multicast", "Event2");
        assertEquals(4, testMessageController3.showMessage("speaker1").size());
    }

    @Test
    public void yytestSpeakerMulticastMessageForMultipleEvents() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent("Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att3");
        testEventService.addEvent("Event2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        testMessageController3.speakerMulticastMessageForEvents("speaker multicast", "Event,Event2");
        assertEquals(4, testMessageController3.showMessage("speaker1").size());
    }

    @After
    public void tearDown(){
        File file = new File(this.test_file_name);
//        file.delete();
    }
}