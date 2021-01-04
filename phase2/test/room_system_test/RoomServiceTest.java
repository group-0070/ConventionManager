package room_system_test;

import room_system.RoomService;
import room_system.RoomServiceEngine;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RoomServiceTest {
    private RoomService testRoomService;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    @Before
    public void setUp(){
        this.testRoomService = new RoomServiceEngine();
    }

    @Test
    public void addRoom(){
        testRoomService.addRoom("BA1007",4);
        List<String> room1 = new ArrayList<>();
        room1.add("BA1007");
        room1.add(Integer.toString(4));

        assertEquals(room1,testRoomService.getAllRooms().get(0));
    }

    @Test
    public void isExceedingRoomCapacity(){
        testRoomService.addRoom("BA1007",4);
        assertTrue(testRoomService.isExceedingRoomCapacity(5,"BA1007"));
        assertFalse(testRoomService.isExceedingRoomCapacity(2,"BA1007"));
    }
}
