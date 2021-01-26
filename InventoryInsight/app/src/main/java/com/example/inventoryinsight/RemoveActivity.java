package com.example.inventoryinsight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class RemoveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        Objects.requireNonNull(this.getSupportActionBar()).hide();

        setContentView(R.layout.activity_remove);

    }
}