package messaging_system;

/**
 * Gateway interface with read and write functions.
 */
public interface IMessageDataProvider {
    /**
     * Persist all information from the running program to the disk
     */
    void write();

    /**
     * Read in all information from disk into the program.
     */
    void read();
}
