package model;

// Toggle work like a collasable where you will have a title
// its own unique id with the entryId which journal it belongs to
// the content and isExpanded for the toggle to expand or collaspe
public class ToggleBlock extends Block {
    private int toggleId;
    private String title;
    private String content;
    private boolean isExpanded;

    public ToggleBlock(int entryId, int toggleId, String title, String content, boolean isExpanded) {
        super(entryId);
        this.toggleId = toggleId;
        this.title = title;
        this.content = content;
        this.isExpanded = isExpanded;
    }

    @Override
    public String getType() {
        return "toggle";
    }

    // Getter and setter for entryId from the parent class Block
    @Override
    public int getEntryId() {
        return super.getEntryId();
    }

    public void setEntryId(int entryId) {
        // Access the protected entryId from the parent class
        this.entryId = entryId;
    }

    // getter/setter for toggleId
    public int getToggleId() {
        return toggleId;
    }

    public void setToggleId(int toggleId) {
        this.toggleId = toggleId;
    }

    // getter/setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // getter/setter for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // getter/setter for isExpanded
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public String toString(){
        return "[Toggle] " + title + (isExpanded? " (expanded)" : " (collapsed)");
    }
}