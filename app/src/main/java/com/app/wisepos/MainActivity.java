package com.app.wisepos;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.wisepos.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_purchase_history, R.id.navigation_catalog, R.id.navigation_settings)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void navigateToPurchaseHistoryFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_purchase_history, null, new NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
    }


   /* @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.mobile_navigation);
        if(mBottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
            finish();
        }
        else {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    } */

    public boolean onSupportNavigateUp() {
        super.onBackPressed();
       /* BottomNavigationView mBottomNavigationView = findViewById(R.id.nav_view);
        if(mBottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
            finish();
        }
        else {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } */
        return true;
    }

}