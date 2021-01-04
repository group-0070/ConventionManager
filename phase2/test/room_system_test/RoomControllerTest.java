package room_system_test;

import event_system.*;

import org.junit.Before;
import org.junit.Test;
import room_system.IRoomController;
import room_system.RoomController;
import room_system.RoomService;
import room_system.RoomServiceEngine;

import static org.junit.Assert.*;

public class RoomControllerTest {

    private RoomService roomService;
    private IRoomController roomController;
    private final String filenameTest = "jdbc:sqlite:assets/UserDataTest.db";

    @Before
    public void setup(){
        roomService = new RoomServiceEngine();
        roomController = new RoomController(filenameTest, roomService);
    }

    @Test
    public void addRoom(){
        assertEquals(EventPrompt.ROOM_ADDED, roomController.addRoom("Janitor's Closet", 2));
        assertEquals(1, roomService.getRoomIDs().size());
    }

    @Test
    public void addExistingRoom(){
        assertEquals(EventPrompt.ROOM_ADDED, roomController.addRoom("Janitor's Closet", 2));
        assertEquals(1, roomService.getRoomIDs().size());
        assertEquals(EventPrompt.ROOM_ALREADY_EXISTS, roomController.addRoom("Janitor's Closet", 3));
        assertEquals(1, roomService.getRoomIDs().size());
    }

    @Test
    public void negativeRoomCapacity(){
        assertEquals(EventPrompt.INVALID_ROOM_CAPACITY, roomController.addRoom("Janitor's Closet", -1));
        assertEquals(0, roomService.getRoomIDs().size());
    }
}
