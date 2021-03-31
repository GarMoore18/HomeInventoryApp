package com.example.inventoryinsight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AlterManualFragment extends Fragment {

    public static final String add_quantity_manual = "http://10.0.0.184/InventoryDB/AddManFragPHP/add_quantity_manual.php";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CANCEL = 0;


    private TextInputEditText upc_field, item_name_field, quantity_field;
    private String quantity, name, barcode, location_id, select_location = "", encodedString;
    private MaterialTextView manual_title;
    private Spinner location_field;
    private Location clickedItem;
    private Button confirm_button;
    private ImageButton img_but;
    public Integer item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);

        manual_title = v.findViewById(R.id.add_screen_title_text);
        upc_field = v.findViewById(R.id.upc_field);

        // Information passed from add fragment
        Bundle bundle = this.getArguments();
        manual_title.setText(getString(R.string.add_screen_manual));
        upc_field.setText(bundle.getString("barcode"));
        upc_field.setEnabled(false);

        img_but = v.findViewById(R.id.image_but);

        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);

        location_field = v.findViewById(R.id.location_field);
        locationSpinner();  // Set spinner information

        confirm_button = v.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = upc_field.getText().toString().trim();
                name = item_name_field.getText().toString().trim();
                quantity = quantity_field.getText().toString().trim();

                if (checkMissingFields()) {
                    return;
                }
                makeRequest();
            }
        });

        captureImageButton();

        return v;
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
            jsonObject.put("name", name);
            jsonObject.put("image", encodedString);
            jsonObject.put("user_id", "1");   // TODO: WILL NEED TO STORE CORRECT USER
            jsonObject.put("quantity", quantity_field.getText().toString().trim());
            jsonObject.put("location_id", location_id);
            Log.d("JSONObject", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom request to POST a JSONObject and Receive a JSONArray
        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, add_quantity_manual, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check for at least one item
                        try {
                            Log.d("TETETE", String.valueOf(response));
                            // Get the response code for the update
                            JSONObject connect = response.getJSONObject(0);

                            // Only move the fragment if the update was successful
                            if (connect.getInt("result_code") == 200) {
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

    public void captureImageButton() {
        img_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IMAGES", "Image button clicked.");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return android.util.Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != REQUEST_IMAGE_CANCEL) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Bitmap takenImage = (Bitmap) data.getExtras().get("data");
                    encodedString = encodeToBase64(takenImage, Bitmap.CompressFormat.PNG, 100);

                    img_but.setAdjustViewBounds(true);
                    img_but.setImageBitmap(takenImage);
            }
        }
    }
}