package user_system_test;

import database.DatabaseReadWriter;
import org.junit.*;
import static org.junit.Assert.*;

public class DatabaseTest extends DatabaseReadWriter {

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


    public DatabaseTest() {
        super("jdbc:sqlite:assets/UserDataTest.db");
    }

    @Before
    public void setUp() {
        if (tableExists("Accounts")) deleteTable("Accounts");
        if (tableExists("Events")) deleteTable("Events");
        if (tableExists("Messages")) deleteTable("Messages");
        if (tableExists("Rooms")) deleteTable("Rooms");
    }

    @Test
    public void createNewTables(){
        assertFalse(tableExists("Accounts"));
        assertFalse(tableExists("Events"));
        assertFalse(tableExists("Messages"));
        assertFalse(tableExists("Rooms"));

        createNewTable(sqlAcc);
        createNewTable(sqlEvent);
        createNewTable(sqlMessages);
        createNewTable(sqlRooms);

        assertTrue(tableExists("Accounts"));
        assertTrue(tableExists("Events"));
        assertTrue(tableExists("Messages"));
        assertTrue(tableExists("Rooms"));

        assertTrue(deleteTable("Accounts"));
        assertTrue(deleteTable("Events"));
        assertTrue(deleteTable("Messages"));
        assertTrue(deleteTable("Rooms"));
    }

    public boolean read(){
        return false;
    }

    public boolean write(){
        return false;
    }


}
