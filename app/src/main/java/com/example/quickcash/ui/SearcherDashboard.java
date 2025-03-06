package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.google.firebase.auth.FirebaseAuth;

public class SearcherDashboard extends AppCompatActivity {

    private TextView welcomeText, locationText;
    private Button logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        welcomeText = findViewById(R.id.heading);
        logoutButton = findViewById(R.id.LogOut);
        locationText = findViewById(R.id.locationText);

        auth = FirebaseAuth.getInstance();

        // Retrieve location data from Intent
        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Display location
        locationText.setText("Your Location: " + latitude + ", " + longitude);

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(SearcherDashboard.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SearcherDashboard.this, LoginActivity.class));
            finish();
        });
    }
}
