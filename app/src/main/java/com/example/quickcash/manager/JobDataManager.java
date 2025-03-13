// JobDataManager.java
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

    public interface JobDataCallback {
        void onJobsLoaded(List<JobModel> jobList);
        void onError(String error);
    }
}
