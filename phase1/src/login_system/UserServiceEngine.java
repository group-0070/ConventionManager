package login_system;

import java.util.ArrayList;

/**
 * A Use-Case class pertaining to User account management, creating and generating user accounts
 * and storing all user accounts, in relation to user Objects
 */
public class UserServiceEngine implements UserService {
    private final ArrayList<User> listOfUsers = new ArrayList<>();
    private final ArrayList<String> listOfAttendeeId = new ArrayList<>();
    private final ArrayList<String> listOfSpeakerId = new ArrayList<>();

    /**
     * Create user account(s) and add to list of user accounts
     * @param name String username of user account
     * @param password String password of user account
     * @return boolean true or false if user account added successfully
     */
    public boolean addUser(String name, String password){
        if (userExists(name)){  //refrains from duplicate users being stored
            return false;
        }
        //verify users follow convention with user types
        if (name.toLowerCase().startsWith("a") || name.toLowerCase().startsWith("o") || name.toLowerCase().startsWith("s")) {
            User newUser = new User(name, password);
            listOfUsers.add(newUser);
            if (userTypeFromUsers(name).equals("Attendee")) {   //stores attendees usernames in a list
                listOfAttendeeId.add(name);
            } else if (userTypeFromUsers(name).equals("Speaker")) { //stores speaker usernames in a list
                listOfSpeakerId.add(name);
            }
            return true;
        }
        return false;
    }

    /**
     * Remove the user if it exists
     * @param username String username of user account being removed
     */
    public void removeUser(String username) {
        if (userExists(username)){  //remove user from subsequent lists first
            if (userTypeFromUsers(username).equals("Attendee")){
                listOfAttendeeId.remove(username);
            }else if(userTypeFromUsers(username).equals("Speaker")){
                listOfSpeakerId.remove(username);
            }
        }
        for (User listOfUser : listOfUsers) {
            if (username.equals(listOfUser.getUserID())) {  //remove user from master list of user accounts last
                listOfUsers.remove(listOfUser);
                break;
            }
        }
    }

    /**
     * Gets list of all user accounts
     * @return ArrayList of all User accounts as user objects
     */
    public ArrayList<User> getListOfUserObjects(){
        return listOfUsers;
    }

    /**
     * Gets the user type as a String for a given username for user account
     * @param username String username of the user
     * @return the user type as a String (Attendee, Organizer, or Speaker)
     */
    public String userTypeFromUsers(String username) {
        for (User listOfUser : listOfUsers) {
            if (username.equals(listOfUser.getUserID())) {
                if (username.toLowerCase().startsWith("a")) return "Attendee";
                else if (username.toLowerCase().startsWith("o")) return "Organizer";
                else return "Speaker";
            }
        }
        return null;
    }

    /**
     * Checks if User with the given username exists
     * @param username the username of user account that is being checked
     * @return true or false whether User with the given username exists in list of users
     */
    public boolean userExists(String username) {
        for (User listOfUser : listOfUsers) {
            if (username.equals(listOfUser.getUserID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if username and password matches the credentials stored for that user account (if it exists)
     * @param username String username input for user account
     * @param password String password input for user account
     * @return True or False whether the username and the password matches an account in the list of user accounts
     */
    public boolean userToPass(String username, String password) {
        for (User listOfUser : listOfUsers) {
            if (username.equals(listOfUser.getUserID())) {
                if (password.equals(listOfUser.getUserPassword())) return true;
            }
        }
        return false;
    }

    /**
     * Gets all the attendee usernames as a list
     * @return ArrayList of String of all attendee usernames
     */
    public ArrayList<String> getListOfAttendeeId() { return listOfAttendeeId; }

    /**
     * Gets all the speaker usernames as a list
     * @return ArrayList of String of all speaker usernames
     */
    public ArrayList<String> getListOfSpeakerId() { return listOfSpeakerId; }

}

