package message_system_test;

import event_system.EventService;
import event_system.EventServiceEngine;
import event_system.EventType;
import user_system.*;
import message_system.*;
import org.junit.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageControllerTest2 {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService testEventService = new EventServiceEngine();
    private final UserService testUserService = new UserServiceEngine();
    private final MessageService testMessageService = new MessageServiceEngine();
    private IMessageController testMessageController;
    String test_file_name = "jdbc:sqlite:assets/UserDataTest.db";

    @Before
    public void Setup() {
        testMessageController = new MessageController(test_file_name, testUserService, testEventService,
                testMessageService);
        testMessageController.load();
        testUserService.addUser("auser1", "pass", UserType.ATTENDEE);
        testUserService.addUser("att1", "pass", UserType.ATTENDEE);
        testUserService.addUser("speaker1", "pass", UserType.SPEAKER);
        testUserService.addUser("org1", "pass", UserType.ORGANIZER);
        testUserService.addUser("org2", "pass", UserType.ORGANIZER);
        testUserService.addUser("att2", "pass", UserType.ATTENDEE);
        testUserService.addUser("att3", "pass", UserType.ATTENDEE);
        testUserService.addUser("speaker2", "pass", UserType.SPEAKER);
        testUserService.addUser("admin1", "pass", UserType.ADMIN);
        testUserService.addUser("admin2", "pass", UserType.ADMIN);
    }

    // Phase1
    @Test
    public void speakerMultiMessageForEvents() {
        List<String> attendeeList = new ArrayList<>();
        List<String> speakerList = new ArrayList<>();
        attendeeList.add("att1");
        attendeeList.add("att2");
        speakerList.add("speaker1");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 10,"Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Room",
                speakerList, attendeeList);

        List<String> attendeeList2 = new ArrayList<>();
        attendeeList2.add("att1");
        attendeeList2.add("att3");
        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT, 10, "Event2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                "Room2", speakerList, attendeeList2);

        List<String> eventIds = new ArrayList<>();
        eventIds.add("Event");
        eventIds.add("Event2");

        String time1 = LocalDateTime.now().format(formatter);
        testMessageController.speakerMultiMessageForEvents("speaker multicast", "speaker1", eventIds);

        List<List<String>> result = testMessageController.showMessage("speaker1");
        assertEquals(4, testMessageController.showMessage("speaker1").size());
        System.out.println(result);
    }

    // Phase2
    @Test
    public void getListOfUsersCanMessage(){
        // attendee can message to anyone except admin and organizer, not including themselves
        List<String> IDs1 = testMessageController.getListOfUsersCanMessage(UserType.ATTENDEE, "att1");
        List<String> ex1 = new ArrayList<>();
        ex1.add("auser1");
        ex1.add("att2");
        ex1.add("att3");
        ex1.add("speaker2");
        ex1.add("speaker1");
        assertEquals(5, IDs1.size());
        assertTrue(IDs1.containsAll(ex1));

        // org can message to anyone except admin
        List<String> IDs2 = testMessageController.getListOfUsersCanMessage(UserType.ORGANIZER, "org1");
        List<String> ex2 = new ArrayList<>();
        ex2.add("auser1");
        ex2.add("att1");
        ex2.add("att2");
        ex2.add("att3");
        ex2.add("org2");
        ex2.add("speaker2");
        ex2.add("speaker1");
        assertEquals(7, IDs2.size());
        assertTrue(IDs2.containsAll(ex2));

        // speaker can message to anyone except admin
        List<String> IDs3 = testMessageController.getListOfUsersCanMessage(UserType.SPEAKER, "speaker1");
        List<String> ex3 = new ArrayList<>();
        ex3.add("auser1");
        ex3.add("att1");
        ex3.add("att2");
        ex3.add("att3");
        ex3.add("org1");
        ex3.add("org2");
        ex3.add("speaker2");
        assertEquals(7, IDs3.size());
        assertTrue(IDs3.containsAll(ex3));

        // admin can message to anyone except admin
        List<String> IDs4 = testMessageController.getListOfUsersCanMessage(UserType.ADMIN, "admin1");
        List<String> ex4 = new ArrayList<>();
        assertEquals(0, IDs4.size());
        assertEquals(ex4, IDs4);
    }

    @Test
    public void testChangeStatus(){
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<List<String>> listOfMessages = testMessageService.getMessageInfo();
        assertEquals(1, listOfMessages.size());

        UUID message1Id = UUID.fromString(listOfMessages.get(0).get(0));
        String time1 = LocalDateTime.now().format(formatter);

        // change from UNREAD to UNREAD
        assertEquals(MessagePrompt.MARK_STATUS_UNREAD_SUCCESS, testMessageController.changeMessageStatus(message1Id, MessageStatus.UNREAD));
        assertEquals(MessageStatus.UNREAD.toString(), testMessageService.getMessageInfo().get(0).get(5));

        // change from UNREAD to READ
        assertEquals(MessagePrompt.MARK_STATUS_READ_SUCCESS, testMessageController.changeMessageStatus(message1Id, MessageStatus.READ));
        assertEquals(MessageStatus.READ.toString(), testMessageService.getMessageInfo().get(0).get(5));

        // change from READ to ARCHIVE
        assertEquals(MessagePrompt.MARK_STATUS_ARCHIVE_SUCCESS, testMessageController.changeMessageStatus(message1Id, MessageStatus.ARCHIVE));
        assertEquals(MessageStatus.ARCHIVE.toString(), testMessageService.getMessageInfo().get(0).get(5));

        // change from ARCHIVE to READ
        assertEquals(MessagePrompt.MARK_STATUS_READ_SUCCESS, testMessageController.changeMessageStatus(message1Id, MessageStatus.READ));
        assertEquals(MessageStatus.READ.toString(), testMessageService.getMessageInfo().get(0).get(5));
    }

    @Test
    public void testChangeStatusInvalidMessageId(){
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        assertEquals(MessagePrompt.MESSAGE_DOES_NOT_EXIST, testMessageController.changeMessageStatus(UUID.randomUUID(), MessageStatus.READ));
    }

    @Test
    public void deleteMessage(){
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<String> listOfMessages = testMessageService.getMessageInfo().get(0);
        UUID message1Id = UUID.fromString(listOfMessages.get(0));

        assertEquals(testMessageController.deleteMessage(message1Id), MessagePrompt.DELETE_SUCCESS);
        List<List<String>> afterDeleteListOfMessages = testMessageService.getMessageInfo();
        assertEquals(0, afterDeleteListOfMessages.size());
    }

    @Test
    public void deleteMessageInvalidID(){
        assertEquals(MessagePrompt.MESSAGE_DOES_NOT_EXIST, testMessageController.deleteMessage(UUID.randomUUID()));

        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<List<String>> listOfMessages = testMessageService.getMessageInfo();
        assertEquals(1, listOfMessages.size());

        assertEquals(MessagePrompt.MESSAGE_DOES_NOT_EXIST, testMessageController.deleteMessage(UUID.randomUUID()));
        List<List<String>> afterDeleteListOfMessages = testMessageService.getMessageInfo();
        assertEquals(1, afterDeleteListOfMessages.size());
    }

    @Test
    public void getGivenStatusMessages() {
        UUID messageID1 = UUID.randomUUID();
        testMessageService.addMessage(messageID1, "hi1","user1", "user2", LocalDateTime.now(), MessageStatus.UNREAD);
        UUID messageID2 = UUID.randomUUID();
        testMessageService.addMessage(messageID2, "hi2","user1", "user2", LocalDateTime.now(), MessageStatus.READ);
        UUID messageID3 = UUID.randomUUID();
        testMessageService.addMessage(messageID3, "hi3","user1", "user2", LocalDateTime.now(), MessageStatus.ARCHIVE);

        UUID messageID4 = UUID.randomUUID();
        testMessageService.addMessage(messageID4, "what's up1","user2", "user1", LocalDateTime.now(), MessageStatus.UNREAD);
        UUID messageID5 = UUID.randomUUID();
        testMessageService.addMessage(messageID5, "what's up2","user2", "user1", LocalDateTime.now(), MessageStatus.ARCHIVE);
        UUID messageID6 = UUID.randomUUID();
        testMessageService.addMessage(messageID6, "what's up3","user2", "user1", LocalDateTime.now(), MessageStatus.READ);

        // add some messages not relevant to "user1" or "user2"
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message","auser1", "att1", LocalDateTime.now(), MessageStatus.READ);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message","speaker1", "att1", LocalDateTime.now(), MessageStatus.UNREAD);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message","org1", "speaker1", LocalDateTime.now(), MessageStatus.READ);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message","org1", "att1", LocalDateTime.now(), MessageStatus.ARCHIVE);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message2","auser1", "att1", LocalDateTime.now(), MessageStatus.READ);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message2","speaker1", "att1", LocalDateTime.now(), MessageStatus.UNREAD);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message2","org1", "speaker1", LocalDateTime.now(), MessageStatus.READ);
        testMessageService.addMessage(UUID.randomUUID(), "Not relevant message2","org1", "att1", LocalDateTime.now(), MessageStatus.ARCHIVE);

        List<List<String>> read_result = testMessageController.getGivenStatusMessagesByUser(MessageStatus.READ, "user1");
        assertEquals(2, read_result.size());
        assertEquals(messageID2.toString(), read_result.get(0).get(0));
        assertEquals(messageID6.toString(), read_result.get(1).get(0));

        List<List<String>> read_result2 = testMessageController.getGivenStatusMessagesByUser(MessageStatus.READ, "user2");
        assertEquals(2, read_result2.size());
        assertEquals(messageID2.toString(), read_result2.get(0).get(0));
        assertEquals(messageID6.toString(), read_result2.get(1).get(0));

        List<List<String>> unread_result = testMessageController.getGivenStatusMessagesByUser(MessageStatus.UNREAD, "user1");
        assertEquals(2, unread_result.size());
        assertEquals(messageID1.toString(), unread_result.get(0).get(0));
        assertEquals(messageID4.toString(), unread_result.get(1).get(0));

        List<List<String>> unread_result2 = testMessageController.getGivenStatusMessagesByUser(MessageStatus.UNREAD, "user2");
        assertEquals(2, unread_result2.size());
        assertEquals(messageID1.toString(), unread_result2.get(0).get(0));
        assertEquals(messageID4.toString(), unread_result2.get(1).get(0));

        List<List<String>> archive_result = testMessageController.getGivenStatusMessagesByUser(MessageStatus.ARCHIVE, "user1");
        assertEquals(2, archive_result.size());
        assertEquals(messageID3.toString(), archive_result.get(0).get(0));
        assertEquals(messageID5.toString(), archive_result.get(1).get(0));

        List<List<String>> archive_result2 = testMessageController.getGivenStatusMessagesByUser(MessageStatus.ARCHIVE, "user2");
        assertEquals(2, archive_result2.size());
        assertEquals(messageID3.toString(), archive_result2.get(0).get(0));
        assertEquals(messageID5.toString(), archive_result2.get(1).get(0));
    }

    @Test
    public void getAllMessagesInSystem() {
        UUID messageID1 = UUID.randomUUID();
        testMessageService.addMessage(messageID1, "hi1","user1", "user2", LocalDateTime.now(), MessageStatus.UNREAD);
        UUID messageID2 = UUID.randomUUID();
        testMessageService.addMessage(messageID2, "hi2","user1", "user2", LocalDateTime.now(), MessageStatus.READ);
        UUID messageID3 = UUID.randomUUID();
        testMessageService.addMessage(messageID3, "hi3","user1", "user2", LocalDateTime.now(), MessageStatus.ARCHIVE);

        UUID messageID4 = UUID.randomUUID();
        testMessageService.addMessage(messageID4, "what's up1","user2", "user1", LocalDateTime.now(), MessageStatus.UNREAD);
        UUID messageID5 = UUID.randomUUID();
        testMessageService.addMessage(messageID5, "what's up2","user2", "user1", LocalDateTime.now(), MessageStatus.ARCHIVE);
        UUID messageID6 = UUID.randomUUID();
        testMessageService.addMessage(messageID6, "what's up3","user2", "user1", LocalDateTime.now(), MessageStatus.READ);

        List<List<String>> all_result = testMessageController.getAllMessagesInSystem();
        assertEquals(6, all_result.size());
        System.out.println(all_result);
    }

    @Test
    public void testShowMessage() {
        // sender checks sent message, should not change UNREAD to READ
        String time1 = LocalDateTime.now().format(formatter);
        List<String> ex1 = new ArrayList<>();
        ex1.add("user1");
        ex1.add("user2");
        ex1.add("1234");
        ex1.add(time1);
        ex1.add("UNREAD");
        this.testMessageService.addMessage("1234", "user1", "user2");
        List<List<String>> result = this.testMessageService.showMessage("user1");
        assertEquals(ex1, result.get(0).subList(1,6));

        // receiver checks sent message, should change UNREAD to READ
        String time2 = LocalDateTime.now().format(formatter);
        List<String> ex2 = new ArrayList<>();
        ex2.add("user2");
        ex2.add("user1");
        ex2.add("abcd");
        ex2.add(time2);
        ex2.add("UNREAD");
        this.testMessageService.addMessage("abcd", "user2", "user1");
        result = this.testMessageController.showMessage("user1");
        assertEquals(result.size(), 2);
        assertEquals(ex2, result.get(1).subList(1,6));
    }

    @Test
    public void testMultiMessage() {
        List<String> receiverIds = new ArrayList<>();
        receiverIds.add("user1");
        receiverIds.add("user2");

        this.testMessageService.multiMessage("hello!", "org1", receiverIds);

        String time1 = LocalDateTime.now().format(formatter);
        List<String> ex1 = new ArrayList<>();
        ex1.add("org1");
        ex1.add("user1");
        ex1.add("hello!");
        ex1.add(time1);
        ex1.add("UNREAD");
        List<List<String>> result1 = this.testMessageController.showMessage("user1");
        assertEquals(ex1, result1.get(0).subList(1,6));

        List<String> ex2 = new ArrayList<>();
        ex2.add("org1");
        ex2.add("user2");
        ex2.add("hello!");
        ex2.add(time1);
        ex2.add("UNREAD");
        List<List<String>> result2 = this.testMessageController.showMessage("user2");
        assertEquals(ex2, result2.get(0).subList(1,6));

    }

    @Test
    public void testShowConversation() {
        String time1 = LocalDateTime.now().format(formatter);
        this.testMessageService.addMessage("hi user2 from user1", "user1", "user2");
        List<String> ex1 = new ArrayList<>();
        ex1.add("user1");
        ex1.add("user2");
        ex1.add("hi user2 from user1");
        ex1.add(time1);
        ex1.add("UNREAD");

        String time2 = LocalDateTime.now().format(formatter);
        this.testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<String> ex2 = new ArrayList<>();
        ex2.add("user2");
        ex2.add("user1");
        ex2.add("hi user1 from user2");
        ex2.add(time2);
        ex2.add("UNREAD");

        // Reason: user1 is the user who is currently logged in.
        List<List<String>> result = this.testMessageController.showConversation("user1", "user2");
        assertEquals(result.size(), 2);
        assertEquals(ex1, result.get(0).subList(1,6));
        assertEquals(ex2, result.get(1).subList(1,6));
    }

    @After
    public void tearDown(){
        MessageDatabaseReadWriter mdbrw = new MessageDatabaseReadWriter(testMessageService, test_file_name);
        assertTrue(mdbrw.deleteAllData("Messages"));
        assertTrue(mdbrw.deleteTable("Messages"));
    }
}