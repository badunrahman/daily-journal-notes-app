package DAO;

import model.ToDoItem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoDAO {
    private final Connection conn;

    public ToDoDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** CREATE */
    public void addToDo(ToDoItem item) throws SQLException {
        String sql = "INSERT INTO todo_items(entry_id,description,is_done) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getEntryId());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.isDone() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setTodoId(rs.getInt(1));
            }
        }
    }

    /** READ */
    public List<ToDoItem> getToDosByEntryId(int entryId) throws SQLException {
        List<ToDoItem> list = new ArrayList<>();
        String sql = "SELECT * FROM todo_items WHERE entry_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ToDoItem(
                            entryId,
                            rs.getInt("todo_id"),
                            rs.getString("description"),
                            rs.getInt("is_done") == 1
                    ));
                }
            }
        }
        return list;
    }

    /** UPDATE */
    public void updateToDo(ToDoItem item) throws SQLException {
        String sql = "UPDATE todo_items SET description=?,is_done=? WHERE todo_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getDescription());
            ps.setInt(2, item.isDone() ? 1 : 0);
            ps.setInt(3, item.getTodoId());
            ps.executeUpdate();
        }
    }

    /** DELETE */
    public void deleteToDoItem(int todoId) throws SQLException {
        String sql = "DELETE FROM todo_items WHERE todo_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, todoId);
            ps.executeUpdate();
        }
    }
}
