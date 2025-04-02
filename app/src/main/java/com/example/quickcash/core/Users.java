package com.example.quickcash.core;

import androidx.annotation.NonNull;

import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.PreferEmployeeModel;
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

    // 🔹 Define constants for frequently used messages
    private static final String DATABASE_ERROR_MSG = "Database error: ";
    private static final String EMAIL_NOT_FOUND_MSG = "Email not found.";
    private static final String PASSWORD_MISMATCH_MSG = "Passwords do not match.";
    private static final String SECURITY_ANSWERS_MISMATCH_MSG = "Security answers do not match.";
    private static final String USER_ADDED_MSG = "User added successfully!";
    private static final String ROLE_NOT_FOUND_MSG = "Role not found.";
    private static final String LOGIN_FAILED_MSG = "Login failed: ";
    private static final String PREFERRED_JOB_UPDATED_MSG = "Preferred job updated successfully.";
    private static final String PREFERRED_JOB_FAILED_MSG = "Failed to update preferred job: ";


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
                                    callback.onSuccess(USER_ADDED_MSG);
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
                    callback.onError(EMAIL_NOT_FOUND_MSG);
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
                    callback.onError(SECURITY_ANSWERS_MISMATCH_MSG);
                    return;
                }

                // Check if passwords match
                if (!newPassword.equals(confirmPassword)) {
                    callback.onError(PASSWORD_MISMATCH_MSG);
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

            private static final String DATABASE_ERROR_MSG = "Database error: ";

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(DATABASE_ERROR_MSG + databaseError.getMessage());
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
                    callback.onError(EMAIL_NOT_FOUND_MSG);
                    return;
                }

                if(newPreferredJobs.isEmpty()) {
                    callback.onSuccess("Did not added new job");
                } else {
                    usersRef.child(sanitizedEmail).child("preferredJob").setValue(newPreferredJobs)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    System.out.println("preferred job successfully updated in Firebase.");
                                    callback.onSuccess(PREFERRED_JOB_UPDATED_MSG);
                                } else {
                                    System.out.println(PREFERRED_JOB_FAILED_MSG + task.getException());
                                    callback.onError(PREFERRED_JOB_FAILED_MSG + task.getException());
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(DATABASE_ERROR_MSG + error.getMessage());
            }
        });
    }

    public void loadPreferredList(String email, LoadPreferredListCallback callback) {
        // Replace "." with "," to match your Firebase keys
        String sanitizedEmail = email.replace(".", ",");

        // Point to the "preferred_list" node for this user
        DatabaseReference preferredListRef = usersRef.child(sanitizedEmail).child("preferredEmployees");

        // Read data once (no real-time updates needed for a simple load)
        preferredListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If the node doesn't exist or is empty, return an empty list
                if (!dataSnapshot.exists()) {
                    callback.onSuccess(java.util.Collections.emptyList());
                    return;
                }

                // Prepare a list to hold all preferred items
                List<PreferEmployeeModel> preferredList = new java.util.ArrayList<>();

                // Each child should map to a PreferEmployerModel
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    PreferEmployeeModel item = childSnapshot.getValue(PreferEmployeeModel.class);
                    if (item != null) {
                        preferredList.add(item);
                    }
                }

                // Return the full list via callback
                callback.onSuccess(preferredList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Database error: " + databaseError.getMessage());
            }
        });
    }


    public void getApplicantsForMyJobs(Firebase firebase, ApplicantsCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("User not logged in.");
            return;
        }

        String currentUserEmail = currentUser.getEmail();
        DatabaseReference jobsRef = firebase.getDb().getReference("Jobs");

        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> allApplicants = new java.util.ArrayList<>();

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String employerEmail = jobSnapshot.child("employerEmail").getValue(String.class);

                    if (employerEmail != null && employerEmail.equals(currentUserEmail)) {
                        DataSnapshot applicantsSnapshot = jobSnapshot.child("applicants");

                        for (DataSnapshot applicant : applicantsSnapshot.getChildren()) {
                            String applicantEmail = applicant.getValue(String.class);
                            if (applicantEmail != null && !allApplicants.contains(applicantEmail)) {
                                allApplicants.add(applicantEmail);
                            }
                        }
                    }
                }

                callback.onSuccess(allApplicants);
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

    public interface ApplicantsCallback {
        void onSuccess(List<String> applicantEmails);
        void onError(String error);
    }

    public interface LoadPreferredListCallback {
        void onSuccess(List<PreferEmployeeModel> preferredList);
        void onError(String error);
    }



}
