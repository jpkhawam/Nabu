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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Font Type SharedPreferences
        String fontType = settings.getString("settings_fonttype", getString(R.string.font_type_default));

        // Add Dyslexia-Friendly fontFamily Style To The Default Theme According To Font Type SharedPreferences
        if (fontType.equals(getString(R.string.font_type_dyslexia))) {
            getTheme().applyStyle(R.style.DyslexiaTheme, false);
        }
        setContentView(R.layout.activity_main);

        // Check firstStartUp SharedPreferences and Show Dialog If True
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStartUp = prefs.getBoolean("firstStartUp", true);
        if (firstStartUp) {
            showFirstStartUpDialog();
        }

        DrawerLayout drawerLayout = findViewById(R.id.mainLayout);
        TextView emptyNotes = findViewById(R.id.no_notes_text);
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);

        AtomicReference<ArrayList<Note>> allNotes = new AtomicReference<>(dataBaseHelper.getAllNotes());
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(this, drawerLayout);
        adapter.setNotes(allNotes.get());
        notesRecyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                allNotes.set(dataBaseHelper.getAllNotes());
                adapter.setNotes(allNotes.get());
                if (allNotes.get().isEmpty()) emptyNotes.setVisibility(View.VISIBLE);
                else emptyNotes.setVisibility(View.GONE);
            }
        });

        if (dataBaseHelper.getAllNotes().isEmpty()) {
            emptyNotes.setVisibility(View.VISIBLE);
        } else {
            emptyNotes.setVisibility(View.GONE);
        }

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            long archivedNoteId = intentReceived.getLongExtra(NoteActivity.ARCHIVED_NOTE_IDENTIFIER_KEY, -1);
            long unarchivedNoteId = intentReceived.getLongExtra(NoteActivity.UNARCHIVED_NOTE_IDENTIFIER_KEY, -1);
            long deletedNoteId = intentReceived.getLongExtra(NoteActivity.DELETED_NOTE_KEY, -1);
            boolean discardedNote = intentReceived.getBooleanExtra(NoteActivity.DISCARDED_NOTE_KEY, false);
            if (archivedNoteId != -1) {
                Snackbar.make(drawerLayout, R.string.notes_archived, Snackbar.LENGTH_SHORT).setAction(R.string.undo, view -> {
                    dataBaseHelper.unarchiveNote(archivedNoteId);
                    allNotes.set(dataBaseHelper.getAllNotes());
                    adapter.setNotes(allNotes.get());
                    notesRecyclerView.setAdapter(adapter);
                    emptyNotes.setVisibility(View.GONE);
                }).show();
            } else if (unarchivedNoteId != -1) {
                Snackbar.make(drawerLayout, R.string.notes_unarchived, Snackbar.LENGTH_SHORT).setAction(R.string.undo, view -> {
                    dataBaseHelper.archiveNote(unarchivedNoteId);
                    allNotes.set(dataBaseHelper.getAllNotes());
                    adapter.setNotes(allNotes.get());
                    notesRecyclerView.setAdapter(adapter);
                    emptyNotes.setVisibility(View.GONE);
                }).show();
            } else if (deletedNoteId != -1) {
                Snackbar.make(drawerLayout, R.string.note_sent_to_trash, Snackbar.LENGTH_SHORT).setAction(R.string.undo, view -> {
                    dataBaseHelper.restoreNote(deletedNoteId);
                    allNotes.set(dataBaseHelper.getAllNotes());
                    adapter.setNotes(allNotes.get());
                    notesRecyclerView.setAdapter(adapter);
                    emptyNotes.setVisibility(View.GONE);
                }).show();
            } else if (discardedNote) {
                Snackbar.make(drawerLayout, R.string.discarded_empty_note, Snackbar.LENGTH_SHORT).show();
            }
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Get Font Size SharedPreferences
        String fontSize = settings.getString("settings_fontsize", "Small");
        // Set NavigationView Font Size According To Font Size SharedPreferences}
        if (fontSize.equals("Small")) {
            navigationView.setItemTextAppearance(R.style.NavigationViewSmall);
        }
        if (fontSize.equals("Medium")) {
            navigationView.setItemTextAppearance(R.style.NavigationViewMedium);
        }
        if (fontSize.equals("Large")) {
            navigationView.setItemTextAppearance(R.style.NavigationViewLarge);
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, NoteActivity.class);
            startActivity(intent);
        });

    }

    private void showFirstStartUpDialog() {
        new MaterialAlertDialogBuilder(this).setTitle(R.string.accessibility_settings).setMessage(R.string.accessibility_prompt).setPositiveButton(R.string.Yes, (dialogInterface, i) -> startActivity(new Intent(MainActivity.this, SettingsActivity.class))).setNegativeButton(R.string.No, (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        // Change firstStartUp SharedPreferences To False
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStartUp", false);
        editor.apply();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trash:
                Intent trashIntent = new Intent(this, TrashActivity.class);
                startActivity(trashIntent);
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

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
