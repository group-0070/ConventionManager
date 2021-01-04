package event_system;

public interface IEventDataProvider {
    /**
     * Insert the information of an event by writing to a .csv file
     * @return true if the file was successfully written to.
     */
    boolean write();
    /**
     * Read the list of events from the .csv file
     * @return true if the file was successfully read.
     */
    boolean read();
}
