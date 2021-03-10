package com.example.inventoryinsight;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AddFragment extends Fragment {

    public static final String select_URL = "http://10.0.0.184/InventoryDB/select_item_info.php";

    private TextInputEditText upc_field, item_name_field;
    public Item found_item;

    public AddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Have to create the view first to use findViewByID in a fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        upc_field = v.findViewById(R.id.upc_field);
        Button fill_button = v.findViewById(R.id.fill_button);
        fill_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String barcode = upc_field.getText().toString().trim();

                //Will only enter if an EAN13 barcode is entered
                if (barcode.length() == 13) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, select_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", "Response:" + response);
                            try {
                                //converting the string to json array object
                                JSONArray array_response = new JSONArray(response);
                                JSONObject hit = array_response.getJSONObject(0);
                                found_item = new Item(hit.getInt("id"), hit.getString("barcode"), hit.getString("name"));

                                //Create bundle to pass data
                                Bundle bundle = new Bundle();
                                bundle.putInt("item_id", found_item.getId());
                                bundle.putString("item_barcode", found_item.getBarcode());
                                bundle.putString("item_name", found_item.getName());

                                // Create new fragment and transaction
                                Fragment newFragment = new AlterAutoFragment();
                                newFragment.setArguments(bundle);   //Arguments to pass to new fragment
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();

                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "NO HIT FOUND", Toast.LENGTH_LONG).show();

                                //Create bundle to pass data
                                Bundle bundle = new Bundle();
                                bundle.putString("barcode", upc_field.getText().toString());

                                // Create new fragment and transaction
                                Fragment newFragment = new AlterManualFragment();
                                newFragment.setArguments(bundle);   //Arguments to pass to new fragment
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("JSONArray Error", "Error:" + error);
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams()  {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("barcode", barcode);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(stringRequest);

                } else {
                    Toast.makeText(getContext(), "Please enter a EAN13 barcode.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Used to load initiate the scanner when the image is clicked
        ImageButton scanButton = v.findViewById(R.id.scan_image_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator barcodeIntegrate = IntentIntegrator.forSupportFragment(AddFragment.this);
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