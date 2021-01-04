package database;

import java.sql.*;
import java.util.List;

/**
 * Gateway class containing operations for tables in the DB.
 */
public abstract class DatabaseReadWriter {

    private final String address;

    /**
     * Constructor to set file Path
     *
     * @param address String address for DB - i.e. "jdbc:sqlite: *filepath* "
     */
    public DatabaseReadWriter(String address) {
        this.address=address;
    }

    /**
     * Connection method to establish a connection to DB, when
     * doing any operations with tables inside the DB
     */
    public Connection makeConnection() {
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            //Address for DB file
            connection = DriverManager.getConnection(address);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + ": " + "Compilation Error with external Library. Bad Connection!");
        }
        return connection;
    }

    /**
     * Checks if table exists in the database
     * @param tableName String name of Table
     * @return  boolean true or false if table exists in DB
     */
    public boolean tableExists(String tableName) {
        Connection connection = makeConnection();

        try {
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet allTables = dmd.getTables(null, null, tableName, null);
            return allTables.next();
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Deletes/Clears all data/rows from a given table in DB
     * @param tableName String name of the table in the DB that is being cleared
     * @return boolean true or false if data cleared from table successfully
     */
    public boolean deleteAllData(String tableName) {
        String sql = "DELETE FROM ";
        Connection connection = makeConnection();

        try {
            PreparedStatement entryToDelete = connection.prepareStatement(sql + tableName);
            entryToDelete.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Entry not deleted.");
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Creates a new Table in the DB based on SQL command passed through parameter
     * @param sql String SQL command for table being created
     */
    public void createNewTable(String sql) {
        Connection connection = makeConnection();

        try {
            Statement action = connection.createStatement();
            action.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Deletes table from the DB
     * @param tableName name of table being deleted
     * @return boolean true or false if table is deleted successfully
     */
    public boolean deleteTable(String tableName) {
        Connection connection = makeConnection();
        String sql = "DROP TABLE ";

        try {
            Statement action = connection.createStatement();
            action.executeUpdate(sql+tableName);
            return true;
        } catch(Exception e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                connection.close();
            } catch(SQLException e){
                System.out.println(e.toString());
            } try {
                connection.close();
            }catch(SQLException e){
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Displays all rows of data in table - Intended to be used to see data from DB in program
     * @param tableName String table name
     * @param colNum int number of columns in table being displayed
     */
    public void displayAllRows(String tableName, int colNum) {
        Connection connection = makeConnection();
        String sql = "SELECT * FROM ";

        try {
            Statement action = connection.createStatement();
            ResultSet actionExecute = action.executeQuery(sql+tableName);
            System.out.println("Displaying Data from: " + tableName + "\n\n");
            while (actionExecute.next()) {
                for (int x=1; x<=colNum; x++) {
                    System.out.print(actionExecute.getString(x) + ", ");
                }
                System.out.println();
            }
        } catch(Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                connection.close();
            } catch(SQLException e){
                System.out.println(e.toString());
            } try {
                connection.close();
            }catch(SQLException e){
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Adds data directly into Tables in DB
     * @param entries List of String entries for one row, each index represents entry in each col
     * @param sqlTableCol String db table name with parameters as col names
     * @param valueCol String db values with param as (?,?,?,...) for each col
     * @return boolean true or false if adding successful
     */
    public boolean insertIntoDB(List<String> entries, String sqlTableCol, String valueCol) {
        Connection connection = makeConnection();

        String sql = "INSERT INTO " + sqlTableCol + " " + valueCol;

        try {
            PreparedStatement entry = connection.prepareStatement(sql);

            for (int x = 1; x<entries.size()+1; x++) {
                entry.setObject(x, entries.get(x-1));
            }
            entry.executeUpdate();
            return true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    /**
     * Removes row from table
     * @param tableName String name of target table
     * @param uniqueCol String name of target col
     * @param value String name of target value in col
     * @return boolean true or false if removal successful
     */
    public boolean deleteRowFromDB(String tableName, String uniqueCol, String value) {
        Connection connection = makeConnection();

        String sql = "DELETE FROM " + tableName + " WHERE " + uniqueCol + " = ?";

        try {
            PreparedStatement remove = connection.prepareStatement(sql);
            remove.setObject(1, value);
            remove.executeUpdate();
            return true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    /**
     * Read DB
     * @return boolean true or false if reading successful
     */
    public abstract boolean read();

    /**
     * Write updated info to DB
     * @return boolean true or false if writing successful
     */
    public abstract boolean write();

}
