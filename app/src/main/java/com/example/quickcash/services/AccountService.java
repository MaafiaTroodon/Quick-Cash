// AccountService.java
package com.example.quickcash.services;

import com.example.quickcash.model.SecurityModel;

public interface AccountService {
    void createAccount(String username, String password, String email, String role,
                       SecurityModel securityModel, AccountCallback callback);

    interface AccountCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
