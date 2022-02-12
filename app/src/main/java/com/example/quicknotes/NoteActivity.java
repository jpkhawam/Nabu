package com.example.quicknotes;

import static com.example.quicknotes.MainActivity.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputEditText;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_TITLE_KEY = "noteTitle";
    public static final String NOTE_CONTENT_KEY = "noteContent";
    public static final String NOTE_IDENTIFIER_KEY = "noteIdentifier";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        TextInputEditText editTextTitle = findViewById(R.id.input_note_title);
        TextInputEditText editTextContent = findViewById(R.id.input_note_content);

        int noteIdentifier;
        Note currentNote;

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            noteIdentifier = intentReceived.getIntExtra(NOTE_IDENTIFIER_KEY, -1);
            if (noteIdentifier != -1) {
                // this is temporary until the database is created
                for (Note note : notes) {
                    if (note.getNoteIdentifier() == noteIdentifier) {
                        editTextTitle.setText(note.getTitle());
                        editTextContent.setText(note.getContent());
                    }
                }
            } else {
                // something went wrong so create new Note ?
                // maybe there is a better way to deal with this
                currentNote = new Note();
            }
        } else {
            currentNote = new Note();
        }

        /* TODO: ADD LISTENER for text changed, or pasted? check how these are used */
        // editTextTitle.setOnReceiveContentListener()
        // editTextTitle.addTextChangedListener()

        MaterialToolbar topAppBar = findViewById(R.id.noteTopBar);
        topAppBar.setNavigationOnClickListener(view -> {
            Intent outgoingIntent = new Intent(this, MainActivity.class);
            startActivity(outgoingIntent);
        });
        topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.note_pin:
                    // pin note
                    return true;
                case R.id.note_add_reminder:
                    // add reminder
                    return true;
                case R.id.note_send_to_archive:
                    // send to archive
                    return true;
                default:
                    return false;
            }
        });

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(view -> {
            // TODO:
            //  @joesabbagh1 this is where to open the bottom sheet fragment
        });

        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.note_color:
                    // give note color options
                    // this also can be a bottom sheet fragment
                    return true;
                case R.id.note_label:
                    // give note label options
                    // also bottom sheet fragment
                    return true;
                default:
                    return false;
            }
        });

    }

}
