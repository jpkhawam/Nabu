package com.example.quicknotes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {

    private long noteIdentifier;
    private String title;
    private String content;
    private final LocalDateTime dateCreated;
    private LocalDateTime dateEdited;
    private int backgroundColor;
    private LocalDateTime dateSentToTrash = null;
    private LocalDateTime dateArchived = null;

    public Note(long noteIdentifier, String title, String content, LocalDateTime dateCreated, LocalDateTime dateEdited, int backgroundColor) {
        this.noteIdentifier = noteIdentifier;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateEdited = dateEdited;
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setDateEdited(LocalDateTime.now());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        setDateEdited(LocalDateTime.now());
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getDateCreated() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTimeFormatter.format(this.dateCreated);
    }

    public String getDateEdited() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTimeFormatter.format(this.dateEdited);
    }

    public void setDateEdited(LocalDateTime dateEdited) {
        this.dateEdited = dateEdited;
    }

    public String getDateCreatedAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTimeFormatter.format(this.dateCreated);
    }

    public String getDateEditedAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTimeFormatter.format(this.dateEdited);
    }

    public long getNoteIdentifier() {
        return noteIdentifier;
    }
}
