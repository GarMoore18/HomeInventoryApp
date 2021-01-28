    package com.example.inventoryinsight;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;

    import android.os.Bundle;
    import android.view.MenuItem;

    import com.google.android.material.bottomnavigation.BottomNavigationView;

    import java.util.Objects;

    public class MainActivity extends AppCompatActivity
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Objects.requireNonNull(this.getSupportActionBar()).hide();
            setContentView(R.layout.activity_main);

            loadFragment(new HomeFragment());

            BottomNavigationView navigation = findViewById(R.id.navigation_view);
            navigation.setOnNavigationItemSelectedListener(this);
            navigation.setSelectedItemId(R.id.menu_home);   //sets the initial selected icon to home
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