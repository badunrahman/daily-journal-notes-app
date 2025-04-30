package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.Parent;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages theme settings for the application.
 * This class implements the Singleton pattern to ensure consistent theme settings across the application.
 */
public class ThemeManager {
    private static ThemeManager instance;

    // Available themes
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_GREEN = "green";
    public static final String THEME_PURPLE = "purple";

    // Current theme
    private String currentTheme;

    // Property to bind UI elements to for theme changes
    private final StringProperty currentThemeProperty = new SimpleStringProperty();

    // CSS file paths for different themes
    private final Map<String, String> themeCssPaths = new HashMap<>();

    // Scene to apply theme to (should be the root scene of the application)
    private Scene scene;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes with default theme (light).
     */
    private ThemeManager() {
        // Initialize theme CSS paths
        themeCssPaths.put(THEME_LIGHT, "/css/themes/light-theme.css");
        themeCssPaths.put(THEME_DARK, "/css/themes/dark-theme.css");
        themeCssPaths.put(THEME_BLUE, "/css/themes/blue-theme.css");
        themeCssPaths.put(THEME_GREEN, "/css/themes/green-theme.css");
        themeCssPaths.put(THEME_PURPLE, "/css/themes/purple-theme.css");

        // Set light theme as default
        setTheme(THEME_LIGHT);
    }

    /**
     * Gets the singleton instance of ThemeManager.
     *
     * @return The ThemeManager instance
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Sets the scene to apply themes to.
     * Should be called once during application initialization.
     *
     * @param scene The main scene of the application
     */
    public void setScene(Scene scene) {
        this.scene = scene;

        // Only apply theme if scene is not null
        if (scene != null) {
            System.out.println("Scene set in ThemeManager. Applying theme: " + currentTheme);
            applyTheme();
        } else {
            System.err.println("Cannot set null scene in ThemeManager");
        }
    }

    /**
     * Sets the current theme.
     *
     * @param themeName The name of the theme to set
     */
    public void setTheme(String themeName) {
        if (!themeCssPaths.containsKey(themeName)) {
            System.err.println("Theme not found: " + themeName);
            return;
        }

        this.currentTheme = themeName;
        currentThemeProperty.set(themeName);
        System.out.println("Theme changed to: " + themeName);

        // Apply the theme if scene is set
        if (scene != null) {
            applyTheme();
        } else {
            System.err.println("Scene is not set. Theme will be applied when scene is available.");
        }
    }

    /**
     * Gets the current theme name.
     *
     * @return The current theme name
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Gets the theme property for binding.
     *
     * @return The theme property
     */
    public StringProperty currentThemeProperty() {
        return currentThemeProperty;
    }

    /**
     * Gets the display name for the current theme.
     *
     * @return The display name of the current theme
     */
    public String getCurrentThemeDisplayName() {
        switch (currentTheme) {
            case THEME_LIGHT:
                return "Light";
            case THEME_DARK:
                return "Dark";
            case THEME_BLUE:
                return "Blue";
            case THEME_GREEN:
                return "Green";
            case THEME_PURPLE:
                return "Purple";
            default:
                return "Unknown";
        }
    }

    /**
     * Applies the current theme to the scene.
     */
    private void applyTheme() {
        if (scene == null) {
            System.err.println("Cannot apply theme: scene is not set");
            return;
        }

        try {
            System.out.println("Applying theme: " + currentTheme);

            // Store reference to the root node
            Parent root = scene.getRoot();
            if (root == null) {
                System.err.println("Cannot apply theme: root node is null");
                return;
            }

            // Clear existing theme stylesheets
            scene.getStylesheets().removeIf(stylesheet ->
                    stylesheet.contains("/css/themes/light-theme.css") ||
                            stylesheet.contains("/css/themes/dark-theme.css") ||
                            stylesheet.contains("/css/themes/blue-theme.css") ||
                            stylesheet.contains("/css/themes/green-theme.css") ||
                            stylesheet.contains("/css/themes/purple-theme.css")
            );

            // Add base stylesheets that should always be present
            addStylesheetIfMissing("/css/auth.css");
            addStylesheetIfMissing("/css/dashboard.css");
            addStylesheetIfMissing("/css/editor.css");

            // Add the active theme sheet
            String cssPath = themeCssPaths.get(currentTheme);
            addStylesheetIfMissing(cssPath);

            // UPDATE: Remove old theme classes and add the current theme class
            root.getStyleClass().removeIf(styleClass ->
                    styleClass.startsWith("theme-"));

            // Add the theme class in format "theme-dark", "theme-light", etc.
            String themeClass = "theme-" + currentTheme;
            if (!root.getStyleClass().contains(themeClass)) {
                root.getStyleClass().add(themeClass);
                System.out.println("Added theme class to root: " + themeClass);
            }

            // Force a CSS refresh by adding and removing a dummy class
            root.getStyleClass().add("css-refresh");
            root.getStyleClass().remove("css-refresh");

            System.out.println("Applied theme: " + currentTheme);
            System.out.println("Current stylesheets: " + scene.getStylesheets());
            System.out.println("Root style classes: " + root.getStyleClass());
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + e.getMessage());
            e.printStackTrace();

            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Theme Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to apply theme: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Adds a stylesheet if it's not already in the scene's stylesheets
     */
    private void addStylesheetIfMissing(String cssPath) {
        try {
            String fullPath = ThemeManager.class.getResource(cssPath).toExternalForm();
            if (!scene.getStylesheets().contains(fullPath)) {
                scene.getStylesheets().add(fullPath);
                System.out.println("Added stylesheet: " + fullPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to add stylesheet " + cssPath + ": " + e.getMessage());
        }
    }
}