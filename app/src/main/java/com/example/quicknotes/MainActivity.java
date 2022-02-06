package com.example.quicknotes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        ArrayList<Note> notes = new ArrayList<>();

        notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum)));
        notes.add(new Note("Note 1", "Note 1 content"));
        notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum_short)));
        notes.add(new Note("Note 2", "Note 2 content"));
        notes.add(new Note("Note 3", "Note 3 content"));
        notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum_short)));
        notes.add(new Note(null, "This note doesn't have a title"));
        notes.add(new Note(null, "This note also doesn't have a title and it looks fine"));
        notes.add(new Note("This note has a title only", null));
        notes.add(new Note("This note has a color", "yes it does, and it needs fixing", R.color.md_theme_light_onSurfaceVariant));
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(this);
        adapter.setNotes(notes);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }
}
