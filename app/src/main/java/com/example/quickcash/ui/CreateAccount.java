package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.model.UserModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private EditText userName, email, password, confirmPassword, securityAnswer1, securityAnswer2, securityAnswer3;
    private Button createAccountButton;
    private TextView loginLink;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize UI Elements
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        securityAnswer1 = findViewById(R.id.securityAnswer1);
        securityAnswer2 = findViewById(R.id.securityAnswer2);
        securityAnswer3 = findViewById(R.id.securityAnswer3);
        createAccountButton = findViewById(R.id.buttonCreateAccount);
        loginLink = findViewById(R.id.textLoginLink);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set click listener for Create Account button
        createAccountButton.setOnClickListener(v -> createUserAccount());

        // Redirect to Login page when clicked
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createUserAccount() {
        String username = userName.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        String userSecurityAnswer1 = securityAnswer1.getText().toString().trim();
        String userSecurityAnswer2 = securityAnswer2.getText().toString().trim();
        String userSecurityAnswer3 = securityAnswer3.getText().toString().trim();

        if (username.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || confirmPass.isEmpty() ||
                userSecurityAnswer1.isEmpty() || userSecurityAnswer2.isEmpty() || userSecurityAnswer3.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create UserModel instance and store security answers
        UserModel user = new UserModel(username, userEmail, userPassword);
        user.setSecurityAns(userSecurityAnswer1, userSecurityAnswer2, userSecurityAnswer3);

        // Save user data in Firebase
        DatabaseReference userRef = databaseReference.push();  // Generates unique user ID
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateAccount.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateAccount.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(CreateAccount.this, "Failed to create account!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
