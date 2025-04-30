package controller;

import DAO.JournalDAO;
import DAO.ToDoDAO;
import DAO.ToggleDAO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.JournalEntry;
import model.Tag;
import model.ToDoItem;
import model.ToggleBlock;
import util.BlockFactory;
import util.SessionManager;
import util.ViewLoader;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.LanguageManager;

public class EditorController {
    @FXML private TextField titleField;
    @FXML private TextField tagsField;
    @FXML private ComboBox<String> tagColorComboBox;
    @FXML private Button addTagButton;
    @FXML private FlowPane tagContainer;
    @FXML private VBox blockContainer;
    @FXML private TextField commandField;
    @FXML private TextArea mainTextArea;
    @FXML private Label saveStatusLabel;
    @FXML private Button boldButton;
    @FXML private Button italicButton;
    @FXML private Button underlineButton;
    @FXML private Button headingButton;
    @FXML private Button bulletButton;
    @FXML private Button todoButton;
    @FXML private Button toggleButton;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private JournalEntry entry;
    private JournalDAO journalDAO;
    private ToDoDAO todoDAO;
    private ToggleDAO toggleDAO;
    private final List<Tag> tags = new ArrayList<>();
    private final Map<String, String> colorMap = new HashMap<>();
    private final PauseTransition autoSaveDelay = new PauseTransition(Duration.seconds(2));
    private final LanguageManager languageManager = LanguageManager.getInstance();

    // Lists to track the blocks to save to the database
    private final List<ToDoItem> todoItems = new ArrayList<>();
    private final List<ToggleBlock> toggleBlocks = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("EditorController initializing...");

