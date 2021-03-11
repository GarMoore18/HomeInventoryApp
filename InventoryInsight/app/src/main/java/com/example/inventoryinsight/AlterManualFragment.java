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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlterManualFragment extends Fragment {

    //================================================================================
    // Database information
    //================================================================================
    public static final String insert_URL = "http://10.0.0.184/InventoryDB/insert_item_info.php";
    public static final String select_URL = "http://10.0.0.184/InventoryDB/select_possible_locations.php";

    private TextInputEditText upc_field, item_name_field, quantity_field;
    private String select_location = "";

    public AlterManualFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);

        //================================================================================
        // Items that need to be altered with preset information
        //================================================================================
        MaterialTextView manual_title = v.findViewById(R.id.add_screen_title_text);
        upc_field = v.findViewById(R.id.upc_field);

        //================================================================================
        // Altering the items that need to be changed
        //================================================================================
        manual_title.setText(getString(R.string.add_screen_manual));
        Bundle bundle = this.getArguments();
        upc_field.setText(bundle.getString("barcode"));
        upc_field.setEnabled(false);

        //================================================================================
        // Items that need to be filled in by user
        //================================================================================
        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);

        //================================================================================
        // Spinner for locations with location request from database
        //================================================================================
        ArrayList<Location> locations = new ArrayList<>();
        LocationAdapter adapter = new LocationAdapter(getContext(), locations);
        Spinner location_field = v.findViewById(R.id.location_field);
        location_field.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, select_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", "Response:" + response);
                JSONArray array_response = null;
                try {
                    array_response = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                locations.add(new Location(-1, "Select Location"));

                // All of the items in the response need to be added to the locations array for the spinner
                for (int i = 0; i < array_response.length(); i++) {
                    try {
                        JSONObject jsonObjectFromArray =
                                array_response.getJSONObject(i);

                        // Create location object for each location
                        Location location = new Location(jsonObjectFromArray.getInt("id"),
                                jsonObjectFromArray.getString("name"));

                        locations.add(location);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged(); // The spinner needs to be refreshed with proper data
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSONArray Error", "Error:" + error);
            }
        });
        // Add the request to the volley queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        //================================================================================
        // Collecting spinner data
        //================================================================================
        location_field.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id)
                    {
                        // It returns the clicked item.
                        Location clickedItem = (Location) parent.getItemAtPosition(position);
                        select_location = clickedItem.getLocation();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                    }
                });

        //================================================================================
        // Submitting information for new item
        //================================================================================
        Button confirm_button = v.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String barcode = upc_field.getText().toString().trim();
                final String name = item_name_field.getText().toString().trim();
                final String quantity = quantity_field.getText().toString().trim();

                //================================================================================
                // If required information is missing raise errors
                //================================================================================
                boolean missing_field = false;
                // Required fields that the user must select
                if (name.equals("")) {
                    item_name_field.setError("An item name is required!");
                    missing_field = true;
                }
                if (quantity.equals("")) {
                    quantity_field.setError("An item quantity is required!");
                    missing_field = true;
                }
                if (select_location.equals("Select Location")) {
                    Toast.makeText(getContext(), "An item location is required!", Toast.LENGTH_LONG).show();
                    missing_field = true;
                }
                if (missing_field) {
                    return;
                }

                //================================================================================
                // Adding new item to the table of items
                //================================================================================
                StringRequest stringRequest = new StringRequest(Request.Method.POST, insert_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "Response:" + response);
                        if (response.equals("200")) {

                            Toast.makeText(getContext(), "Item added!", Toast.LENGTH_LONG).show();

                            // Create new fragment and transaction
                            Fragment newFragment = new AddFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack
                            transaction.replace(R.id.fragment_container, newFragment);

                            //If a new item is added, do not let the user use the back button
                            FragmentManager fm = getFragmentManager();
                            fm.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            // Commit the transaction
                            transaction.commit();
                        } else {
                            Toast.makeText(getContext(), "Try adding item again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSONArray Error", "Error:" + error);
                    }
                }){
                    // Items to be posted to the item_info table
                    @Override
                    protected Map<String, String> getParams()  {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("barcode", barcode);
                        params.put("name", name);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);
            }
        });

        return v;
    }
}