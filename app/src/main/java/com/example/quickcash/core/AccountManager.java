// AccountManager.java
package com.example.quickcash.core;

import com.example.quickcash.model.SecurityModel;
import com.example.quickcash.services.AccountService;

public class AccountManager {

    private final AccountService accountService;

    // Constructor to accept an AccountService implementation
    public AccountManager(AccountService accountService) {
        this.accountService = accountService;
    }

    public void createAccount(String username, String password, String email, String role,
                              SecurityModel securityModel, AccountCallback callback) {
        accountService.createAccount(username, password, email, role, securityModel, new AccountService.AccountCallback() {
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

    public interface AccountCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
