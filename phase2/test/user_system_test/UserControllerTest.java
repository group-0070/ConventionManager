package user_system_test;

import user_system.*;
import org.junit.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class UserControllerTest {

    private IUserController login;
    private String address;
    private final UserService userService0 = new UserServiceEngine();
    private final UserService userService = new UserServiceEngine();
    private final String username1 = "testNewUser1";
    private final String password1 = "pass1";
    private final String username2 = "testNewUser2";
    private final String password2 = "pass2";
    private final String username3 = "testNewUser3";
    private final String password3 = "pass3";
    private final String username4 = "testNewUser4";
    private final String password4 = "pass4";
    private UserDatabaseReadWriter urw;

    @Before
    public void setUp() {
        address = "jdbc:sqlite:assets/UserDataTest.db";
        IUserController login0 = new UserController(address, userService0);
        login0.addUser(username1, password1, UserType.ATTENDEE);
        login0.addUser(username2, password2, UserType.ORGANIZER);
        login0.addUser(username3, password3, UserType.SPEAKER);
        login0.addUser(username4, password4, UserType.ADMIN);
        login0.save();
        login = new UserController(address, userService);
        login.load();
        urw = new UserDatabaseReadWriter(userService, address);
    }

    @Test
    public void testCurrentUserValidated() {
        assertTrue(login.loginUser(username1, password1));
        assertEquals(username1, login.getCurrentUserID());
    }

    @Test
    public void testCurrentUserValidated2() {
        assertNull(login.getCurrentUserID());
        assertTrue(login.loginUser(username3, password3));
        assertEquals(username3, login.getCurrentUserID());
    }

    @Test
    public void testCurrentUserValidated3() {
        assertNull(login.getCurrentUserID());
        assertNull(login.getCurrentUserType());
        assertTrue(login.loginUser(username4, password4));
        assertEquals(login.userTypeFromName(username4), login.getCurrentUserType());
    }

    @Test
    public void testCurrentUserValidated4() {
        assertNull(login.getCurrentUserID());
        assertNull(login.getCurrentUserType());
        assertFalse(login.loginUser("UnknownUser", password4));
        assertNull(login.getCurrentUserID());
        assertNull(login.getCurrentUserType());
    }

    @Test
    public void addNewUser() {
        assertTrue(login.addUser("KnownTypeSpeaker", "1234test", UserType.SPEAKER));
        assertTrue(login.addUser("KnownTypeOrg", "1234test", UserType.ORGANIZER));
        assertTrue(login.addUser("KnownTypeAtt", "1234test", UserType.ATTENDEE));
        assertTrue(login.addUser("KnownTypeAdmin", "1234test", UserType.ADMIN));

        assertTrue(userService.userExists("KnownTypeSpeaker"));
        assertTrue(userService.userExists("KnownTypeOrg"));
        assertTrue(userService.userExists("KnownTypeAtt"));
        assertTrue(userService.userExists("KnownTypeAdmin"));

        assertEquals(UserType.ATTENDEE, userService.userTypeFromUsers("KnownTypeAtt"));
        assertEquals(UserType.ORGANIZER, userService.userTypeFromUsers("KnownTypeOrg"));
        assertEquals(UserType.SPEAKER, userService.userTypeFromUsers("KnownTypeSpeaker"));
        assertEquals(UserType.ADMIN, userService.userTypeFromUsers("KnownTypeAdmin"));

        assertTrue(login.removeUser("KnownTypeSpeaker"));
        assertTrue(login.removeUser("KnownTypeOrg"));
        assertTrue(login.removeUser("KnownTypeAtt"));
        assertTrue(login.removeUser("KnownTypeAdmin"));
    }

    @Test
    public void addNewUserIncorrectFormat(){
        assertFalse(login.addUser("KnownTypeInvalidCredential", ",$12 34$", UserType.ATTENDEE));
        assertFalse(login.addUser("$&, test", "KnownTypeInvalidCredential", UserType.ORGANIZER));
        assertFalse(login.addUser(" ", " ", UserType.ORGANIZER));
    }

    @Test
    public void addNewUserDuplicateInDB(){
        assertFalse(login.addUser(username1, password1, UserType.ATTENDEE));
        assertFalse(login.addUser(username3, password3, UserType.SPEAKER));
        assertFalse(login.addUser(username4, password4, UserType.ADMIN));
    }

    @Test
    public void addNewUserEmpty(){
        assertFalse(login.addUser("", "", UserType.ATTENDEE));
        assertFalse(login.addUser("", "pass", UserType.ADMIN));
        assertFalse(login.addUser("usertest", "", UserType.ORGANIZER));
        assertFalse(login.addUser(" ", "", UserType.SPEAKER));
    }

    @Test
    public void addNewUserOddInput(){
        assertTrue(login.addUser("test User", "pass test", UserType.ATTENDEE));
        assertTrue(login.addUser(" testUser2", " passtest", UserType.ATTENDEE));
        assertTrue(login.addUser(" testUser3 ", " passtest ", UserType.ORGANIZER));
        assertTrue(login.addUser("testUser4 ", "passtest ", UserType.SPEAKER));
        assertTrue(login.addUser(" t e s t  U s e r 5 " , " p a s s t e s t ", UserType.ATTENDEE));

        assertFalse(login.addUser(" t e s t  U s e r 5 " , " p a s s t e s t ", UserType.ADMIN));

        assertFalse(login.removeUser("testUser"));
        assertFalse(login.removeUser("testUser2"));
        assertFalse(login.removeUser("testUser3"));
        assertFalse(login.removeUser("testUser4"));
        assertFalse(login.removeUser("testUser5"));

        assertTrue(login.removeUser("test User"));
        assertTrue(login.removeUser(" testUser2"));
        assertTrue(login.removeUser(" testUser3 "));
        assertTrue(login.removeUser("testUser4 "));
        assertTrue(login.removeUser(" t e s t  U s e r 5 "));
    }

    @Test
    public void addNewUserOddInput2(){
        assertFalse(login.addUser("test_User", "pass test", UserType.ATTENDEE));
        assertFalse(login.addUser(" test-User2", " passtest", UserType.ATTENDEE));
        assertFalse(login.addUser(" testUser3% ", " passtest ", UserType.ORGANIZER));
        assertFalse(login.addUser("testUser4- ", "passtest ", UserType.SPEAKER));
        assertFalse(login.addUser(" t e s t.  U s e r 5 " , " p a s s t e s t ", UserType.ATTENDEE));
    }

    @Test
    public void addDuplicateUser(){
        assertTrue(login.addUser("xy", "z", UserType.ATTENDEE));
        assertFalse(login.addUser("xy", "z", UserType.ATTENDEE));

        assertTrue(userService.userExists("xy"));
        assertEquals(UserType.ATTENDEE, userService.userTypeFromUsers("xy"));

        assertTrue(login.addUser("DifferentType", "test1234", UserType.ATTENDEE));
        assertFalse(login.addUser("DifferentType", "test12345", UserType.ADMIN));
        assertFalse(login.addUser("DifferentType", "test123456", UserType.ORGANIZER));
        assertFalse(login.addUser("DifferentType", "test1234567", UserType.SPEAKER));

        assertTrue(userService.userExists("DifferentType"));
        assertEquals(UserType.ATTENDEE, userService.userTypeFromUsers("DifferentType"));

        assertTrue(login.addUser("DifferentCase", "test1234", UserType.ADMIN));
        assertTrue(login.addUser("differentCase", "test1234", UserType.ADMIN));
        assertFalse(login.addUser("DifferentCase", "test1234", UserType.ADMIN));
        assertFalse(login.addUser("differentCase", "test1234", UserType.SPEAKER));

        assertTrue(userService.userExists("DifferentCase"));
        assertEquals(UserType.ADMIN, userService.userTypeFromUsers("DifferentCase"));

        assertTrue(userService.userExists("differentCase"));
        assertEquals(UserType.ADMIN, userService.userTypeFromUsers("differentCase"));

        assertTrue(login.removeUser("xy"));
        assertTrue(login.removeUser("DifferentType"));
        assertTrue(login.removeUser("DifferentCase"));
        assertTrue(login.removeUser("differentCase"));

    }

    @Test
    public void userTypeFromUsername(){
        assertEquals(UserType.ATTENDEE,login.userTypeFromName(username1));
        assertEquals(UserType.ORGANIZER, login.userTypeFromName(username2));
        assertEquals(UserType.SPEAKER, login.userTypeFromName(username3));
        assertEquals(UserType.ADMIN, login.userTypeFromName(username4));
    }

    @Test
    public void userTypeFromUnknownUsername(){
        assertNull(login.userTypeFromName("testUnknown1"));
        assertNull(login.userTypeFromName("testUnknown2"));
        assertNull(login.userTypeFromName("test Unknown 3"));
    }

    @Test
    public void userTypeFromUnknownUsername2(){
        assertNull(login.userTypeFromName("test Invalid #1"));
        assertNull(login.userTypeFromName("test Invalid $2"));
        assertNull(login.userTypeFromName("_testInvalid3"));
        assertNull(login.userTypeFromName("test-Invalid4"));
        assertNull(login.userTypeFromName("test,Invalid,5"));
    }

    @Test
    public void testLoginCredentials(){
        assertTrue(login.loginUser(username1, password1));
        assertTrue(login.loginUser(username2, password2));
        assertTrue(login.loginUser(username3, password3));
        assertTrue(login.loginUser(username4, password4));
    }

    @Test
    public void testIncorrectLogin(){
        assertFalse(login.loginUser(username1, "4321"));
        assertFalse(login.loginUser(username2, "qwkdjn"));
        assertFalse(login.loginUser(username3, "--incorrect"));
        assertFalse(login.loginUser(username4, "test"));
        assertFalse(login.loginUser("random1", "random2"));
        assertFalse(login.loginUser("394943", "564356"));
        assertFalse(login.loginUser("break in text", "test this"));
    }

    @Test
    public void emptyInputLogin(){
        assertFalse(login.loginUser("", ""));
        assertFalse(login.loginUser(" ", " "));
        assertFalse(login.loginUser(username3, ""));
        assertFalse(login.loginUser("", password1));
    }

    @Test
    public void InvalidInputLogin2(){
        assertFalse(login.loginUser("$", "test"));
        assertFalse(login.loginUser("_", "test2"));
        assertFalse(login.loginUser("/", "test"));
        assertFalse(login.loginUser(" , ", password1));
    }

    @Test
    public void testUserRemoval(){
        assertFalse(login.removeUser("test01"));
        assertFalse(login.removeUser("test02"));
        assertFalse(login.removeUser("test03"));
        assertFalse(login.removeUser("test04"));

        assertTrue(login.addUser("test01", "pass01", UserType.ATTENDEE));
        assertTrue(login.addUser("test02", "pass02", UserType.ORGANIZER));
        assertTrue(login.addUser("test03", "pass03", UserType.SPEAKER));
        assertTrue(login.addUser("test04", "pass04", UserType.ADMIN));

        assertTrue(login.removeUser("test01"));
        assertTrue(login.removeUser("test02"));
        assertTrue(login.removeUser("test03"));
        assertTrue(login.removeUser("test04"));
    }

    @Test
    public void testCurrentUserInfo(){
        userService.setCurrentUser(username1);

        assertEquals(username1,login.getCurrentUserID());
        assertEquals(UserType.ATTENDEE,login.getCurrentUserType());
    }

    @Test
    public void testGetAllIdList() {
        List<String> temp = new ArrayList<>(login.getListOfAllIds());
        assertEquals(4, temp.size());

        for (int x = 0; x<login.getListOfAllIds().size(); x++) {
            assertEquals("testNewUser" + (x+1), temp.get(x));
        }
    }

    @Test
    public void testGetIdListAttendee() {
        List<String> temp = new ArrayList<>(login.getListOfIDsByType(UserType.ATTENDEE));
        assertEquals(1, temp.size());
        assertEquals("testNewUser1", temp.get(0));

        login.addUser("newUser1", "pass", UserType.ATTENDEE);
        login.addUser("newUser2", "pass", UserType.ADMIN);
        login.addUser("newUser3", "pass", UserType.ATTENDEE);
        login.addUser("newUser4", "pass", UserType.SPEAKER);

        List<String> temp2 = new ArrayList<>(login.getListOfIDsByType(UserType.ATTENDEE));
        assertEquals(3, temp2.size());

        assertEquals("testNewUser1", temp2.get(0));
        assertEquals("newUser1", temp2.get(1));
        assertEquals("newUser3", temp2.get(2));

        login.removeUser("newUser1");
        login.removeUser("newUser2");
        login.removeUser("newUser3");
        login.removeUser("newUser4");

    }

    @Test
    public void testGetIdListOrganizer() {
        List<String> temp = new ArrayList<>(login.getListOfIDsByType(UserType.ORGANIZER));
        assertEquals(1, temp.size());
        assertEquals("testNewUser2", temp.get(0));

        login.addUser("newUser1", "pass", UserType.ATTENDEE);
        login.addUser("newUser2", "pass", UserType.ORGANIZER);
        login.addUser("newUser3", "pass", UserType.ORGANIZER);
        login.addUser("newUser4", "pass", UserType.ORGANIZER);

        List<String> temp2 = new ArrayList<>(login.getListOfIDsByType(UserType.ORGANIZER));
        assertEquals(4, temp2.size());

        assertEquals("testNewUser2", temp2.get(0));
        assertEquals("newUser2", temp2.get(1));
        assertEquals("newUser3", temp2.get(2));
        assertEquals("newUser4", temp2.get(3));

        login.removeUser("newUser1");
        login.removeUser("newUser2");
        login.removeUser("newUser3");
        login.removeUser("newUser4");

    }

    @Test
    public void testGetIdListSpeaker() {
        List<String> temp = new ArrayList<>(login.getListOfIDsByType(UserType.SPEAKER));
        assertEquals(1, temp.size());
        assertEquals("testNewUser3", temp.get(0));

        login.addUser("newUser1", "pass", UserType.SPEAKER);
        login.addUser("newUser2", "pass", UserType.SPEAKER);
        login.addUser("newUser3", "pass", UserType.ADMIN);
        login.addUser("newUser4", "pass", UserType.SPEAKER);

        List<String> temp2 = new ArrayList<>(login.getListOfIDsByType(UserType.SPEAKER));
        assertEquals(4, temp2.size());

        assertEquals("testNewUser3", temp2.get(0));
        assertEquals("newUser1", temp2.get(1));
        assertEquals("newUser2", temp2.get(2));
        assertEquals("newUser4", temp2.get(3));

        login.removeUser("newUser1");
        login.removeUser("newUser2");
        login.removeUser("newUser3");
        login.removeUser("newUser4");

    }

    @Test
    public void testGetIdListAdmin() {
        List<String> temp = new ArrayList<>(login.getListOfIDsByType(UserType.ADMIN));
        assertEquals(1, temp.size());
        assertEquals("testNewUser4", temp.get(0));

        login.addUser("newUser1", "pass", UserType.SPEAKER);
        login.addUser("newUser2", "pass", UserType.ADMIN);
        login.addUser("newUser3", "pass", UserType.ADMIN);
        login.addUser("newUser4", "pass", UserType.SPEAKER);

        List<String> temp2 = new ArrayList<>(login.getListOfIDsByType(UserType.ADMIN));
        assertEquals(3, temp2.size());

        assertEquals("testNewUser4", temp2.get(0));
        assertEquals("newUser2", temp2.get(1));
        assertEquals("newUser3", temp2.get(2));

        login.removeUser("newUser1");
        login.removeUser("newUser2");
        login.removeUser("newUser3");
        login.removeUser("newUser4");

    }

    @Test
    public void saveNewData() {
        assertTrue(login.addUser("testNewUser5", "pass5", UserType.ADMIN));
        assertTrue(login.addUser("testNewUser6", "pass6", UserType.ATTENDEE));
        assertTrue(login.addUser("testNewUser7", "pass7", UserType.ORGANIZER));
        assertTrue(login.addUser("testNewUser8", "pass8", UserType.SPEAKER));

        assertTrue(login.save());

        UserService userService2 = new UserServiceEngine();
        IUserController login2 = new UserController(address, userService2);
        assertTrue(login2.load());
        assertEquals(8,userService2.getUserInfo().size());

        assertTrue(login2.loginUser("testNewUser5", "pass5"));
        assertTrue(login2.loginUser("testNewUser6", "pass6"));
        assertTrue(login2.loginUser("testNewUser7", "pass7"));
        assertTrue(login2.loginUser("testNewUser8", "pass8"));

        assertEquals(UserType.ADMIN,userService2.userTypeFromUsers("testNewUser5"));
        assertEquals(UserType.ATTENDEE,userService2.userTypeFromUsers("testNewUser6"));
        assertEquals(UserType.ORGANIZER,userService2.userTypeFromUsers("testNewUser7"));
        assertEquals(UserType.SPEAKER,userService2.userTypeFromUsers("testNewUser8"));

        assertTrue(login2.removeUser("testNewUser5"));
        assertTrue(login2.removeUser("testNewUser6"));
        assertTrue(login2.removeUser("testNewUser7"));
        assertTrue(login2.removeUser("testNewUser8"));
        assertTrue(login2.save());
    }

    @After
    public void exitAction() {
        urw.deleteAllData("Accounts");
    }

}
