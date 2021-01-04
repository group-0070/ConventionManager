package user_system;

import java.util.ArrayList;
import java.util.List;

/**
 * A Use-Case class pertaining to User account management, creating and generating user accounts
 * and storing all user accounts
 */
public class UserServiceEngine implements UserService {
    private final List<User> list_of_users = new ArrayList<>();
    private User currentUser = null;

    /**
     * Sets the current user logged in
     * @param userID String of current user ID
     */
    public void setCurrentUser(String userID){
        currentUser = getUserByID(userID);
    }

    /**
     * Gets the current user's ID, that is logged in
     * @return String User ID for user currently logged in
     */
    public String getCurrentUserID(){
        if (currentUser != null){
            return currentUser.getUserID();
        }
        return null;
    }

    /**
     * Gets the current user's type, that is logged in
     * @return UserType enum for current user's type
     */
    public UserType getCurrentUserType(){
        if (currentUser != null){
            return currentUser.getUserType();
        }
        return null;
    }

    /**
     * Create user account and add to list of user accounts
     * @param name String username of user account
     * @param password String password of user account
     * @param type UserType enum of user type
     */
    public void addUser(String name, String password, UserType type){
        User newUser = new User(name, password, type);
        list_of_users.add(newUser);
    }

    /**
     * Remove the user if it exists
     * @param username String username of user account being removed
     * @return boolean true or false if user removed
     */
    public boolean removeUser(String username) {
        User u = getUserByID(username);
        if (u != null){
            list_of_users.remove(getUserByID(username));
            return true;
        }
        return false;
    }

    /**
     * Checks if User with the given username exists
     * @param username the username of user account that is being checked
     * @return true or false whether User with the given username exists in list of users
     */
    public boolean userExists(String username) {
        return !(getUserByID(username) == null);
    }

    /**
     * Gets the user type as a String for a given username for user account
     * @param username String username of the user
     * @return UserType enum user type
     */
    public UserType userTypeFromUsers(String username) {
        User u = getUserByID(username);
        if (u != null){
            return u.getUserType();
        }
        return null;
    }

    /**
     * Verifies if username and password matches the credentials stored for that user account (if it exists)
     * @param username String username input for user account
     * @param password String password input for user account
     * @return True or False whether the username and the password matches an account in the list of user accounts
     */
    public boolean validateCredentials(String username, String password) {
        for (User listOfUser : list_of_users) {
            if ((username.equals(listOfUser.getUserID())) && (password.equals(listOfUser.getUserPassword()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates list of all user accounts with each users list of info
     * @return 2d array with list of user accounts - each entry is a list with user account info
     */
    public List<List<String>> getUserInfo(){
        List<List<String>> res = new ArrayList<>();
        for (User u: list_of_users){
            res.add(u.toStrings());
        }
        return res;
    }

    /**
     * Generates a list of all User IDs with given user type
     * @param userType UserType enum for user type
     * @return List of String representation of all User IDs with given Type
     */
    public List<String> getListOfIDsByType(UserType userType){
        List<String> res = new ArrayList<>();
        for (User user : list_of_users) {
            if (user.getUserType().equals(userType)) {
                res.add(user.getUserID());
            }
        }
        return res;
    }

    /*
     * Returns user object if it exists
     */
    private User getUserByID(String userID){
        for (User user: list_of_users){
            if (user.getUserID().equals(userID)){
                return user;
            }
        }
        return null;
    }

}

