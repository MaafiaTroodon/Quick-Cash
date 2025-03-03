package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, forgotPasswordButton, createAccountBack;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        forgotPasswordButton = findViewById(R.id.buttonForgotPassword);
        createAccountBack = findViewById(R.id.buttonCreateAccount2);

        // Initialize Users class
        Firebase firebase = new Firebase();
        users = new Users(firebase);

        // Set button click listeners
        loginButton.setOnClickListener(v -> loginUser());
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        createAccountBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call loginUser() from Users class
        users.loginUser(email, password, new Users.UserCallback() {

            @Override
            public void onSuccess(String role) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Get the user's location
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Pass location to the dashboard
                                Intent intent;
                                if ("creator".equalsIgnoreCase(role)) {
                                    intent = new Intent(LoginActivity.this, CreatorDashboard.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, SearcherDashboard.class);
                                }

                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error fetching location", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
