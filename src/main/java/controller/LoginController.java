package controller;

import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import util.LanguageManager;
import util.SessionManager;
import util.ViewLoader;

import java.sql.SQLException;

/**
 * Controller for the login view.
 * Handles user authentication and navigation to register view.
 */
public class LoginController {
    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;
    @FXML private Label welcomeLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label userIdLabel;
    @FXML private Label passwordLabel;
    @FXML private Label noAccountLabel;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    @FXML
    public void initialize() {
        // Update UI with current language
        updateUILanguage();
    }

    /**
     * Updates all UI text based on the current language.
     */
    private void updateUILanguage() {
        // Update static text elements
        welcomeLabel.setText(languageManager.getString("login.welcome"));
        subtitleLabel.setText(languageManager.getString("login.subtitle"));
        userIdLabel.setText(languageManager.getString("login.userid"));
        passwordLabel.setText(languageManager.getString("login.password"));
        loginButton.setText(languageManager.getString("login.button"));
        noAccountLabel.setText(languageManager.getString("login.noAccount"));
        createAccountButton.setText(languageManager.getString("login.createAccount"));

        // Update field placeholders
        userIdField.setPromptText(languageManager.getString("login.userid"));
        passwordField.setPromptText(languageManager.getString("login.password"));
    }

    /**
     * Handles the login process when the Login button is clicked.
     * Validates credentials and navigates to dashboard on success.
     */
    @FXML
    private void handleLogin() {
        // Clear previous status message
        statusLabel.setText("");

        String userId = userIdField.getText().trim();
        String password = passwordField.getText();

        // Check for empty fields
        if (userId.isEmpty() || password.isEmpty()) {
            statusLabel.setText(languageManager.getString("login.error.empty"));
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Try to authenticate by ID first
            User user = userDAO.getUserById(userId);

            if (user != null && user.getPassword().equals(password)) {
                loginSuccess(user);
            } else {
                // If ID login fails, try username/password login
                user = userDAO.authenticateUser(userId, password);

                if (user != null) {
                    loginSuccess(user);
                } else {
                    loginFailed();
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles successful login.
     * Sets the current user in session and navigates to dashboard.
     *
     * @param user The authenticated user
     */
    private void loginSuccess(User user) {
        // Store user in session
        SessionManager.setCurrentUser(user);

        // Show success message
        statusLabel.setText("✅ " + languageManager.getString("register.success"));

        // Navigate to dashboard
        ViewLoader.load("view/DashboardView.fxml");
    }

    /**
     * Handles failed login attempt.
     */
    private void loginFailed() {
        statusLabel.setText("❌ " + languageManager.getString("login.error.invalid"));
        passwordField.clear();
    }

    /**
     * Navigates to the registration view.
     */
    @FXML
    private void handleCreateAccount() {
        ViewLoader.load("view/RegisterView.fxml");
    }
}