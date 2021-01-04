package room_system;

import event_system.EventPrompt;

import java.util.List;

public class RoomController implements IRoomController {

    private final RoomService roomService;
    private final RoomDatabaseReadWriter data_provider;

    /**
     * Constructor to initialize an RoomController
     * @param file_name    String       Contains the file name of the database where rooms are stored
     * @param roomService  RoomService  The interface to give access to use case methods
     */
    public RoomController(String file_name, RoomService roomService){
        this.roomService = roomService;
        this.data_provider = new RoomDatabaseReadWriter(roomService, file_name);
    }

    /**
     * Adds a room to the list of events
     * @param roomID         String       Contains the roomID
     * @param roomCapacity   int          Contains the room capacity
     * @return               EventPrompt  1) ROOM_ALREADY_EXISTS if a room with roomID already exists
     *                                    2) INVALID_ROOM_CAPACITY if roomCapacity is not valid
     *                                    3) ROOM_ADDED if the room has been added successfully
     */
    @Override
    public EventPrompt addRoom(String roomID, int roomCapacity){
       if (roomService.isRoomExist(roomID)){
           return EventPrompt.ROOM_ALREADY_EXISTS;
       }
       else if (!roomService.isValidRoomCapacity(roomCapacity)){
           return EventPrompt.INVALID_ROOM_CAPACITY;
       }
       else{
           roomService.addRoom(roomID, roomCapacity);
           return EventPrompt.ROOM_ADDED;
       }
    }

    /**
     * Method to get a nested list of list, each of which contains a room ID and its corresponding capacity
     * @return  List<List<String>>  Contains a list of list in the format of [Room ID, Capacity]
     */
    @Override
    public List<List<String>> getRoomInfo(){
        return roomService.getAllRooms();
    }

    /**
     * Method to read rooms from the database
     * @return  boolean  True if the events have successfully been read from the database
     */
    @Override
    public boolean load() {
        return data_provider.read();
    }

    /**
     * Writes to external DB with updated rooms list before exiting program
     * @return  boolean  True if the the data has been written to the DB successfully
     */
    @Override
    public boolean save() {
        return data_provider.write();
    }
}
