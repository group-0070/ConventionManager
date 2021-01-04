package login_system_test;

import login_system.User;
import login_system.UserService;
import login_system.UserServiceEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserServiceTest {
    UserService service;
    @Before
    public void setUp() {
        service = new UserServiceEngine();
        ArrayList<User> listOfUser = service.getListOfUserObjects();
        for(User u:listOfUser){
            service.removeUser(u.getUserID());
        }
    }
    @Test
    public void addTestDuplicate(){
        service.addUser("att1","pass1");
        ArrayList<User> lis = service.getListOfUserObjects();
        assertEquals(lis.size(),1);
        assertEquals(lis.get(0).getUserID(),"att1");
        assertEquals(lis.get(0).getUserPassword(),"pass1");
        service.addUser("att1","pass1");
        assertEquals(lis.size(),1);
        service.addUser("att1","pas2r");
        assertEquals(lis.size(),1);
        String[][] info = {{"att2", "pass2"},{"org1","pass3"},{"spk1", "pass4"},{"att3","pass5"}};
        for(String[] a:info){
            service.addUser(a[0], a[1]);
        }
        lis = service.getListOfUserObjects();
        for(int i = 1; i < lis.size(); i++){
            assertEquals(lis.get(i).getUserID(),info[i-1][0]);
            assertEquals(lis.get(i).getUserPassword(),info[i-1][1]);
        }
    }

    @Test
    public void addTestInvalidUsername(){
        service.addUser("903j","pass1");
        ArrayList<User> lis = service.getListOfUserObjects();
        assertEquals(lis.size(),0);
        service.addUser("i390","pas2r");
        assertEquals(lis.size(),0);

    }

    @Test
    public void addUserSpeaker(){
        service.addUser("spk9", "pass1");
        assertEquals(1,service.getListOfSpeakerId().size());
    }

    @Test
    public void removeTest(){
        service.addUser("att1","pass1");
        service.addUser("att2","pass2");
        service.addUser("att3","pass5");
        service.addUser("org1","pass3");
        service.addUser("skp1","pass4");

        service.removeUser("att93");
        assertEquals(service.getListOfUserObjects().size(),5);

        service.removeUser("att1");
        assertEquals(service.getListOfUserObjects().size(),4);
        assertEquals(2,service.getListOfAttendeeId().size());
        assertFalse(service.userExists("att1"));
        assertTrue(service.userExists("att2"));

        service.removeUser("att1");
        assertEquals(service.getListOfUserObjects().size(),4);
        assertFalse(service.userExists("att1"));
        assertTrue(service.userExists("att2"));

        service.removeUser("att3");
        assertFalse(service.userExists("att3"));
        assertTrue(service.userExists("att2"));

        service.removeUser("skp1");
        assertEquals(service.getListOfUserObjects().size(),2);
        assertFalse(service.userExists("skp1"));

        service.removeUser("org1");
        assertFalse(service.userExists("org1"));
        assertTrue(service.userExists("att2"));
    }

    @Test
    public void listGetterTest(){
        String[][] info = {{"att1","pass1"}, {"att2", "pass2"},{"org1","pass3"},{"spk1", "pass4"},{"att3","pass5"}};
        for(String[] a:info){
            service.addUser(a[0],a[1]);
        }
        ArrayList<User> lis = service.getListOfUserObjects();
        assertEquals(lis.size(),info.length);
        for(int i = 0; i < lis.size(); i++){
            assertEquals(lis.get(i).getUserID(),info[i][0]);
            assertEquals(lis.get(i).getUserPassword(),info[i][1]);
        }
    }

    @Test
    public void userToPassTest(){
        String[][] info = {{"att1","pass1"}, {"att2", "pass2"},{"org1","pass3"},{"spk1", "pass4"}};
        for(String[] a:info){
            service.addUser(a[0],a[1]);
        }
        for(String[] a:info) {
            assertTrue(service.userToPass(a[0],a[1]));
        }
        assertFalse(service.userToPass(info[2][0],info[3][1]));
        assertFalse(service.userToPass("att3","pass5"));
        assertFalse(service.userToPass("org3","oejrs2"));
    }

    @Test
    public void userExistsTest(){
        assertFalse(service.userExists("att1"));
        String[][] info = {{"att1","pass1"}, {"att2", "pass2"},{"org1","pass3"},{"spk1", "pass4"}};
        for(String[] a:info){
            service.addUser(a[0],a[1]);
        }
        for(String[] a:info){
            assertTrue(service.userExists(a[0]));
        }
        assertFalse(service.userExists("att4"));
        assertFalse(service.userExists("org3"));
        assertFalse(service.userExists("skp21"));
    }

    @Test
    public void userTypeFromUsersTest(){
        assertNull(service.userTypeFromUsers("att1"));
        assertNull(service.userTypeFromUsers("org1"));
        assertNull(service.userTypeFromUsers("skp1"));
        assertFalse(service.userExists("att1"));
        String[][] info = {{"att1","pass1"}, {"att2", "pass2"},{"org1","pass3"},{"spk1", "pass4"},{"att3","pass5"}};
        for(String[] a:info){
            service.addUser(a[0],a[1]);
        }
        assertEquals("Attendee", service.userTypeFromUsers("att1"));
        assertEquals("Attendee", service.userTypeFromUsers("att2"));
        assertEquals("Attendee", service.userTypeFromUsers("att3"));
        assertEquals("Organizer", service.userTypeFromUsers("org1"));
        assertEquals("Speaker", service.userTypeFromUsers("spk1"));
        assertNotEquals("Organizer", service.userTypeFromUsers("att1"));
        assertNotEquals("Attendee", service.userTypeFromUsers("spk1"));
        assertNotEquals("Attendee", service.userTypeFromUsers("org1"));
        assertNull(service.userTypeFromUsers("att32"));


    }


}
