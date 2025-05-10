// FirebaseAccountService.java
package com.example.quickcash.services;

import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.SecurityModel;

public class FirebaseAccountService implements AccountService {

    private final Users users;

    public FirebaseAccountService() {
        this.users = new Users(new Firebase());
    }

    @Override
    public void createAccount(String username, String password, String email, String role,
                              SecurityModel securityModel, AccountCallback callback) {
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
}
