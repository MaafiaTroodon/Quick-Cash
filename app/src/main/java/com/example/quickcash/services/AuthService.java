// AuthService.java
package com.example.quickcash.services;

import com.example.quickcash.model.SecurityModel;

public interface AuthService {
    void loginUser(String email, String password, UserCallback callback);
    void createUser(String username, String password, String email, String role, SecurityModel securityModel, UserCallback callback);
    void forgotPassword(String email, String answer1, String answer2, String answer3, String newPassword, String confirmPassword, UserCallback callback);
    void signOut();

    interface UserCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
