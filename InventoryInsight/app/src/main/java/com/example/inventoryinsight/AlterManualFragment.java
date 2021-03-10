package com.example.inventoryinsight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class AlterManualFragment extends Fragment {

    public static final String insert_URL = "http://10.0.0.184/InventoryDB/insert_item_info.php";
    private TextInputEditText upc_field, item_name_field, quantity_field;
    private AutoCompleteTextView location_field;

    public AlterManualFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.database_checked, container, false);
        MaterialTextView manual_title = v.findViewById(R.id.add_screen_title_text);
        manual_title.setText(getString(R.string.add_screen_manual));

        TextInputEditText upc_field = v.findViewById(R.id.upc_field);
        Bundle bundle = this.getArguments();
        upc_field.setText(bundle.getString("barcode"));
        upc_field.setEnabled(false);

        item_name_field = v.findViewById(R.id.item_name_field);
        quantity_field = v.findViewById(R.id.quantity_field);

        Button confirm_button = v.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String barcode = upc_field.getText().toString().trim();
                final String name = item_name_field.getText().toString().trim();
                final String quantity = quantity_field.getText().toString().trim();

                //ensure that required fields are filled
                if (name.equals("")) {
                    item_name_field.setError("An item name is required!");
                    return;
                }
                if (quantity.equals("")) {
                    quantity_field.setError("An item quantity is required!");
                    return;
                }

                //need to add the new item to item_info table first
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