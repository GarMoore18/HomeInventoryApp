package com.example.inventoryinsight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        EditText barcodeET = findViewById(R.id.upc_field);
        EditText nameET = findViewById(R.id.item_name_field);
        EditText quantityET = findViewById(R.id.quantity_field);
        EditText locationET = findViewById(R.id.location_field);

    }
}