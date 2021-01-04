package login_system_test;

import login_system.ILoginController;
import login_system.LoginController;
import org.junit.*;

import static org.junit.Assert.*;

public class LoginTest {

    private ILoginController login;
    private final String username1 = "att1";
    private final String password1 = "pass1";
    private final String username2 = "org1";
    private final String password2 = "pass3";
    private final String username3 = "spk1";
    private final String password3 = "pass4";

    @Before
    public void setUp() {
        String address = "assets/testAccounts.txt";
        login = new LoginController(address, username1, username2);
    }

    @Test
    public void addSpeakerUser(){
        assertFalse(login.addNewSpeakerUser("userNew", "1234"));
        assertFalse(login.userExistsInList("userNew"));

        assertTrue(login.addNewSpeakerUser("SpeakerTestUpper", "1234"));
        assertTrue(login.userExistsInList("SpeakerTestUpper"));

        assertTrue(login.addNewSpeakerUser("speakerTestLower", "1234"));
        assertTrue(login.userExistsInList("speakerTestLower"));

        //duplicate User
        assertFalse(login.addNewSpeakerUser("speakerTestLower", "1234"));
        assertFalse(login.addNewSpeakerUser("att2", "pass2"));

        login.removeUser("SpeakerTestUpper");
        login.removeUser("speakerTestLower");
        assertFalse(login.userExistsInList("SpeakerTestUpper"));
        assertFalse(login.userExistsInList("speakerTestLower"));

        assertFalse(login.addNewSpeakerUser("sTest,WithComma", "1234"));
        assertFalse(login.addNewSpeakerUser("sTestWith$'[}[*(!)@#^%&SpecialChar", "1234"));
        assertFalse(login.addNewSpeakerUser(",StartWithSpecialChar", "1234"));
        assertFalse(login.addNewSpeakerUser("sTestPassInput", ",1234"));
        assertFalse(login.addNewSpeakerUser("sTestPassInput", "12,34"));
        assertFalse(login.addNewSpeakerUser("sTestPassInput", "12,$*&!@34"));
        assertFalse(login.addNewSpeakerUser(" ", " "));
    }

    @Test
    public void userTypeFromUsername(){
        assertEquals(login.userTypeFromName(username1), "Attendee");
        assertEquals(login.userTypeFromName(username2), "Organizer");
        assertEquals(login.userTypeFromName(username3), "Speaker");
    }

    @Test
    public void testLoginCredentials(){
        assertTrue(login.loginUser(username1, password1));
        assertTrue(login.loginUser(username2, password2));
        assertTrue(login.loginUser(username3, password3));
    }

    @Test
    public void testCurrentUserInfo() {
        assertEquals(login.getCurrentUserId(), "att1");
        assertEquals(login.getCurrentUserType(), "Attendee");
    }

    @Test
    public void testIncorrectLogin(){
        assertFalse(login.loginUser(username1, password2));
        assertFalse(login.loginUser(username2, password3));
        assertFalse(login.loginUser(username3, password1));
        assertFalse(login.loginUser("random1", "random2"));
        assertFalse(login.loginUser("394943", "564356"));
        assertFalse(login.loginUser("break in text", "test this"));
    }

    @Test
    public void emptyInput(){
        assertFalse(login.loginUser("", ""));
        assertFalse(login.loginUser(" ", " "));
        assertFalse(login.loginUser(username3, ""));
        assertFalse(login.loginUser("", password1));
    }

}
