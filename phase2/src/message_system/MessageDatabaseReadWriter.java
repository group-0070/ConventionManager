package message_system;

import database.DatabaseReadWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class MessageDatabaseReadWriter extends DatabaseReadWriter {

    private final MessageService message_service;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String table_name = "Messages";
    String sql_table = "CREATE TABLE IF NOT EXISTS "+ table_name + " (\n"
            + "	messageID TEXT UNIQUE,\n"
            + "	sender TEXT,\n"
            + "	receiver TEXT,\n"
            + "	message TEXT,\n"
            + "	time TEXT,\n"
            + "	status TEXT\n"
            + ");";

    /**
     * Initialize MessageReadWriter
     * @param  messageService MessageService Contains the messageList to be persisted onto disk via database
     * @param  address      String       The address of the database.
     */
    public MessageDatabaseReadWriter(MessageService messageService, String address){
        super(address);
        message_service = messageService;
    }

    /**
     * Read the list of events from the database table "Messages" (table_name)
     * @return true if the messages was successfully read in.
     */
    public boolean read(){
        Connection connection = makeConnection();
        PreparedStatement entry = null;
        ResultSet resultEntry = null;

        if (!tableExists(table_name)) createNewTable(sql_table);

        String sql = "SELECT * FROM Messages";

        try {
            entry = connection.prepareStatement(sql);   //DB connection to be queried
            resultEntry = entry.executeQuery();     //executes the SQL query
            while (resultEntry.next()) {    //iterating through all rows of the table
                UUID messageId = UUID.fromString(resultEntry.getString("messageID"));
                String senderId = resultEntry.getString("sender");
                String receiverId = resultEntry.getString("receiver");
                String info = resultEntry.getString("message");
                LocalDateTime time = LocalDateTime.parse(resultEntry.getString("time"),format);
                MessageStatus messageStatus = MessageStatus.valueOf(resultEntry.getString("status"));

                //     public void addMessage(UUID messageId, String info, String senderId, String receiverId, LocalDateTime time, MessageStatus status) {
                try{
                    message_service.addMessage(messageId, info, senderId, receiverId, time, messageStatus);
                } catch (Exception e){
                    System.out.println("Incorrect format of row" + resultEntry.getRow());
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
     * Insert the information of messages to a database table "Messages" (table_name)
     * @return true if the messages were successfully written
     */
    public boolean write(){
        if (!tableExists(table_name)) createNewTable(sql_table);
        deleteAllData(table_name);

        String sql = "INSERT INTO Messages(messageID,sender,receiver,message, time, status) VALUES(?,?,?,?,?,?) ";
        Connection connection = makeConnection();

        try {
            for (List<String> message: message_service.getMessageInfo()){
                PreparedStatement entry;    //Prepared Statement object for non static entries

                entry = connection.prepareStatement(sql);   // creates the preparedStatement Object
                entry.setString(1, message.get(MessageIndex.MESSAGE_ID.getValue())); // messageId
                entry.setString(2, message.get(MessageIndex.SENDER.getValue())); //senderId
                entry.setString(3, message.get(MessageIndex.RECEIVER.getValue())); // receiverId
                entry.setString(4, message.get(MessageIndex.MESSAGE_INFO.getValue())); // messageInfo
                entry.setString(5, message.get(MessageIndex.TIME.getValue())); // messageTime
                entry.setString(6, message.get(MessageIndex.STATUS.getValue())); // messageStatus
                entry.execute();    //preparedStatement is entered into table, executes the query
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

