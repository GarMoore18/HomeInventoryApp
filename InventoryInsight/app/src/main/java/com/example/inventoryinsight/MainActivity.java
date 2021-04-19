    package com.example.inventoryinsight;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;

    import android.Manifest;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.FragmentTransaction;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.graphics.Bitmap;
    import android.graphics.Matrix;
    import android.hardware.Camera;
    import android.hardware.camera2.CameraCharacteristics;
    import android.hardware.camera2.CameraManager;
    import android.hardware.camera2.CaptureRequest;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.util.Base64;
    import android.util.Log;
    import android.view.MenuItem;
    import android.view.Surface;
    import android.view.View;
    import android.widget.ArrayAdapter;
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
    import com.journeyapps.barcodescanner.CaptureManager;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.ByteArrayOutputStream;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;

    public class MainActivity extends AppCompatActivity
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        public static final String select_locations_URL = "http://10.0.0.184/InventoryDB/MainActPHP/select_possible_locations.php";
        public static final String sqlite_filler = "http://10.0.0.184/InventoryDB/Test/sqliteRequest.php";

        public static DatabaseHelper db;

        public static Context contextOfApplication;
        public static Context getContextOfApplication()
        {
            return contextOfApplication;
        }

        public static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 100;
        public static int cTheme = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d("VERSION", Build.VERSION.RELEASE);

            // Set up the database and have the database update when the app is opened
            db = new DatabaseHelper(MainActivity.this);
            //Sets the old database version to one below the new version
            SQLiteDatabase sqlite_db = db.getReadableDatabase();
            int new_version = DatabaseHelper.getDbVersion();
            sqlite_db.setVersion(new_version - 1);
            //sqlite_db.getVersion();
            Log.d("VERSION", String.valueOf(sqlite_db));

            makeSQLite();

            //hide the name of the app
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            setContentView(R.layout.activity_main);

            //TODO: FIX VOLLEY ERROR IN HOME
            //load the base fragment
            loadFragment(new HomeFragment());

            //allows for navigation
            BottomNavigationView navigation = findViewById(R.id.navigation_view);
            navigation.setOnNavigationItemSelectedListener(this);
            navigation.setSelectedItemId(R.id.menu_home);   //sets the initial selected icon to home

            contextOfApplication = getApplicationContext();
            requestAppPermissions();
        }

        public int getcTheme() {
            return cTheme;
        }

        public static Bitmap RotateBitmap(Bitmap source, float angle)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        ////////////////////////////////////////////////////////////////////////////
        // THESE WILL WORK FOR VERSIONS ABOVE 8 (ORE0)
        //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

        // THESE ARE METHODS THAT ARE ATTEMPTING TO ROTATE THE IMAGE CORRECTLY
        public static Bitmap rotateImage(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        }

        // THESE ARE METHODS THAT ARE ATTEMPTING TO ROTATE THE IMAGE CORRECTLY
        public static String getRealPathFromURI(Uri uri) {
            Cursor cursor = contextOfApplication.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }

        // THESE ARE METHODS THAT ARE ATTEMPTING TO ROTATE THE IMAGE CORRECTLY
        public static Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        }

        // THESE ARE METHODS TO ALLOW FOR PROPER WRITE PERMISSIONS
        private void requestAppPermissions() {

            if (hasReadPermissions() && hasWritePermissions()) {
                return;
            }

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
        }

        // THESE ARE METHODS TO ALLOW FOR PROPER WRITE PERMISSIONS
        private boolean hasReadPermissions() {
            return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

        // THESE ARE METHODS TO ALLOW FOR PROPER WRITE PERMISSIONS
        private boolean hasWritePermissions() {
            return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

        //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
        // THESE WILL WORK FOR VERSIONS ABOVE 8 (ORE0)
        ////////////////////////////////////////////////////////////////////////////

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
        // Requests information from database and fills SQLite
        //================================================================================
        private void makeSQLite() {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

            // Custom request to POST a JSONObject and Receive a JSONArray
            CustomRequest jsonObjReq = new CustomRequest(Request.Method.GET, sqlite_filler,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Check for at least one item
                            JSONArray itemArray = new JSONArray();
                            JSONArray combinedArray = new JSONArray();
                            JSONArray locationArray = new JSONArray();
                            JSONArray userArray = new JSONArray();

                            try {
                                itemArray = response.getJSONArray(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                combinedArray = response.getJSONArray(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                locationArray = response.getJSONArray(2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                userArray = response.getJSONArray(3);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < itemArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectFromArray =
                                            itemArray.getJSONObject(i);

                                    //String iid = jsonObjectFromArray.getString("iid");
                                    String barcode = jsonObjectFromArray.getString("barcode");
                                    String iname = jsonObjectFromArray.getString("iname");
                                    String image = jsonObjectFromArray.getString("image");

                                    db.addItem(iname, barcode, image);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            for (int i = 0; i < combinedArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectFromArray =
                                            combinedArray.getJSONObject(i);

                                    //String cid = jsonObjectFromArray.getString("cid");
                                    String ciid = jsonObjectFromArray.getString("ciid");
                                    String clid = jsonObjectFromArray.getString("clid");
                                    String cuid = jsonObjectFromArray.getString("cuid");
                                    String quantity = jsonObjectFromArray.getString("quantity");
                                    String last_mod = jsonObjectFromArray.getString("last_mod");

                                    db.addCombined(Integer.parseInt(ciid), Integer.parseInt(clid), Integer.parseInt(cuid), Integer.parseInt(quantity), last_mod);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            for (int i = 0; i < locationArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectFromArray =
                                            locationArray.getJSONObject(i);

                                    //String lid = jsonObjectFromArray.getString("lid");
                                    String lname = jsonObjectFromArray.getString("lname");

                                    db.addLocation(lname);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            for (int i = 0; i < userArray.length(); i++) {
                                try {
                                    JSONObject jsonObjectFromArray =
                                            userArray.getJSONObject(i);

                                    //String uid = jsonObjectFromArray.getString("iid");
                                    String username = jsonObjectFromArray.getString("username");
                                    String password = jsonObjectFromArray.getString("password");
                                    String admin = jsonObjectFromArray.getString("admin");

                                    db.addUser(username, password, Integer.parseInt(admin));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjReq);   // Add request to queue
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
        public void noSearchMatches() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setCancelable(false);
            builder.setTitle("No Results");
            builder.setMessage("There are not any items that match that search.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
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

        // This will ask the user to confirm logging out
        @Override
        public void onBackPressed() {
            logoutDialog();
        }

        public void logoutDialog() {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        // TODO: IN PROGRESS
        public void disableBack() {
            // This will not allow the user to return to previous screen once moving fragment
            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }