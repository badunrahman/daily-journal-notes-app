package controller;

import DAO.JournalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import model.JournalEntry;
import util.SessionManager;
import util.ViewLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import util.LanguageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import util.ThemeManager;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<JournalEntry> journalTableView;
    @FXML private TableColumn<JournalEntry, String> titleColumn;
    @FXML private TableColumn<JournalEntry, String> dateModifiedColumn;
    @FXML private TableColumn<JournalEntry, String> dateCreatedColumn;
    @FXML private TableColumn<JournalEntry, String> tagsColumn;
    @FXML private TableColumn<JournalEntry, Void> deleteColumn;
    @FXML private TextField searchField;
    @FXML private ToggleButton languageToggle;
    @FXML private MenuButton themeMenuButton;
    @FXML private Button newJournalButton;

    private final ObservableList<JournalEntry> masterList = FXCollections.observableArrayList();
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ThemeManager themeManager = ThemeManager.getInstance();

    @FXML
    public void initialize() {
        // Debug print to verify initialization
        System.out.println("DashboardController initializing...");

        // Set welcome message with current user's name
        if (SessionManager.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome " + SessionManager.getCurrentUser().getName());
            System.out.println("Current user: " + SessionManager.getCurrentUser().getName());
        } else {
            System.out.println("WARNING: No user is logged in!");
        }

        // Configure table columns
        setupTableColumns();

        // Load journal entries
        loadAllEntries();

        // Configure search functionality
        searchField.textProperty().addListener((o, old, nw) -> filter(nw));

        // Double-click handler for opening a journal entry
        journalTableView.setRowFactory(tv -> {
            TableRow<JournalEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    JournalEntry selected = row.getItem();
                    openJournalEntry(selected);
                }
            });
            return row;
        });

        // Language toggle handler
        languageToggle.setOnAction(event -> toggleLanguage());

        // Explicitly set up the new journal button handler
        newJournalButton.setOnAction(event -> handleNewJournal());

        // Set up theme menu
        setupThemeMenu();

        // Update UI language
        updateUILanguage();

        // Apply current theme to ensure it's properly initialized
        applyCurrentTheme();
    }

    // Add this method to explicitly apply the current theme when dashboard is loaded
    private void applyCurrentTheme() {
        String currentTheme = themeManager.getCurrentTheme();
        System.out.println("Applying current theme on dashboard initialization: " + currentTheme);

        // This ensures we re-apply the theme when the dashboard loads
        themeManager.setTheme(currentTheme);

        // Update the theme button text
        updateThemeButtonText();
    }

    private void setupTableColumns() {
        System.out.println("Setting up table columns...");

        // Configure cell value factories
        titleColumn.setCellValueFactory(cellData -> {
            JournalEntry entry = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> entry.getTitle());
        });

        // Format date columns to display readable dates
        dateModifiedColumn.setCellValueFactory(cellData -> {
            JournalEntry entry = cellData.getValue();
            if (entry.getDateModified() != null) {
                String formattedDate = entry.getDateModified().toString()
                        .replace("T", " ")  // Replace T with space
                        .substring(0, 16);  // Only show up to minutes
                return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "");
            }
        });

        dateCreatedColumn.setCellValueFactory(cellData -> {
            JournalEntry entry = cellData.getValue();
            if (entry.getDateCreated() != null) {
                String formattedDate = entry.getDateCreated().toString()
                        .replace("T", " ")  // Replace T with space
                        .substring(0, 16);  // Only show up to minutes
                return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "");
            }
        });

        // Setup tags column with colored tags
        tagsColumn.setCellFactory(column -> {
            return new TableCell<JournalEntry, String>() {
                private final FlowPane flowPane = new FlowPane();

                {
                    flowPane.setHgap(5);
                    flowPane.setVgap(5);
                    flowPane.setPrefWidth(100);
                    flowPane.setPadding(new Insets(2));
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }

                    JournalEntry entry = (JournalEntry) getTableRow().getItem();
                    String tagString = entry.getTags();

                    flowPane.getChildren().clear();

                    if (tagString != null && !tagString.isEmpty()) {
                        String[] tagParts = tagString.split(",");
                        for (String tag : tagParts) {
                            String[] parts = tag.split(":");
                            if (parts.length == 2) {
                                String name = parts[0].trim();
                                String color = parts[1].trim();

                                Label tagLabel = new Label(name);
                                tagLabel.setStyle(String.format(
                                        "-fx-background-color: %s; " +
                                                "-fx-text-fill: white; " +
                                                "-fx-background-radius: 10px; " +
                                                "-fx-padding: 2px 5px;",
                                        color
                                ));

                                flowPane.getChildren().add(tagLabel);
                            }
                        }
                    }

                    setGraphic(flowPane);
                    setText(null);
                }
            };
        });

        // Add delete button to each row
        setupDeleteColumn();

        System.out.println("Table columns setup complete");
    }

    private void setupDeleteColumn() {
        Callback<TableColumn<JournalEntry, Void>, TableCell<JournalEntry, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<JournalEntry, Void> call(final TableColumn<JournalEntry, Void> param) {
                return new TableCell<JournalEntry, Void>() {
                    private final Button deleteBtn = new Button();

                    {
                        deleteBtn.getStyleClass().add("delete-button");
                        deleteBtn.setText("🗑"); // Trash icon
                        deleteBtn.setOnAction(event -> {
                            JournalEntry entry = getTableView().getItems().get(getIndex());
                            handleDeleteEntry(entry);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        };

        deleteColumn.setCellFactory(cellFactory);
    }

    private void loadAllEntries() {
        try {
            if (SessionManager.getCurrentUser() == null) {
                System.out.println("No user logged in, can't load entries");
                return;
            }

            String userId = SessionManager.getCurrentUser().getUserID();
            System.out.println("Loading entries for user: " + userId);

            JournalDAO journalDAO = new JournalDAO();
            List<JournalEntry> entries = journalDAO.getEntriesByUserId(userId);

            System.out.println("Loaded " + entries.size() + " entries from database");

            // Clear and add all entries to the master list
            masterList.clear();
            masterList.addAll(entries);

            // Set items to the table view
            journalTableView.setItems(masterList);
            journalTableView.refresh();

            System.out.println("Table updated with " + masterList.size() + " entries");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load journal entries: " + e.getMessage());
        }
    }

    /**
     * Refreshes the table data by reloading entries from the database.
     * This can be called from other controllers to ensure the dashboard is up-to-date.
     */
    public void refreshTableData() {
        System.out.println("Refreshing dashboard table data...");
        loadAllEntries();
    }

    private void filter(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            journalTableView.setItems(masterList);
        } else {
            try {
                String userId = SessionManager.getCurrentUser().getUserID();
                journalTableView.setItems(FXCollections.observableArrayList(
                        new JournalDAO().searchEntriesByKeyword(userId, keyword)
                ));
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Search failed: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleNewJournal() {
        System.out.println("New Journal button clicked");

        // Clear any current entry in session
        SessionManager.setCurrentEntry(null);

        // Open EditorView for a new entry
        ViewLoader.load("view/EditorView.fxml");
    }

    @FXML
    public void handleLogout() {
        SessionManager.clear();
        ViewLoader.load("view/LoginView.fxml");
    }

    private void handleDeleteEntry(JournalEntry entry) {
        try {
            // Confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Journal");
            alert.setHeaderText("Delete Journal Entry");
            alert.setContentText("Are you sure you want to delete \"" + entry.getTitle() + "\"?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                new JournalDAO().deleteEntry(entry.getEntryId());
                masterList.remove(entry);
                journalTableView.refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete entry: " + e.getMessage());
        }
    }

    private void openJournalEntry(JournalEntry entry) {
        if (entry != null) {
            SessionManager.setCurrentEntry(entry);
            ViewLoader.load("view/EditorView.fxml");
        }
    }

    private void setupThemeMenu() {
        // Clear existing items to avoid duplicates if this method is called multiple times
        themeMenuButton.getItems().clear();

        // Create menu items for each theme
        MenuItem lightTheme = new MenuItem("Light");
        MenuItem darkTheme = new MenuItem("Dark");
        MenuItem blueTheme = new MenuItem("Blue");
        MenuItem greenTheme = new MenuItem("Green");
        MenuItem purpleTheme = new MenuItem("Purple");

        // Add action handlers
        lightTheme.setOnAction(e -> setTheme(ThemeManager.THEME_LIGHT));
        darkTheme.setOnAction(e -> setTheme(ThemeManager.THEME_DARK));
        blueTheme.setOnAction(e -> setTheme(ThemeManager.THEME_BLUE));
        greenTheme.setOnAction(e -> setTheme(ThemeManager.THEME_GREEN));
        purpleTheme.setOnAction(e -> setTheme(ThemeManager.THEME_PURPLE));

        // Add items to menu
        themeMenuButton.getItems().addAll(lightTheme, darkTheme, blueTheme, greenTheme, purpleTheme);

        // Make sure the theme menu button has the correct CSS class
        themeMenuButton.getStyleClass().add("theme-menu-button");

        // Set initial button text based on current theme
        updateThemeButtonText();
    }

    private void toggleLanguage() {
        // Toggle the language
        languageManager.toggleLanguage();
        System.out.println("Language toggled to: " + languageManager.getCurrentLocale().getDisplayLanguage());

        // Update UI text
        updateUILanguage();
    }

    private void updateThemeButtonText() {
        themeMenuButton.setText(themeManager.getCurrentThemeDisplayName());
    }

    private void setTheme(String themeName) {
        System.out.println("Setting theme to: " + themeName);
        themeManager.setTheme(themeName);
        updateThemeButtonText();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add this new method to update all UI text
    private void updateUILanguage() {
        // Update all static text elements
        welcomeLabel.setText(languageManager.getString("app.welcome",
                SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getName() : ""));
        newJournalButton.setText(languageManager.getString("dashboard.newJournal"));
        searchField.setPromptText(languageManager.getString("dashboard.search"));

        // Update column headers
        titleColumn.setText(languageManager.getString("dashboard.journalTitle"));
        dateModifiedColumn.setText(languageManager.getString("dashboard.dateModified"));
        dateCreatedColumn.setText(languageManager.getString("dashboard.dateCreated"));
        tagsColumn.setText(languageManager.getString("dashboard.tags"));

        // Update toggle button text
        languageToggle.setText(languageManager.getCurrentLanguageCode().equals("fr") ? "FR/EN" : "EN/FR");

        // Refresh the table to update any language-specific content
        journalTableView.refresh();
    }
}