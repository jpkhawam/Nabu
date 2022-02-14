package com.example.quicknotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_IDENTIFIER_KEY = "noteIdentifier";
    private Note currentNote;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        TextInputEditText editTextTitle = findViewById(R.id.input_note_title);
        TextInputEditText editTextContent = findViewById(R.id.input_note_content);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(NoteActivity.this);

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            int noteIdentifier = intentReceived.getIntExtra(NOTE_IDENTIFIER_KEY, -1);
            if (noteIdentifier != -1) {
                currentNote = dataBaseHelper.getNote(noteIdentifier);
                editTextTitle.setText(currentNote.getTitle());
                editTextContent.setText(currentNote.getContent());
            }
        } else {
            currentNote = new Note(dataBaseHelper.createNewNote(), "", "",
                    LocalDateTime.now(), LocalDateTime.now(), 0);
        }

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentNote.setDateEdited(LocalDateTime.now());
                //dataBaseHelper.updateNote(currentNote);
                if (editTextTitle.getText() != null) {
                    currentNote.setTitle(editTextTitle.getText().toString());
                } else {
                    currentNote.setTitle(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentNote.setDateEdited(LocalDateTime.now());
               // dataBaseHelper.updateNote(currentNote);
                if (editTextContent.getText() != null) {
                    currentNote.setContent(editTextContent.getText().toString());
                } else {
                    currentNote.setContent(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        MaterialToolbar topAppBar = findViewById(R.id.noteTopBar);
        topAppBar.setNavigationOnClickListener(view -> {
            Intent outgoingIntent = new Intent(this, MainActivity.class);
            if (currentNote.getTitle() == null && currentNote.getContent() == null) {
                dataBaseHelper.deleteNote(currentNote);
            }
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
