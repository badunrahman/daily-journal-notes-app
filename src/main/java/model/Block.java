package model;

public abstract class Block {
    protected int entryId;

    public Block(int entryId) {
        this.entryId = entryId;
    }

    public int getEntryId() {
        return entryId;
    }

    /**
     * Returns a String tag identifying the block type ("todo", "toggle", etc.).
     */
    public abstract String getType();
}