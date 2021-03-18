package com.example.inventoryinsight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RemoveAutoFragment extends Fragment {

    //================================================================================
    // Database information
    //================================================================================
    public static final String select_locations_URL = "http://10.0.0.184/InventoryDB/possible_locations/select_possible_locations.php";
    public static final String update_combined_URL = "http://10.0.0.184/InventoryDB/combined_info/update_combined_info.php";

    //================================================================================
    // Layout information
    //================================================================================
    private TextInputEditText upc_field, item_name_field, quantity_field;

    //================================================================================
    // Variables for database
    //================================================================================
    private String location_id, select_location = "";;
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
        update_title.setText(getString(R.string.remove_screen_auto));
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
        ArrayList<Location> locations = (ArrayList<Location>)getArguments().getSerializable("found_locations");
        LocationAdapter adapter = new LocationAdapter(getContext(), locations);
        Spinner location_field = v.findViewById(R.id.location_field);
        location_field.setAdapter(adapter);
        //adapter.notifyDataSetChanged(); // The spinner needs to be refreshed with proper data

        //================================================================================
        // Collecting spinner data
        //================================================================================
        location_field.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id)
                    {
                        // It returns the clicked item
                        Location clickedItem = (Location) parent.getItemAtPosition(position);
                        select_location = clickedItem.getLocation();
                        location_id = clickedItem.getId().toString();   // Used to add to combined table
                        Toast.makeText(getActivity(), "Selected item" + clickedItem, Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        return v;
    }
}