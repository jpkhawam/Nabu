package com.example.quicknotes;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;

public class NoteActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        MaterialToolbar topAppBar = findViewById(R.id.noteTopBar);
        topAppBar.setNavigationOnClickListener(view -> {
            // go to home page
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
