package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.utils.NotificationHelper;
import com.example.expensetracker.utils.PermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Request SMS permissions on first launch
        if (!PermissionHelper.hasSmsPermissions(this)) {
            PermissionHelper.requestSmsPermissions(this);
        }

        // Handle navigation from notification
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("navigate_to")) {
            String destination = intent.getStringExtra("navigate_to");
            if ("pending".equals(destination) && navController != null) {
                navController.navigate(R.id.navigation_pending);
            }
        }
    }
}
