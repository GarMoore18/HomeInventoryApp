    package com.example.inventoryinsight;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;

    import android.app.AlertDialog;
    import android.app.FragmentTransaction;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageButton;
    import android.widget.ViewFlipper;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.zxing.integration.android.IntentIntegrator;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.Objects;

    public class MainActivity extends AppCompatActivity
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        public static final String select_locations_URL = "http://10.0.0.184/InventoryDB/MainActPHP/select_possible_locations.php";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //hide the name of the app
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            setContentView(R.layout.activity_main);

            //load the base fragment
            loadFragment(new HomeFragment());

            //allows for navigation
            BottomNavigationView navigation = findViewById(R.id.navigation_view);
            navigation.setOnNavigationItemSelectedListener(this);
            navigation.setSelectedItemId(R.id.menu_home);   //sets the initial selected icon to home

        }

        //Pass the result to the fragment
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        //Method for the navigation bar screen select
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            int itemId = item.getItemId();

            //Will navigate to correct activity based on selected destination
            if (itemId == R.id.menu_add) {
                fragment = new AddFragment();
            } else if (itemId == R.id.menu_remove) {
                fragment = new RemoveFragment();
            } else if (itemId == R.id.menu_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.menu_search) {
                fragment = new SearchFragment();
            } else if (itemId == R.id.menu_settings) {
                fragment = new SettingsFragment();
            }
            return loadFragment(fragment);
        }

        //Used to load the selected fragment
        private boolean loadFragment(Fragment fragment) {
            //switching fragment
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
            }
            return false;
        }

        //================================================================================
        // Gets all locations from the database for search and add spinners
        //================================================================================
        public ArrayList<Location> locationSpinnerRequest(ArrayList<Location> locations, LocationAdapter adapter) {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, select_locations_URL, null,
                    new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    locations.add(new Location(-1, "Select Location"));

                    // All of the items in the response need to be added to the locations array for the spinner
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObjectFromArray =
                                    response.getJSONObject(i);

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
                    //((MainActivity)getActivity()).volleyRequestError();
                    Log.d("error", String.valueOf(error));
                    volleyRequestError();
                }
            });
            requestQueue.add(jsonArrayRequest);   // Add request to queue

            return locations;
        }

        //================================================================================
        // Creates a dialog for when request is unsuccessful
        //================================================================================
        public void volleyRequestError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setCancelable(false);
            builder.setTitle("Request Unsuccessful");
            builder.setMessage("Things to do:\n\tSubmit again\n\tCheck internet connection\n\tCheck server status\n\tContact Garrett");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }

        //================================================================================
        // Creates a dialog for when the barcode is invalid
        //================================================================================
        public void invalidBarcodeDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setCancelable(false);
            builder.setTitle("Invalid Barcode");
            builder.setMessage("The barcode must be a EAN13 or UPC");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }

        // TODO: IN PROGRESS
        public void disableBack() {
            // This will not allow the user to return to previous screen once moving fragment
            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }