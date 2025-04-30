package DAO;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO {
    private final Connection conn;

    public UserDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** CREATE: generate UUID for user_id, persist, and print it to console */
    public void createUser(String username, String password) throws SQLException {
        String userId = UUID.randomUUID().toString();
        String sql = "INSERT INTO users(user_id,username,password) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
        }
        System.out.println("🆔 New User ID: " + userId);
    }

    /**
     * CREATE: generate UUID for user_id, persist, and return the generated ID
     * This is a new method for the registration process
     */
    public String createUserAndReturnId(String username, String password) throws SQLException {
        String userId = UUID.randomUUID().toString();
        String sql = "INSERT INTO users(user_id,username,password) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
        }
        return userId;
    }

    /** READ: authenticate by username/password */
    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT user_id,username,password FROM users WHERE username=? AND password=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    /** READ: by user_id */
    public User getUserById(String userId) throws SQLException {
        String sql = "SELECT username,password FROM users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            userId,
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    /** READ: check if a username already exists */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    /** UPDATE: username/password for given user_id */
    public void updateUser(User u) throws SQLException {
        String sql = "UPDATE users SET username=?, password=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getUserID());
            ps.executeUpdate();
        }
    }

    /** DELETE: remove by user_id */
    public void deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }


}