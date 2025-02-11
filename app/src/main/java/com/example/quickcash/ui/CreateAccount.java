package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;

public class CreateAccount extends AppCompatActivity {
    private static final String TAG = "CreateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button loginButton = findViewById(R.id.buttonLogin);

        if (loginButton == null) {
            Log.e(TAG, "Login Button not found in layout!");
            return;
        }

        Log.d(TAG, "Login Button FOUND in layout!");

        // Set onClickListener for buttonLogin
        loginButton.setOnClickListener(v -> goToLogin());
    }

    // Corrected goToLogin method
    public void goToLogin() {
        Log.d(TAG, "goToLogin method called");
        Intent intent = new Intent(CreateAccount.this, LoginActivity.class);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "Intent to LoginActivity RESOLVED!");
            startActivity(intent);
        } else {
            Log.e(TAG, "ERROR: LoginActivity NOT FOUND!");
        }
    }
}
