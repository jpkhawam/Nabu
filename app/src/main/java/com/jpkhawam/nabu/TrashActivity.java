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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class TrashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Font Type SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String fontType = settings.getString("settings_fonttype", getString(R.string.font_type_default));

        // Add Dyslexia-Friendly fontFamily Style To The Default Theme According To Font Type SharedPreferences
        if (fontType.equals(getString(R.string.font_type_dyslexia))) {
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
                if (allNotes.get().isEmpty()) emptyNotes.setVisibility(View.VISIBLE);
                else emptyNotes.setVisibility(View.GONE);
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
            boolean deletedNoteFromTrash = intentReceived.getBooleanExtra(NoteActivity.DELETED_NOTE_FROM_TRASH_KEY, false);
            if (archivedNoteId != -1) {
                Snackbar.make(drawerLayout, R.string.note_archived, Snackbar.LENGTH_SHORT).setAction(R.string.undo, view -> {
                    dataBaseHelper.unarchiveNote(archivedNoteId);
                    dataBaseHelper.deleteNote(archivedNoteId);
                    allNotes.set(dataBaseHelper.getAllNotesFromTrash());
                    adapter.setNotes(allNotes.get());
                    notesRecyclerView.setAdapter(adapter);
                    emptyNotes.setVisibility(View.GONE);
                }).show();
            } else if (unarchivedNoteId != -1) {
                Snackbar.make(drawerLayout, R.string.note_unarchived, Snackbar.LENGTH_SHORT).setAction(R.string.undo, view -> {
                    dataBaseHelper.archiveNote(unarchivedNoteId);
                    allNotes.set(dataBaseHelper.getAllNotesFromTrash());
                    adapter.setNotes(allNotes.get());
                    notesRecyclerView.setAdapter(adapter);
                    emptyNotes.setVisibility(View.GONE);
                }).show();
            } else if (discardedNote) {
                Snackbar.make(drawerLayout, R.string.discarded_empty_note, Snackbar.LENGTH_SHORT).show();
            } else if (deletedNoteFromTrash) {
                Snackbar.make(drawerLayout, R.string.note_deleted_successfully, Snackbar.LENGTH_SHORT).show();
            }
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar_trash);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.empty_trash) {
                if (dataBaseHelper.getAllNotesFromTrash().isEmpty()) {
                    Snackbar.make(findViewById(R.id.mainLayout), R.string.trash_is_empty, Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                Intent outgoingIntent = new Intent(this, TrashActivity.class);
                new MaterialAlertDialogBuilder(this).setTitle(R.string.ask_are_you_sure).setMessage(R.string.delete_notes_permanently).setPositiveButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss()).setNegativeButton(R.string.delete_permanently, (dialogInterface, i) -> {
                    dataBaseHelper.emptyTrash();
                    outgoingIntent.putExtra("deletedNoteFromTrash", true);
                    startActivity(outgoingIntent);
                }).create().show();
                return true;
            } else {
                return false;
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Get Font Size SharedPreferences
        String fontSize = settings.getString("settings_fontsize", getString(R.string.font_size_small));
        // Set NavigationView Font Size According To Font Size SharedPreferences}
        if (fontSize.equals(getString(R.string.font_size_small))) {
            navigationView.setItemTextAppearance(R.style.NavigationViewSmall);
        }
        if (fontSize.equals(getString(R.string.font_size_medium))) {
            navigationView.setItemTextAppearance(R.style.NavigationViewMedium);
        }
        if (fontSize.equals(getString(R.string.font_size_large))) {
            navigationView.setItemTextAppearance(R.style.NavigationViewLarge);
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);

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

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
