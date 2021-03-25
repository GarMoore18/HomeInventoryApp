package com.example.inventoryinsight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.ParseError;
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

public class RemoveAutoFragment extends Fragment {

    public static final String remove_quantity = "http://10.0.0.184/InventoryDB/RemoveAutoFragPHP/remove_quantity.php";

    private TextInputEditText upc_field, item_name_field, quantity_field;
    private MaterialTextView update_title;
    public int item, passed_id;
    private Spinner location_field;
    private int max_quan, row_id;
    private Button confirm_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);

        update_title = v.findViewById(R.id.add_screen_title_text);
        upc_field = v.findViewById(R.id.upc_field);
        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);
        location_field = v.findViewById(R.id.location_field);
        confirm_button = v.findViewById(R.id.confirm_button);

        setKnownInfo();  // Sets info that can be preset

        locationSpinner();  // Spinner set and ItemListener

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();  // Volley request when confirm is selected
                }
        });

        return v;
    }

    //================================================================================
    // Sets text fields that need a preset value
    //================================================================================
    private void setKnownInfo () {
        Bundle bundle = this.getArguments();
        update_title.setText(getString(R.string.remove_screen_auto));
        upc_field.setText(bundle.getString("item_barcode"));
        item_name_field.setText(bundle.getString("item_name"));
        passed_id = bundle.getInt("item_id");
        upc_field.setEnabled(false);
        item_name_field.setEnabled(false);
    }

    //================================================================================
    // Correctly populates spinner and ensures that location is picked before a
    // a quantity is able to be entered
    //================================================================================
    private void locationSpinner() {
        ArrayList<Location> locations = (ArrayList<Location>)getArguments().getSerializable("found_locations");
        LocationAdapter adapter = new LocationAdapter(getContext(), locations);
        location_field.setAdapter(adapter);

        location_field.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id)
                    {
                        // It returns the clicked item
                        int curr_position = parent.getSelectedItemPosition();

                        ArrayList<CombinedTable> combined= (ArrayList<CombinedTable>)getArguments().getSerializable("found_combined");

                        if (curr_position != 0) {
                            CombinedTable current_item = combined.get(parent.getSelectedItemPosition());
                            max_quan = current_item.getQuantity();

                            //TODO: POPUP WHEN MAX_QUAN IS 0, ASK USER IF WANT TO ADD ITEM (OK MOVES TO ADD SCREEN)

                            row_id = current_item.getId();

                            quantity_field.setEnabled(true);
                            quantity_field.requestFocus();
                            quantity_field.setError("Quantity must be " + max_quan + " or less.");
                            quantity_field.setFilters(new InputFilter[]{ new InputFilterMinMax("0", String.valueOf(max_quan))});

                        } else {
                            max_quan = 0;
                            quantity_field.setError("Select location first.");
                            quantity_field.setEnabled(false);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    //================================================================================
    // Requests information from database
    //================================================================================
    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        int new_quan = max_quan - Integer.parseInt(quantity_field.getText().toString().trim());

        // Create JSON object to POST to database
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(row_id));
            jsonObject.put("user_id", "1");   // TODO: WILL NEED TO STORE CORRECT USER
            jsonObject.put("quantity", String.valueOf(new_quan));
            Log.d("JSONObject", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, remove_quantity, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check for at least one item
                        try {
                            // Get the response code for the update
                            JSONObject connect = response.getJSONObject(0);

                            // Only move the fragment if the update was successful
                            if (connect.getInt("result_code") == 200) {
                                moveToRemoveFragment();
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
                //((MainActivity)getActivity()).volleyRequestError();
                Log.d("eeeeeeeee", String.valueOf(error));
            }
        });
        requestQueue.add(jsonObjReq);   // Add request to queue
    }

    //================================================================================
    // Transitions back to remove fragment
    //================================================================================
    private void moveToRemoveFragment () {
        // Create new fragment and transaction
        Fragment newFragment = new RemoveFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);

        // Do not allow the user to return to previous screen once moving fragment
        ((MainActivity)getActivity()).disableBack();

        // Commit the transaction
        transaction.commit();
    }

}