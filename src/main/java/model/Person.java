package model;

/**
 * Abstract base for any person in the system.
 */
public abstract class Person {
    protected String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the role of this person (“User”, “Admin”, etc.).
     */
    public abstract String getRole();
}
