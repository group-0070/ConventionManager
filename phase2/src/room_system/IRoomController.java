package room_system;

import event_system.EventPrompt;

import java.util.List;

public interface IRoomController {

    /**
     * Adds a room to the list of events
     * @param roomID         String       Contains the roomID
     * @param roomCapacity   int          Contains the room capacity
     * @return               EventPrompt  1) ROOM_ALREADY_EXISTS if a room with roomID already exists
     *                                    2) INVALID_ROOM_CAPACITY if roomCapacity is not valid
     *                                    3) ROOM_ADDED if the room has been added successfully
     */
    EventPrompt addRoom(String roomID, int roomCapacity);


    /**
     * Method to get a nested list of list, each of which is contains a room ID and its corresponding capacity
     * @return  List<List<String>>  Contains a list of list in the format of [Room ID, Capacity]
     */
    List<List<String>> getRoomInfo();

    /**
     * Method to read rooms from the database
     * @return  boolean  True if the events have successfully been read from the database
     */
    boolean load();

    /**
     * Writes to external DB with updated rooms list before exiting program
     * @return  boolean  True if the the data has been written to the DB successfully
     */
    boolean save();
}
