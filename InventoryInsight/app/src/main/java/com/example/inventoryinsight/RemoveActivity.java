package com.example.inventoryinsight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RemoveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e) {}

        setContentView(R.layout.activity_remove);

    }
}