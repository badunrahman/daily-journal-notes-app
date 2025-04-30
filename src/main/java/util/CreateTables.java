package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Run this class once to drop & re‑create all tables with the correct TEXT user_id.
 */
public class CreateTables {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop old tables if they exist (to avoid conflicts)
            stmt.execute("DROP TABLE IF EXISTS toggle_blocks;");
            stmt.execute("DROP TABLE IF EXISTS todo_items;");
            stmt.execute("DROP TABLE IF EXISTS journal_entries;");
            stmt.execute("DROP TABLE IF EXISTS users;");

            // 1) Users table (user_id as TEXT primary key)
            stmt.execute(
                    "CREATE TABLE users (" +
                            "  user_id TEXT PRIMARY KEY," +
                            "  username TEXT NOT NULL UNIQUE," +
                            "  password TEXT NOT NULL" +
                            ");"
            );

            // 2) Journal entries (user_id TEXT FK → users.user_id)
            stmt.execute(
                    "CREATE TABLE journal_entries (" +
                            "  entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  user_id  TEXT NOT NULL," +
                            "  title    TEXT," +
                            "  content  TEXT," +
                            "  date_created  TEXT," +
                            "  date_modified TEXT," +
                            "  tags     TEXT," +
                            "  FOREIGN KEY(user_id) REFERENCES users(user_id)" +
                            ");"
            );

            // 3) To‑do items
            stmt.execute(
                    "CREATE TABLE todo_items (" +
                            "  todo_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  entry_id  INTEGER NOT NULL," +
                            "  description TEXT," +
                            "  is_done   INTEGER," +
                            "  FOREIGN KEY(entry_id) REFERENCES journal_entries(entry_id)" +
                            ");"
            );

            // 4) Toggle blocks
            stmt.execute(
                    "CREATE TABLE toggle_blocks (" +
                            "  toggle_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  entry_id    INTEGER NOT NULL," +
                            "  title       TEXT," +
                            "  content     TEXT," +
                            "  is_expanded INTEGER," +
                            "  FOREIGN KEY(entry_id) REFERENCES journal_entries(entry_id)" +
                            ");"
            );

            System.out.println("✅ All tables dropped & recreated.");
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables:");
            e.printStackTrace();
        }
    }
}
