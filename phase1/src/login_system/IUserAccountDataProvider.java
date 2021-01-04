package login_system;

/**
 * Interface that writes and reads user account(s) to and from an external .txt file
 */
public interface IUserAccountDataProvider {

    /**
     * Reads in external file containing existing users, and calls method in
     * interface UserService for use-case class to create and store users read in
     */
    void read();

    /**
     * Overwrites user accounts back to file, with updated list of user accounts (whether new users added or not)
     */
    void writeToFile();
}
