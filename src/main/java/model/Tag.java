package model;

/**
 * Represents a tag that can be attached to a journal entry.
 * Tags have a name and an associated color.
 */
public class Tag {
    private String name;
    private String color;

    /**
     * Constructor for creating a new Tag
     *
     * @param name The name/text of the tag
     * @param color The color of the tag in hex format (e.g., "#5599ff")
     */
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Gets the name of the tag
     *
     * @return The tag name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tag
     *
     * @param name The new tag name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the color of the tag
     *
     * @return The tag color in hex format
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the tag
     *
     * @param color The new tag color in hex format
     */
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + ":" + color;
    }
}