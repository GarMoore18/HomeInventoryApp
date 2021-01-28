    package com.example.inventoryinsight;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageButton;

    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.textfield.TextInputEditText;

    import java.util.Objects;

    public class MainActivity extends AppCompatActivity
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        public static TextInputEditText upcField;
        ImageButton scanImage;

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

            //camera implementation
            //TODO: NEEDS WORK BECAUSE THE SCANBARCODEIMAGE IS IN FRAGMENTS
            /*
            upcField = findViewById(R.id.upc_field);
            scanImage = findViewById(R.id.scan_image_button);
            scanImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                }
            });
             */
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

    }