package user_system;

import database.DatabaseReadWriter;
import java.sql.*;
import java.util.List;

/**
 * A gateway to read in and write existing user account information from an external SQL .db file
 */
public class UserDatabaseReadWriter extends DatabaseReadWriter {
    private final UserService user_service;
    private final String table_name = "Accounts";

    //SQL query string for command to create Accounts Table
    private final String sql_table = "CREATE TABLE IF NOT EXISTS " + table_name + " (\n"
            + "	Username TEXT UNIQUE,\n"
            + "	Password TEXT,\n"
            + "	Type TEXT\n"
            + ");";

    /**
     * Initialize, and set the filepath and UserService Interface
     * @param user_service UserService interface for use-case to add and store users that are read in
     * @param address String representation of filepath of .db file containing user accounts
     */
    public UserDatabaseReadWriter(UserService user_service, String address) {
        super(address);
        this.user_service=user_service;
    }

    /**
     * Reads from SQL database and stores info locally in program.
     * @return boolean true or false if all entries read in correct
     */
    public boolean read() {
        Connection connection = makeConnection();   //establish connection to DB
        PreparedStatement entry = null;     //preparedStatement object initialized - stores sql query executed
        ResultSet resultEntry = null;   //ResultSet used to execute prepared statement for SQL

        if (!tableExists(table_name)) createNewTable(sql_table);

        String sql = "SELECT * FROM " + table_name;  //Selects all rows from the table named 'Accounts'. this is our sql query

        try {
            entry = connection.prepareStatement(sql);   //DB connection to be queried
            resultEntry = entry.executeQuery();     //executes the SQL query
            while (resultEntry.next()) {    //iterating through all rows of the table

                String un = resultEntry.getString("Username");    //storing value of col 1 for the row
                String pw = resultEntry.getString("Password");    //storing value of col 2 for the row
                String ut = resultEntry.getString("Type");    //storing value of col 3 for the row

                try {

                    //Verifies the inputted values from the DB are valid, before calling use case to store locally
                    if (un.matches("[A-Za-z0-9]+") && pw.matches("[A-Za-z0-9]+"))
                        user_service.addUser(un, pw, UserType.valueOf(ut));

                    else throw new Exception("Incorrect format of row");
                } catch (Exception e) {
                    System.out.println("Incorrect format of row " + resultEntry.getRow());
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
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
     * Writes updated data to database - overwrites existing rows with users that have the same username
     * @return boolean true or false if writing to db successful
     */
    public boolean write() {
        if (!tableExists(table_name)) createNewTable(sql_table);

        // 'Accounts' is the name of the table in db, with fields/col: Username, Password, Type
        // Values(?,?,?) Indicates we are entering data into each field - we can also replace '?' with static string references
        // REPLACE keyword overwrites all data in table, avoids duplicates iff field is set as unique
        // INSERT can be used to simply insert data to table
        String sql = "REPLACE INTO Accounts(Username, Password, Type) VALUES(?,?,?) ";
        Connection connection = makeConnection();

        try {
            for (List<String> user : user_service.getUserInfo()) {
                PreparedStatement entry;    //Prepared Statement object for non static entries

                //store the data we are entering into a preparedStatement
                entry = connection.prepareStatement(sql);   //creates the preparedStatement Object
                entry.setString(1, user.get(0));   //stores username to be entered for col 1
                entry.setString(2, user.get(1));   //stores password to be entered for col 2
                entry.setString(3, user.get(2));    //stores user type for col 3
                entry.execute();    //preparedStatement is entered into table, executes the query
                //System.out.println("Data has been inserted to Database!");
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
