package user_system_test;

import user_system.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserDatabaseReadWriterTest {

    UserDatabaseReadWriter userAccDaPr;
    UserService service;

    @Before
    public void setUp() {
        String address = "jdbc:sqlite:assets/UserDataTest.db";
        service = new UserServiceEngine();
        userAccDaPr = new UserDatabaseReadWriter(service,address);
        List<List<String>> listOfUser = service.getUserInfo();
        for(List<String> u:listOfUser){
            service.removeUser(u.get(0));
        }
    }

    @Test
    public void incorrectUserIdInDB() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));
        assertTrue(userAccDaPr.read());
        assertTrue(userAccDaPr.tableExists("Accounts"));
        service.addUser("bad Input", "pass", UserType.ATTENDEE);
        service.addUser(",badInput", "pass", UserType.ORGANIZER);
        service.addUser("bad? input", "pass", UserType.SPEAKER);
        service.addUser("bad_input", "pass", UserType.ADMIN);
        service.addUser("bad%input", "pass", UserType.ADMIN);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("bad Input"));
        assertTrue(service.removeUser(",badInput"));
        assertTrue(service.removeUser("bad? input"));
        assertTrue(service.removeUser("bad_input"));
        assertTrue(service.removeUser("bad%input"));

        assertTrue(userAccDaPr.read());
        assertEquals(0,service.getUserInfo().size());

        userAccDaPr.displayAllRows("Accounts", 3);

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));
    }

    @Test
    public void incorrectUserPassInDB() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));

        assertTrue(userAccDaPr.read());
        assertTrue(userAccDaPr.tableExists("Accounts"));

        service.addUser("badInput", "!pass", UserType.ATTENDEE);
        service.addUser("badInput2", "pass%", UserType.SPEAKER);
        service.addUser("badInput3", "pa_ss", UserType.ORGANIZER);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("badInput"));
        assertTrue(service.removeUser("badInput2"));
        assertTrue(service.removeUser("badInput3"));

        assertTrue(userAccDaPr.read());
        assertEquals(0,service.getUserInfo().size());

        userAccDaPr.displayAllRows("Accounts", 3);

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));

    }

    @Test
    public void userInfoListTest() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));

        assertTrue(userAccDaPr.read());
        assertTrue(userAccDaPr.tableExists("Accounts"));

        service.addUser("test1", "pass1", UserType.ATTENDEE);
        service.addUser("test2", "pass2", UserType.SPEAKER);
        service.addUser("test3", "pass3", UserType.ORGANIZER);
        service.addUser("test4", "pass4", UserType.ADMIN);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("test1"));
        assertTrue(service.removeUser("test2"));
        assertTrue(service.removeUser("test3"));
        assertTrue(service.removeUser("test4"));

        assertTrue(userAccDaPr.read());
        assertEquals(4,service.getUserInfo().size());

        for (int x = 0; x<service.getUserInfo().size(); x++) {
            assertEquals("test" + (x+1), service.getUserInfo().get(x).get(0));
            assertEquals("pass" + (x+1), service.getUserInfo().get(x).get(1));
            assertEquals(service.userTypeFromUsers("test"+(x+1)).toString(), service.getUserInfo().get(x).get(2));
        }

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));

    }

    @Test
    public void writeTableDoesNotExist() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));
        assertFalse(userAccDaPr.tableExists("Accounts"));

        service.addUser("testUser1", "pass1", UserType.ATTENDEE);
        service.addUser("testUser2", "pass2", UserType.ORGANIZER);
        service.addUser("testUser3", "pass3", UserType.SPEAKER);
        service.addUser("testUser4", "pass4", UserType.ADMIN);

        assertTrue(userAccDaPr.write());
        assertTrue(userAccDaPr.tableExists("Accounts"));

        assertTrue(service.removeUser("testUser1"));
        assertTrue(service.removeUser("testUser2"));
        assertTrue(service.removeUser("testUser3"));
        assertTrue(service.removeUser("testUser4"));

        assertTrue(userAccDaPr.read());

        assertEquals(4, service.getUserInfo().size());
        assertEquals(UserType.ATTENDEE, service.userTypeFromUsers("testUser1"));
        assertEquals(UserType.ORGANIZER, service.userTypeFromUsers("testUser2"));
        assertEquals(UserType.SPEAKER, service.userTypeFromUsers("testUser3"));
        assertEquals(UserType.ADMIN, service.userTypeFromUsers("testUser4"));
        assertTrue(service.validateCredentials("testUser1", "pass1"));
        assertTrue(service.validateCredentials("testUser2", "pass2"));
        assertTrue(service.validateCredentials("testUser3", "pass3"));
        assertTrue(service.validateCredentials("testUser4", "pass4"));

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));
    }

    @Test
    public void writeTableExists() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));
        String sqlAcc = "CREATE TABLE IF NOT EXISTS Accounts (\n"
                + "	Username TEXT UNIQUE,\n"
                + "	Password TEXT,\n"
                + "	Type TEXT\n"
                + ");";
        userAccDaPr.createNewTable(sqlAcc);
        assertTrue(userAccDaPr.tableExists("Accounts"));

        service.addUser("testUser1", "pass1", UserType.ATTENDEE);
        service.addUser("testUser2", "pass2", UserType.ORGANIZER);
        service.addUser("testUser3", "pass3", UserType.SPEAKER);
        service.addUser("testUser4", "pass4", UserType.ADMIN);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("testUser1"));
        assertTrue(service.removeUser("testUser2"));
        assertTrue(service.removeUser("testUser3"));
        assertTrue(service.removeUser("testUser4"));

        assertTrue(userAccDaPr.read());

        assertEquals(4, service.getUserInfo().size());
        assertEquals(UserType.ATTENDEE, service.userTypeFromUsers("testUser1"));
        assertEquals(UserType.ORGANIZER, service.userTypeFromUsers("testUser2"));
        assertEquals(UserType.SPEAKER, service.userTypeFromUsers("testUser3"));
        assertEquals(UserType.ADMIN, service.userTypeFromUsers("testUser4"));
        assertTrue(service.validateCredentials("testUser1", "pass1"));
        assertTrue(service.validateCredentials("testUser2", "pass2"));
        assertTrue(service.validateCredentials("testUser3", "pass3"));
        assertTrue(service.validateCredentials("testUser4", "pass4"));

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));
    }

    @Test
    public void readTest() {
        if (userAccDaPr.tableExists("Accounts")) assertTrue(userAccDaPr.deleteTable("Accounts"));
        assertTrue(userAccDaPr.read());
        assertTrue(userAccDaPr.tableExists("Accounts"));
        service.addUser("testUser1", "pass1", UserType.ATTENDEE);
        service.addUser("testUser2", "pass2", UserType.ORGANIZER);
        service.addUser("testUser3", "pass3", UserType.SPEAKER);
        service.addUser("testUser4", "pass4", UserType.ADMIN);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("testUser1"));
        assertTrue(service.removeUser("testUser2"));
        assertTrue(service.removeUser("testUser3"));
        assertTrue(service.removeUser("testUser4"));

        assertTrue(userAccDaPr.read());
        assertEquals(4,service.getUserInfo().size());

        userAccDaPr.displayAllRows("Accounts", 3);

        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));
    }

    @Test
    public void dbTestReadWriteBadInput() {

        service.addUser("?testBadInput1", "pass1", UserType.ATTENDEE);
        service.addUser(",testBadInput2", "pass2", UserType.ORGANIZER);
        service.addUser("testBadInput3,", "pass3", UserType.SPEAKER);
        service.addUser("testBadInput4$", "pass4", UserType.ADMIN);
        service.addUser("InvalidPass1", "$ pass1", UserType.ATTENDEE);
        service.addUser("InvalidPass2", ",pass2", UserType.ORGANIZER);
        service.addUser("InvalidPass3", "pass3,", UserType.SPEAKER);
        service.addUser("InvalidPass4", "pass4*", UserType.ADMIN);

        assertTrue(userAccDaPr.write());

        assertTrue(service.removeUser("?testBadInput1"));
        assertTrue(service.removeUser(",testBadInput2"));
        assertTrue(service.removeUser("testBadInput3,"));
        assertTrue(service.removeUser("testBadInput4$"));
        assertTrue(service.removeUser("InvalidPass1"));
        assertTrue(service.removeUser("InvalidPass2"));
        assertTrue(service.removeUser("InvalidPass3"));
        assertTrue(service.removeUser("InvalidPass4"));

        assertTrue(userAccDaPr.read());
        assertEquals(0,service.getUserInfo().size());
        assertTrue(userAccDaPr.deleteAllData("Accounts"));
        assertTrue(userAccDaPr.deleteTable("Accounts"));

    }

}
