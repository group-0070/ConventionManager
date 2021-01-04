package user_system_test;

import database.DatabaseReadWriter;
import org.junit.Before;
import org.junit.Test;
import user_system.UserType;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;

/**
 * Writes/Removes entries directly from DB
 */
public class DatabaseModificationTest extends DatabaseReadWriter {

    String sqlAcc = "CREATE TABLE IF NOT EXISTS Accounts (\n"
            + "	Username TEXT UNIQUE,\n"
            + "	Password TEXT,\n"
            + "	Type TEXT\n"
            + ");";

    String sqlEvent = "CREATE TABLE IF NOT EXISTS Events (\n"
            + "    eventID TEXT UNIQUE,\n"
            + "    eventType TEXT,\n"
            + "    eventCapacity TEXT,\n"
            + "    startTime TEXT,\n"
            + "    endTime TEXT,\n"
            + "    roomID TEXT,\n"
            + "    speakerIDs TEXT,\n"
            + "    attendeeIDs TEXT\n"
            + ");";

    String sqlRooms = "CREATE TABLE IF NOT EXISTS Rooms(\n"
            + "	roomID TEXT UNIQUE,\n"
            + "	roomCapacity INTEGER\n"
            + ");";

    String sqlMessages = "CREATE TABLE IF NOT EXISTS Messages (\n"
            + "	messageID TEXT UNIQUE,\n"
            + "	sender TEXT,\n"
            + "	receiver TEXT,\n"
            + "	message TEXT,\n"
            + "	time TEXT,\n"
            + "	status TEXT\n"
            + ");";

    public DatabaseModificationTest() {
        super("jdbc:sqlite:assets/UserDataTest.db");
    }

    @Before
    public void setUp() {
        if (!tableExists("Accounts")) createNewTable(sqlAcc);
        if (!tableExists("Events")) createNewTable(sqlEvent);
        if (!tableExists("Messages")) createNewTable(sqlMessages);
        if (!tableExists("Rooms")) createNewTable(sqlRooms);
    }

    @Test
    public void enterDataIntoDBAccount(){
        String sqlCommandAcc = "Accounts(Username,Password,Type)";
        String sqlCommandAccVal = "VALUES(?,?,?)";
        List<String> entriesAcc = new ArrayList<>();
        entriesAcc.add("att8");
        entriesAcc.add("pass8");
        entriesAcc.add(UserType.ATTENDEE.toString());

        assertTrue(insertIntoDB(entriesAcc, sqlCommandAcc, sqlCommandAccVal));

        assertTrue(deleteRowFromDB("Accounts", "Username", "att8"));
        assertTrue(deleteAllData("Accounts"));

        assertTrue(deleteTable("Accounts"));
        assertTrue(deleteTable("Events"));
        assertTrue(deleteTable("Messages"));
        assertTrue(deleteTable("Rooms"));
    }

    @Test
    public void enterDataIntoDBEvents() {
        String sqlCommandEvent = "Events(eventID,eventType,eventCapacity,startTime,endTime,roomID,speakerIDs,attendeeIDs)";
        String sqlCommandEventVal = "VALUES(?,?,?,?,?,?,?,?)";
        List<String> entriesEvent = new ArrayList<>();
        entriesEvent.add("testInsertDataEventID");
        entriesEvent.add("MULTI_SPEAKER_EVENT");
        entriesEvent.add("4");
        entriesEvent.add("startTime test:");
        entriesEvent.add("endTime test:");
        entriesEvent.add("roomID_test");
        entriesEvent.add("[testOne, testTwo, testThree]");
        entriesEvent.add("[att1, att2, att3]");

        assertTrue(insertIntoDB(entriesEvent, sqlCommandEvent, sqlCommandEventVal));

        assertTrue(deleteRowFromDB("Events", "eventID", "testInsertDataEventID"));
        assertTrue(deleteAllData("Events"));

        assertTrue(deleteTable("Accounts"));
        assertTrue(deleteTable("Events"));
        assertTrue(deleteTable("Messages"));
        assertTrue(deleteTable("Rooms"));
    }

    @Test
    public void enterDataIntoDBMessage() {
        String sqlCommandMsg = "Messages(messageID,sender,receiver,message,time,status)";
        String sqlCommandMsgVal = "VALUES(?,?,?,?,?,?)";
        List<String> entriesMsg = new ArrayList<>();
        entriesMsg.add("660123xTestID");
        entriesMsg.add("user1");
        entriesMsg.add("user2");
        entriesMsg.add("Test this message");
        entriesMsg.add("sentTimeTest:");
        entriesMsg.add("UNREAD");

        assertTrue(insertIntoDB(entriesMsg, sqlCommandMsg, sqlCommandMsgVal));
        assertTrue(deleteRowFromDB("Messages", "messageID", "660123xTestID"));

        assertTrue(deleteAllData("Messages"));

        assertTrue(deleteTable("Accounts"));
        assertTrue(deleteTable("Events"));
        assertTrue(deleteTable("Messages"));
        assertTrue(deleteTable("Rooms"));
    }

    @Test
    public void enterDataIntoDBRoom() {
        String sqlCommandRoom = "Rooms(roomID,roomCapacity)";
        String sqlCommandRoomVal = "VALUES(?,?)";
        List<String> entriesRoom = new ArrayList<>();
        entriesRoom.add("RoomIDTest");
        entriesRoom.add("3");

        assertTrue(insertIntoDB(entriesRoom, sqlCommandRoom, sqlCommandRoomVal));
        assertTrue(deleteRowFromDB("Rooms", "roomID", "RoomIDTest"));

        assertTrue(deleteAllData("Rooms"));

        assertTrue(deleteTable("Accounts"));
        assertTrue(deleteTable("Events"));
        assertTrue(deleteTable("Messages"));
        assertTrue(deleteTable("Rooms"));
    }

    public boolean read() {
        return false;
    }

    public boolean write() {
        return false;
    }
}
