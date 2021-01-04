package room_system;

import database.DatabaseReadWriter;

import java.sql.*;
import java.util.List;

public class RoomDatabaseReadWriter extends DatabaseReadWriter {
    private final RoomService room_service;
    private final String table_name = "Rooms";
    private final String sqlCreate = "CREATE TABLE IF NOT EXISTS " + table_name + " (\n"
            + "	roomID TEXT UNIQUE,\n"
            + "	roomCapacity INTEGER\n"
            + ");";


    /**
     * Constructor to initialize a new RoomDatabaseReadWriter
     * @param roomService  RoomService  A variable to access use case methods for rooms
     * @param address      String       The file address
     */
    public RoomDatabaseReadWriter(RoomService roomService, String address){
        super(address);
        this.room_service =roomService;
    }

    /**
     * Reads from SQL database and stores info locally.
     * @return boolean True if all entries read are correct.
     */
    public boolean read(){
        Connection connection = makeConnection();
        PreparedStatement entry = null;
        ResultSet resultEntry = null;

        if (!tableExists(table_name)) createNewTable(sqlCreate);

        String sql = "SELECT * FROM Rooms";

        try {
            entry = connection.prepareStatement(sql);   //DB connection to be queried
            resultEntry = entry.executeQuery();     //executes the SQL query
            while (resultEntry.next()) {    //iterating through all rows of the table
                String roomID = resultEntry.getString("roomID");
                int roomCapacity = resultEntry.getInt("roomCapacity");

                try {
                    room_service.addRoom(roomID,roomCapacity);
                } catch (Exception e){
                    System.out.println("Incorrect format of rows in Room" + resultEntry.getRow());
                }

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
     * Writes updated data to database
     * @return boolean Returns true if writing to the database is successful.
     */
    public boolean write(){

        if (!tableExists(table_name)) createNewTable(sqlCreate);
        deleteAllData(table_name);

        String sql = "REPLACE INTO Rooms(roomID, roomCapacity) VALUES(?,?) ";
        Connection connection = makeConnection();

        try {
            for (List<String> room: room_service.getAllRooms()){
                String roomID = room.get(0);
                int roomCapacity = Integer.parseInt(room.get(1));
                PreparedStatement entry;

                entry = connection.prepareStatement(sql);
                entry.setString(1,roomID);
                entry.setInt(2,roomCapacity);
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
}
