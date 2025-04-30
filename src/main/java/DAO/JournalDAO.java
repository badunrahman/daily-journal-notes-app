package DAO;

import model.JournalEntry;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {
    private final Connection conn;

    public JournalDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** CREATE */
    public void saveEntry(JournalEntry e) throws SQLException {
        String sql = "INSERT INTO journal_entries(user_id,title,content,date_created,date_modified,tags) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getUserId());
            ps.setString(2, e.getTitle());
            ps.setString(3, e.getContent());
            ps.setString(4, e.getDateCreated().toString());
            ps.setString(5, e.getDateModified().toString());
            ps.setString(6, e.getTags());

            int result = ps.executeUpdate();
            System.out.println("Rows inserted: " + result);

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    e.setEntryId(generatedId);
                    System.out.println("Generated entry ID: " + generatedId);
                } else {
                    System.out.println("No ID generated for journal entry");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error saving journal entry: " + ex.getMessage());
            throw ex;
        }
    }

    /** READ single */
    public JournalEntry getEntryById(int entryId) throws SQLException {
        String sql = "SELECT * FROM journal_entries WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                } else {
                    System.out.println("No entry found with ID: " + entryId);
                    return null;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving entry by ID: " + ex.getMessage());
            throw ex;
        }
    }

    /** READ all for one user */
    public List<JournalEntry> getEntriesByUserId(String userId) throws SQLException {
        List<JournalEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM journal_entries WHERE user_id=? ORDER BY date_modified DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            System.out.println("Executing query for user ID: " + userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JournalEntry entry = mapRow(rs);
                    System.out.println("Found entry: " + entry.getTitle() + " (ID: " + entry.getEntryId() + ")");
                    list.add(entry);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving entries for user: " + ex.getMessage());
            throw ex;
        }

        System.out.println("Retrieved " + list.size() + " entries for user " + userId);
        return list;
    }

    /** UPDATE */
    public void updateEntry(JournalEntry e) throws SQLException {
        String sql = "UPDATE journal_entries SET title=?,content=?,date_modified=?,tags=? WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTitle());
            ps.setString(2, e.getContent());
            ps.setString(3, LocalDateTime.now().toString());
            ps.setString(4, e.getTags());
            ps.setInt(5, e.getEntryId());

            int result = ps.executeUpdate();
            System.out.println("Rows updated: " + result);

            if (result == 0) {
                System.out.println("Warning: No rows updated for entry ID: " + e.getEntryId());
            }
        } catch (SQLException ex) {
            System.err.println("Error updating journal entry: " + ex.getMessage());
            throw ex;
        }
    }

    /** DELETE */
    public void deleteEntry(int entryId) throws SQLException {
        // First delete related records in child tables
        deleteTodoItems(entryId);
        deleteToggleBlocks(entryId);

        // Then delete the journal entry
        String sql = "DELETE FROM journal_entries WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            int result = ps.executeUpdate();
            System.out.println("Deleted journal entry: " + (result > 0));
        } catch (SQLException ex) {
            System.err.println("Error deleting journal entry: " + ex.getMessage());
            throw ex;
        }
    }

    /** Helper method to delete todo items for an entry */
    private void deleteTodoItems(int entryId) throws SQLException {
        String sql = "DELETE FROM todo_items WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.executeUpdate();
        }
    }

    /** Helper method to delete toggle blocks for an entry */
    private void deleteToggleBlocks(int entryId) throws SQLException {
        String sql = "DELETE FROM toggle_blocks WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.executeUpdate();
        }
    }

    /** SEARCH by keyword */
    public List<JournalEntry> searchEntriesByKeyword(String userId, String keyword) throws SQLException {
        List<JournalEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM journal_entries WHERE user_id=? AND (title LIKE ? OR content LIKE ? OR tags LIKE ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            String kw = "%" + keyword + "%";
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error searching entries: " + ex.getMessage());
            throw ex;
        }
        return list;
    }

    private JournalEntry mapRow(ResultSet rs) throws SQLException {
        try {
            int entryId = rs.getInt("entry_id");
            String userId = rs.getString("user_id");
            String title = rs.getString("title");
            String content = rs.getString("content");
            String tags = rs.getString("tags");

            LocalDateTime dateCreated = null;
            String dateCreatedStr = rs.getString("date_created");
            if (dateCreatedStr != null && !dateCreatedStr.isEmpty()) {
                try {
                    dateCreated = LocalDateTime.parse(dateCreatedStr);
                } catch (Exception e) {
                    System.err.println("Error parsing date_created: " + e.getMessage());
                    dateCreated = LocalDateTime.now(); // Fallback to current time
                }
            }

            LocalDateTime dateModified = null;
            String dateModifiedStr = rs.getString("date_modified");
            if (dateModifiedStr != null && !dateModifiedStr.isEmpty()) {
                try {
                    dateModified = LocalDateTime.parse(dateModifiedStr);
                } catch (Exception e) {
                    System.err.println("Error parsing date_modified: " + e.getMessage());
                    dateModified = LocalDateTime.now(); // Fallback to current time
                }
            }

            JournalEntry entry = new JournalEntry(entryId, userId, title, content, tags, dateCreated, dateModified);
            return entry;

        } catch (SQLException e) {
            System.err.println("Error mapping row to JournalEntry: " + e.getMessage());
            throw e;
        }
    }
}