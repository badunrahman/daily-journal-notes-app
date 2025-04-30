package model;

// Main purpose of this class is to Hold info about the TodoItem with their ownId and EntryId which are journal they belong to
// The description part where you put you ideas
// and isdone for to check and uncheck
public class ToDoItem extends Block {
    private int todoId;
    private String description;
    private boolean isDone;

    public ToDoItem(int entryId, int todoId, String description, boolean isDone) {
        super(entryId);
        this.todoId = todoId;
        this.description = description;
        this.isDone = isDone;
    }

    @Override
    public String getType() {
        return "todo";
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

    // getter/setter for todoId
    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    // getter/setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // getter/setter for isDone
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}