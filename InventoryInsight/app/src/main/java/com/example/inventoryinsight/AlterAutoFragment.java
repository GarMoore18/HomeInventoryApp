package com.example.inventoryinsight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlterAutoFragment extends Fragment {

    public static final String add_quantity = "http://10.0.0.184/InventoryDB/AddAutoFragPHP/add_quantity.php";

    private TextInputEditText upc_field, item_name_field, quantity_field;
    private MaterialTextView update_title;
    private String quantity, location_id, select_location = "";
    private Spinner location_field;
    private Location clickedItem;
    private Button confirm_button;
    public int item, passed_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);

        update_title = v.findViewById(R.id.add_screen_title_text);
        upc_field = v.findViewById(R.id.upc_field);
        item_name_field = v.findViewById(R.id.item_name_field);

        // Information passed from add fragment
        Bundle bundle = this.getArguments();
        update_title.setText(getString(R.string.add_screen_auto));
        upc_field.setText(bundle.getString("item_barcode"));
        item_name_field.setText(bundle.getString("item_name"));
        passed_id = bundle.getInt("item_id");

        // Disable these fields since the item exists
        upc_field.setEnabled(false);
        item_name_field.setEnabled(false);

        location_field = v.findViewById(R.id.location_field);
        locationSpinner();  // Set spinner information

        confirm_button = v.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity_field.getText().toString().trim();

                // Some required fields are missing
                if (checkMissingFields()) {
                    return;
                }
                makeRequest();  // Request to database
            }
        });

        return v;
    }

    //================================================================================
    // Raises errors for missing fields and returns true if fields are empty
    //================================================================================
    private boolean checkMissingFields() {
        confirm_button.setEnabled(false);   // Disable button once attempt to add item has started

        boolean missing_field = false;
        // Required fields that the user must select
        if (quantity.equals("")) {
            quantity_field.setError("An item quantity is required!");
            missing_field = true;
        }
        if (select_location.equals("Select Location")) {
            Toast.makeText(getContext(), "An item location is required!", Toast.LENGTH_LONG).show();
            missing_field = true;
        }
        if (missing_field) {
            confirm_button.setEnabled(true);   // Enable confirm button if item was not added
        }
        return missing_field;
    }

    //================================================================================
    // Moves back to the add screen
    //================================================================================
    private void moveToAddFragment() {
        // Create new fragment and transaction
        Fragment newFragment = new AddFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);

        // Do not allow the user to return to previous screen once moving fragment
        ((MainActivity)getActivity()).disableBack();

        // Commit the transaction
        transaction.commit();
    }

    //================================================================================
    // Requests information from database
    //================================================================================
    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Create JSON object to POST to database
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iid", String.valueOf(passed_id));
            jsonObject.put("user_id", "1");   // TODO: WILL NEED TO STORE CORRECT USER
            jsonObject.put("quantity", quantity_field.getText().toString().trim());
            jsonObject.put("location_id", location_id);
            //Log.d("JSONObject", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, add_quantity, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check for at least one item
                        try {
                            Log.d("TETETE", String.valueOf(response));
                            // Get the response code for the update
                            JSONObject connect = response.getJSONObject(0);

                            // Only move the fragment if the update was successful
                            if (connect.getInt("result_code") == 20 || connect.getInt("result_code") == 200) {
                                moveToAddFragment();
                            } else {
                                ((MainActivity)getActivity()).volleyRequestError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((MainActivity)getActivity()).volleyRequestError();
                confirm_button.setEnabled(true);
                Log.d("eeeeeeeee", String.valueOf(error));
            }
        });
        requestQueue.add(jsonObjReq);   // Add request to queue
    }

    //================================================================================
    // Correctly populates spinner
    //================================================================================
    private void locationSpinner() {
        ArrayList<Location> locations = new ArrayList<>();
        LocationAdapter adapter = new LocationAdapter(getContext(), locations);
        location_field.setAdapter(adapter);

        ((MainActivity)getActivity()).locationSpinnerRequest(locations, adapter);

        location_field.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id)
                    {
                        // It returns the clicked item
                        clickedItem = (Location) parent.getItemAtPosition(position);
                        select_location = clickedItem.getLocation();
                        location_id = clickedItem.getId().toString();   // Used to add to combined table
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

}