package user_system;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the execution of the login system
 */
public class UserController implements IUserController {

    private final UserService user_service;
    private final UserDatabaseReadWriter user_accounts;

    /**
     * Initializes UserController
     * @param address String filepath of the external .txt file containing existing user account(s)
     * @param user_service Use case class for User/Login System
     */
    public UserController(String address, UserService user_service) {
        this.user_service = user_service;
        user_accounts = new UserDatabaseReadWriter(user_service, address);
    }

    /**
     * Calls read method in gateway, loading all local data (i.e. existing user accounts)
     * @return boolean true or false if read method executed successfully
     */
    public boolean load() { return user_accounts.read(); }

    /**
     * Commences the process to attempt to log user in
     * @param un String username of the user account being logged into
     * @param pw String password of the user account being logged into
     * @return boolean true or false if user credentials successfully log user in
     */
    public boolean loginUser(String un, String pw) {
        if (user_service.validateCredentials(un, pw)){
            user_service.setCurrentUser(un);
            return true;
        }
        return false;
    }

    /**
     * Gets stored current user id from use-case
     * @return String user ID of current user logged in
     */
    public String getCurrentUserID(){
        return user_service.getCurrentUserID();
    }

    /**
     * Gets Current User's Type stored in use-case
     * @return UserType enum of current User's Type
     */
    public UserType getCurrentUserType(){
        return user_service.getCurrentUserType();
    }

    /**
     * Creates new user account - for use by Organizer accounts only
     * @param username String username of the user account being made
     * @param password String password of the user account being made
     * @param usertype UserType enum user type of user account being made
     * @return boolean true or false if user credentials are valid and creates account
     */
    public boolean addUser(String username, String password, UserType usertype) {
        if ((!validInput(username)) || !(validInput(password)))  {     //validate username/password input
            return false;
        }
        else if (user_service.userExists(username)){
            return false;
        }
        user_service.addUser(username, password, usertype);
        return true;
    }

    /*
     * Verifies if a string is a valid input by checking if it is alphanumerical
     * @param a String being checked
     * @returns boolean true or false if input string is valid or not
     */
    private boolean validInput (String a) {
        if (a == null) return false;
        String temp = a.replaceAll("\\s","");
        return temp.matches("[A-Za-z0-9]+");
    }

    /**
     * Gets a list of all Ids for all Users
     * @return List of Strings of all user ids
     */
    public List<String> getListOfAllIds() {
        List<String> allUserId = new ArrayList<>();
        for (UserType type : UserType.values()) {
            allUserId.addAll(user_service.getListOfIDsByType(type));
        }
        return allUserId;
    }

    /**
     * Gets a List of IDs based on user type
     * @param type UserType enum for user type
     * @return List of Strings of all user id with given user type
     */
    public List<String> getListOfIDsByType(UserType type) {
        return new ArrayList<>(user_service.getListOfIDsByType(type));
    }

    /**
     * Writes to external DB with updated user accounts list before exiting program
     * @return boolean true or false if write to DB successful
     */
    public boolean save() {
        return user_accounts.write();
    }

    /**
     * Removes a particular user
     * @param username String username of user account being removed
     * @return boolean true or false if removal successful
     */
    public boolean removeUser(String username) {
        return user_service.removeUser(username);
    }

    /**
     * Gets the user type for a specific user
     * @param name String username of user account intended to find user type for
     * @return UserType enum user Type for user
     */
    public UserType userTypeFromName(String name) {
        return user_service.userTypeFromUsers(name);
    }

}
