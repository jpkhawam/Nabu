package com.example.quicknotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        MaterialToolbar topAppBar = findViewById(R.id.noteTopBar);
        topAppBar.setNavigationOnClickListener(view -> {
            // go to home page
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        });
    }

}
