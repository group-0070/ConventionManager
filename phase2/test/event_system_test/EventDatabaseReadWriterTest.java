package event_system_test;

import event_system.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class EventDatabaseReadWriterTest {

    private EventService testEventService = new EventServiceEngine();
    private final String filename = "jdbc:sqlite:assets/UserDataTest.db";
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime t1 = LocalDateTime.parse("2001-03-30 10:10", format);
    private final LocalDateTime t2 = LocalDateTime.parse("2001-03-30 11:10", format);
    private final LocalDateTime t5 = LocalDateTime.parse("2001-03-30 13:00", format);
    private final LocalDateTime t6 = LocalDateTime.parse("2001-03-30 13:30", format);
    private EventDatabaseReadWriter dp = new EventDatabaseReadWriter(testEventService, filename);



    @Before
    public void Setup() {
        if (dp.tableExists("Events")) dp.deleteTable("Events");
    }

    private String toString(List<String> listIDs){
        StringBuilder stringIds= new StringBuilder();
        if(listIDs.size()>0){
            for (String ID:listIDs){
                stringIds.append(ID+'|');
            }
        }
        //removing the last '|' if the StringBuilder is not empty
        if (stringIds.length()>0){
            stringIds.setLength(stringIds.length() - 1);
        }
        return stringIds.toString();
    }

    /**
     * Convert the nested List into a HashMap.
     * @param nestedList List<List<String>> A nested 2-D List containing all info of one event.
     */
    private HashMap<String,String> listToMap(List<List<String>> nestedList){
        HashMap<String,String> map=new HashMap<>();
        map.put("eventType",nestedList.get(0).get(0));
        map.put("eventCapacity",nestedList.get(1).get(0));
        map.put("eventID",nestedList.get(2).get(0));
        map.put("startTime",nestedList.get(3).get(0));
        map.put("endTime",nestedList.get(4).get(0));
        map.put("roomID",nestedList.get(5).get(0));
        map.put("speakerIDs",toString(nestedList.get(6)));
        map.put("attendeeIDs",toString(nestedList.get(7)));
        return map;
    }

    /**
     * Takes the input parameter and make it a HashMap instead.
     */
    private HashMap<String,String> toMap(EventType eventType,int eventCapacity,String eventID,
                                        LocalDateTime startTime,LocalDateTime endTime,String roomID,
                                        List<String> speakerIDs, List<String> attendeeIDs){
        HashMap<String,String> map = new HashMap<>();
        map.put("eventType",eventType.toString());
        map.put("eventCapacity",Integer.toString(eventCapacity));
        map.put("eventID",eventID);
        map.put("startTime", startTime.format(format));
        map.put("endTime",endTime.format(format));
        map.put("roomID",roomID);
        if(attendeeIDs.size()!=0){
            map.put("attendeeIDs",toString(attendeeIDs));
        } else{
            map.put("attendeeIDs","");
        }
        if(speakerIDs.size()!=0){
            map.put("speakerIDs",toString(speakerIDs));
        } else{
            map.put("speakerIDs","");
        }
        return map;
    }

    @Test
    public void readWriteSingleEvent(){
        List<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        s0.add("Lily");
        List<String> a0 = new ArrayList<>();
        a0.add("Hassan");
        a0.add("Helen");
        a0.add("Lucia");
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSC207",
                                  t1, t2, "Somewhere in Bahen", s0, a0);
        assertTrue(dp.write());
        assertTrue(dp.read());
        dp.displayAllRows("Events",8);

        HashMap<String,String> actual = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected = toMap(EventType.MULTI_SPEAKER_EVENT, 2, "CSC207",
                t1, t2, "Somewhere in Bahen", s0, a0);
        assertEquals(expected,actual);
    }

    @Test
    public void readWriteMultipleEvent(){
        List<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        s0.add("Lily");
        List<String> a0 = new ArrayList<>();
        a0.add("Hassan");
        a0.add("Helen");
        a0.add("Lucia");
        List<String> s1 = new ArrayList<>();
        s1.add("Leo");
        s1.add("Jan");
        List<String> a1 = new ArrayList<>();
        a1.add("Seyon");
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSC207", t1, t2, "Somewhere in Bahen",
                s0, a0);
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSCB07", t5, t6, "Somewhere in IC", s1
                , a1);
        assertTrue(dp.write());
        assertTrue(dp.read());

        HashMap<String,String> actual0 = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected0 = toMap(EventType.MULTI_SPEAKER_EVENT,2,"CSC207",
                t1,t2,"Somewhere in Bahen",s0,a0);
        assertEquals(expected0,actual0);
        HashMap<String,String> actual1 = listToMap(testEventService.getListEvents().get(1));
        HashMap<String,String> expected1 = toMap(EventType.MULTI_SPEAKER_EVENT,2,"CSCB07",
                t5,t6,"Somewhere in IC",s1,a1);
        assertEquals(expected1,actual1);
        dp.displayAllRows("Events", 7);
    }

    @Test
    public void readWriteEmpty(){
        List<String> s0 = new ArrayList<>();
        s0.add("Not Jan");
        List<String> a0 = new ArrayList<>();
        testEventService.addEvent(EventType.NO_SPEAKER_EVENT, 2, "Empty Event", t1, t2, "Empty Room", s0,
                a0);

        String tableName = "Events";
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + "(\n"
                + "	eventType TEXT,\n"
                + " eventCapacity TEXT, \n"
                + "	eventID TEXT UNIQUE,\n"
                + "	startTime TEXT,\n"
                + "	endTime TEXT,\n"
                + "	roomID TEXT,\n"
                + "	speakerIDs TEXT,\n"
                + "	attendeeIDs TEXT\n"
                + ");";

        if (!dp.tableExists("Events")) dp.createNewTable(sqlCreate);

        // read and write data
        dp.write();
        testEventService = new EventServiceEngine();
        dp = new EventDatabaseReadWriter(testEventService, filename);
        dp.read();

        HashMap<String,String> actual = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected = toMap(EventType.NO_SPEAKER_EVENT,2,
                "Empty Event", t1, t2, "Empty Room", s0, a0);
        assertEquals(expected,actual);
    }

    @Test
    public void writeTableDNE(){
        if (dp.tableExists("Events")) assertTrue(dp.deleteTable("Events"));
        assertFalse(dp.tableExists("Events"));

        List<String> s0 = new ArrayList<>();
        s0.add("Jan");
        List<String> a0 = new ArrayList<>();
        a0.add("Helen");
        a0.add("Seyon");
        List<String> s1 = new ArrayList<>();
        s1.add("Jan");
        s1.add("Lucia");
        List<String> a1 = new ArrayList<>();
        a1.add("Lily");

        testEventService.addEvent(EventType.SINGLE_SPEAKER_EVENT,3,"CSC207",t1,t2,"BA1007",
                s0,a0);
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT,2,"MAT224",t5,t6,
                "Somewhere in Bahen",s1,a1);

        //EventDatabaseReadWriter dp = new EventDatabaseReadWriter(testEventService, filename);
        assertTrue(dp.write());
        assertTrue(dp.tableExists("Events"));
    }

    @Test
    public void readWriteWithDeletionOfEvents(){
        List<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        s0.add("Lily");
        List<String> a0 = new ArrayList<>();
        a0.add("Hassan");
        a0.add("Helen");
        a0.add("Lucia");
        List<String> s1 = new ArrayList<>();
        s1.add("Leo");
        s1.add("Jan");
        List<String> a1 = new ArrayList<>();
        a1.add("Seyon");
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSC207", t1, t2, "Somewhere in Bahen",
                s0, a0);
        testEventService.addEvent(EventType.MULTI_SPEAKER_EVENT, 2, "CSCB07", t5, t6, "Somewhere in IC", s1
                , a1);
        //EventDatabaseReadWriter dp = new EventDatabaseReadWriter(testEventService, filename);
        assertTrue(dp.write());
        dp.displayAllRows("Events",8);

        testEventService.cancelEventByID("CSCB07");
        testEventService.cancelEventByID("CSC207");
        List<String> a2 = new ArrayList<>();
        a2.add("Helen");
        List<String> s2 = new ArrayList<>();
        testEventService.addEvent(EventType.NO_SPEAKER_EVENT,2,"MAT224",t1,t2,
                "Somewhere",s2,a2);
        assertEquals(1,testEventService.getListEvents().size());
        assertTrue(dp.write());

        HashMap<String,String> actual = listToMap(testEventService.getListEvents().get(0));
        HashMap<String,String> expected = toMap(EventType.NO_SPEAKER_EVENT,2,
                "MAT224",t1,t2, "Somewhere",new ArrayList<>(),a2);
        assertEquals(expected,actual);
        assertEquals(1,testEventService.getListEvents().size());
        dp.displayAllRows("Events",8);
    }

    @After
    public void deleteTemp(){
        dp.deleteAllData("Events");
        dp.deleteTable("Events");
    }
}