        try {
            // Initialize DAOs
            journalDAO = new JournalDAO();
            todoDAO = new ToDoDAO();
            toggleDAO = new ToggleDAO();

            // Initialize the color map
            setupColorMap();

            // Initialize the color combo box
            tagColorComboBox.getItems().addAll(
                    "Red", "Blue", "Green", "Purple", "Orange",
                    "Yellow", "Pink", "Cyan", "Gray", "Black"
            );
            tagColorComboBox.setValue("Blue");

            // Add tag button action
            addTagButton.setOnAction(e -> addTag());

            // Load existing entry if editing
            entry = SessionManager.getCurrentEntry();
            if (entry != null) {
                System.out.println("Editing existing entry: " + entry.getTitle());
                titleField.setText(entry.getTitle());
                mainTextArea.setText(entry.getContent());
                loadTags(entry.getTags());

                // Load existing blocks (To-Dos & Toggles)
                loadExistingBlocks();
            } else {
                System.out.println("Creating new entry");
            }

            // Setup auto-save feature
            setupAutoSave();

            // Setup text area listeners
            setupTextAreaListeners();

        } catch (SQLException e) {
            System.err.println("Failed to initialize EditorController:");
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
        }
        updateUILanguage();
    }

    private void setupColorMap() {
        colorMap.put("Red", "#ff5555");
        colorMap.put("Blue", "#5599ff");
        colorMap.put("Green", "#55cc55");
        colorMap.put("Purple", "#9955dd");
        colorMap.put("Orange", "#ff9955");
        colorMap.put("Yellow", "#ffdd55");
        colorMap.put("Pink", "#ff55aa");
        colorMap.put("Cyan", "#55dddd");
        colorMap.put("Gray", "#999999");
        colorMap.put("Black", "#333333");
    }

    private void loadTags(String tagString) {
        if (tagString == null || tagString.isEmpty()) return;

        // Parse tag string (format: name:color,name:color)
        String[] tagParts = tagString.split(",");
        for (String tag : tagParts) {
            String[] parts = tag.split(":");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String color = parts[1].trim();

                // Create tag object and add to list
                Tag newTag = new Tag(name, color);
                tags.add(newTag);

                // Add tag to UI
                createTagLabel(newTag);
            }
        }
    }

    private void addTag() {
        String tagText = tagsField.getText().trim();
        if (tagText.isEmpty()) return;

        String color = colorMap.get(tagColorComboBox.getValue());

        // Create new tag
        Tag newTag = new Tag(tagText, color);
        tags.add(newTag);

        // Create visual tag representation
        createTagLabel(newTag);

        // Clear input field
        tagsField.clear();
    }

    private void createTagLabel(Tag tag) {
        Label tagLabel = new Label(tag.getName());
        tagLabel.getStyleClass().add("tag-label");

        // Set background color from tag color
        String style = String.format("-fx-background-color: %s; -fx-text-fill: white;", tag.getColor());
        tagLabel.setStyle(style);

        // Add padding and make it look like a tag
        tagLabel.setPadding(new Insets(3, 8, 3, 8));

        // Add click handler to remove tag
        tagLabel.setOnMouseClicked(e -> removeTag(tag, tagLabel));

        // Add to container
        tagContainer.getChildren().add(tagLabel);
    }

    private void removeTag(Tag tag, Label tagLabel) {
        tags.remove(tag);
        tagContainer.getChildren().remove(tagLabel);

        // Trigger auto-save
        triggerAutoSave();
    }

    private void loadExistingBlocks() {
        try {
            if (entry == null || entry.getEntryId() == 0) {
                System.out.println("No entry to load blocks for");
                return;
            }

            int entryId = entry.getEntryId();

            // Load Todo items
            List<ToDoItem> todos = todoDAO.getToDosByEntryId(entryId);
            System.out.println("Loaded " + todos.size() + " todo items");

            for (ToDoItem todo : todos) {
                // Create a visual representation
                Node todoNode = BlockFactory.create("todo", todo.getDescription());
                blockContainer.getChildren().add(todoNode);

                // Add to tracking list
                todoItems.add(todo);
            }

            // Load Toggle blocks
            List<ToggleBlock> toggles = toggleDAO.getTogglesByEntryId(entryId);
            System.out.println("Loaded " + toggles.size() + " toggle blocks");

            for (ToggleBlock toggle : toggles) {
                // Create a visual representation
                Node toggleNode = BlockFactory.create("toggle", toggle.getContent());
                blockContainer.getChildren().add(toggleNode);

                // Add to tracking list
                toggleBlocks.add(toggle);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load existing blocks:");
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load content blocks: " + e.getMessage());
        }
    }

    private void setupTextAreaListeners() {
        mainTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                // Get current text and caret position
                String text = mainTextArea.getText();
                int caretPosition = mainTextArea.getCaretPosition();

                // Find the start of the current line
                int lineStart = text.lastIndexOf('\n', caretPosition - 1);
                if (lineStart == -1) lineStart = 0;
                else lineStart++; // Move past the newline character

                // Get the current line content
                String currentLine = text.substring(lineStart, caretPosition);

                // Check for ordered list pattern (e.g., "1. Some text")
                if (currentLine.matches("^\\d+\\. .*")) {
                    // Extract the number and increment
                    String numStr = currentLine.replaceAll("^(\\d+)\\. .*", "$1");
                    try {
                        int num = Integer.parseInt(numStr);
                        String nextItem = "\n" + (num + 1) + ". ";

                        // Insert the next numbered item
                        event.consume(); // Prevent default Enter behavior
                        mainTextArea.insertText(caretPosition, nextItem);
                    } catch (NumberFormatException e) {
                        // Not a valid number, ignore
                    }
                }
                // Check for bullet list pattern (e.g., "• Some text")
                else if (currentLine.startsWith("• ")) {
                    event.consume(); // Prevent default Enter behavior
                    mainTextArea.insertText(caretPosition, "\n• ");
                }
                // Check for dash list pattern (e.g., "- Some text")
                else if (currentLine.startsWith("- ")) {
                    event.consume(); // Prevent default Enter behavior
                    mainTextArea.insertText(caretPosition, "\n- ");
                }

                // Trigger auto-save
                triggerAutoSave();
            }
        });

        // Setup auto-detection for list formatting
        mainTextArea.textProperty().addListener((obs, oldText, newText) -> {
            int caretPosition = mainTextArea.getCaretPosition();

            // If the text is too short or no change, skip
            if (newText == null || newText.isEmpty() || newText.equals(oldText)) {
                return;
            }

            // Check if the user just typed "1. " at start of line
            if (newText.endsWith("1. ") && (newText.length() == 3 || newText.charAt(newText.length() - 4) == '\n')) {
                // This activates the first item in a numbered list
                System.out.println("Numbered list detected");
            }

            // Check if the user just typed "- " at start of line
            if (newText.endsWith("- ") && (newText.length() == 2 || newText.charAt(newText.length() - 3) == '\n')) {
                // This activates bullet list with dash
                System.out.println("Dash list detected");
            }
        });

        System.out.println("Text area listeners set up");
    }

    private void setupAutoSave() {
        // Set up the auto-save delay
        autoSaveDelay.setOnFinished(e -> saveEntry(true));

        // Listen for changes to trigger auto-save
        titleField.textProperty().addListener((obs, old, newVal) -> triggerAutoSave());
        mainTextArea.textProperty().addListener((obs, old, newVal) -> triggerAutoSave());
    }

    private void triggerAutoSave() {
        saveStatusLabel.setText(languageManager.getString("editor.saving"));
        autoSaveDelay.playFromStart();
    }

    @FXML
    public void handleBoldButton() {
        applyFormatting("bold");
    }

    @FXML
    public void handleItalicButton() {
        applyFormatting("italic");
    }

    @FXML
    public void handleUnderlineButton() {
        applyFormatting("underline");
    }

    @FXML
    public void handleHeadingButton() {
        insertHeading();
    }

    @FXML
    public void handleBulletButton() {
        insertBulletList();
    }

    private void applyFormatting(String format) {
        // Get the selected text
        TextArea textArea = mainTextArea;
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();

        if (start == end) {
            // No selection, show a message
            saveStatusLabel.setText(languageManager.getString("editor.formatFirst"));
            return;
        }

        String selectedText = textArea.getSelectedText();
        String formattedText;

        // Apply formatting based on the type
        switch (format) {
            case "bold":
                formattedText = "**" + selectedText + "**";
                break;
            case "italic":
                formattedText = "*" + selectedText + "*";
                break;
            case "underline":
                formattedText = "_" + selectedText + "_";
                break;
            default:
                return;
        }

        // Replace the selected text with formatted text
        textArea.replaceSelection(formattedText);

        // Reselect the text including formatting marks
        textArea.selectRange(start, start + formattedText.length());

        // Trigger auto-save
        triggerAutoSave();

        // Show confirmation
        saveStatusLabel.setText(languageManager.getString("editor.formatApplied"));
    }

    @FXML
    public void handleCommand() {
        String cmd = commandField.getText().trim();
        if (cmd.isEmpty()) return;

        if (cmd.startsWith("/todo")) {
            insertTodoBlock();
        } else if (cmd.startsWith("/toggle")) {
            insertToggleBlock();
        } else if (cmd.startsWith("/bullet")) {
            insertBulletList();
        } else if (cmd.startsWith("/heading")) {
            insertHeading();
        }

        commandField.clear();
    }

    @FXML
    public void insertTodoBlock() {
        Node todoBlock = BlockFactory.create("todo", "");
        blockContainer.getChildren().add(todoBlock);

        // Add a new ToDo item to track
        if (entry != null && entry.getEntryId() != 0) {
            ToDoItem newTodo = new ToDoItem(entry.getEntryId(), 0, "", false);
            todoItems.add(newTodo);
        }

        // Trigger auto-save
        triggerAutoSave();
    }

    @FXML
    public void insertToggleBlock() {
        Node toggleBlock = BlockFactory.create("toggle", "");
        blockContainer.getChildren().add(toggleBlock);

        // Add a new Toggle block to track
        if (entry != null && entry.getEntryId() != 0) {
            ToggleBlock newToggle = new ToggleBlock(entry.getEntryId(), 0, "Toggle Title", "", false);
            toggleBlocks.add(newToggle);
        }

        // Trigger auto-save
        triggerAutoSave();
    }

    private void insertBulletList() {
        // Insert bullet point at cursor position
        int caretPosition = mainTextArea.getCaretPosition();
        mainTextArea.insertText(caretPosition, "\n• ");
        mainTextArea.positionCaret(caretPosition + 3);

        // Trigger auto-save
        triggerAutoSave();
    }

    private void insertHeading() {
        // Insert heading block
        Node headingBlock = BlockFactory.create("heading", "");
        blockContainer.getChildren().add(headingBlock);

        // Trigger auto-save
        triggerAutoSave();
    }

    @FXML
    public void handleSave() {
        saveEntry(false);
    }

    private void saveEntry(boolean isAutoSave) {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                title = "Untitled - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }

            if (entry == null) {
                entry = new JournalEntry();
                entry.setUserId(SessionManager.getCurrentUser().getUserID());
                entry.setDateCreated(LocalDateTime.now());
            }

            // Update entry fields
            entry.setTitle(title);
            entry.setContent(mainTextArea.getText());
            entry.setDateModified(LocalDateTime.now());

            // Build tag string
            StringBuilder tagBuilder = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                tagBuilder.append(tag.getName()).append(":").append(tag.getColor());
                if (i < tags.size() - 1) {
                    tagBuilder.append(",");
                }
            }
            entry.setTags(tagBuilder.toString());

            // Save journal entry to database
            if (entry.getEntryId() == 0) {
                journalDAO.saveEntry(entry);
                System.out.println("New entry saved with ID: " + entry.getEntryId());
            } else {
                journalDAO.updateEntry(entry);
                System.out.println("Updated entry with ID: " + entry.getEntryId());
            }

            // Save ToDo items - simplified for now
            // In a complete implementation, you'd gather data from UI
            for (ToDoItem item : todoItems) {
                item.setEntryId(entry.getEntryId());
                if (item.getTodoId() == 0) {
                    todoDAO.addToDo(item);
                } else {
                    todoDAO.updateToDo(item);
                }
            }

            // Save Toggle blocks - simplified for now
            for (ToggleBlock toggle : toggleBlocks) {
                toggle.setEntryId(entry.getEntryId());
                if (toggle.getToggleId() == 0) {
                    toggleDAO.addToggle(toggle);
                } else {
                    toggleDAO.updateToggle(toggle);
                }
            }

            // Update save status
            saveStatusLabel.setText(languageManager.getString("editor.saved"));

            // Only navigate back if this is a manual save and not auto-save
            if (!isAutoSave) {
                try {
                    // Return to dashboard
                    ViewLoader.load("view/DashboardView.fxml");

                    // Try to refresh dashboard
                    try {
                        DashboardController dashboardController =
                                (DashboardController) ViewLoader.getController("view/DashboardView.fxml");
                        if (dashboardController != null) {
                            dashboardController.refreshTableData();
                        }
                    } catch (Exception e) {
                        System.err.println("Could not refresh dashboard: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Error returning to dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            saveStatusLabel.setText(languageManager.getString("editor.saveFailed"));

            // Show error alert
            showErrorAlert("editor.title", "editor.error.save", e.getMessage());
        }
    }

    @FXML
    public void handleBack() {
        // Save before going back
        saveEntry(false);
    }

    private void showErrorAlert(String titleKey, String messageKey, Object... params) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(languageManager.getString(titleKey));
        alert.setHeaderText(null);
        alert.setContentText(languageManager.getString(messageKey, params));
        alert.showAndWait();
    }
    // Add this new method to update all UI text
    private void updateUILanguage() {
        // Update static text elements
        backButton.setText(languageManager.getString("editor.back"));
        saveStatusLabel.setText(languageManager.getString("editor.saved"));

        // Update field labels and placeholders
        ((Label) titleField.getParent().getChildrenUnmodifiable().get(0)).setText(
                languageManager.getString("editor.title.label"));
        titleField.setPromptText(languageManager.getString("editor.placeholder"));

        ((Label) tagsField.getParent().getParent().getChildrenUnmodifiable().get(0)).setText(
                languageManager.getString("editor.tags.label"));
        tagColorComboBox.setPromptText(languageManager.getString("editor.selectColor"));
        tagsField.setPromptText(languageManager.getString("editor.enterTags"));

        // Update text area placeholder
        mainTextArea.setPromptText(languageManager.getString("editor.placeholder"));

        // Update button text
        boldButton.setText(languageManager.getString("button.bold"));
        italicButton.setText(languageManager.getString("button.italic"));
        underlineButton.setText(languageManager.getString("button.underline"));
        headingButton.setText(languageManager.getString("button.heading"));
        bulletButton.setText(languageManager.getString("button.bullet"));
        todoButton.setText(languageManager.getString("button.todo"));
        toggleButton.setText(languageManager.getString("button.toggle"));
        saveButton.setText(languageManager.getString("editor.save"));

        // Update command field placeholder
        commandField.setPromptText(languageManager.getString("editor.commandPrompt"));

        // Update color names in the combo box
        String[] colors = {"Red", "Blue", "Green", "Purple", "Orange", "Yellow", "Pink", "Cyan", "Gray", "Black"};
        String currentValue = tagColorComboBox.getValue();

        tagColorComboBox.getItems().clear();
        for (String color : colors) {
            tagColorComboBox.getItems().add(languageManager.getString("color." + color.toLowerCase()));
        }

        // Restore the selected value if possible
        if (currentValue != null) {
            for (String item : tagColorComboBox.getItems()) {
                if (item.equals(languageManager.getString("color." + currentValue.toLowerCase()))) {
                    tagColorComboBox.setValue(item);
                    break;
                }
            }
        }
    }
}