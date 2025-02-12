package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText answer1, answer2, answer3, newPassword, confirmPassword;
    private Button submitButton, backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize input fields
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        submitButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBackToLogin);

        // Set click listener for submit button
        submitButton.setOnClickListener(v -> validateAndSubmit());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void validateAndSubmit() {
        String ans1 = answer1.getText().toString().trim();
        String ans2 = answer2.getText().toString().trim();
        String ans3 = answer3.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (ans1.isEmpty() || ans2.isEmpty() || ans3.isEmpty()) {
            Toast.makeText(this, "Please answer all security questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please enter your new password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with updating password logic (e.g., update database)
        Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();

        // Redirect to login page or close activity
        finish();
    }
}
