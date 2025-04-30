package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Factory class for creating different types of content blocks.
 * Implements the Factory pattern to generate UI components for todos, toggles, etc.
 */
public class BlockFactory {

    /**
     * Creates a content block based on the specified type.
     *
     * @param type The type of block to create ("todo", "toggle", etc.)
     * @param content Initial content for the block (can be empty)
     * @return A JavaFX Node representing the requested block type
     */
    public static Node create(String type, String content) {
        switch (type.toLowerCase()) {
            case "todo":
                return createTodoBlock(content);
            case "toggle":
                return createToggleBlock(content);
            case "bullet":
                return createBulletItem(content);
            case "heading":
                return createHeadingBlock(content);
            default:
                // Default to a simple text field
                TextField defaultField = new TextField(content);
                defaultField.setPromptText("Text block");
                return defaultField;
        }
    }

    /**
     * Creates a todo list item with a checkbox and text field.
     *
     * @param content Initial content for the todo item
     * @return A VBox containing the todo item
     */
    private static Node createTodoBlock(String content) {
        VBox todoBlock = new VBox(5);
        todoBlock.getStyleClass().add("todo-block");
        todoBlock.setPadding(new Insets(10));

        // Create a HBox for the checkbox and text field
        HBox todoItem = new HBox(10);
        todoItem.setAlignment(Pos.CENTER_LEFT);

        // Create checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.getStyleClass().add("todo-checkbox");

        // Create text field for the todo item
        TextField textField = new TextField(content);
        textField.setPromptText("Todo item");
        textField.getStyleClass().add("todo-text");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Add delete button
        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-block-button");
        deleteBtn.setOnAction(e -> {
            // Get the parent of this todo block
            VBox parent = (VBox) todoBlock.getParent();
            if (parent != null) {
                parent.getChildren().remove(todoBlock);
            }
        });

        // Add components to the todo item
        todoItem.getChildren().addAll(checkbox, textField, deleteBtn);
        todoBlock.getChildren().add(todoItem);

        // Add a "Add Item" button
        Button addItemBtn = new Button("+ Add Item");
        addItemBtn.getStyleClass().add("add-item-button");
        addItemBtn.setOnAction(e -> {
            HBox newItem = new HBox(10);
            newItem.setAlignment(Pos.CENTER_LEFT);

            CheckBox newCheckbox = new CheckBox();
            newCheckbox.getStyleClass().add("todo-checkbox");

            TextField newTextField = new TextField();
            newTextField.setPromptText("Todo item");
            newTextField.getStyleClass().add("todo-text");
            HBox.setHgrow(newTextField, Priority.ALWAYS);

            Button newDeleteBtn = new Button("×");
            newDeleteBtn.getStyleClass().add("delete-block-button");
            newDeleteBtn.setOnAction(ev -> todoBlock.getChildren().remove(newItem));

            newItem.getChildren().addAll(newCheckbox, newTextField, newDeleteBtn);
            todoBlock.getChildren().add(todoBlock.getChildren().size() - 1, newItem);
        });

        todoBlock.getChildren().add(addItemBtn);

        return todoBlock;
    }

