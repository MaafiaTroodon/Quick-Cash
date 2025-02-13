package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.validator.Validator;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput, answer1, answer2, answer3, newPassword, confirmPassword;
    private Button submitButton, backButton;
    private Users users;
    private Validator validator;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize input fields
        emailInput = findViewById(R.id.emailInput);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        submitButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBackToLogin);

        Firebase firebase = new Firebase();
        this.users = new Users(firebase);
        this.validator = new Validator();

        // Set click listener for submit button
        submitButton.setOnClickListener(v -> validateAndSubmit());

        // Navigate back to login screen
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateAndSubmit() {
        String email = emailInput.getText().toString().trim();
        String sanitizedEmail = email.replace(".", ","); // Match Firebase key format

        String ans1 = answer1.getText().toString().trim().toLowerCase();
        String ans2 = answer2.getText().toString().trim().toLowerCase();
        String ans3 = answer3.getText().toString().trim().toLowerCase();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        // Validate inputs
        if (validator.isEmptyEmailAddress(email)) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validator.isValidEmailAddress(email)) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ans1.isEmpty() || ans2.isEmpty() || ans3.isEmpty()) {
            Toast.makeText(this, "Please answer all security questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validator.isValidPassword(newPass)) {
            Toast.makeText(this, "Password must be at least 8 characters and contain a special character!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call forgotPassword method
        users.forgotPassword(sanitizedEmail, ans1, ans2, ans3, newPass, confirmPass, new Users.UserCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
