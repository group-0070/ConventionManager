package event_system_test;

import event_system.EventDataProvider;
import event_system.EventService;
import event_system.EventServiceEngine;
import event_system.IEventDataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class EventDataProviderTest {
    private EventService es = new EventServiceEngine();
    private final String filename = "./assets/temp_events.csv";
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime t1 = LocalDateTime.parse("2001-03-30 10:10", format);
    private final LocalDateTime t2 = LocalDateTime.parse("2001-03-30 11:10", format);
    private final LocalDateTime t5 = LocalDateTime.parse("2001-03-30 13:00", format);
    private final LocalDateTime t6 = LocalDateTime.parse("2001-03-30 13:30", format);

    @Before
    public void Setup() {
        // make sure this is empty when testing
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void readWriteEvent(){
        // populate data
        ArrayList<String> s0 = new ArrayList<>();
        s0.add("Nancy");
        s0.add("Lily");
        ArrayList<String> a0 = new ArrayList<>();
        a0.add("Hassan");
        a0.add("Helen");
        a0.add("Lucia");
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Leo");
        s1.add("Jan");
        ArrayList<String> a1 = new ArrayList<>();
        a1.add("Seyon");
        es.addEvent("CSC207", t1, t2, "Somewhere in Bahen", s0, a0);
        es.addEvent("CSCB07", t5, t6, "Somewhere in IC", s1, a1);
        IEventDataProvider dp = new EventDataProvider(es, filename);
        dp.write();
        es = new EventServiceEngine();
        dp = new EventDataProvider(es, filename);
        dp.read();

//        String ex0 = "CSC207,2001-03-30 10:10,2001-03-30 11:10,Somewhere in Bahen,Nancy|Lily,Hassan|Helen|Lucia";
        String ex1 = "CSCB07,2001-03-30 13:00,2001-03-30 13:30,Somewhere in IC,Leo|Jan,Seyon";
//        assertEquals(es.getStringEvents().get(0), ex0);
        assertEquals(es.getStringEvents().get(0), ex1);
    }

    @Test
    public void readWriteEmpty(){
        // populate data
        ArrayList<String> s0 = new ArrayList<>();
        s0.add("Not Jan");
        ArrayList<String> a0 = new ArrayList<>();
        es.addEvent("Empty Event", t1, t2, "Empty Room", s0, a0);
        IEventDataProvider dp = new EventDataProvider(es, filename);

        // read and write data
        dp.write();
        es = new EventServiceEngine();
        dp = new EventDataProvider(es, filename);
        dp.read();

        String ex0 = "Empty Event,2001-03-30 10:10,2001-03-30 11:10,Empty Room,Not Jan,ε";
        assertEquals(es.getStringEvents().get(0), ex0);
    }

    @After
    public void deleteTemp(){
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
