package DAO;

import model.ToggleBlock;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToggleDAO {
    private final Connection conn;

    public ToggleDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** CREATE */
    public void addToggle(ToggleBlock t) throws SQLException {
        String sql = "INSERT INTO toggle_blocks(entry_id,title,content,is_expanded) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getEntryId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getContent());
            ps.setInt(4, t.isExpanded() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setToggleId(rs.getInt(1));
            }
        }
    }

    /** READ */
    public List<ToggleBlock> getTogglesByEntryId(int entryId) throws SQLException {
        List<ToggleBlock> list = new ArrayList<>();
        String sql = "SELECT * FROM toggle_blocks WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ToggleBlock(
                            entryId,
                            rs.getInt("toggle_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getInt("is_expanded") == 1
                    ));
                }
            }
        }
        return list;
    }

    /** UPDATE */
    public void updateToggle(ToggleBlock t) throws SQLException {
        String sql = "UPDATE toggle_blocks SET title=?,content=?,is_expanded=? WHERE toggle_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getContent());
            ps.setInt(3, t.isExpanded() ? 1 : 0);
            ps.setInt(4, t.getToggleId());
            ps.executeUpdate();
        }
    }

    /** DELETE */
    public void deleteToggle(int toggleId) throws SQLException {
        String sql = "DELETE FROM toggle_blocks WHERE toggle_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toggleId);
            ps.executeUpdate();
        }
    }
}
