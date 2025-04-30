package controller;

import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import util.LanguageManager;
import util.ViewLoader;

import java.sql.SQLException;

/**
 * Controller for the registration view.
 * Handles user registration and displays generated user ID.
 */
public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;
    @FXML private VBox idDisplayContainer;
    @FXML private TextField generatedIdField;
    @FXML private Button copyIdButton;
    @FXML private Label headerLabel;
    @FXML private Label subheaderLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Label idLabel;
    @FXML private Label saveIdLabel;

    private String generatedUserId = null;
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
        headerLabel.setText(languageManager.getString("register.title"));
        subheaderLabel.setText(languageManager.getString("register.subtitle"));
        usernameLabel.setText(languageManager.getString("register.username"));
        passwordLabel.setText(languageManager.getString("register.password"));
        confirmPasswordLabel.setText(languageManager.getString("register.confirmPassword"));
        registerButton.setText(languageManager.getString("register.button"));
        cancelButton.setText(languageManager.getString("register.cancel"));
        idLabel.setText(languageManager.getString("register.accountId"));
        saveIdLabel.setText(languageManager.getString("register.saveId"));
        copyIdButton.setText(languageManager.getString("register.copy"));

        // Update field placeholders
        usernameField.setPromptText(languageManager.getString("register.username"));
        passwordField.setPromptText(languageManager.getString("register.password"));
        confirmPasswordField.setPromptText(languageManager.getString("register.confirmPassword"));
    }

    /**
     * Handles the registration process when the Register button is clicked.
     * Validates input fields and creates a new user account.
     */
    @FXML
    private void handleRegister() {
        // Reset status
        statusLabel.setText("");
        statusLabel.getStyleClass().remove("success");

        // Validate input fields
        if (!validateFields()) {
            return;
        }

        // Try to create user
        try {
            UserDAO userDAO = new UserDAO();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                statusLabel.setText(languageManager.getString("register.error.userExists"));
                return;
            }

            // This will generate a UUID and store it in the database
            generatedUserId = userDAO.createUserAndReturnId(username, password);

            // Show success message and ID
            statusLabel.setText(languageManager.getString("register.success"));
            statusLabel.getStyleClass().add("success");

            // Display the generated ID
            generatedIdField.setText(generatedUserId);
            idDisplayContainer.setVisible(true);

            // Disable registration fields and button
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            confirmPasswordField.setDisable(true);
            registerButton.setDisable(true);

            // Change cancel button to "Continue to Login"
            cancelButton.setText(languageManager.getString("login.button"));

        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("UNIQUE constraint failed")) {
                statusLabel.setText(languageManager.getString("register.error.userExists"));
            } else {
                statusLabel.setText("Registration failed: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    /**
     * Handles copying the generated user ID to clipboard.
     */
    @FXML
    private void handleCopyId() {
        if (generatedUserId != null) {
            ClipboardContent content = new ClipboardContent();
            content.putString(generatedUserId);
            Clipboard.getSystemClipboard().setContent(content);

            // Provide feedback
            copyIdButton.setText(languageManager.getString("register.copied"));

            // Reset button text after delay
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() ->
                            copyIdButton.setText(languageManager.getString("register.copy")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Handles returning to the login screen.
     */
    @FXML
    private void handleCancel() {
        ViewLoader.load("view/LoginView.fxml");
    }

/**
 * Validates registration form fields.
 *
 * @return true if all fields are valid, false otherwise
 */
private boolean validateFields() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    // Check for empty fields
    if (username.isEmpty()) {
        statusLabel.setText(languageManager.getString("register.error.emptyUsername"));
        return false;
    }

    if (password.isEmpty()) {
        statusLabel.setText(languageManager.getString("register.error.emptyPassword"));
        return false;
    }

    // Check password length
    if (password.length() < 4) {
        statusLabel.setText(languageManager.getString("register.error.shortPassword"));
        return false;
    }

    // Check if passwords match
    if (!password.equals(confirmPassword)) {
        statusLabel.setText(languageManager.getString("register.error.passwordMismatch"));
        return false;
    }

    return true;
}
}