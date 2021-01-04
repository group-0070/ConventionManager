package room_system;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String room_Id;
    private final int room_capacity;

    /**
     * Constructor to initialize a new Room
     * @param roomId          String    Contains the room ID
     * @param roomCapacity    int       Contains the room capacity
     */
    public Room(String roomId, int roomCapacity) {
        this.room_Id = roomId;
        this.room_capacity = roomCapacity;
    }

    /**
     * Getter for the room ID
     * @return  String  the room ID
     */
    public String getRoomId(){
        return room_Id;
    }

    /**
     * Getter for the room capacity
     * @return  int  the room capacity
     */
    public int getRoomCapacity(){
        return room_capacity;
    }

    /**
     * Returns a List of Strings containing roomId and roomCapacity of a Room
     * @return  List<String>  The list of rooms
     */
    public List<String> toList(){
        List<String> list = new ArrayList<>();
        list.add(this.room_Id);
        list.add(Integer.toString(this.room_capacity));
        return list;
    }


}
