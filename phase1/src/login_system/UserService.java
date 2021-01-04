package login_system;

import java.util.ArrayList;

/**
 * A representation of model object UserServiceEngine, allowing user account management
 * to add, remove, or get information of user account(s)
 */
public interface UserService {

    /**
     * Add User account
     * @param name String username of user account
     * @param password String password of user account
     * @return boolean true or false if user account is added successfully
     */
    boolean addUser(String name, String password);

    /**
     * Gets the user type from a given username of an account
     * @param username String username of user account
     * @return String for user type (Attendee, Organizer, or Speaker)
     */
    String userTypeFromUsers(String username);

    /**
     * Gets list of all User accounts
     * @return ArrayList of User Objects
     */
    ArrayList<User> getListOfUserObjects();

    /**
     * Checks if username matches a given password
     * @param username String username of user account
     * @param password String password of user account
     * @return boolean true or false if the username matches the password
     */
    boolean userToPass(String username, String password);

    /**
     * Verifies if the user exists
     * @param username String username of user account
     * @return boolean true or false if user exists
     */
    boolean userExists(String username);

    /**
     * Removes a user account
     * @param username String username of intended user account to remove
     */
    void removeUser(String username);

    /**
     * Gets list of all Attendee usernames
     * @return ArrayList of String usernames of all attendees
     */
    ArrayList<String> getListOfAttendeeId();

    /**
     * Gets list of all Speaker usernames
     * @return ArrayList of String usernames of all speakers
     */
    ArrayList<String> getListOfSpeakerId();

}
