package com.example.inventoryinsight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment {

    public static final String search_auto_url = "http://10.0.0.184/InventoryDB/use/search_auto.php";

    private TextInputEditText upc_field, item_name_field, quantity_field;
    private Spinner location_field;
    private Button confirm_button_auto, confirm_button_manual, to_auto_search, to_manual_search;
    private ConstraintLayout manual_field, auto_field;
    private ViewFlipper viewFlipper;
    private String barcode, location_id, name = "", quantity = "", select_location = "";
    private ListView listView;
    private Location clickedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Have to create the view first to use findViewByID in a fragment
        View v = inflater.inflate(R.layout.search_view_flipper, container, false);

        // For the ViewFlipper
        viewFlipper = v.findViewById(R.id.myViewFlipper);
        manual_field = v.findViewById(R.id.manual_field);
        auto_field = v.findViewById(R.id.auto_field);
        to_auto_search = v.findViewById(R.id.to_auto_search);
        to_manual_search = v.findViewById(R.id.to_manual_search);

        // For the auto search
        upc_field = v.findViewById(R.id.upc_field);

        // For the manual search
        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);

        // For the spinner
        location_field = v.findViewById(R.id.location_field);
        listView = v.findViewById(R.id.listview);

        locationSpinner();

        autoSearchOnClick();
        manualSearchOnClick();

        confirm_button_auto = v.findViewById(R.id.confirm_button_auto);
        confirm_button_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = upc_field.getText().toString().trim();

                //Will only enter if an EAN13 barcode is entered
                if (barcode.length() == 12 || barcode.length() == 13) {
                    autoMakeRequest();
                    Log.d("AUTO_TEST", "auto works");
                } else {
                    ((MainActivity)getActivity()).invalidBarcodeDialog();
                }

            }
        });

        confirm_button_manual = v.findViewById(R.id.confirm_button_manual);
        confirm_button_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = item_name_field.getText().toString().trim();
                quantity = quantity_field.getText().toString().trim();

                //Will only enter if an EAN13 barcode is entered
                if (checkForField()) {
                    manualMakeRequest();
                    Log.d("AUTO_TEST", "manual works");
                } else {
                    fillAFieldDialog(); // Prompts user to fill in at least one field
                }
            }
        });

        //Used to load initiate the scanner when the image is clicked
        cameraScanEvent(v.findViewById(R.id.scan_image_button));

        return v;   //Returning the view for the fragment
    }

    //================================================================================
    // Moves to the manual(1) search view flip
    //================================================================================
    private void manualSearchOnClick() {
        to_manual_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1);
            }
        });
    }

    //================================================================================
    // Moves to the auto(0) search view flip
    //================================================================================
    private void autoSearchOnClick() {
        to_auto_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });
    }

    //================================================================================
    // Requests information from database for auto search
    //================================================================================
    private void autoMakeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        ArrayList<Recents> arrayOfHits = new ArrayList<>();
        RecentsAdapter adapter = new RecentsAdapter(getActivity(), arrayOfHits);

        listView.setAdapter(adapter);

        // Create JSON object to POST to database
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("barcode", barcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, search_auto_url, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GFRGADSRGARD", String.valueOf(response));
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObjectFromArray =
                                        response.getJSONObject(i);

                                Recents recent = new Recents(jsonObjectFromArray.getString("barcode"),
                                        jsonObjectFromArray.getString("iid"),
                                        jsonObjectFromArray.getString("image"),
                                        jsonObjectFromArray.getString("lid"),
                                        jsonObjectFromArray.getString("username"),
                                        jsonObjectFromArray.getString("quantity"),
                                        jsonObjectFromArray.getString("last_modified"));

                                arrayOfHits.add(recent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //change page to the results page only if volley was successful
                        //viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(v.findViewById(R.id.results_page)));
                        viewFlipper.setDisplayedChild(2);
                        adapter.notifyDataSetChanged();
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
    // Requests information from database for manual search
    //================================================================================
    private void manualMakeRequest() {

    }

    //================================================================================
    // Checks that at least one field has information
    //================================================================================
    private boolean checkForField() {
        // At least one field must be filled
        return (!name.equals("") || !quantity.equals("") || !select_location.equals("Select Location"));
    }

    //================================================================================
    // Creates a dialog for when the barcode is invalid
    //================================================================================
    private void fillAFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Enter Information");
        builder.setMessage("Please provide information for at least one field.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
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
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

    //================================================================================
    // Loads the camera for scanning barcode
    //================================================================================
    private void cameraScanEvent(ImageButton scanButton) {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator barcodeIntegrate = IntentIntegrator.forSupportFragment(SearchFragment.this);
                barcodeIntegrate.setPrompt("Scan a barcode.");
                barcodeIntegrate.initiateScan();
            }
        });
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