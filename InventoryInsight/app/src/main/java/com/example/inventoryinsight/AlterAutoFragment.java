package com.example.inventoryinsight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class AlterAutoFragment extends Fragment {

    public AlterAutoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);
        MaterialTextView update_title = v.findViewById(R.id.add_screen_title_text);
        update_title.setText(getString(R.string.add_screen_auto));

        TextInputEditText upc_field = v.findViewById(R.id.upc_field);
        TextInputEditText name_field = v.findViewById(R.id.item_name_field);
        Bundle bundle = this.getArguments();
        upc_field.setText(bundle.getString("item_barcode"));
        name_field.setText(bundle.getString("item_name"));
        upc_field.setEnabled(false);
        name_field.setEnabled(false);

        return v;
    }
}