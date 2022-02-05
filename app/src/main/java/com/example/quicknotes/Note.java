package com.example.quicknotes;

public class Note {

    private String title;
    private String content;

    // TODO: Possible attributes to be implemented
    // private Color backgroundColor;
    // private Date dateCreated;
    // private Date dateEdited;
    // private Integer noteIdentifier;

    public Note() {

    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
