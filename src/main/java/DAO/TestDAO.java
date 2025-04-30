package DAO;

import model.User;

import java.sql.SQLException;

public class TestDAO {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();

            // 1. Create new user
            System.out.println("Creating user:");
            userDAO.createUser("bob", "pw123");

            // 2. Authenticate the same user
            User u = userDAO.authenticateUser("bob", "pw123");

            if (u != null) {
                System.out.println("✅ Logged in as: " + u.getName() + " (ID: " + u.getUserID() + ")");
            } else {
                System.out.println("❌ Login failed.");
            }

        } catch (SQLException e) {
            System.out.println("🚨 SQL Error:");
            e.printStackTrace();
        }
    }
}
