package com.jpkhawam.nabu;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private final LocalDateTime dateCreated;
    private long noteIdentifier;
    private String title;
    private String content;
    private LocalDateTime dateEdited;

    public Note(long noteIdentifier, String title, String content, LocalDateTime dateCreated, LocalDateTime dateEdited) {
        this.noteIdentifier = noteIdentifier;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateEdited = dateEdited;
    }

    public Note() {
        this.dateCreated = LocalDateTime.now();
        this.dateEdited = LocalDateTime.now();
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

    public long getNoteIdentifier() {
        return noteIdentifier;
    }
}
