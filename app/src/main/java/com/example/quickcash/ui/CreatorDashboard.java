package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.google.firebase.auth.FirebaseAuth;

public class CreatorDashboard extends AppCompatActivity {

    private TextView welcomeText;
    private Button logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatordashboard);

        welcomeText = findViewById(R.id.heading);
        logoutButton = findViewById(R.id.LogOut);

        auth = FirebaseAuth.getInstance();

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
    }
}
