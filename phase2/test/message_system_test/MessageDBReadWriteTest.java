package message_system_test;

import message_system.MessageDatabaseReadWriter;
import message_system.MessageService;
import message_system.MessageServiceEngine;
import message_system.MessageStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageDBReadWriteTest {
    MessageDatabaseReadWriter messageDatabaseReadWriter;

    private MessageService messageService;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Before
    public void Setup() {
        this.messageService = new MessageServiceEngine();
        String address = "jdbc:sqlite:assets/UserDataTest.db";
        messageDatabaseReadWriter = new MessageDatabaseReadWriter(messageService, address);
    }

    @Test
    public void testReadWriteSingleMessage(){
        UUID id1 = UUID.randomUUID();
        LocalDateTime time1 = LocalDateTime.parse("2020-11-12 09:21:59", format);

        messageService.addMessage(id1, "test read single message", "user1", "user2",
                time1, MessageStatus.UNREAD);

        // No new messages are loaded from the empty db
        assertTrue(messageDatabaseReadWriter.read());
        // TODO: deal with READ and UNREAD problem
        assertEquals(1, messageService.showMessage("user1").size());

        messageDatabaseReadWriter.write();

        messageDatabaseReadWriter.displayAllRows("Messages", 6);

        messageDatabaseReadWriter.read();
        System.out.println(messageService.getMessageInfo().get(0));
        assertEquals(id1.toString(), messageService.getMessageInfo().get(1).get(0));
        assertEquals(2, messageService.showMessage("user1").size());
        // TODO: double check how controller/service class now work. Do they recognize the ownership of the class?
        assertEquals(2, messageService.showMessage("user2").size());
    }

    @Test
    public void testReadWriteMultipleMessages() {
        UUID id1 = UUID.randomUUID();
        String read1 = id1.toString()+"ç"+"user1 (2020-11-12 09:21:59): test read multi message 1 @user2 (Status: UNREAD)";
        String write1 = id1.toString()+"ç"+"user1çuser2çtest read multi message 1ç2020-11-12 09:21:59çUNREAD";
        LocalDateTime time1 = LocalDateTime.parse("2020-11-12 09:21:59", format);
        MessageStatus status1 = MessageStatus.valueOf("UNREAD");

        UUID id2 = UUID.randomUUID();
        String read2 = id1.toString()+"ç"+"user1 (2020-12-01 09:21:59): test read multi message 2 @user2 (Status: UNREAD)";
        String write2 = id1.toString()+"ç"+"user1çuser2çtest read multi message 2ç2020-12-01 09:21:59çUNREAD";
        LocalDateTime time2 = LocalDateTime.parse("2020-12-01 09:21:59", format);
        MessageStatus status2 = MessageStatus.valueOf("UNREAD");

        messageService.addMessage(id1, "test read multi message 1", "user1", "user2", time1, status1);
        messageService.addMessage(id2, "test read multi message 2", "user2", "user1", time2, status2);

        // No new messages are loaded from the empty db
        assertTrue(messageDatabaseReadWriter.read());

        assertEquals(2, messageService.showMessage("user1").size());

        messageDatabaseReadWriter.write();

        messageDatabaseReadWriter.displayAllRows("Messages", 6);

        messageDatabaseReadWriter.read();
//        assertEquals(read1, messageService.getMessageInfo().get(1));
        assertEquals(4, messageService.showMessage("user1").size());

        assertEquals(4, messageService.showMessage("user2").size());
    }

    @Test
    public void testReadWriteMessagesWithDelete() {
        UUID id1 = UUID.randomUUID();
        String read1 = id1.toString()+"ç"+"user1 (2020-11-12 09:21:59): test read multi message 1 @user2 (Status: UNREAD)";
        String write1 = id1.toString()+"ç"+"user1çuser2çtest read multi message 1ç2020-11-12 09:21:59çUNREAD";
        LocalDateTime time1 = LocalDateTime.parse("2020-11-12 09:21:59", format);
        MessageStatus status1 = MessageStatus.valueOf("UNREAD");

        UUID id2 = UUID.randomUUID();
        String read2 = id1.toString()+"ç"+"user1 (2020-12-01 09:21:59): test read multi message 2 @user2 (Status: UNREAD)";
        String write2 = id1.toString()+"ç"+"user1çuser2çtest read multi message 2ç2020-12-01 09:21:59çUNREAD";
        LocalDateTime time2 = LocalDateTime.parse("2020-12-01 09:21:59", format);
        MessageStatus status2 = MessageStatus.valueOf("UNREAD");

        UUID id3 = UUID.randomUUID();
        String read3 = id1.toString()+"ç"+"user1 (2020-12-01 09:21:59): test read multi message 3 @user2 (Status: UNREAD)";
        String write3 = id1.toString()+"ç"+"user1çuser2çtest read multi message 3ç2020-12-01 09:21:59çUNREAD";
        LocalDateTime time3 = LocalDateTime.parse("2020-12-01 09:21:59", format);
        MessageStatus status3 = MessageStatus.valueOf("UNREAD");

        messageService.addMessage(id1, "test read multi message 1", "user1", "user2", time1, status1);
        messageService.addMessage(id2, "test read multi message 2", "user2", "user1", time2, status2);
        messageService.addMessage(id3, "test read multi message 3", "user1", "user2", time3, status3);

        // No new messages are loaded from the empty db
        assertTrue(messageDatabaseReadWriter.read());

        assertEquals(3, messageService.getMessageInfo().size());

        messageDatabaseReadWriter.write();

        messageDatabaseReadWriter.displayAllRows("Messages", 6);

        messageService.deleteMessage(id2);

        assertEquals(2, messageService.getMessageInfo().size());

        messageDatabaseReadWriter.write();

        messageDatabaseReadWriter.read();

        assertEquals(4, messageService.getMessageInfo().size());
    }

    @After
    public void tearDown() {
        messageDatabaseReadWriter.deleteAllData("Messages");
        messageDatabaseReadWriter.deleteTable("Messages");
    }
}
