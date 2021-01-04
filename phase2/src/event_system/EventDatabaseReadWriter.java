package event_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import database.DatabaseReadWriter;

import java.sql.*;

public class EventDatabaseReadWriter extends DatabaseReadWriter {

    private final EventService event_service;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final String table_name = "Events";
    private final String sql_table = "CREATE TABLE IF NOT EXISTS " + table_name + " (\n"
            + "    eventID TEXT UNIQUE,\n"
            + "    eventType TEXT,\n"
            + "    eventCapacity TEXT,\n"
            + "    startTime TEXT,\n"
            + "    endTime TEXT,\n"
            + "    roomID TEXT,\n"
            + "    speakerIDs TEXT,\n"
            + "    attendeeIDs TEXT\n"
            + ");";


    /**
     * Initialize EventDatabaseReadWriter
     * @param  eventService EventService Contains the Event ID.
     * @param  address      String       The address of the database.
     */
    public EventDatabaseReadWriter(EventService eventService, String address){
        super(address);
        event_service = eventService;
    }

    /**
     * Insert the information of an event by writing to a database
     * @return true if the file was successfully written to.
     */
    public boolean write(){
        if (!tableExists(table_name)) createNewTable(sql_table);
        deleteAllData(table_name);

        String sql = "REPLACE INTO Events(eventID,eventType,eventCapacity,startTime," +
                "endTime,roomID,speakerIDs,attendeeIDs) VALUES(?,?,?,?,?,?,?,?) ";
        Connection connection = makeConnection();

        try {
            for (List<List<String>> event:event_service.getListEvents()){
                PreparedStatement entry;
                entry = connection.prepareStatement(sql);
                entry.setString(2,event.get(EventIndex.EVENT_TYPE.getValue()).get(0)); //eventType
                entry.setInt(3, Integer.parseInt(event.get(EventIndex.EVENT_CAPACITY.getValue()).get(0))); //eventCapacity
                entry.setString(1, event.get(EventIndex.EVENT_ID.getValue()).get(0)); //eventID
                entry.setString(4, event.get(EventIndex.START_TIME.getValue()).get(0)); //startTime
                entry.setString(5, event.get(EventIndex.END_TIME.getValue()).get(0));//endTime
                entry.setString(6, event.get(EventIndex.ROOM_ID.getValue()).get(0));//roomID
                entry.setObject(7, listToString(event.get(EventIndex.SPEAKER_IDS.getValue()))); //speakerIds
                entry.setObject(8, listToString(event.get(EventIndex.ATTENDEE_IDS.getValue()))); //attendeeIds

                entry.execute();
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Read the list of events from the .csv file
     * @return true if the file was successfully read.
     */
    public boolean read(){
        Connection connection = makeConnection();
        PreparedStatement entry = null;
        ResultSet resultEntry = null;

        if (!tableExists(table_name)) createNewTable(sql_table);

        String sql = "SELECT * FROM Events";

        try {
            entry = connection.prepareStatement(sql);   //DB connection to be queried
            resultEntry = entry.executeQuery();     //executes the SQL query
            while (resultEntry.next()) {    //iterating through all rows of the table
                String eventID = resultEntry.getString("eventID");
                int eventCapacity = resultEntry.getInt("eventCapacity");
                LocalDateTime startTime = LocalDateTime.parse(resultEntry.getString("startTime"),format);
                LocalDateTime endTime = LocalDateTime.parse(resultEntry.getString("endTime"),format);
                String roomID = resultEntry.getString("roomID");

                List<String> speakerIDs = stringToList(resultEntry.getObject("speakerIDs").toString());
                List<String> attendeeIDs = stringToList(resultEntry.getObject("attendeeIDs").toString());

                EventType eventType = EventType.valueOf(resultEntry.getString("eventType"));

                event_service.addEvent(eventType,eventCapacity, eventID,startTime,endTime,roomID,speakerIDs,
                                        attendeeIDs);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                assert resultEntry != null;     //closes all connections and queries before returning boolean
                resultEntry.close();
                entry.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

    }


    /**
     * Converts a a list into a string without list brackets
     * @param  target  List<String>  A list containing the string to be converted
     * @return         String        A string representation of the list
     */
    private String listToString(List<String> target){
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (String item: target){
            res.append(item).append(",");
        }
        if (res.length() > 1) {
            res.deleteCharAt(res.length() - 1);
        }
        res.append("]");
        return res.toString();
    }

    /**
     * Converts a string to a list
     * @param  target  String        Converts a string into a list
     * @return         List<String>  The list representation of the string target
     */
    private List<String> stringToList(String target){
        String res = target.replace("[", "").replace("]", "");
        if (res.equals("")){
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(res.split(",")));
    }
}

