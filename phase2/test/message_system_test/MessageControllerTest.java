package message_system_test;

import event_system.EventService;
import event_system.EventServiceEngine;
import event_system.EventType;
import message_system.*;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import user_system.UserService;
import user_system.UserServiceEngine;
import user_system.UserType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageControllerTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MessageService testMessageService = new MessageServiceEngine();
    private final EventService testEventService = new EventServiceEngine();
    private final UserService testUserService = new UserServiceEngine();
    private IMessageController testMessageController;
    private MessageDatabaseReadWriter DB;
    String address = "jdbc:sqlite:assets/UserDataTest.db";

    @Before
    public void Setup() {
        testMessageController = new MessageController(address,
                testUserService, testEventService, testMessageService);
        testMessageController.load();
        DB = new MessageDatabaseReadWriter(testMessageService, address);
        testUserService.addUser("auser1", "pass", UserType.ATTENDEE);
        testUserService.addUser("att1", "pass", UserType.ATTENDEE);
        testUserService.addUser("att2", "pass", UserType.ATTENDEE);
        testUserService.addUser("att3", "pass", UserType.ATTENDEE);
        testUserService.addUser("speaker1", "pass", UserType.SPEAKER);
        testUserService.addUser("org1", "pass", UserType.ORGANIZER);
        testUserService.addUser("speaker2", "pass", UserType.SPEAKER);
    }

    @Test
    public void atestSendMessageToNonExistingUser() {
        assertEquals(MessagePrompt.USER_NOT_FOUND, testMessageController.sendMessage("send to non-existing user", "someRandomPerson",
                "att1"));
    }

    @Test
    public void btestSendMessageToOneself() {
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send to oneself", "att1", "att1"));
        assertEquals(1, testMessageController.showMessage("att1").size());
    }

    @Test
    public void ctestSendMessageToExistingUser() {
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send to existing user", "att1", "att2"));
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
    }

    @Test
    public void dtestSendMultipleMessages() {
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send multiple messages 1", "att2", "att1"));
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send multiple messages 2", "att3", "att1"));
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send multiple messages 3", "att2", "att1"));
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send multiple messages 4", "att2", "att1"));
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.sendMessage("send multiple messages 5", "att2", "att1"));
        assertEquals(5, testMessageController.showMessage("att1").size());
    }

    @Test
    public void etestOrganizerLegalMulticast() {
        // Message all attendees
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.multiMessage("organizer legal multicast to attendee",
                "org1", UserType.ATTENDEE));
        assertEquals(4, testMessageController.showMessage("org1").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(1, testMessageController.showMessage("att3").size());
        assertEquals(0, testMessageController.showMessage("speaker1").size());

        // Message all speakers
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.multiMessage("organizer legal multicast to speaker",
                "org1", UserType.SPEAKER));
        assertEquals(6, testMessageController.showMessage("org1").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(1, testMessageController.showMessage("att3").size());
        assertEquals(1, testMessageController.showMessage("speaker1").size());
        assertEquals(1, testMessageController.showMessage("speaker2").size());
    }

    @Test
    public void fatestSpeakerLegalMulticastForOneEvent() {
        // No messages sent when no attendee/speaker registers for any event
        ArrayList<String> speakerList = new ArrayList<>();
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room", speakerList, new ArrayList<>());
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.speakerMultiMessageForEvents("speaker legal multicast", "speaker1", Arrays.asList("Event")));
        assertEquals(0, testMessageController.showMessage("att2").size());
        assertEquals(0, testMessageController.showMessage("speaker1").size());

        // Lists of attendees for an new non-empty Event
        ArrayList<String> attendeeList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event2", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room", speakerList, attendeeList);
        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.speakerMultiMessageForEvents(
                "speaker legal multicast to attendees", "speaker1", Arrays.asList("Event2")));
        assertEquals(2, testMessageController.showMessage("speaker1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(0, testMessageController.showMessage("att3").size());
    }

    @Test
    public void fbtestSpeakerMulticastWithMultipleEvents() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event2",
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        testMessageController.speakerMultiMessageForEvents("speaker message", "speaker1", Arrays.asList("Event", "Event2"));
        assertEquals(3, testMessageController.showMessage("speaker1").size());
    }

    @Test
    public void gtestIlegalMulticast() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        assertEquals(MessagePrompt.UNAUTHORIZED, testMessageController.multiMessage("illegal multicast",
                "att1",UserType.ATTENDEE));
        assertEquals(0, testMessageController.showMessage("att1").size());
        assertEquals(0, testMessageController.showMessage("att2").size());
    }

    @Test
    public void htestOrganizerMultipleEventMulticast() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList1 = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList1.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room1",
                speakerList1, attendeeList);

        ArrayList<String> speakerList2 = new ArrayList<>();
        speakerList2.add("speaker2");

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att2");
        attendeeList2.add("att3");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event2", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                "Room2", speakerList2, attendeeList2);

        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.multiMessage("multicast attendee", "org1", UserType.ATTENDEE));
        assertEquals(4, testMessageController.showMessage("org1").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(1, testMessageController.showMessage("att3").size());
        assertEquals(1, testMessageController.showMessage("auser1").size());
        assertEquals(0, testMessageController.showMessage("speaker1").size());
        assertEquals(0, testMessageController.showMessage("speaker2").size());

        assertEquals(MessagePrompt.SEND_SUCCESS, testMessageController.multiMessage("multicast Speaker", "org1", UserType.SPEAKER));
        assertEquals(6, testMessageController.showMessage("org1").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(1, testMessageController.showMessage("att3").size());
        assertEquals(1, testMessageController.showMessage("speaker1").size());
        assertEquals(1, testMessageController.showMessage("speaker2").size());
    }

    @Test
    public void itestSaveMessagesMulticast() {
        // 4 messages
        testMessageController.multiMessage("org1 multicast attendee", "org1", UserType.ATTENDEE);
        // 2 message
        testMessageController.multiMessage("org1 multicast speaker", "org1", UserType.SPEAKER);
        System.out.println(testMessageController.showMessage("org1"));
        // 6 messages in total
        // Save the message into the file
        testMessageController.save();
        // TODO: change this to number of rows
        DB.displayAllRows("Messages", 6);
    }

    @Test
    public void jtestSaveMessagesSent() {
        // 1 message
        testMessageController.sendMessage("auser1 send att1", "att1", "auser1");
        // 1 message
        testMessageController.sendMessage("auser1 send att2", "att2", "auser1");
        // 1 message
        testMessageController.sendMessage("auser1 send att3", "att3", "auser1");

        // 3 messages in total.
        // Save the message into the file
        testMessageController.save();
        // TODO: change this to number of rows
        DB.displayAllRows("Messages", 6);
    }

    @Test
    public void ktestOverwrite() {
        // check that there is no double writing
        // 1 message
        testMessageController.sendMessage("auser1 send att1", "att1", "auser1");
        // 1 message
        testMessageController.sendMessage("auser1 send att2", "att2", "auser1");
        // 1 message
        testMessageController.sendMessage("auser1 send att3", "att3", "auser1");
        testMessageController.save();
        testMessageController.save();
        // TODO: change this to number of rows
        DB.displayAllRows("Messages", 6);
    }

    @Test
    public void mtestShowMessagesAfterNewMessages() {
        assertEquals(0, testMessageController.showMessage("att3").size());

        // Send a new message
        testMessageController.sendMessage("some new message", "att3", "org1");

        assertEquals(1, testMessageController.showMessage("att3").size());

        assertEquals(1, testMessageController.showMessage("org1").size());
        // TODO: does show Message get messages ready to print?
        // TODO: nested array for Message info?
        System.out.println(testMessageController.showMessage("att3").get(0));
        System.out.println(testMessageController.showMessage("org1").get(0));
    }

    @Test
    public void otestShowConversation() {
        testMessageController.sendMessage("att1 to org1", "org1", "auser1");
        // att1 1 messages, auser1, att2, att3 one messages, org1 4 messages
        testMessageController.multiMessage("org1 multicast", "org1", UserType.ATTENDEE);

        assertEquals(1, testMessageController.showConversation("att1", "org1").size());
        assertEquals(1, testMessageController.showConversation("org1", "att1").size());
        assertEquals(1, testMessageController.showConversation("att3", "org1").size());

        // org send a new message: this should succeed
        testMessageController.sendMessage("new from org1 to att3","att3", "org1");
        assertEquals(2, testMessageController.showConversation("att3", "org1").size());
        assertEquals(2, testMessageController.showConversation("org1", "att3").size());
        assertEquals(1, testMessageController.showConversation("org1", "att2").size());
    }


    @Test
    // The following is a test case related to ShowConversation: ShowConversation is being used in Phase 2
    // It is not dead code. :)
    public void qtestShowConversationWithNonExisitingUser() {
        testMessageController.sendMessage("some message that never reaches some user", "att1",
                "auser1");
        assertEquals(0,
                testMessageController.showConversation("auser1", "some user").size());
        assertEquals(0,
                testMessageController.showConversation("auser1", "some user").size());
        assertEquals(1, testMessageController.showConversation("auser1", "att1").size());
    }

    @Test
    public void rtestSpeakerMulticastMessage() {
        // no message should be sent before the speaker has any event
        testUserService.setCurrentUser("speaker1");
        testMessageController.speakerMultiMessageForEvents("speaker Multicast", Arrays.asList(""));
        assertEquals(0, testMessageController.showMessage("speaker1").size());

        // set up the event for speaker1
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room", speakerList, attendeeList);

        // speaker1 multiMessage success after setting up the event
        testMessageController.speakerMultiMessageForEvents("speaker multicast", Arrays.asList("Event"));
        assertEquals(2, testMessageController.showMessage("speaker1").size());
        assertEquals(1, testMessageController.showMessage("att1").size());
        assertEquals(1, testMessageController.showMessage("att2").size());
        assertEquals(0, testMessageController.showMessage("att3").size());
    }

    @Test
    public void stestSpeakerMulticastMessageForNonExistingEvents() {
        ArrayList<String> events = new ArrayList<>();
        events.add("Some Event");
        assertEquals(MessagePrompt.INVALID_EVENT_INPUT, testMessageController.speakerMultiMessageForEvents(
                "speaker Multicast", "speaker1", events));
        assertEquals(0, testMessageController.showMessage("speaker1").size());
    }

    @Test
    public void stestSpeakerMulticastMessageForMixedExistingNonExistingEvents() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room", speakerList, attendeeList);

        ArrayList<String> events = new ArrayList<>();
        events.add("Some Event");
        events.add("Event");

        assertEquals(MessagePrompt.INVALID_EVENT_INPUT, testMessageController.speakerMultiMessageForEvents(
                "speaker Multicast", "speaker1", events));

        assertEquals(2, testMessageController.showMessage("speaker1").size());
    }

    @Test
    public void stestSpeakerMulticastMessageForMixedExistingNonExistingEvents2() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room", speakerList, attendeeList);

        ArrayList<String> events = new ArrayList<>();
        events.add("Event");
        events.add("Some Event");

        assertEquals(MessagePrompt.INVALID_EVENT_INPUT, testMessageController.speakerMultiMessageForEvents(
                "speaker Multicast", "speaker1", events));

        // TODO：Double check how this part should work: some events valid some invalid. Messages are sent to those
        //  valid events. What should UI present?
        assertEquals(2, testMessageController.showMessage("speaker1").size());
    }

    @Test
    public void ttestSpeakerMulticastMessageForOneSingleEvent() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "Room",speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att3");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event2",
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),"Room2", speakerList, attendeeList2);

        testMessageController.speakerMultiMessageForEvents("speaker multicast", "speaker1", new ArrayList<>(Arrays.asList("Event")));
        assertEquals(2, testMessageController.showMessage("speaker1").size());
        testMessageController.speakerMultiMessageForEvents("another speaker multicast", "speaker1", new ArrayList<>(Arrays.asList("Event")));
        assertEquals(4, testMessageController.showMessage("speaker1").size());
    }

    @Test
    public void utestSpeakerMulticastMessageForMultipleEvents() {
        ArrayList<String> attendeeList = new ArrayList<>();
        ArrayList<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        ArrayList<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att3");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 3, "Event2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        testMessageController.speakerMultiMessageForEvents("speaker multicast", "speaker1", new ArrayList<>(Arrays.asList("Event", "Event2")));
        assertEquals(4, testMessageController.showMessage("speaker1").size());
    }

    @After
    public void tearDown(){
        DB.deleteAllData("Messages");
    }
}