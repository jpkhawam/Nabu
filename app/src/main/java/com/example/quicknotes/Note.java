package com.example.quicknotes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {

    private final LocalDateTime dateCreated;
    private String title;
    private String content;
    private LocalDateTime dateEdited;

    public Note() {
        this.dateCreated = LocalDateTime.now();
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.dateCreated = LocalDateTime.now();
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
}
