// AccountValidator.java (Input Validation)
package com.example.quickcash.validator;

import android.content.Context;
import android.widget.Toast;

public class AccountValidator {

    private final Context context;

    // Constructor to accept a Context object
    public AccountValidator(Context context) {
        this.context = context;
    }

    public boolean validateInputs(String username, String email, String password, String confirmPassword,
                                  String answer1, String answer2, String answer3, String role) {
        if (username.isEmpty()) {
            showToast("Username is required!");
            return false;
        }

        if (email.isEmpty()) {
            showToast("Email is required!");
            return false;
        }

        if (!isValidEmailAddress(email)) {
            showToast("Invalid email format!");
            return false;
        }

        if (!isValidPassword(password)) {
            showToast("Password must be at least 8 characters and contain a special character!");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match!");
            return false;
        }

        if (answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty()) {
            showToast("All security questions must be answered!");
            return false;
        }

        if (!isValidRole(role)) {
            showToast("Invalid Role");
            return false;
        }

        return true;
    }

    private boolean isValidEmailAddress(String email) {
        // Email validation logic
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    private boolean isValidPassword(String password) {
        // Password validation logic
        return password.length() >= 8 && password.matches(".*[!@#$%^&*()].*");
    }

    private boolean isValidRole(String role) {
        // Role validation logic
        return role.equalsIgnoreCase("creator") || role.equalsIgnoreCase("searcher");
    }

    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
