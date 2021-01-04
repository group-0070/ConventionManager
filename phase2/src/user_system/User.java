package user_system;

import java.util.ArrayList;
import java.util.List;

/**
 * An Entity for storing the User's data (Username and Password and user type), defining the user object
 */
public class User {
    private final String user_id;
    private final String user_password;
    private final UserType user_type;

    /**
     * Creates a new instance of User and sets the login credentials
     * @param user_id       String that stores the user's username.
     * @param user_password String that stores the user's password.
     * @param user_type     UserType enum that stores user type
     */
    public User(String user_id, String user_password, UserType user_type){
        this.user_id = user_id;
        this.user_password = user_password;
        this.user_type=user_type;
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

    /**
     * Returns the User's type.
     * @return UserType enum Containing the user's type.
     */
    public UserType getUserType(){
        return user_type;
    }

    /**
     * toString method adding user info to arraylist
     * @return ArrayList of String representation of User
     */
    public List<String> toStrings() {
        List<String> res = new ArrayList<>();
        res.add(user_id);
        res.add(user_password);
        res.add(user_type.toString());
        return res;
    }

}
