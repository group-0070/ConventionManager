package room_system;

import java.util.ArrayList;
import java.util.List;

public class RoomServiceEngine implements RoomService {
    private final List<Room> room_list = new ArrayList<>();

    /**
     * Adds a room to the list of rooms
     * @param roomID         String   Contains the room ID
     * @param roomCapacity   int      Contains the room capacity
     */
    @Override
    public void addRoom(String roomID,int roomCapacity){
        Room roomToAdd = new Room(roomID,roomCapacity);
        this.room_list.add(roomToAdd);
    }

    /**
     * Returns true if room exists.
     * @param roomID String The room ID.
     */
    @Override
    public boolean isRoomExist(String roomID){
        Room room = searchRoom(roomID);
        return room != null;
    }

    /**
     * Return true if the size of the attendeeList is exceeding the room capacity.
     * @param attendeeSize int    The list of the attendee.
     * @param roomID       String The id of the room.
     */
    @Override
    public boolean isExceedingRoomCapacity(int attendeeSize,String roomID){
        Room room = searchRoom(roomID);
        if (room != null) {
            int roomCapacity = room.getRoomCapacity();
            return attendeeSize > roomCapacity;
        }
        else {
            return false; // no room exist.
        }
    }

    /**
     * Checks if the event capacity is valid based on the room size
     * @param eventCapacity   int      Contains the event capacity
     * @param roomID          String   Contains the room ID
     * @return                boolean  True if the event capacity is valid for an event happening in the room "roomID"
     */
    @Override
    public boolean isValidEventCapacity(int eventCapacity, String roomID){
        Room room = searchRoom(roomID);
        if (room != null) {
            int roomCapacity = room.getRoomCapacity();
            return eventCapacity <= roomCapacity;
        }
        return false;
    }

    /**
     * Checks if the room capacity is a positive number
     * @param roomCapacity  int      Contains a potential room capacity
     * @return              boolean  True if the room capacity is > 0
     */
    public boolean isValidRoomCapacity(int roomCapacity){
        return roomCapacity >= 0;
    }

    /**
     * Returns a nested list containing all rooms.
     * @return  List<List<String>>  A nested list of the form [[roomID1, 10], [roomID2, 23], .... , [roomID12, 45]]
     */
    @Override
    public List<List<String>> getAllRooms(){
        List<List<String>> allRooms = new ArrayList<>();
        for (Room room:room_list){
            List<String> roomList = room.toList();
            allRooms.add(roomList);
        }
        return allRooms;
    }

    /**
     * Returns a list of string room IDs
     * @return List<String>  A list containing a room ID in each entry
     */
    public List<String> getRoomIDs(){
        List<String> roomIDs = new ArrayList<>();
        for (Room room: room_list){
            roomIDs.add(room.getRoomId());
        }
        return roomIDs;
    }

    /**
     * Return a room by the roomID given. If the room not found, return null.
     * @param    roomID  String ID of the room.
     * @return   Room    A room variable that has the same room ID as roomID
     */
    private Room searchRoom(String roomID){
        for (Room room:room_list){
            if (room.getRoomId().equals(roomID)){
                return room;
            }
        }
        return null;
    }
}
