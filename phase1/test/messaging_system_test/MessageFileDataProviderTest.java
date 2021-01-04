package messaging_system_test;

import messaging_system.*;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MessageFileDataProviderTest {

    private final String test_file_name = "temp_messages.txt";
    private MessageService testMessageService = new MessageServiceEngine();
    private IMessageDataProvider testProvider = new MessageFileDataProvider(this.test_file_name, this.testMessageService);

    @Before
    public void Setup() {
        File file = new File(test_file_name);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testReadSingleMessage() throws IOException{
        String read1 = "user1 (2020-11-12 09:21:59): test read single message @user2";
        String write1 = "user1çuser2çtest read single messageç2020-11-12 09:21:59";
        FileWriter fw = new FileWriter(test_file_name, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(write1);
        pw.flush();
        pw.close();

        testProvider.read();
        assertEquals(1, testMessageService.getStringMessage().size());
//        System.out.println(testMessageService.getStringMessage());
        assertEquals(read1, testMessageService.getStringMessage().get(0));
    }

    @Test
    public void testReadMultipleMessages() throws IOException{
        String read1 = "user1 (2020-11-12 09:21:59): test read single message @user2";
        String write1 = "user1çuser2çtest read single messageç2020-11-12 09:21:59";
        String read2 = "org1 (2020-11-12 09:54:55): test read multiple messages @user1";
        String write2 = "org1çuser1çtest read multiple messagesç2020-11-12 09:54:55";
        FileWriter fw = new FileWriter(test_file_name, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(write1);
        pw.println(write2);
        pw.flush();
        pw.close();

        testProvider.read();
        assertEquals(2, testMessageService.getStringMessage().size());
//        System.out.println(testMessageService.getStringMessage());
        assertEquals(read1, testMessageService.getStringMessage().get(0));
        assertEquals(read2, testMessageService.getStringMessage().get(1));
    }

    @Test
    public void testWriteSingleMessage() throws IOException {
//        Message m1 = new Message("user1", "user2", "user1 to user2");
        testMessageService.sendMessage("user1 to user2", "user1", "user2");
        testProvider.write();
        assertEquals(testMessageService.getStringMessage().size(), 1);

        BufferedReader reader = new BufferedReader(new FileReader(test_file_name));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();

        assertEquals(1, lines);
    }

    @Test
    public void testWriteMultipleMessages() throws IOException {
        testMessageService.sendMessage("send message", "Nancy", "Lily");
        List<String> multicastIds = new ArrayList<>();
        multicastIds.add("Jan");
        multicastIds.add("Nancy");
        multicastIds.add("Lily");
        testMessageService.multicastMessage("multicast message", "org1", multicastIds);
        testProvider.write();
        assertEquals(4, testMessageService.getStringMessage().size());

        BufferedReader reader = new BufferedReader(new FileReader(test_file_name));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();

        assertEquals(4, lines);
    }

    @After
    public void tearDown() {
        File file = new File(this.test_file_name);
        file.delete();
    }
}
