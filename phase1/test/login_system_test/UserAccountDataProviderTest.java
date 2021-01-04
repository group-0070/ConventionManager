package login_system_test;

import login_system.*;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserAccountDataProviderTest {
    IUserAccountDataProvider userAccDaPr;
    UserService service;
    File file;
    @Before
    public void setUp() {
        String address = "assets/testAccounts.txt";
        service = new UserServiceEngine();
        userAccDaPr = new UserAccountDataProvider(service,address);
        file = new File(address);
        ArrayList<User> listOfUser = service.getListOfUserObjects();
        for(User u:listOfUser){
            service.removeUser(u.getUserID());
        }
    }
    @Test
    public void readNoFileExistTest() throws IOException {
        if (file.exists()) {
            file.delete();
        }
        userAccDaPr.read();
        BufferedReader textFile = new BufferedReader(new FileReader(file));
        String ln1 = textFile.readLine();
        String ln2 = textFile.readLine();
        assertTrue(service.getListOfUserObjects().isEmpty());
        assertEquals("Name,Password", ln1);
        assertNull(ln2);
    }

    @Test
    public void writeNoFileExist0Test() throws IOException {
        if (file.exists()) {
            file.delete();}
        userAccDaPr.writeToFile();
        BufferedReader textFile = new BufferedReader(new FileReader(file));
        String ln1 = textFile.readLine();
        String ln2 = textFile.readLine();
        assertEquals("Name,Password", ln1);
        assertNull(ln2);
    }

    @Test
    public void writeNoFileExist1Test() throws IOException {

        if (file.exists()) {
            file.delete();
        }
        service.addUser("att1","pass1");
        userAccDaPr.writeToFile();
        BufferedReader textFile = new BufferedReader(new FileReader(file));
        String ln1 = textFile.readLine();
        String ln2 = textFile.readLine();
        String ln3 = textFile.readLine();
        assertEquals("Name,Password", ln1);
        assertEquals("att1,pass1", ln2);
        assertNull(ln3);

    }

    @Test
    public void writeNoFileExistTest() throws IOException {
        if (file.exists()) {
            file.delete();
        }
        service.addUser("att1","pass1");
        service.addUser("att3","pass2");
        service.addUser("org1","pass3");
        service.addUser("spk1","pass4");
        service.addUser("att2","pass5");
        userAccDaPr.writeToFile();
        BufferedReader textFile = new BufferedReader(new FileReader(file));
        String ln1 = textFile.readLine();
        String ln2 = textFile.readLine();
        String ln3 = textFile.readLine();
        String ln4 = textFile.readLine();
        String ln5 = textFile.readLine();
        String ln6 = textFile.readLine();
        String ln7 = textFile.readLine();

        assertEquals("Name,Password", ln1);
        assertEquals("att1,pass1", ln2);
        assertEquals("att3,pass2", ln3);
        assertEquals("org1,pass3", ln4);
        assertEquals("spk1,pass4", ln5);
        assertEquals("att2,pass5", ln6);
        assertNull(ln7);

    }
    @Test
    public void writeToFileTest() throws IOException {
        service.addUser("att1","pass1");
        service.addUser("att2","pass2");
        service.addUser("org1","pass3");
        service.addUser("spk1","pass4");
        service.addUser("att3","pass5");
        userAccDaPr.writeToFile();
        BufferedReader textFile = new BufferedReader(new FileReader(file));
        String ln1 = textFile.readLine();
        String ln2 = textFile.readLine();
        String ln3 = textFile.readLine();
        String ln4 = textFile.readLine();
        String ln5 = textFile.readLine();
        String ln6 = textFile.readLine();
        String ln7 = textFile.readLine();
        assertEquals("Name,Password", ln1);
        assertEquals("att1,pass1", ln2);
        assertEquals("att2,pass2", ln3);
        assertEquals("org1,pass3", ln4);
        assertEquals("spk1,pass4", ln5);
        assertEquals("att3,pass5", ln6);
        assertNull(ln7);
    }
    @Test
    public void read0Test() {
        userAccDaPr.writeToFile();
        userAccDaPr.read();
        assertTrue(service.getListOfUserObjects().isEmpty());
    }
    @Test
    public void read1Test() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        writeText.write("Name,Password");
        writeText.newLine();
        writeText.write("att1,pass1");
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertTrue(service.userExists("att1"));
        assertTrue(service.userToPass("att1","pass1"));

    }
    @Test
    public void readTest() throws IOException {

        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Name,Password";
        data[1] = "att1,pass1";
        data[2] = "att2,pass2";
        data[3] = "org1,pass3";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertTrue(service.userToPass("att1","pass1"));
        assertTrue(service.userToPass("att2","pass2"));
        assertTrue(service.userToPass("org1","pass3"));
        assertTrue(service.userToPass("spk1","pass4"));
        assertTrue(service.userToPass("att3","pass5"));
    }


    @Test
    public void readWrongFormatHN() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Name";
        data[1] = "att1,pass1";
        data[2] = "att2,pass2";
        data[3] = "org1,pass3";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(0,service.getListOfUserObjects().size());
    }

    @Test
    public void readWrongFormatHO() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Password,Name";
        data[1] = "att1,pass1";
        data[2] = "att2,pass2";
        data[3] = "org1,pass3";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(0,service.getListOfUserObjects().size());
    }

    @Test
    public void readWrongFormatHC() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Something,name";
        data[1] = "att1,pass1";
        data[2] = "att2,pass2";
        data[3] = "org1,pass3";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(0,service.getListOfUserObjects().size());
    }

    @Test
    public void readWrongFormatHL() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "name,password";
        data[1] = "att1,pass1";
        data[2] = "att2,pass2";
        data[3] = "org1,pass3";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(0,service.getListOfUserObjects().size());
    }

    @Test
    public void readWrongFormatIN() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Name,Password";
        data[1] = "0923j,eoen0";
        data[2] = "att2,pass2";
        data[3] = "org1";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(0,service.getListOfUserObjects().size());
    }


    @Test
    public void readWrongFormatIl() throws IOException {
        BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
        String[] data = new String[6];
        data[0] = "Name,Password";
        data[3] = "i932, pswi3";
        data[2] = "att2,pass2";
        data[1] = "org1, pass1";
        data[4] = "spk1,pass4";
        data[5] = "att3,pass5";
        writeText.write(data[0]);
        for(int z = 1;z <data.length;z++){
            writeText.newLine();
            writeText.write(data[z]);
        }
        writeText.flush();
        writeText.close();
        userAccDaPr.read();
        assertEquals(4,service.getListOfUserObjects().size());
    }
}
