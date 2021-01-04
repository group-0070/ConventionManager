package login_system;

/**
 * An Entity for storing the User's data (Username and Password), defining the user object
 */
public class User {
    private final String user_id;
    private final String user_password;

    /**
     * Creates a new instance of User and sets the login credentials
     * @param user_id       String that stores the user's username.
     * @param user_password String that stores the user's password.
     */
    public User(String user_id, String user_password){
        this.user_id = user_id;
        this.user_password = user_password;
    }

    /**
     * Returns the User's username
     * @return String Containing the user's username.
     */
    public String getUserID(){
        return user_id;
    }

    /**
     * Returns the User's password.
     * @return String Containing the user's password.
     */
    public String getUserPassword(){
        return user_password;
    }

}
