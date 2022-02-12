package com.example.quicknotes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {

    private final LocalDateTime dateCreated;
    private String title;
    private String content;
    private LocalDateTime dateEdited;
    private int backgroundColor;
    private final int noteIdentifier;
    private static int s_identifier_counter = 0;

    public Note() {
        this.dateCreated = LocalDateTime.now();
        this.noteIdentifier = s_identifier_counter++;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.dateCreated = LocalDateTime.now();
        this.noteIdentifier = s_identifier_counter++;
    }

    public Note(String title, String content, int backgroundColor) {
        this.title = title;
        this.content = content;
        this.backgroundColor = backgroundColor;
        this.dateCreated = LocalDateTime.now();
        this.noteIdentifier = s_identifier_counter++;
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

    public int getNoteIdentifier() {
        return noteIdentifier;
    }
}
