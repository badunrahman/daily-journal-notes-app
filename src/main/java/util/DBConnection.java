package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBConnection {
    private static volatile DBConnection instance;
    // holds the actual JDBC connection object after opening it
    private Connection connection;

    // Use an absolute path that will work regardless of how the application is run
    private static final String DB_FOLDER = "src/main/resources";
    private static final String DB_NAME = "journal.db";

    private DBConnection() throws SQLException {
        try {
            // Ensure folder exists
            File folder = new File(DB_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Created folder: " + folder.getAbsolutePath());
            }

            // Get the absolute path to the database file
            File dbFile = new File(folder, DB_NAME);
            String dbPath = dbFile.getAbsolutePath();

            // Print diagnostic information
            System.out.println("Attempting to connect to database at: " + dbPath);
            System.out.println("File exists: " + dbFile.exists());

            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC driver loaded successfully");

            // Connect to the database
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            System.out.println("Successfully connected to the database");

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: SQLite JDBC driver not found");
            e.printStackTrace();
            throw new SQLException("SQLite JDBC driver not found.", e);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to initialize database connection");
            e.printStackTrace();
            throw new SQLException("Failed to initialize database connection: " + e.getMessage(), e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        } else if (instance.getConnection().isClosed()) {
            synchronized (DBConnection.class) {
                instance = new DBConnection();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to close database connection");
            e.printStackTrace();
        }
    }
}