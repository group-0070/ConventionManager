package user_system;

import java.util.List;

/**
 * A representation of model object UserServiceEngine, allowing user account management
 * to add, remove, or get information of user account(s)
 */
public interface UserService {

    /**
     * Sets the current user logged in
     * @param userID String of current user ID
     */
    void setCurrentUser(String userID);

    /**
     * Gets the current user's ID, that is logged in
     * @return String User ID for user currently logged in
     */
    String getCurrentUserID();

    /**
     * Gets the current user's type, that is logged in
     * @return UserType enum for current user's type
     */
    UserType getCurrentUserType();

    /**
     * Create user account and add to list of user accounts
     * @param name String username of user account
     * @param password String password of user account
     * @param type UserType enum of user type
     */
    void addUser(String name, String password, UserType type);

    /**
     * Gets the user type as a String for a given username for user account
     * @param username String username of the user
     * @return UserType enum user type
     */
    UserType userTypeFromUsers(String username);

    /**
     * Verifies if username and password matches the credentials stored for that user account (if it exists)
     * @param username String username input for user account
     * @param password String password input for user account
     * @return True or False whether the username and the password matches an account in the list of user accounts
     */
    boolean validateCredentials(String username, String password);

    /**
     * Checks if User with the given username exists
     * @param username the username of user account that is being checked
     * @return true or false whether User with the given username exists in list of users
     */
    boolean userExists(String username);

    /**
     * Remove the user if it exists
     * @param username String username of user account being removed
     * @return boolean true or false if user removed
     */
    boolean removeUser(String username);

    /**
     * Generates list of all user accounts with each users list of info
     * @return 2d array with list of user accounts - each entry is a list with user account info
     */
    List<List<String>> getUserInfo();

    /**
     * Generates a list of all User IDs with given user type
     * @param userType UserType enum for user type
     * @return List of String representation of all User IDs with given Type
     */
    List<String> getListOfIDsByType(UserType userType);
}
