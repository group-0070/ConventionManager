package login_system;

/**
 * Models the execution of the login system
 */
public class LoginController implements ILoginController {

    private final String usernameInfo;
    private final String passwordInfo;
    private final UserService userService;
    private final IUserAccountDataProvider userAccounts;

    /**
     * Initializes LoginController
     * @param address String filepath of the external .txt file containing existing user account(s)
     * @param usernameInfo String username of the user account being logged into
     * @param passwordInfo String password of the user account being logged into
     */
    public LoginController(String address, String usernameInfo, String passwordInfo) {
        this.usernameInfo=usernameInfo;
        this.passwordInfo=passwordInfo;
        userService = new UserServiceEngine();
        userAccounts = new UserAccountDataProvider(userService, address);
        userAccounts.read();
    }

    /**
     * Commences the process to attempt to log user in
     * @param un String username of the user account being logged into
     * @param pw String password of the user account being logged into
     * @return boolean true or false if user credentials successfully log user in
     */
    public boolean loginUser(String un, String pw) {
        return userService.userToPass(un, pw);
    }

    /**
     * Creates new speaker account
     * @param username String username of the user account being made
     * @param password String password of the user account being made
     * @return boolean true or false if user credentials are valid and creates account
     */
    public boolean addNewSpeakerUser(String username, String password) {
        if (validInput(username)) {     //validate username input
            if (validInput(password)) {     //validate password input
                if (username.toLowerCase().startsWith("s")) {   //verify speaker username begins with 's'
                    return userService.addUser(username, password);
                }
            }
        }
        return false;
    }

    /*
     * Verifies if a string is a valid input by checking if it is alphanumerical
     * parameter for a String being checked
     * returns boolean true or false if input string is valid or not
     */
    private boolean validInput (String a) {
        if (a == null) return false;
        int length = a.length();
        for (int x = 0; x < length; x++) {
            if ((!Character.isLetterOrDigit(a.charAt(x)))) {    //alphanum characters are only valid
                return false;
            }
        }
        return true;
    }

    /**
     * Writes to external file with updated user accounts before exiting program
     */
    public void exitAction() {
        userAccounts.writeToFile();
    }

    /**
     * Removes a particular user
     * @param username String username of user account being removed
     */
    public void removeUser(String username) {
        userService.removeUser(username);
    }

    /**
     * Verifies if a particular user exists
     * @param name String username of user account being verified
     * @return boolean true or false if user exists
     */
    public boolean userExistsInList(String name) {
        return userService.userExists(name);
    }

    /**
     * Gets the user type (Attendee, Organizer, or Speaker)
     * @param name String username of user account intended to find user type for
     * @return String user Type for user (Attendee, Organizer, or Speaker)
     */
    public String userTypeFromName(String name) {
        return userService.userTypeFromUsers(name);
    }

    /**
     * Gets the current username of the user logged in
     * @return String username of current user logged in
     */
    public String getCurrentUserId() { return usernameInfo; }

    /**
     * Gets the current user type of the user logged in
     * @return String user type of current user logged in (Attendee, Organizer, or Speaker)
     */
    public String getCurrentUserType() { return userService.userTypeFromUsers(usernameInfo); }

    /**
     * Gets the interface for UserService initialized
     * @return UserService initialized class object
     */
    public UserService getUserService() { return userService; }

}
