// src/main/java/model/JournalEntry.java
package model;

import java.time.LocalDateTime;

public class JournalEntry {
    private int entryId;
    private String userId;           // <-- changed from int to String
    private String title;
    private String content;
    private String tags;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public JournalEntry() { }

    public JournalEntry(
            int entryId,
            String userId,
            String title,
            String content,
            String tags,
            LocalDateTime dateCreated,
            LocalDateTime dateModified
    ) {
        this.entryId     = entryId;
        this.userId      = userId;
        this.title       = title;
        this.content     = content;
        this.tags        = tags;
        this.dateCreated = dateCreated;
        this.dateModified= dateModified;
    }

    // getters & setters
    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) { this.entryId = entryId; }

    public String getUserId() { return userId; }            // <-- returns String
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getDateModified() { return dateModified; }
    public void setDateModified(LocalDateTime dateModified) { this.dateModified = dateModified; }

    @Override
    public String toString(){
        return "JournalEntry [ID=" + entryId + ", title=\"" + title + "\", tags=" + tags + "]";
    }
}
