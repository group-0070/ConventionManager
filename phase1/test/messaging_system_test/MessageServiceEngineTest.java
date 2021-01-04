package messaging_system_test;

import messaging_system.*;
import org.junit.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        String ex1 = "user1" + " (" + time1 + "): " + "1234" + " @" + "user2";
        this.testMessageService.sendMessage("1234", "user1", "user2");
        assertEquals(testMessageService.getStringMessage().get(0), ex1);

        String time2 = LocalDateTime.now().format(formatter);
        String ex2 = "user2" +" (" + time2 + "): " + "abcd" + " @" + "user1";
        this.testMessageService.sendMessage("abcd", "user2", "user1");
        assertEquals(testMessageService.getStringMessage().get(1), ex2);
    }

    @Test
    public void testEmptySendMessage(){
        String time2 = LocalDateTime.now().format(formatter);
        String ex2 = "user2" +" (" + time2 + "): " + "" + " @" + "user1";
        this.testMessageService.sendMessage("", "user2", "user1");
        assertEquals(testMessageService.getStringMessage().get(0), ex2);
    }

    @Test
    public void testShowMessage() {
        String time1 = LocalDateTime.now().format(formatter);
        String ex1 = "user1" + " (" + time1 + "): " + "1234" + " @" + "user2";
        this.testMessageService.sendMessage("1234", "user1", "user2");
        String time2 = LocalDateTime.now().format(formatter);
        String ex2 = "user2" +" (" + time2 + "): " + "abcd" + " @" + "user1";
        this.testMessageService.sendMessage("abcd", "user2", "user1");
        List<String> result = this.testMessageService.showMessage("user1");
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), ex1);
        assertEquals(result.get(1), ex2);
    }

    @Test
    public void testMulticastMessage() {
        List<String> receiverIds = new ArrayList<>();
        receiverIds.add("user1");
        receiverIds.add("user2");

        this.testMessageService.multicastMessage("hello!", "org1", receiverIds);

        String time1 = LocalDateTime.now().format(formatter);
        String ex1 = "org1" + " (" + time1 + "): " + "hello!" + " @" + "user1";
        String ex2 = "org1" +" (" + time1 + "): " + "hello!" + " @" + "user2";

        List<String> result1 = this.testMessageService.showMessage("user1");
        assertEquals(result1.get(0), ex1);
        List<String> result2 = this.testMessageService.showMessage("user2");
        assertEquals(result2.get(0), ex2);

    }

    @Test
    public void testAddMessageSend() {
        String time1 = "2020-11-12 09:08:00";
        testMessageService.addMessage("test addMessage", "user3", "user4", time1);
        String added1 = "user3" + " (" + time1 + "): " + "test addMessage" + " @" + "user4";
        assertEquals(testMessageService.getStringMessage().get(0), added1);

        String time2 = "2020-11-12 09:11:35";
        testMessageService.addMessage("test addMessage twice", "user1", "user2", time2);
        String added2 = "user1" + " (" + time2 + "): " + "test addMessage twice" + " @" + "user2";
        assertEquals(testMessageService.getStringMessage().get(1), added2);

        assertEquals(testMessageService.getStringMessage().size(), 2);
    }

    // For Phase2 test cases
//    @Test
//    public void testShowConversation() {
//
//    }

    @After
    public void tearDown(){
        File file = new File(this.test_file_name);
        file.delete();
    }
}