    /**
     * Creates a toggle (collapsible) block with title and content.
     *
     * @param content Initial content for the toggle block
     * @return A VBox containing the toggle block
     */
    private static Node createToggleBlock(String content) {
        VBox toggleBlock = new VBox(0);
        toggleBlock.getStyleClass().add("toggle-block");

        // Create title bar with toggle button
        HBox titleBar = new HBox(10);
        titleBar.getStyleClass().add("toggle-header");
        titleBar.setPadding(new Insets(10));
        titleBar.setAlignment(Pos.CENTER_LEFT);

        // Toggle button/indicator
        Button toggleBtn = new Button("▶");
        toggleBtn.getStyleClass().add("toggle-button");

        // Title field
        TextField titleField = new TextField("Toggle Title");
        titleField.getStyleClass().add("toggle-title");
        HBox.setHgrow(titleField, Priority.ALWAYS);

        // Delete button
        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-block-button");
        deleteBtn.setOnAction(e -> {
            // Get the parent of this toggle block
            VBox parent = (VBox) toggleBlock.getParent();
            if (parent != null) {
                parent.getChildren().remove(toggleBlock);
            }
        });

        titleBar.getChildren().addAll(toggleBtn, titleField, deleteBtn);

        // Create content area
        VBox contentArea = new VBox(10);
        contentArea.getStyleClass().add("toggle-content");
        contentArea.setPadding(new Insets(0, 10, 10, 20));

        // Add a text area for content
        TextArea contentField = new TextArea(content);
        contentField.setPromptText("Toggle content");
        contentField.getStyleClass().add("toggle-content-field");
        contentField.setPrefRowCount(5);
        contentField.setWrapText(true);

        // Add block insertion button
        Button addBlockBtn = new Button("+ Add Block Inside");
        addBlockBtn.getStyleClass().add("add-block-button");

        // Create a context menu for block types
        ContextMenu blockMenu = new ContextMenu();
        MenuItem todoItem = new MenuItem("Todo List");
        MenuItem bulletItem = new MenuItem("Bullet List");
        MenuItem toggleItem = new MenuItem("Toggle Block");

        todoItem.setOnAction(e -> contentArea.getChildren().add(createTodoBlock("")));
        bulletItem.setOnAction(e -> contentArea.getChildren().add(createBulletItem("")));
        toggleItem.setOnAction(e -> contentArea.getChildren().add(createToggleBlock("")));

        blockMenu.getItems().addAll(todoItem, bulletItem, toggleItem);

        addBlockBtn.setOnAction(e -> blockMenu.show(addBlockBtn, javafx.geometry.Side.BOTTOM, 0, 0));

        contentArea.getChildren().addAll(contentField, addBlockBtn);

        // Initially collapsed
        contentArea.setManaged(false);
        contentArea.setVisible(false);

        // Toggle button action
        toggleBtn.setOnAction(e -> {
            boolean isVisible = contentArea.isVisible();
            contentArea.setManaged(!isVisible);
            contentArea.setVisible(!isVisible);
            toggleBtn.setText(isVisible ? "▶" : "▼");
        });

        toggleBlock.getChildren().addAll(titleBar, contentArea);

        return toggleBlock;
    }

    /**
     * Creates a bullet list item.
     *
     * @param content Initial content for the bullet item
     * @return A HBox containing the bullet item
     */
    private static Node createBulletItem(String content) {
        HBox bulletItem = new HBox(10);
        bulletItem.getStyleClass().add("bullet-item");
        bulletItem.setPadding(new Insets(5, 10, 5, 10));
        bulletItem.setAlignment(Pos.CENTER_LEFT);

        // Bullet point
        Label bullet = new Label("•");
        bullet.getStyleClass().add("bullet-point");

        // Text field
        TextField textField = new TextField(content);
        textField.setPromptText("Bullet item");
        textField.getStyleClass().add("bullet-text");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Delete button
        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-block-button");
        deleteBtn.setOnAction(e -> {
            // Get the parent of this bullet item
            VBox parent = (VBox) bulletItem.getParent();
            if (parent != null) {
                parent.getChildren().remove(bulletItem);
            }
        });

        bulletItem.getChildren().addAll(bullet, textField, deleteBtn);

        return bulletItem;
    }

    /**
     * Creates a heading block with styled text.
     *
     * @param content Initial content for the heading
     * @return A HBox containing the heading
     */
    private static Node createHeadingBlock(String content) {
        HBox headingBlock = new HBox(10);
        headingBlock.getStyleClass().add("heading-block");
        headingBlock.setPadding(new Insets(10));
        headingBlock.setAlignment(Pos.CENTER_LEFT);

        // Heading dropdown
        ComboBox<String> headingLevel = new ComboBox<>();
        headingLevel.getItems().addAll("H1", "H2", "H3");
        headingLevel.setValue("H1");
        headingLevel.getStyleClass().add("heading-level");

        // Text field
        TextField textField = new TextField(content.isEmpty() ? "Heading" : content);
        textField.getStyleClass().add("heading-text");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Update styling based on heading level
        headingLevel.setOnAction(e -> {
            textField.getStyleClass().removeAll("h1", "h2", "h3");
            textField.getStyleClass().add(headingLevel.getValue().toLowerCase());
        });

        // Initially set H1 style
        textField.getStyleClass().add("h1");

        // Delete button
        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-block-button");
        deleteBtn.setOnAction(e -> {
            // Get the parent of this heading block
            VBox parent = (VBox) headingBlock.getParent();
            if (parent != null) {
                parent.getChildren().remove(headingBlock);
            }
        });

        headingBlock.getChildren().addAll(headingLevel, textField, deleteBtn);

        return headingBlock;
    }
}