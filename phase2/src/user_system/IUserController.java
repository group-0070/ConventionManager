package user_system;

import java.util.List;

/**
 * Interface to model the execution of the login system
 */
public interface IUserController {

    /**
     * Gets the user type for a specific user
     * @param name String username of user account intended to find user type for
     * @return UserType enum user Type for user
     */
    UserType userTypeFromName(String name);

    /**
     * Creates new user account - for use by Organizer accounts only
     * @param username String username of the user account being made
     * @param password String password of the user account being made
     * @param usertype UserType enum user type of user account being made
     * @return boolean true or false if user credentials are valid and creates account
     */
    boolean addUser(String username, String password, UserType usertype);

    /**
     * Commences the process to attempt to log user in
     * @param un String username of the user account being logged into
     * @param pw String password of the user account being logged into
     * @return boolean true or false if user credentials successfully log user in
     */
    boolean loginUser(String un, String pw);

    /**
     * Removes a particular user
     * @param username String username of user account being removed
     * @return boolean true or false if removal successful
     */
    boolean removeUser(String username);

    /**
     * Writes to external DB with updated user accounts list before exiting program
     * @return boolean true or false if write to DB successful
     */
    boolean save();

    /**
     * Calls read method in gateway, loading all local data (i.e. existing user accounts)
     * @return boolean true or false if read method executed successfully
     */
    boolean load();

    /**
     * Gets Current User's Type stored in use-case
     * @return UserType enum of current User's Type
     */
    UserType getCurrentUserType();

    /**
     * Gets stored current user id from use-case
     * @return String user ID of current user logged in
     */
    String getCurrentUserID();

    /**
     * Gets a list of all Ids for all Users
     * @return List of Strings of all user ids
     */
    List<String> getListOfAllIds();

    /**
     * Gets a List of IDs based on user type
     * @param type UserType enum for user type
     * @return List of Strings of all user id with given user type
     */
    List<String> getListOfIDsByType(UserType type);
}
