package com.example.quickcash.core;

import androidx.annotation.NonNull;

import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.model.PreferEmployerModel;
import com.example.quickcash.model.SecurityModel;
import com.example.quickcash.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class Users {
    private final DatabaseReference usersRef;
    private final FirebaseAuth auth;

    public Users(Firebase firebase) {
        this.auth = FirebaseAuth.getInstance();
        this.usersRef = firebase.getDb().getReference("Users");
    }

    public void createUser(String username, String password, String email, String role, SecurityModel securityAnswers, UserCallback callback) {
        // Sanitize email to make it a valid Firebase key
        String sanitizedEmail = email.replace(".", ",");

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {

                    // Include security answers in UserModel
                    UserModel newUser = new UserModel(username, email, password, role);
                    newUser.setSecurityAns(securityAnswers);

                    // Store user under email instead of UID
                    usersRef.child(sanitizedEmail).setValue(newUser)
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


    public void forgotPassword(String email, String ans1, String ans2, String ans3, String newPassword, String confirmPassword, UserCallback callback) {
        // Sanitize email to match Firebase database structure
        String sanitizedEmail = email.replace(".", ",");

        // Directly reference the user entry in Firebase
        usersRef.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    callback.onError("Email not found.");
                    return;
                }

                // Retrieve security answers as a SecurityModel object
                SecurityModel securityAnswers = dataSnapshot.child("securityAns").getValue(SecurityModel.class);
                if (securityAnswers == null) {
                    callback.onError("Security answers not found.");
                    return;
                }

                // Convert user-provided answers to lowercase before checking
                boolean isMatch = securityAnswers.isQuestions(
                        ans1.trim().toLowerCase(),
                        ans2.trim().toLowerCase(),
                        ans3.trim().toLowerCase()
                );

                if (!isMatch) {
                    callback.onError("Security answers do not match.");
                    return;
                }

                // Check if passwords match
                if (!newPassword.equals(confirmPassword)) {
                    callback.onError("Passwords do not match.");
                    return;
                }

                // Debugging log
                System.out.println("Updating password for user: " + sanitizedEmail);

                // Update password in Firebase
                usersRef.child(sanitizedEmail).child("password").setValue(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                System.out.println("Password successfully updated in Firebase.");
                                callback.onSuccess("Password updated successfully.");
                            } else {
                                System.out.println("Failed to update password: " + task.getException());
                                callback.onError("Failed to update password: " + task.getException());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Database error: " + databaseError.getMessage());
            }
        });
    }

    public void loginUser(String email, String password, UserCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Fetch user role from Firebase
                        String sanitizedEmail = email.replace(".", ","); // Ensure it matches Firebase key
                        usersRef.child(sanitizedEmail).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String role = dataSnapshot.getValue(String.class);
                                    callback.onSuccess(role); // Pass the role to the callback
                                } else {
                                    callback.onError("Role not found.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.onError("Database error: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        callback.onError("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    public void setPreferredList(String email, List<PreferEmployerModel> newPreferredJobs, UserCallback callback) {
        // Sanitize email to match Firebase database structure
        String sanitizedEmail = email.replace(".", ",");
        usersRef.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onError("Email not found.");
                    return;
                }

                if(newPreferredJobs.isEmpty()) {
                    callback.onSuccess("Did not added new job");
                } else {
                      usersRef.child(sanitizedEmail).child("preferredJob").setValue(newPreferredJobs)
                              .addOnCompleteListener(task -> {
                                  if (task.isSuccessful()) {
                                      System.out.println("preferred job successfully updated in Firebase.");
                                      callback.onSuccess("preferred job updated successfully.");
                                  } else {
                                      System.out.println("Failed to update preferred job: " + task.getException());
                                      callback.onError("Failed to preferred job: " + task.getException());
                                  }
                              });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Database error: " + error.getMessage());
            }
        });
    }

    private DatabaseReference getUserRef(String userId) {
        return usersRef.child(userId);
    }

    private DatabaseReference getUsersRef(){
        return usersRef;
    }

    public FirebaseAuth getAuth() {
        return auth;
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
