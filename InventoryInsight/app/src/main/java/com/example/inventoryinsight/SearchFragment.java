package com.example.inventoryinsight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    //TODO: TEMP FIX FOR WHEN QUANTITY RADIO BUTTON IS NOT SELECTED
    public static final String search_auto_url = "http://10.0.0.184/InventoryDB/SearchFragPHP/search_auto.php";
    public static final String search_manual_url = "http://10.0.0.184/InventoryDB/SearchFragPHP/search_manual.php";

    private TextInputEditText upc_field, quantity_field;
    private AutoCompleteTextView item_name_field;
    private Spinner location_field;
    private Button confirm_button_auto, confirm_button_manual, to_auto_search, to_manual_search;
    private ConstraintLayout manual_field, auto_field;
    private ViewFlipper viewFlipper;
    private String barcode, location_id = "", name = "", quantity = "", select_location = "", filterName = "", operator = "";
    private ListView listView;
    private Location clickedItem;

    private RadioGroup radioGroupQuan, radioGroupSelection;
    private RadioButton more, less, equal, description, quan_rad, loca_rad, curr;
    private final String moreText = "More than: ", lessText = "Less than: ", equalText = "Exactly: ";

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

        radioGroupQuan = v.findViewById(R.id.radio_group);
        radioGroupSelection = v.findViewById(R.id.radio_group_start);
        more = v.findViewById(R.id.more_than);
        less = v.findViewById(R.id.less_than);
        equal = v.findViewById(R.id.exactly);
        description = v.findViewById(R.id.description_radio);
        quan_rad = v.findViewById(R.id.quantity_radio);
        loca_rad = v.findViewById(R.id.location_radio);

        // For the auto search
        upc_field = v.findViewById(R.id.upc_field);

        // For the manual search
        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);

        // For the spinner
        location_field = v.findViewById(R.id.location_field);
        listView = v.findViewById(R.id.listview);

        ///////////////////////////////////////////////////////////////
        SQLiteDatabase useDb = MainActivity.db.getReadableDatabase();
        final String [] itemNames;
        ArrayList<String> array = new ArrayList<>();
        String sql = "SELECT name FROM item_info";

        Cursor cr = useDb.rawQuery(sql, null);
        cr.moveToFirst();//cursor pointing to first row
        itemNames = new String[cr.getCount()];//create array string based on numbers of row
        int i=0;
        do  {
            itemNames[i] = cr.getString(0);//insert new stations to the array list
            Log.i("ArrayList",itemNames[i]);
            i++;
        }while(cr.moveToNext());
        //Finally Set the adapter to AutoCompleteTextView like this,
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, itemNames);
        //populate the list to the AutoCompleteTextView controls
        item_name_field.setAdapter(autoAdapter);
        autoAdapter.notifyDataSetChanged();
        ///////////////////////////////////////////////////////////////

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

        getStrings(v); //get the current radio button text

        updateRadio();
        radioGroupFilterListener(v);
        radioGroupQuanListener();

        //Used to load initiate the scanner when the image is clicked
        cameraScanEvent(v.findViewById(R.id.scan_image_button));

        return v;   //Returning the view for the fragment
    }

    //================================================================================
    // Gets current radio button filter text
    //================================================================================
    private void getStrings(View v) {
        curr = v.findViewById(radioGroupSelection.getCheckedRadioButtonId());
        filterName = (String) curr.getText();  // To decide how to SQL query
    }

    //================================================================================
    // Sets field visibility based on radio button
    //================================================================================
    private void radioGroupFilterListener(View v) {
        radioGroupSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getStrings(v);

                if (description.isChecked()) {
                    item_name_field.setVisibility(View.VISIBLE);
                    quantity_field.setVisibility(View.GONE);
                    radioGroupQuan.setVisibility(View.GONE);
                    location_field.setVisibility(View.GONE);
                } else if (quan_rad.isChecked()) {
                    item_name_field.setVisibility(View.GONE);
                    quantity_field.setVisibility(View.VISIBLE);
                    radioGroupQuan.setVisibility(View.VISIBLE);
                    location_field.setVisibility(View.GONE);
                } else if (loca_rad.isChecked()) {
                    item_name_field.setVisibility(View.GONE);
                    quantity_field.setVisibility(View.GONE);
                    radioGroupQuan.setVisibility(View.GONE);
                    location_field.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //================================================================================
    // Sets field visibility based on radio button
    //================================================================================
    private void radioGroupQuanListener() {
        radioGroupQuan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //operator = "<>";
                if (more.isChecked()) {
                    operator = ">";
                } else if (less.isChecked()) {
                    operator = "<";
                } else if (equal.isChecked()) {
                    operator = "=";
                }
            }
        });
    }

    //================================================================================
    // Changes text for radio buttons of quantity
    //================================================================================
    private void updateRadio() {
        quantity_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                more.setText(moreText + s);
                less.setText(lessText + s);
                equal.setText(equalText + s);
            }
        });

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
                        try {
                            Log.d("HEYHO", String.valueOf(response));
                            if (response.getJSONObject(0).getString("no_matches").equals("true")) {
                                ((MainActivity)getActivity()).noSearchMatches();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                Log.d("eee", String.valueOf(error));
                ((MainActivity)getActivity()).volleyRequestError();
            }
        });
        requestQueue.add(jsonObjReq);   // Add request to queue
    }

    //================================================================================
    // Requests information from database for manual search
    //================================================================================
    private void manualMakeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        ArrayList<Recents> arrayOfHits = new ArrayList<>();
        RecentsAdapter adapter = new RecentsAdapter(getActivity(), arrayOfHits);

        listView.setAdapter(adapter);

        // Create JSON object to POST to database
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("radio", filterName);
            jsonObject.put("description", name);
            jsonObject.put("quantity", quantity);
            jsonObject.put("operator", operator);
            jsonObject.put("location", location_id);
            Log.d("JSON", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, search_manual_url, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("HEYHO", String.valueOf(response));
                            if (response.getJSONObject(0).getString("no_matches").equals("true")) {
                                ((MainActivity)getActivity()).noSearchMatches();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.d("GFRGADSRGARD", String.valueOf(response));
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
                Log.d("rrgsagr", String.valueOf(error));
            }
        });
        requestQueue.add(jsonObjReq);   // Add request to queue
    }

    //================================================================================
    // Checks that at least one field has information
    //================================================================================
    private boolean checkForField() {
        // At least one field must be filled
        Log.d("Description", String.valueOf(!name.equals("")));
        Log.d("Quantity", String.valueOf(!quantity.equals("")));
        Log.d("Location Value", String.valueOf(clickedItem));
        Log.d("Location", String.valueOf(!select_location.equals("Select Location")));
        return (!name.equals("") || !quantity.equals("") || (clickedItem != null & !select_location.equals("Select Location")));
    }

    //================================================================================
    // Creates a dialog for when the barcode is invalid
    //================================================================================
    private void fillAFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Enter Information");
        builder.setMessage("Please provide information for one field.");

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