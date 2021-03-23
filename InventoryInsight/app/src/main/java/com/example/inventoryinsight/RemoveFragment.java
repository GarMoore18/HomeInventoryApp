package com.example.inventoryinsight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RemoveFragment extends Fragment {

    public static final String remove_item_check = "http://10.0.0.184/InventoryDB/use/check_remove_possible.php";
    
    private TextInputEditText upc_field;
    private Button fill_button;
    private Item found_item;
    private String barcode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Have to create the view first to use findViewByID in a fragment
        View v = inflater.inflate(R.layout.fragment_remove, container, false);

        HttpsTrustManager.allowAllSSL();

        upc_field = v.findViewById(R.id.upc_field);
        fill_button = v.findViewById(R.id.fill_button);

        fill_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = upc_field.getText().toString().trim();

                //Will only enter if an EAN13 barcode is entered
                if (barcode.length() == 12 || barcode.length() == 13) {
                    makeRequest();
                } else {
                    ((MainActivity)getActivity()).invalidBarcodeDialog();
                }
            }
        });

        cameraScanEvent(v.findViewById(R.id.scan_image_button)); // Call to load camera for scanning

        return v;   //Returning the view for the fragment
    }

    //================================================================================
    // Requests information from database
    //================================================================================
    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Create JSON object to POST to database
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("barcode", barcode);
            Log.d("JSONObject", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, remove_item_check, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Check for at least one item
                            JSONObject hit = response.getJSONObject(0);
                            found_item = new Item(hit.getInt("iid"), barcode, hit.getString("iname"));

                            Object[] quan_locate = locations_quantities(response);
                            ArrayList<Location> locations = (ArrayList<Location>)quan_locate[0]; // Locations where item is stored
                            ArrayList<CombinedTable> combined = (ArrayList<CombinedTable>)quan_locate[1];

                            moveToRemoveScreen(found_item, locations, combined);  // Change fragment to the remove screen

                            Log.d("CHECK REMOVE", String.valueOf(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            moveToAddScreen();   // Change fragment to the add screen
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((MainActivity)getActivity()).volleyRequestError();
            }
        });
        requestQueue.add(jsonObjReq);   // Add request to queue
    }

    //================================================================================
    // Creates an array of locations for the item
    //================================================================================
    private Object[] locations_quantities (JSONArray response) throws JSONException {
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(new Location(-1, "Select Location"));

        ArrayList<CombinedTable> combinedTables = new ArrayList<>();
        combinedTables.add(new CombinedTable(-1, -1, -1, -1, -1));

        // All of the items in the response need to be added to the locations array for the spinner
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonObjectFromArray =
                    response.getJSONObject(i);

            // Create location object for each location
            Location location = new Location(jsonObjectFromArray.getInt("lid"),
                    jsonObjectFromArray.getString("lname"));

            CombinedTable combinedTable = new CombinedTable(jsonObjectFromArray.getInt("cid"),
                    jsonObjectFromArray.getInt("iid"), jsonObjectFromArray.getInt("lid"),
                    jsonObjectFromArray.getInt("user_id"), jsonObjectFromArray.getInt("quantity"));

            locations.add(location);
            combinedTables.add(combinedTable);
        }

        Object[] arrayObjects = new Object[2];
        arrayObjects[0] = locations;
        arrayObjects[1] = combinedTables;
        return arrayObjects;

        //return locations;
    }

    //================================================================================
    // Passes information in bundle and commits moving to RemovingAutoFragment
    //================================================================================
    private void moveToRemoveScreen (Item found_item, ArrayList<Location> locations, ArrayList<CombinedTable> combined) {
        //Create bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putInt("item_id", found_item.getId());
        bundle.putString("item_barcode", found_item.getBarcode());
        bundle.putString("item_name", found_item.getName());
        //TODO: PASS CORRECT QUANTITIES SOMEHOW
        //TODO: WILL NEED TO PASS THE IMAGE AS WELL
        bundle.putSerializable("found_locations", locations);
        bundle.putSerializable("found_combined", combined);

        // Create new fragment and transaction
        Fragment newFragment = new RemoveAutoFragment();
        commitToFragment(newFragment, bundle);
    }

    //================================================================================
    // Creates a dialog for when there is not an item in the database
    //================================================================================
    private void addItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Item must be added!");
        builder.setMessage("There are not any items in the database that match that barcode.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //================================================================================
    // Passes information in bundle and commits moving to AlterManualFragment
    //================================================================================
    private void moveToAddScreen() {
        addItemDialog();

        //Create bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putString("barcode", barcode);

        // Create new fragment and transaction
        Fragment newFragment = new AlterManualFragment();
        commitToFragment(newFragment, bundle);
    }

    //================================================================================
    // Duplicate part of committing a fragment change
    //================================================================================
    private void commitToFragment (Fragment newFragment, Bundle bundle) {
        newFragment.setArguments(bundle);   //Arguments to pass to new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    //================================================================================
    // Loads the camera for scanning barcode
    //================================================================================
    private void cameraScanEvent(ImageButton scanButton) {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator barcodeIntegrate = IntentIntegrator.forSupportFragment(RemoveFragment.this);
                barcodeIntegrate.setPrompt("Scan a barcode.");
                barcodeIntegrate.initiateScan();
            }
        });
    }

    //================================================================================
    // Changes barcode field to barcode taken with camera
    //================================================================================    @Override
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