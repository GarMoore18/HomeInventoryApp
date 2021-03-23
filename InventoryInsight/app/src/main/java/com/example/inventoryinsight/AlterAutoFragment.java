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

    //================================================================================
    // Database information
    //================================================================================
    public static final String update_combined_URL = "http://10.0.0.184/InventoryDB/combined_info/update_combined_info.php";

    //================================================================================
    // Layout information
    //================================================================================
    private TextInputEditText upc_field, item_name_field, quantity_field;

    //================================================================================
    // Variables for database
    //================================================================================
    private String location_id, select_location = "";
    private Spinner location_field;
    private Location clickedItem;
    public int item, passed_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);

        //================================================================================
        // Items that need to be altered with preset information
        //================================================================================
        MaterialTextView update_title = v.findViewById(R.id.add_screen_title_text);
        upc_field = v.findViewById(R.id.upc_field);
        item_name_field = v.findViewById(R.id.item_name_field);

        //================================================================================
        // Altering the items that need to be changed
        //================================================================================
        Bundle bundle = this.getArguments();
        update_title.setText(getString(R.string.add_screen_auto));
        upc_field.setText(bundle.getString("item_barcode"));
        item_name_field.setText(bundle.getString("item_name"));
        passed_id = bundle.getInt("item_id");
        Log.d("This should be an id: ", String.valueOf(passed_id));
        upc_field.setEnabled(false);
        item_name_field.setEnabled(false);

        //================================================================================
        // Items that need to be filled in by user
        //================================================================================
        quantity_field = v.findViewById(R.id.quantity_field);

        //================================================================================
        // Spinner for locations with location request from database
        //================================================================================
        location_field = v.findViewById(R.id.location_field);

        locationSpinner();

        //================================================================================
        // Submitting information for new item
        //================================================================================
        Button confirm_button = v.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_button.setEnabled(false);   // Disable button once attempt to add item has started

                final String quantity = quantity_field.getText().toString().trim();

                //================================================================================
                // If required information is missing raise errors
                //================================================================================
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
                    return;
                }

                //================================================================================
                // Add/update row in main table with item id, location id, user id, quantity
                //================================================================================
                StringRequest stringRequestCombined = new StringRequest(Request.Method.POST, update_combined_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "Response insert combined:" + response);
                        if (response.equals("U200") || response.equals("I200")) {

                            Toast.makeText(getContext(), "Item updated!", Toast.LENGTH_LONG).show();

                            // Create new fragment and transaction
                            Fragment newFragment = new AddFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack
                            transaction.replace(R.id.fragment_container, newFragment);

                            //If a new item is added, do not let the user use the back button
                            FragmentManager fm = getFragmentManager();
                            ((MainActivity)getActivity()).disableBack();

                            // Commit the transaction
                            transaction.commit();
                        } else {
                            Toast.makeText(getContext(), "Try updating item again!", Toast.LENGTH_LONG).show();
                            confirm_button.setEnabled(true);   // Enable confirm button if item was not added
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSONArray Error", "Error:" + error);
                    }
                }){
                    // Items to be posted to the combined info  table
                    @Override
                    protected Map<String, String> getParams()  {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("item_id", String.valueOf(passed_id));
                        params.put("location_id", location_id);
                        params.put("user_id", "1");   // TODO: WILL NEED TO STORE CORRECT USER
                        params.put("quantity", quantity);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequestCombined);
            }
        });

        return v;
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