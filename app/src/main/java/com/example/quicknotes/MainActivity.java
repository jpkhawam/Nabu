package com.example.quicknotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView notesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        ArrayList<Note> notes = new ArrayList<>();

        // TODO: how to add notes here

        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(this);
        adapter.setNotes(notes);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }
}
