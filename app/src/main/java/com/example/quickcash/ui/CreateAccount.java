// CreateAccountActivity.java
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
import com.example.quickcash.core.AccountManager;
import com.example.quickcash.model.SecurityModel;
import com.example.quickcash.services.AccountService;
import com.example.quickcash.services.FirebaseAccountService;
import com.example.quickcash.validator.AccountValidator;

public class CreateAccount extends AppCompatActivity {

    private EditText userName, email, password, confirmPassword, securityAnswer1, securityAnswer2, securityAnswer3;
    private Button createAccountButton;
    private TextView loginLink;
    private RadioGroup roleGroup;
    private AccountManager accountManager;
    private AccountValidator validator;

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
        roleGroup = findViewById(R.id.roleGroup);

        // Initialize dependencies
        AccountService accountService = new FirebaseAccountService(); // Use abstraction
        accountManager = new AccountManager(accountService); // Pass the service to AccountManager
        validator = new AccountValidator(this);

        // Set click listeners
        createAccountButton.setOnClickListener(v -> createUserAccount());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void createUserAccount() {
        String username = userName.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        String userSecurityAnswer1 = securityAnswer1.getText().toString().trim().toLowerCase();
        String userSecurityAnswer2 = securityAnswer2.getText().toString().trim().toLowerCase();
        String userSecurityAnswer3 = securityAnswer3.getText().toString().trim().toLowerCase();

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String userRole = selectedRoleButton.getText().toString().trim();

        if (!validator.validateInputs(username, userEmail, userPassword, confirmPass, userSecurityAnswer1, userSecurityAnswer2, userSecurityAnswer3, userRole)) {
            return;
        }

        SecurityModel securityModel = new SecurityModel(userSecurityAnswer1, userSecurityAnswer2, userSecurityAnswer3);
        accountManager.createAccount(username, userPassword, userEmail, userRole, securityModel, new AccountManager.AccountCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(CreateAccount.this, "Role: " + userRole + "\n" + message, Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateAccount.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
