package login_system;

/**
 * Interface to model the execution of the login system
 */
public interface ILoginController {

    /**
     * Gets the current username of the user logged in
     * @return String username of current user logged in
     */
    String getCurrentUserId();

    /**
     * Gets the current user type of the user logged in
     * @return String user type of current user logged in (Attendee, Organizer, or Speaker)
     */
    String getCurrentUserType();

    /**
     * Verifies if a particular user exists
     * @param name String username of user account being verified
     * @return boolean true or false if user exists
     */
    boolean userExistsInList(String name);

    /**
     * Gets the user type (Attendee, Organizer, or Speaker)
     * @param name String username of user account intended to find user type for
     * @return String user Type for user (Attendee, Organizer, or Speaker)
     */
    String userTypeFromName(String name);

    /**
     * Creates new speaker account
     * @param username String username of the user account being made
     * @param password String password of the user account being made
     * @return boolean true or false if user credentials are valid and creates account
     */
    boolean addNewSpeakerUser(String username, String password);

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
     */
    void removeUser(String username);

    /**
     * Writes to external file with updated user accounts before exiting program
     */
    void exitAction();

    /**
     * Gets the interface for UserService initialized
     * @return UserService initialized class object
     */
    UserService getUserService();
}
