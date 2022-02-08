package com.example.quicknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView notesRecyclerView;
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        ArrayList<Note> notes = new ArrayList<>();
        {
            notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum)));
            notes.add(new Note("Note 1", "Note 1 content"));
            notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum_short)));
            notes.add(new Note("Note 2", "Note 2 content"));
            notes.add(new Note("Note 3", "Note 3 content"));
            notes.add(new Note(getString(R.string.lorem_ipsum_title), getString(R.string.lorem_ipsum_short)));
            notes.add(new Note(null, "This note doesn't have a title"));
            notes.add(new Note(null, "This note also doesn't have a title and it looks fine"));
            notes.add(new Note("This note has a title only", null));
        }
        NotesRecyclerViewAdapter adapter = new NotesRecyclerViewAdapter(this);
        adapter.setNotes(notes);
        notesRecyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
