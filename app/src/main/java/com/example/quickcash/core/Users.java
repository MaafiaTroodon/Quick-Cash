package com.example.quickcash.core;

import androidx.annotation.NonNull;

import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Users {
    private final DatabaseReference usersRef;
    private final FirebaseAuth auth;

    public Users(Firebase firebase) {
        this.auth = FirebaseAuth.getInstance();
        this.usersRef = firebase.getDb().getReference("Users");
    }

    public void createUser(String username, String password, String email, UserCallback callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();

                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();

                    UserModel newUser = new UserModel(username, email, password);

                    usersRef.child(userId).setValue(newUser)
                            .addOnCompleteListener(item -> {
                                if (item.isSuccessful()) {
                                    callback.onSuccess("User added successfully!");
                                } else {
                                    callback.onError(Objects.requireNonNull(item.getException()).getMessage());
                                }
                            });
                }
            }
            else {
                callback.onError(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void checkIfEmailExists(String username, UsernameCallback callback) {
        usersRef.orderByChild("email").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onResult(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    private DatabaseReference getUserRef(String userId) {
        return usersRef.child(userId);
    }

    private DatabaseReference getUsersRef(){
        return usersRef;
    }




    public interface UserCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface UsernameCallback {
        void onResult(boolean exists);
        void onError(String error);
    }



}
