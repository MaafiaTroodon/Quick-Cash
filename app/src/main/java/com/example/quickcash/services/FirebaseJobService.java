// FirebaseJobService.java
package com.example.quickcash.services;

import androidx.annotation.NonNull;

import com.example.quickcash.model.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FirebaseJobService implements JobService {

    private final DatabaseReference jobsRef;

    public FirebaseJobService() {
        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
    }

    @Override
    public void createJob(JobModel job, JobCallback callback) {
        String jobId = jobsRef.push().getKey();
        if (jobId != null) {
            jobsRef.child(jobId).setValue(job)
                    .addOnSuccessListener(aVoid -> callback.onSuccess("Job created successfully!"))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
    }

    @Override
    public void loadAllJobs(JobDataCallback callback) {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JobModel> jobList = new ArrayList<>();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    if (job != null) {
                        jobList.add(job);
                    }
                }
                callback.onJobsLoaded(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
