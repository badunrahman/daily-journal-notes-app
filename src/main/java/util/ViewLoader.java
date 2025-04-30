package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading and switching between views in the application.
 * Uses a simple caching mechanism to avoid reloading FXML files.
 */
public class ViewLoader {
    // Cache for loaded FXML views
    private static final Map<String, Parent> viewCache = new HashMap<>();

    // Cache for controllers
    private static final Map<String, Object> controllerCache = new HashMap<>();

    // The current active stage
    private static Stage mainStage;

    /**
     * Sets the main application stage.
     * This should be called once during application initialization.
     *
     * @param stage The main JavaFX stage
     */
    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Loads a view from an FXML file and displays it in the main stage.
     *
     * @param fxmlPath The path to the FXML file
     */
    public static void load(String fxmlPath) {
        try {
            // Get or load the view and controller
            FXMLLoader loader = getFXMLLoader(fxmlPath);
            Parent view = loader.load();

            // Cache the controller
            Object controller = loader.getController();
            controllerCache.put(fxmlPath, controller);

            // Cache the view
            viewCache.put(fxmlPath, view);

            // Update stage title based on view
            updateStageTitle(fxmlPath);

            // Set scene and show stage
            if (mainStage.getScene() == null) {
                System.out.println("Creating new Scene for " + fxmlPath);
                Scene scene = new Scene(view);
                mainStage.setScene(scene);

                // Important: When creating a new scene, we need to update ThemeManager
                ThemeManager.getInstance().setScene(scene);
            } else {
                System.out.println("Updating existing Scene for " + fxmlPath);
                mainStage.getScene().setRoot(view);
            }

            // Ensure theme is applied after view change
            String currentTheme = ThemeManager.getInstance().getCurrentTheme();
            if (currentTheme != null) {
                System.out.println("Re-applying theme after view change: " + currentTheme);
                ThemeManager.getInstance().setTheme(currentTheme);
            }

            mainStage.show();

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Gets the controller for a specific view.
     *
     * @param fxmlPath The path to the FXML file
     * @return The controller instance
     * @throws Exception If the controller cannot be found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getController(String fxmlPath) throws Exception {
        // If controller is cached, return it
        if (controllerCache.containsKey(fxmlPath)) {
            return (T) controllerCache.get(fxmlPath);
        }

        // Otherwise, load the FXML to get the controller
        try {
            FXMLLoader loader = getFXMLLoader(fxmlPath);
            loader.load();
            T controller = loader.getController();
            controllerCache.put(fxmlPath, controller);
            return controller;
        } catch (IOException e) {
            throw new Exception("Failed to load controller for " + fxmlPath, e);
        }
    }

    /**
     * Gets an FXMLLoader for the specified FXML file.
     *
     * @param fxmlPath The path to the FXML file
     * @return A new FXMLLoader instance
     */
    private static FXMLLoader getFXMLLoader(String fxmlPath) {
        return new FXMLLoader(ViewLoader.class.getResource("/" + fxmlPath));
    }

    /**
     * Loads a view as a component to be embedded in another container.
     *
     * @param fxmlPath The path to the FXML file
     * @return The loaded view as a Parent node
     * @throws IOException If loading fails
     */
    public static Parent loadComponent(String fxmlPath) throws IOException {
        FXMLLoader loader = getFXMLLoader(fxmlPath);
        Parent view = loader.load();
        controllerCache.put(fxmlPath, loader.getController());
        viewCache.put(fxmlPath, view);
        return view;
    }

    /**
     * Loads a view into a specific container pane.
     *
     * @param fxmlPath The path to the FXML file
     * @param container The container to load the view into
     */
    public static void loadInto(String fxmlPath, Pane container) {
        try {
            FXMLLoader loader = getFXMLLoader(fxmlPath);
            Parent view = loader.load();
            controllerCache.put(fxmlPath, loader.getController());
            viewCache.put(fxmlPath, view);
            container.getChildren().clear();
            container.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Error loading view into container: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Updates the stage title based on the view being loaded.
     *
     * @param fxmlPath The path to the FXML file
     */
    private static void updateStageTitle(String fxmlPath) {
        if (fxmlPath.contains("LoginView")) {
            mainStage.setTitle("Daily Journal - Login");
        } else if (fxmlPath.contains("RegisterView")) {
            mainStage.setTitle("Daily Journal - Create Account");
        } else if (fxmlPath.contains("DashboardView")) {
            mainStage.setTitle("Daily Journal - Dashboard");
        } else if (fxmlPath.contains("EditorView")) {
            mainStage.setTitle("Daily Journal - Editor");
        } else {
            mainStage.setTitle("Daily Journal");
        }
    }

    /**
     * Clears the view and controller caches, forcing views to be reloaded next time.
     */
    public static void clearCache() {
        viewCache.clear();
        controllerCache.clear();
    }
}