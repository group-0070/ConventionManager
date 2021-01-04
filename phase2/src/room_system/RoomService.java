package room_system;

import java.util.List;

public interface RoomService {
    /**
     * Adds a room to the list of rooms
     * @param roomID         String   Contains the room ID
     * @param roomCapacity   int      Contains the room capacity
     */
    void addRoom(String roomID,int roomCapacity);

    /**
     * Returns true if room exists.
     * @param roomID String The room ID.
     */
    boolean isRoomExist(String roomID);

    /**
     * Return true if the size of the attendeeList is exceeding the room capacity.
     * @param attendeeSize int    The list of the attendee.
     * @param roomID       String The id of the room.
     */
    boolean isExceedingRoomCapacity(int attendeeSize,String roomID);

    /**
     * Checks if the event capacity is valid based on the room size
     * @param eventCapacity   int      Contains the event capacity
     * @param roomID          String   Contains the room ID
     * @return                boolean  True if the event capacity is valid for an event happening in the room "roomID"
     */
    boolean isValidEventCapacity(int eventCapacity, String roomID);

    /**
     * Checks if the room capacity is a positive number
     * @param roomCapacity  int      Contains a potential room capacity
     * @return              boolean  True if the room capacity is > 0
     */
    boolean isValidRoomCapacity(int roomCapacity);

    /**
     * Returns a nested list containing all rooms.
     * @return  List<List<String>>  A nested list of the form [[roomID1, 10], [roomID2, 23], .... , [roomID12, 45]]
     */
    List<List<String>> getAllRooms();

    /**
     * Returns a list of string room IDs
     * @return List<String>  A list containing a room ID in each entry
     */
    List<String> getRoomIDs();
}
