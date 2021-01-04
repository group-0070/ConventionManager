package login_system;

import java.io.*;
import java.util.regex.*;

/**
 * A gateway to read in and write existing user account information from an external .txt file
 */
public class UserAccountDataProvider implements IUserAccountDataProvider {
    private final String address;
    private final UserService user_service;

    /**
     * Initialize, and set the filepath and UserService Interface
     * @param user_service UserService interface for use-case to add and store users that are read in
     * @param address String representation of filepath of .txt file containing user accounts
     */
    public UserAccountDataProvider(UserService user_service, String address) {
        this.address = address;
        this.user_service=user_service;
    }

    /**
     * Reads in external file containing existing users, and calls method in
     * interface UserService for use-case class to create and store users read in
     */
    public void read() {
        File file = new File(address);
        try {
            if (!file.exists()) {   //create new file if file does not exist and populate with file header
                file.createNewFile();
                BufferedWriter writeText = new BufferedWriter(new FileWriter(file));
                writeText.write("Name,Password");
                writeText.flush();
                writeText.close();
            }
            if (verifyFileFormat()) {   //validate file format before reading
                BufferedReader textFile = new BufferedReader(new FileReader(file));
                if (textFile.readLine() != null) {
                    String line;
                    while ((line = textFile.readLine()) != null) {
                        line = line.replaceAll("\\s","");   //remove any white spaces between char's
                        String[] info = line.split(",");    //split info at commas
                        user_service.addUser(info[0], info[1]); //users read in are created and stored in use-case
                    }
                    textFile.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Something wrong with file address and/or file format incorrect");
        }
    }

    /**
     * Overwrites user accounts back to file, with updated list of user accounts (whether new users added or not)
     */
    @Override
    public void writeToFile() {
        File file = new File(address);
        try{
            if (file.exists()) {    //delete file and create a new file to write into, avoids duplicate data
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriterInitial = new FileWriter(file);
            BufferedWriter writeText = new BufferedWriter(fileWriterInitial);
            writeText.write("Name,Password");   //write header of file
            for (User user: user_service.getListOfUserObjects()) {
                writeText.newLine();
                writeText.write(user.getUserID() + "," + user.getUserPassword());   //write each user to each line
            }
             writeText.flush();
             writeText.close();
        }
        catch (IOException e) {
            System.out.println("data file is missing");
        }
    }

    /**
     * Verifies if the file being read in follows the specific format needed
     * returns boolean true or false if external file follows intended format
     */
    private boolean verifyFileFormat() {
        File file = new File(address);
        try {
            BufferedReader tf = new BufferedReader(new FileReader(file));
            String line = tf.readLine();
            String[] infoInitial = line.split(",");
            if (infoInitial.length != 2) return false;  //header must contain two strings
            else if (!infoInitial[0].equals("Name")) return false;  //Header must include Name as first field
            else if (!infoInitial[1].equals("Password")) return false;  //Header must include Password as second field
            line = tf.readLine();
            while (line!= null) {
                String[] info = line.split(",");
                if (info.length != 2) return false;     //each user account info on seperate lines
                else if (Pattern.matches("\\W", info[0])) return false; //read in usernames that are alphanum
                else if (Pattern.matches("\\W", info[1])) return false; //read in passwords that are alphanum
                line = tf.readLine();
            }
            tf.close();
        } catch (IOException e) {
            System.out.println("Incorrect File Format");
        }
        return true;
    }

}
