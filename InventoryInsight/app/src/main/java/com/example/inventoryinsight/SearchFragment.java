package com.example.inventoryinsight;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Have to create the view first to use findViewByID in a fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        //Used to load initiate the scanner when the image is clicked
        ImageButton scanButton = v.findViewById(R.id.scan_image_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator barcodeIntegrate = IntentIntegrator.forSupportFragment(SearchFragment.this);
                barcodeIntegrate.setPrompt("Scan a barcode.");
                barcodeIntegrate.initiateScan();
            }
        });

        return v;   //Returning the view for the fragment
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getContext(), "Scanned : " + result.getContents(), Toast.LENGTH_LONG).show();
                TextInputEditText barcode = Objects.requireNonNull(getView()).findViewById(R.id.upc_field);
                barcode.setText(result.getContents());
            }
        }
    }
}