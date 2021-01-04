package room_system_test;

import room_system.RoomDatabaseReadWriter;
import room_system.RoomService;
import room_system.RoomServiceEngine;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class RoomDatabaseReadWriterTest {

    private RoomService testRoomService = new RoomServiceEngine();
    private final String filename = "jdbc:sqlite:assets/UserDataTest.db";
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime t1 = LocalDateTime.parse("2001-03-30 10:10", format);
    private final LocalDateTime t2 = LocalDateTime.parse("2001-03-30 11:10", format);
    private final LocalDateTime t5 = LocalDateTime.parse("2001-03-30 13:00", format);
    private final LocalDateTime t6 = LocalDateTime.parse("2001-03-30 13:30", format);
    private RoomDatabaseReadWriter roomDatabaseReadWriter = new RoomDatabaseReadWriter(testRoomService, filename);


    @Before
    public void Setup(){
        if (roomDatabaseReadWriter.tableExists("Rooms")) roomDatabaseReadWriter.deleteTable("Rooms");

    }

    @Test
    public void readWriteSingleRoom(){
        testRoomService.addRoom("BA1007",4);
        //RoomDatabaseReadWriter roomDatabaseReadWriter = new RoomDatabaseReadWriter(testRoomService,filename);
        assertTrue(roomDatabaseReadWriter.write());
        assertTrue(roomDatabaseReadWriter.read());

        roomDatabaseReadWriter.displayAllRows("Rooms",2);
        List<List<String>> allRooms = new ArrayList<>();
        List<String> roomBA1007 = new ArrayList<>();
        roomBA1007.add("BA1007");
        roomBA1007.add(Integer.toString(4));
        allRooms.add(roomBA1007);

        assertEquals(allRooms.get(0),testRoomService.getAllRooms().get(0));
    }

    @Test
    public void readWriteMultipleRoom(){
        testRoomService.addRoom("BA1007",4);
        testRoomService.addRoom("HL205",5);
        //RoomDatabaseReadWriter roomDatabaseReadWriter = new RoomDatabaseReadWriter(testRoomService,filename);
        assertTrue(roomDatabaseReadWriter.write());
        assertTrue(roomDatabaseReadWriter.read());

        roomDatabaseReadWriter.displayAllRows("Rooms",2);

        List<List<String>> allRooms = new ArrayList<>();
        List<String> roomBA1007 = new ArrayList<>();
        roomBA1007.add("BA1007");
        roomBA1007.add(Integer.toString(4));
        allRooms.add(roomBA1007);
        List<String> roomHL = new ArrayList<>();
        roomHL.add("HL205");
        roomHL.add(Integer.toString(5));
        allRooms.add(roomHL);

        assertEquals(allRooms.get(0),testRoomService.getAllRooms().get(0));
        assertEquals(allRooms.get(1),testRoomService.getAllRooms().get(1));

    }


}
