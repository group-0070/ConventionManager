package login_system;

/**
 * Presenter class for Login system
 */
public class LoginPresenter {

    /**
     * Prompt for user username to log in
     * @return String message prompting user to enter username to log in
     */
    public String loginPromptUsername() { return "Please enter your Username:"; }

    /**
     * Prompt for user password to log in
     * @return String message prompting user to enter password to log in
     */
    public String loginPromptPassword() { return "Please enter your Password:"; }

    /**
     * Gets message for successful login and greets User
     * @param currentUserId String of current username for user that logged in
     * @return String message prompting user that login was successful and greets user
     */
    public String loginSuccess(String currentUserId) { return "Login Successful! Welcome " + currentUserId + "."; }

    /**
     * Gets message for failed login
     * @return String message prompting user that login was unsuccessful
     */
    public String loginFailed() { return "Incorrect Login Credentials. Please Try Again."; }

    /**
     * Prompt for user to create username of new Speaker account
     * @return String message prompting user to enter the Speaker accounts username
     */
    public String addSpeakerUsernamePrompt() { return "Please enter the desired username for the speaker " +
            "(must begin with letter 's' and only contain alphanumeric characters): "; }

    /**
     * Prompt for user to create password of new Speaker account
     * @return String message prompting user to enter the Speaker accounts password
     */
    public String addSpeakerPasswordPrompt() { return "Please enter the desired password for the " +
            "speaker(must only contain alphanumeric characters): "; }

    /**
     * Gets Message for successful creation of new Speaker account
     * @param newUserId String of new speaker user's username
     * @return String message for successful account creation
     */
    public String newSpeakerAddedSuccess(String newUserId) { return newUserId + " " + "Added Successfully!"; }

    /**
     * Gets Message for unsuccessful creation of new Speaker account
     * @return String message for unsuccessful account creation
     */
    public String newSpeakerAddedFailed() { return "Username taken or invalid Entry. Please Try Again"; }

    /**
     * Gets Message for logging off
     * @param currentUserId String of current user logged in
     * @return String message for logging user off
     */
    public String logOffPrompt(String currentUserId) { return "Logged off. See you soon " + currentUserId + "!"; }

    /**
     * Gets Message for verifying exiting the program without successful login
     * @return String message prompting user to verify exit
     */
    public String exitMessage() { return "Would you like to exit? Enter y/n: "; }

    /**
     * Gets Message for exiting program without successful login
     * @return String message for user exiting program
     */
    public String exitProgram() { return "Exiting Program. See you Soon!"; }

}

