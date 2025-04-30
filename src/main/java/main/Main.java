package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.DBConnection;
import util.ViewLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import util.LanguageManager;

import util.ThemeManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Set the primary stage in the ViewLoader
            ViewLoader.setStage(stage);

            // Initialize the database
            boolean dbInitialized = initializeDatabase();

            if (!dbInitialized) {
                // If database initialization failed, show error and exit
                showErrorAlert("Database Error",
                        "Could not initialize the database. The application will now exit.");
                Platform.exit();
                return;
            }

            // Initialize language manager
            LanguageManager.getInstance();
            System.out.println("Language manager initialized with default locale: " +
                    LanguageManager.getInstance().getCurrentLocale().getDisplayLanguage());

            // Load the login view first
            ViewLoader.load("view/LoginView.fxml");

            // Set application-wide properties
            stage.setMinWidth(600);
            stage.setMinHeight(500);
            stage.setTitle("Daily Journal & Notes");

            // Initialize the theme manager AFTER scene is created
            // This is a critical change - we must set the scene after it's been created by ViewLoader
            if (stage.getScene() != null) {
                ThemeManager.getInstance().setScene(stage.getScene());
                System.out.println("Theme manager initialized with theme: " +
                        ThemeManager.getInstance().getCurrentTheme());
            } else {
                System.err.println("WARNING: Stage scene is null, theme will not be applied");
            }

            // Show the stage
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Application Error", "Failed to start application: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Initializes the database, creating tables if they don't exist.
     *
     * @return true if database initialization was successful, false otherwise
     */
    private boolean initializeDatabase() {
        Connection conn = null;
        try {
            System.out.println("Initializing database...");

            // Get database connection
            conn = DBConnection.getInstance().getConnection();

            // Create tables if they don't exist
            try (Statement stmt = conn.createStatement()) {
                // Create users table
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS users (" +
                                "  user_id TEXT PRIMARY KEY," +
                                "  username TEXT NOT NULL UNIQUE," +
                                "  password TEXT NOT NULL" +
                                ");"
                );

                // Create journal entries table
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS journal_entries (" +
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

                // Create to-do items table
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS todo_items (" +
                                "  todo_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "  entry_id  INTEGER NOT NULL," +
                                "  description TEXT," +
                                "  is_done   INTEGER," +
                                "  FOREIGN KEY(entry_id) REFERENCES journal_entries(entry_id) ON DELETE CASCADE" +
                                ");"
                );

                // Create toggle blocks table
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS toggle_blocks (" +
                                "  toggle_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "  entry_id    INTEGER NOT NULL," +
                                "  title       TEXT," +
                                "  content     TEXT," +
                                "  is_expanded INTEGER," +
                                "  FOREIGN KEY(entry_id) REFERENCES journal_entries(entry_id) ON DELETE CASCADE" +
                                ");"
                );

                System.out.println("✅ Database tables created successfully.");
            }

            return true;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to initialize database");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Shows an error alert dialog.
     *
     * @param title The alert title
     * @param content The alert content message
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}