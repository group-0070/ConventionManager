package message_system_test;

import message_system.*;
import org.junit.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MessageServiceEngineTest {

    private final String test_file_name = "./assets/temp_Messages.txt";
    private final MessageService testMessageService = new MessageServiceEngine();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Before
    public void Setup() {
        File file = new File(test_file_name);
        if (file.exists()){
            file.delete();
        }
    }

    @Test
    public void testSendMessage() {
        String time1 = LocalDateTime.now().format(formatter);
        List<String> ex1 = new ArrayList<>();
        ex1.add("user1");
        ex1.add("user2");
        ex1.add("1234");
        ex1.add(time1);
        ex1.add("UNREAD");
        this.testMessageService.addMessage("1234", "user1", "user2");
        assertEquals(ex1, testMessageService.getMessageInfo().get(0).subList(1,6));

        String time2 = LocalDateTime.now().format(formatter);
        List<String> ex2 = new ArrayList<>();
        ex2.add("user2");
        ex2.add("user1");
        ex2.add("abcd");
        ex2.add(time2);
        ex2.add("UNREAD");
        this.testMessageService.addMessage("abcd", "user2", "user1");
        assertEquals(ex2, testMessageService.getMessageInfo().get(1).subList(1,6));
    }

    @Test
    public void testEmptySendMessage(){
        String time2 = LocalDateTime.now().format(formatter);
        List<String> ex2 = new ArrayList<>();
        ex2.add("user2");
        ex2.add("user1");
        ex2.add("");
        ex2.add(time2);
        ex2.add("UNREAD");
        this.testMessageService.addMessage("", "user2", "user1");
        assertEquals(ex2, testMessageService.getMessageInfo().get(0).subList(1,6));
    }

    @Test
    public void testAddMessageSend() {
        // for adding existing message on the file
        LocalDateTime time1 = LocalDateTime.parse("2020-11-12 09:08:00", formatter);
        UUID id1 = UUID.randomUUID();
        testMessageService.addMessage(id1,"test addMessage", "user3", "user4", time1, MessageStatus.UNREAD);
        assertEquals(id1.toString(), testMessageService.getMessageInfo().get(0).get(0));


        LocalDateTime time2 = LocalDateTime.parse("2020-11-12 09:11:35", formatter);
        UUID id2 = UUID.randomUUID();
        testMessageService.addMessage(id2, "test addMessage twice", "user1", "user2", time2, MessageStatus.UNREAD);
        assertEquals(id2.toString(), testMessageService.getMessageInfo().get(1).get(0));

        assertEquals(2, testMessageService.getMessageInfo().size());
    }

    @Test
    public void testChangeStatus(){
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<List<String>> listOfMessages = testMessageService.getMessageInfo();
        assertEquals(1, listOfMessages.size());

        List<String> message1 = listOfMessages.get(0);
        UUID message1Id = UUID.fromString(message1.get(0));
        String time1 = LocalDateTime.now().format(formatter);

        // change from UNREAD to UNREAD
        testMessageService.changeMessageStatus(message1Id, MessageStatus.UNREAD);
        List<String> actual1 = testMessageService.getMessageInfo().get(0);
        assertEquals(MessageStatus.UNREAD.toString(), actual1.get(actual1.size()-1));

        // change from UNREAD to READ
        testMessageService.changeMessageStatus(message1Id, MessageStatus.READ);
        List<String> actual2 = testMessageService.getMessageInfo().get(0);
        assertEquals(MessageStatus.READ.toString(), actual2.get(actual1.size()-1));

        // change from READ to ARCHIVE
        testMessageService.changeMessageStatus(message1Id, MessageStatus.ARCHIVE);
        List<String> actual3 = testMessageService.getMessageInfo().get(0);
        assertEquals(MessageStatus.ARCHIVE.toString(), actual3.get(actual1.size()-1));
    }

    @Test
    public void deleteMessage(){
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<List<String>> listOfMessages = testMessageService.getMessageInfo();
        assertEquals(1, listOfMessages.size());
        List<String> message1 = listOfMessages.get(0);
        UUID message1Id = UUID.fromString(message1.get(0));

        testMessageService.deleteMessage(message1Id);
        List<List<String>> afterDeleteListOfMessages = testMessageService.getMessageInfo();
        assertEquals(0, afterDeleteListOfMessages.size());
    }

    @Test
    public void getGivenStatusMessages() {
        testMessageService.addMessage("hi user1 from user2", "user2", "user1");
        List<List<String>> listOfMessages = testMessageService.getMessageInfo();
        List<String> message1 = listOfMessages.get(0);
        UUID message1Id = UUID.fromString(message1.get(0));

        List<List<String>> actual = testMessageService.getGivenStatusMessages(MessageStatus.UNREAD);
        assertEquals(1, actual.size());
        assertEquals(0, testMessageService.getGivenStatusMessages(MessageStatus.READ).size());
        assertEquals(0, testMessageService.getGivenStatusMessages(MessageStatus.ARCHIVE).size());

        testMessageService.changeMessageStatus(message1Id, MessageStatus.READ);
        List<List<String>> actual2 = testMessageService.getGivenStatusMessages(MessageStatus.READ);
        assertEquals(1, actual2.size());
        assertEquals(0, testMessageService.getGivenStatusMessages(MessageStatus.UNREAD).size());
        assertEquals(0, testMessageService.getGivenStatusMessages(MessageStatus.ARCHIVE).size());
    }

    @After
    public void tearDown(){
        File file = new File(this.test_file_name);
        file.delete();
    }

}
