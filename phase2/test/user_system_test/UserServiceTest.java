package user_system_test;

import user_system.UserService;
import user_system.UserServiceEngine;
import user_system.UserType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserServiceTest {
    UserService service;
    @Before
    public void setUp() {
        service = new UserServiceEngine();
        List<List<String>> listOfUser = service.getUserInfo();
        for(List<String> u:listOfUser){
            assertTrue(service.removeUser(u.get(0)));
        }
    }

    @Test
    public void addUserAllType(){
        service.addUser("testUser", "pass1", UserType.ATTENDEE);
        service.addUser("testUser2", "pass2", UserType.ORGANIZER);
        service.addUser("testUser3", "pass3", UserType.SPEAKER);
        service.addUser("testUser4", "pass4", UserType.ADMIN);
        assertEquals(4,service.getUserInfo().size());
    }

    @Test
    public void removeTest(){
        service.addUser("att1","pass1", UserType.ATTENDEE);
        service.addUser("att2","pass2", UserType.ATTENDEE);
        service.addUser("att3","pass5", UserType.ADMIN);
        service.addUser("org1","pass3", UserType.ORGANIZER);
        service.addUser("skp1","pass4", UserType.ORGANIZER);

        assertFalse(service.removeUser("att93"));
        assertEquals(service.getUserInfo().size(),5);

        assertTrue(service.removeUser("att1"));
        assertEquals(service.getUserInfo().size(),4);
        assertEquals(1,service.getListOfIDsByType(UserType.ATTENDEE).size());
        assertFalse(service.userExists("att1"));
        assertTrue(service.userExists("att2"));

        assertFalse(service.removeUser("att1"));
        assertEquals(service.getUserInfo().size(),4);
        assertFalse(service.userExists("att1"));
        assertTrue(service.userExists("att2"));

        assertEquals(1,service.getListOfIDsByType(UserType.ADMIN).size());
        assertTrue(service.removeUser("att3"));
        assertFalse(service.userExists("att3"));
        assertTrue(service.userExists("att2"));

        assertTrue(service.removeUser("skp1"));
        assertEquals(service.getUserInfo().size(),2);
        assertFalse(service.userExists("skp1"));

        assertTrue(service.removeUser("org1"));
        assertFalse(service.userExists("org1"));
        assertTrue(service.userExists("att2"));
    }

    @Test
    public void currentUserIDTest() {
        service.addUser("test1", "pass", UserType.ADMIN);
        service.setCurrentUser("test1");
        assertEquals("test1", service.getCurrentUserID());
    }

    @Test
    public void currentUserTypeTest() {
        service.addUser("test1", "pass", UserType.ADMIN);
        service.setCurrentUser("test1");
        assertEquals(UserType.ADMIN, service.getCurrentUserType());
    }

    @Test
    public void currentUserInfoOverwriteTest() {
        service.addUser("test1", "pass", UserType.ADMIN);
        service.setCurrentUser("test1");
        assertEquals("test1", service.getCurrentUserID());
        assertEquals(UserType.ADMIN, service.getCurrentUserType());

        service.addUser("test2", "pass2", UserType.ORGANIZER);
        service.setCurrentUser("test2");
        assertEquals("test2", service.getCurrentUserID());
        assertEquals(UserType.ORGANIZER, service.getCurrentUserType());
    }

    @Test
    public void currentUserInfoInvalidTest() {
        service.addUser("test1", "pass", UserType.ADMIN);
        assertNull(service.getCurrentUserID());
        assertNull(service.getCurrentUserType());
    }

    @Test
    public void listGetterTest(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<List<String>> lis = service.getUserInfo();
        assertEquals(lis.size(),info.length);
        for(int i = 0; i < lis.size(); i++){
            assertEquals(lis.get(i).get(0),info[i][0]);
            assertEquals(lis.get(i).get(1),info[i][1]);
        }
    }

    @Test
    public void listGetterTest2(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<List<String>> lis = service.getUserInfo();
        for(int i = 0; i < lis.size(); i++){
            assertEquals(lis.get(i).get(0),info[i][0]);
            assertEquals(lis.get(i).get(1),info[i][1]);
            assertEquals(lis.get(i).get(2),info[i][2]);
        }
    }

    @Test
    public void listGetterTestStringRep(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<List<String>> lis = service.getUserInfo();
        assertEquals(lis.size(),info.length);
    }

    @Test
    public void listGetterTestAttendee(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<String> lis = service.getListOfIDsByType(UserType.ATTENDEE);
        assertEquals(lis.size(),info.length-3);
        assertEquals(lis.get(0), info[0][0]);
        assertEquals(lis.get(1), info[1][0]);
    }

    @Test
    public void listGetterTestSpeaker(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<String> lis = service.getListOfIDsByType(UserType.SPEAKER);
        assertEquals(lis.size(),info.length-4);
        assertEquals(lis.get(0), info[3][0]);
    }

    @Test
    public void listGetterTestAdmin(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<String> lis = service.getListOfIDsByType(UserType.ADMIN);
        assertEquals(lis.size(),info.length-4);
        assertEquals(lis.get(0), info[4][0]);
    }

    @Test
    public void listGetterTestOrg(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<String> lis = service.getListOfIDsByType(UserType.ORGANIZER);
        assertEquals(lis.size(),info.length-4);
        assertEquals(lis.get(0), info[2][0]);
    }

    @Test
    public void listGetterTestAllUserTypeIDs(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        List<List<String>> lis = service.getUserInfo();
        assertEquals(lis.size(),info.length);
        for (int x = 0; x<lis.size(); x++) {
            assertEquals(lis.get(x).get(0), info[x][0]);
        }
    }

    @Test
    public void validationTest(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        for(String[] a:info) {
            assertTrue(service.validateCredentials(a[0],a[1]));
        }
    }

    @Test
    public void validationTest2(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }

        assertFalse(service.validateCredentials(info[2][0],info[3][1]));
        assertFalse(service.validateCredentials("att3","pass1"));
        assertFalse(service.validateCredentials("att5","pass1"));
        assertFalse(service.validateCredentials("org3","oejrs2"));
    }

    @Test
    public void validationTest3(){
        assertFalse(service.validateCredentials("!badInput","pass"));
        assertFalse(service.validateCredentials("bad_input","pass"));
        assertFalse(service.validateCredentials("badInput","?pass"));
        assertFalse(service.validateCredentials("badInput","pa_ss"));
        assertFalse(service.validateCredentials("badInput","pass!"));
    }

    @Test
    public void userExistsTest(){
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        for(String[] a:info){
            assertTrue(service.userExists(a[0]));
        }
    }

    @Test
    public void userExistsEmptyTest(){
        assertFalse(service.userExists(""));
        assertFalse(service.userExists(" "));
    }

    @Test
    public void userExistsTest2() {
        assertFalse(service.userExists("att1"));
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"att3","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }

        assertFalse(service.userExists("att4"));
        assertFalse(service.userExists("org3"));
        assertFalse(service.userExists("skp12"));
    }

    @Test
    public void emptyUserTypeFromUsersTest(){
        assertNull(service.userTypeFromUsers(""));
        assertNull(service.userTypeFromUsers(" "));
    }

    @Test
    public void incorrectUserTypeFromUsersTest(){
        assertNull(service.userTypeFromUsers("testUnknown"));
        assertNull(service.userTypeFromUsers("testUnknown2"));
    }

    @Test
    public void incorrectInputUserTypeFromUsersTest(){
        assertNull(service.userTypeFromUsers(",!teset"));
        assertNull(service.userTypeFromUsers("test_this"));
    }

    @Test
    public void userTypeFromUsersTest(){
        assertFalse(service.userExists("att1"));
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"testUser","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }
        assertEquals(UserType.ATTENDEE, service.userTypeFromUsers("att1"));
        assertEquals(UserType.ATTENDEE, service.userTypeFromUsers("att2"));
        assertEquals(UserType.ADMIN, service.userTypeFromUsers("testUser"));
        assertEquals(UserType.ORGANIZER, service.userTypeFromUsers("org1"));
        assertEquals(UserType.SPEAKER, service.userTypeFromUsers("spk1"));
    }

    @Test
    public void userTypeFromUsersTest2() {
        assertNull(service.userTypeFromUsers("att1"));
        assertNull(service.userTypeFromUsers("org1"));
        assertNull(service.userTypeFromUsers("skp1"));

        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"testUser","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }

        assertNotEquals(UserType.SPEAKER, service.userTypeFromUsers("att1"));
        assertNotEquals(UserType.ATTENDEE, service.userTypeFromUsers("spk1"));
        assertNotEquals(UserType.ADMIN, service.userTypeFromUsers("org1"));
        assertNotEquals(UserType.ORGANIZER, service.userTypeFromUsers("testUser"));
    }

    @Test
    public void userTypeFromUsersTest3() {
        String[][] info = {{"att1","pass1", "ATTENDEE"}, {"att2", "pass2", "ATTENDEE"},{"org1","pass3", "ORGANIZER"},{"spk1", "pass4", "SPEAKER"},{"testUser","pass5", "ADMIN"}};
        for(String[] a:info){
            service.addUser(a[0],a[1], UserType.valueOf(a[2]));
        }

        assertNull(service.userTypeFromUsers("att12"));
        assertNull(service.userTypeFromUsers("test User"));
        assertNull(service.userTypeFromUsers("$invalidInput"));
    }

}
