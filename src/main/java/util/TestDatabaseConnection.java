package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Standalone utility to test database connection and table creation.
 * Run this class directly to diagnose database issues.
 */
public class TestDatabaseConnection {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            System.out.println("Attempting to connect to database...");

            // Get connection
            conn = DBConnection.getInstance().getConnection();
            System.out.println("Connection successful!");

            // Test creating a table
            System.out.println("Testing table creation...");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT)");
                System.out.println("Table creation successful!");

                // Cleanup test table
                stmt.execute("DROP TABLE IF EXISTS test_table");
                System.out.println("Test table cleaned up.");
            }

            System.out.println("All database tests passed successfully!");

        } catch (Exception e) {
            System.err.println("ERROR: Database test failed");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connection closed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}