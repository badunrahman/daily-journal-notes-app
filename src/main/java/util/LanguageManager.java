package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages language settings and resource bundles for internationalization.
 * This class implements the Singleton pattern to ensure consistent language settings across the application.
 */
public class LanguageManager {
    private static LanguageManager instance;

    // Current locale
    private Locale currentLocale;

    // Base name for resource bundles
    private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/messages";

    // Resource bundle for current locale
    private ResourceBundle resources;

    // Property to bind UI elements to for language changes
    private final StringProperty currentLanguage = new SimpleStringProperty();

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes with default locale (English).
     */
    private LanguageManager() {
        // Set English as default locale
        setLocale(Locale.ENGLISH);
    }

    /**
     * Gets the singleton instance of LanguageManager.
     *
     * @return The LanguageManager instance
     */
    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * Sets the application locale.
     *
     * @param locale The locale to set
     */
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.resources = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
        currentLanguage.set(locale.getLanguage());
        System.out.println("Language changed to: " + locale.getDisplayLanguage());
    }

    /**
     * Convenient method to set the locale by language code.
     *
     * @param languageCode The language code (e.g., "en", "fr")
     */
    public void setLocale(String languageCode) {
        switch (languageCode.toLowerCase()) {
            case "fr":
                setLocale(Locale.FRENCH);
                break;
            case "en":
            default:
                setLocale(Locale.ENGLISH);
                break;
        }
    }

    /**
     * Toggles between English and French.
     */
    public void toggleLanguage() {
        if (currentLocale.getLanguage().equals("fr")) {
            setLocale(Locale.ENGLISH);
        } else {
            setLocale(Locale.FRENCH);
        }
    }

    /**
     * Gets the current locale.
     *
     * @return The current locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Gets the current language code.
     *
     * @return The current language code (e.g., "en", "fr")
     */
    public String getCurrentLanguageCode() {
        return currentLocale.getLanguage();
    }

    /**
     * Gets a localized message from the resource bundle.
     *
     * @param key The resource key
     * @return The localized message
     */
    public String getString(String key) {
        try {
            return resources.getString(key);
        } catch (Exception e) {
            System.err.println("Missing translation key: " + key);
            return key; // Return the key itself as fallback
        }
    }

    /**
     * Gets a localized message with parameters.
     * Parameters in the message should be marked with {0}, {1}, etc.
     *
     * @param key The resource key
     * @param params The parameters to insert
     * @return The formatted localized message
     */
    public String getString(String key, Object... params) {
        try {
            String message = resources.getString(key);
            for (int i = 0; i < params.length; i++) {
                message = message.replace("{" + i + "}", params[i].toString());
            }
            return message;
        } catch (Exception e) {
            System.err.println("Missing translation key: " + key);
            return key; // Return the key itself as fallback
        }
    }

    /**
     * Gets the language property for binding.
     *
     * @return The language property
     */
    public StringProperty currentLanguageProperty() {
        return currentLanguage;
    }
}