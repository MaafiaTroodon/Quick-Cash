package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.SecurityModel;
import com.example.quickcash.validator.UserValidator;

public class CreateAccount extends AppCompatActivity {

    private EditText userName, email, password, confirmPassword, securityAnswer1, securityAnswer2, securityAnswer3;
    private Button createAccountButton;
    private TextView loginLink;
    private RadioGroup roleGroup; // RadioGroup for role selection
    private Users users;
    private UserValidator validator;

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
        roleGroup = findViewById(R.id.roleGroup); // Get the RadioGroup

        // Initialize Users and Validator classes
        Firebase firebase = new Firebase();
        users = new Users(firebase);
        validator = new UserValidator();

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
        String userSecurityAnswer1 = securityAnswer1.getText().toString().trim().toLowerCase();
        String userSecurityAnswer2 = securityAnswer2.getText().toString().trim().toLowerCase();
        String userSecurityAnswer3 = securityAnswer3.getText().toString().trim().toLowerCase();

        // Get selected role
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String userRole = selectedRoleButton.getText().toString().trim(); // Get role text

        // Validate inputs using Validator class
        if (username.isEmpty()) {
            Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validator.isEmptyEmailAddress(userEmail)) {
            Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validator.isValidEmailAddress(userEmail)) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validator.isValidPassword(userPassword)) {
            Toast.makeText(this, "Password must be at least 8 characters and contain a special character!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userSecurityAnswer1.isEmpty() || userSecurityAnswer2.isEmpty() || userSecurityAnswer3.isEmpty()) {
            Toast.makeText(this, "All security questions must be answered!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validator.isValidRole(userRole)) {
            Toast.makeText(this, "Invalid Role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call createUser method from Users class
        SecurityModel securityModel = new SecurityModel(userSecurityAnswer1, userSecurityAnswer2, userSecurityAnswer3);
        users.createUser(username, userPassword, userEmail, userRole, securityModel, new Users.UserCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(CreateAccount.this, "Role: " + userRole + "\n" + message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateAccount.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateAccount.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
