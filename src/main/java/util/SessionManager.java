package util;

import model.JournalEntry;
import model.User;

/**
 * Manages user session information across the application.
 * Implements the Singleton pattern to maintain a single, consistent session state.
 */
public class SessionManager {
    // Private static fields to hold session data
    private static User currentUser;
    private static JournalEntry currentEntry;

    // Prevent instantiation - this is a utility class with static methods only
    private SessionManager() { }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current User object, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user for the session.
     *
     * @param user The User to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the currently selected journal entry.
     *
     * @return The current JournalEntry object, or null if no entry is selected
     */
    public static JournalEntry getCurrentEntry() {
        return currentEntry;
    }

    /**
     * Sets the current journal entry for editing.
     *
     * @param entry The JournalEntry to set as current
     */
    public static void setCurrentEntry(JournalEntry entry) {
        currentEntry = entry;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clears all session data, effectively logging out the user.
     */
    public static void clear() {
        currentUser = null;
        currentEntry = null;
    }
}