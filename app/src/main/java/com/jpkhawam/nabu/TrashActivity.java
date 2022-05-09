package com.jpkhawam.nabu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class TrashActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Font Type SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String fontType = settings.getString("settings_fonttype", "Default");

        // Add Dyslexia-Friendly fontFamily Style To The Default Theme According To Font Type SharedPreferences
        if (fontType.equals("Dyslexia-friendly")){
            getTheme().applyStyle(R.style.DyslexiaTheme, false);
        }
        setContentView(R.layout.activity_trash);

        DrawerLayout drawerLayout = findViewById(R.id.mainLayout);
        TextView emptyNotes = findViewById(R.id.no_trash_text);
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(TrashActivity.this);

        AtomicReference<ArrayList<Note>> allNotes = new AtomicReference<>(dataBaseHelper.getAllNotesFromTrash());
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(this, drawerLayout);
        adapter.setNotes(allNotes.get());
        notesRecyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                allNotes.set(dataBaseHelper.getAllNotesFromTrash());
                adapter.setNotes(allNotes.get());
                if (allNotes.get().isEmpty())
                    emptyNotes.setVisibility(View.VISIBLE);
                else
                    emptyNotes.setVisibility(View.GONE);
            }
        });

        if (dataBaseHelper.getAllNotesFromTrash().isEmpty()) {
            emptyNotes.setVisibility(View.VISIBLE);
        } else {
            emptyNotes.setVisibility(View.GONE);
        }

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            long archivedNoteId = intentReceived.getLongExtra(NoteActivity.ARCHIVED_NOTE_IDENTIFIER_KEY, -1);
            long unarchivedNoteId = intentReceived.getLongExtra(NoteActivity.UNARCHIVED_NOTE_IDENTIFIER_KEY, -1);
            boolean discardedNote = intentReceived.getBooleanExtra(NoteActivity.DISCARDED_NOTE_KEY, false);
            if (archivedNoteId != -1) {
                Snackbar.make(drawerLayout, "Note archived", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", view -> {
                            dataBaseHelper.unarchiveNote(archivedNoteId);
                            dataBaseHelper.deleteNote(archivedNoteId);
                            allNotes.set(dataBaseHelper.getAllNotesFromTrash());
                            adapter.setNotes(allNotes.get());
                            notesRecyclerView.setAdapter(adapter);
                            emptyNotes.setVisibility(View.GONE);
                        })
                        .show();
            } else if (unarchivedNoteId != -1) {
                Snackbar.make(drawerLayout, "Note unarchived", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", view -> {
                            dataBaseHelper.archiveNote(unarchivedNoteId);
                            allNotes.set(dataBaseHelper.getAllNotesFromTrash());
                            adapter.setNotes(allNotes.get());
                            notesRecyclerView.setAdapter(adapter);
                            emptyNotes.setVisibility(View.GONE);
                        })
                        .show();
            } else if (discardedNote)
                Snackbar.make(drawerLayout, "Discarded empty note", Snackbar.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar_trash);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notes:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.archive:
                Intent archiveIntent = new Intent(this, ArchiveActivity.class);
                startActivity(archiveIntent);
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
