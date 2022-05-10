package com.jpkhawam.nabu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Font Type SharedPreferences
        String fontType = settings.getString("settings_fonttype", "Default");
        // Get Font Size SharedPreferences
        String fontSize = settings.getString("settings_fontsize", "Small");

        // Set Font Size Value According To Font Size SharedPreferences
        if (fontSize.equals("Medium")) {
            getTheme().applyStyle(R.style.settingsMediumTheme, false);
        }
        if (fontSize.equals("Large")) {
            getTheme().applyStyle(R.style.settingsLargeTheme, false);
        }   
        // Add Dyslexia-Friendly fontFamily Style To The Default Theme According To Font Type SharedPreferences
        if (fontType.equals("Dyslexia-friendly")) {
            getTheme().applyStyle(R.style.DyslexiaTheme, false);
        }
        setContentView(R.layout.activity_settings);

        DrawerLayout drawerLayout = findViewById(R.id.mainLayout);
        Toolbar toolbar = findViewById(R.id.main_toolbar_settings);

        setSupportActionBar(toolbar);

        // Instantiate Settings Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.SettingsConstraintLayout, new SettingsFragment())
                .commit();

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
            case R.id.trash:
                Intent trashIntent = new Intent(this, TrashActivity.class);
                startActivity(trashIntent);
                return true;
            default:
                return false;
        }
    }
}
