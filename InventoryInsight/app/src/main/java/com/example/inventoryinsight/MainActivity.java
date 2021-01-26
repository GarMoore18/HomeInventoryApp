package com.example.inventoryinsight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the navigation for the bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            //Will navigate to correct activity based on selected destination
            if (itemId == R.id.menu_add) {
                Toast.makeText(MainActivity.this, "ADDING", Toast.LENGTH_SHORT).show();
                Intent intent_add = new Intent(getBaseContext(), AddActivity.class);
                startActivity(intent_add);
            } else if (itemId == R.id.menu_remove) {
                Toast.makeText(MainActivity.this, "REMOVING", Toast.LENGTH_SHORT).show();
                Intent intent_remove = new Intent(getBaseContext(), RemoveActivity.class);
                startActivity(intent_remove);
            } else if (itemId == R.id.menu_home) {
                Toast.makeText(MainActivity.this, "YOUR ALREADY HOME!", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.menu_search) {
                Toast.makeText(MainActivity.this, "SEARCHING", Toast.LENGTH_SHORT).show();
                Intent intent_search = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent_search);
            } else if (itemId == R.id.menu_settings) {
                Toast.makeText(MainActivity.this, "SETTINGS", Toast.LENGTH_SHORT).show();
                Intent intent_settings = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent_settings);
            }
            return true;
        });
    }
}