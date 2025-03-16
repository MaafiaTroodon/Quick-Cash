// FirebaseAuthService.java
package com.example.quickcash.services;

import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.SecurityModel;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthService implements AuthService {

    private final Users users;

    public FirebaseAuthService() {
        this.users = new Users(new Firebase());
    }

    @Override
    public void loginUser(String email, String password, UserCallback callback) {
        users.loginUser(email, password, new Users.UserCallback() {
            @Override
            public void onSuccess(String role) {
                callback.onSuccess("Login successful!");
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void createUser(String username, String password, String email, String role, SecurityModel securityModel, UserCallback callback) {
        users.createUser(username, password, email, role, securityModel, new Users.UserCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void forgotPassword(String email, String answer1, String answer2, String answer3, String newPassword, String confirmPassword, UserCallback callback) {
        users.forgotPassword(email, answer1, answer2, answer3, newPassword, confirmPassword, new Users.UserCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
