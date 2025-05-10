package com.example.quickcash.manager;

import androidx.annotation.NonNull;

import com.example.quickcash.model.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class JobDataManager {

    private final DatabaseReference jobsRef;

    public JobDataManager() {
        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
    }

    public void loadAllJobs(JobDataCallback callback) {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JobModel> jobList = new ArrayList<>();
                List<String> jobIds = new ArrayList<>();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    if (job != null) {
                        jobList.add(job);
                        jobIds.add(jobSnapshot.getKey());
                    }
                }
                callback.onJobsLoaded(jobList, jobIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void applyForJob(String jobId, String userEmail, ApplyCallback callback) {
        DatabaseReference jobRef = jobsRef.child(jobId);
        jobRef.child("applicants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> applicants = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot applicantSnapshot : snapshot.getChildren()) {
                        applicants.add(applicantSnapshot.getValue(String.class));
                    }
                }

                if (!applicants.contains(userEmail)) {
                    applicants.add(userEmail);
                    jobRef.child("applicants").setValue(applicants)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                } else {
                    callback.onError("You've already applied to this job");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface JobDataCallback {
        void onJobsLoaded(List<JobModel> jobList, List<String> jobIds);
        void onError(String error);
    }

    public interface ApplyCallback {
        void onSuccess();
        void onError(String error);
    }
}