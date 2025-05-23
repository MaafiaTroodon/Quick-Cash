package com.example.quickcash.ui;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.quickcash.R;
import com.example.quickcash.ui.CreateJobPage;
import com.example.quickcash.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreatorDashboard extends AppCompatActivity {

    private TextView welcomeText, locationText;
    private Button logoutButton, jobCreationButton, viewSearchersButton, viewPreferredListButton;
    private FirebaseAuth auth;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button viewEmployeesButton;


    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatordashboard);

        welcomeText = findViewById(R.id.heading);
        logoutButton = findViewById(R.id.LogOut);
        jobCreationButton = findViewById(R.id.jobCreationButton);
        viewSearchersButton = findViewById(R.id.viewSearchersButton);
        viewPreferredListButton = findViewById(R.id.viewPreferredListButton); // New button initialization
        locationText = findViewById(R.id.locationText);

        auth = FirebaseAuth.getInstance();

        // Initialize LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Open CreateJobPage when button is clicked
        jobCreationButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorDashboard.this, CreateJobPage.class);
            startActivity(intent);
        });

        // Open CreatorEmployeeList when View Searchers button is clicked
        viewSearchersButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorDashboard.this, CreatorEmployeeList.class);
            startActivity(intent);
        });

        // New: Open CreatorPreferredList when View Preferred List button is clicked
        viewPreferredListButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorDashboard.this, CreatorPreferredList.class);
            startActivity(intent);
        });

        viewEmployeesButton = findViewById(R.id.viewEmployeesButton);
        viewEmployeesButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorDashboard.this, EmployeeListPage.class);
            startActivity(intent);
        });


        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(CreatorDashboard.this)
                    .setMessage("Are you sure you want to log out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        auth.signOut();
                        Toast.makeText(CreatorDashboard.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreatorDashboard.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                    .show();
        });

        // Location-related code from US1-Location
        initializeLocationListener();
        checkLocationPermissionAndStartUpdates();
    }

    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Convert coordinates to address
                String address = getAddressFromCoordinates(latitude, longitude);

                // Display the address
                locationText.setText("Your Location: " + address);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Method intentionally left empty as it is not required for our use case
            }

            @Override
            public void onProviderEnabled(String provider) {
                // No additional action needed when provider is enabled
            }

            @Override
            public void onProviderDisabled(String provider) {
                // No additional action needed when provider is disabled
            }
        };
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address";
    }

    private void checkLocationPermissionAndStartUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}
