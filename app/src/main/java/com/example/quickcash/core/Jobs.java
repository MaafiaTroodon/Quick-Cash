package com.example.quickcash.core;

import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Jobs {
    private final DatabaseReference jobsRef;
    private final FirebaseAuth auth;

    public Jobs(Firebase firebase) {
        this.auth = FirebaseAuth.getInstance();
        this.jobsRef = firebase.getDb().getReference("Jobs");
    }

    public interface JobCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public void createJob(JobModel jobModel, JobCallback callback) {
        String jobId = jobsRef.push().getKey();
        if (jobId != null) {
            jobsRef.child(jobId).setValue(jobModel)
                    .addOnSuccessListener(aVoid -> callback.onSuccess("Job added successfully."))
                    .addOnFailureListener(e -> callback.onError("Failed to add job: " + e.getMessage()));
        } else {
            callback.onError("Failed to generate job ID.");
        }
    }
}