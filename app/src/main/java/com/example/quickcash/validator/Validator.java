package com.example.quickcash.validator;

public class Validator {
    public boolean isEmptyEmailAddress(String emailAddress) {
        return emailAddress.trim().isEmpty();
    }

    public boolean isValidEmailAddress(String emailAddress) {
        if(emailAddress == null || emailAddress.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return emailAddress.matches(emailRegex);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        String passwordRegex = "^(?=.*[a-zA-Z0-9]{7,})(?=.*[!@#$%^&*]+).{8,}$";
        return password.matches(passwordRegex);
    }

    public boolean isValidRole(String role) {
        if (role == null || role.isEmpty()) {
            return false;
        }
        return !role.equals("Select your role"); //"Select your role" may vary depending on the default hint message.
    }


}
