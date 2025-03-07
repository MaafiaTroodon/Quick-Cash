package com.example.quickcash.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.model.JobModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class SearcherDashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    public JobAdapter jobAdapter;
    private List<JobModel> jobList;

    private DatabaseReference jobsRef;

    public DatabaseReference getJobsRef() {
        return jobsRef;
    }

    public void setJobsRef(DatabaseReference jobsRef) {
        this.jobsRef = jobsRef;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false); // Ensures RecyclerView measures dynamically
        recyclerView.setNestedScrollingEnabled(true); // Enables scrolling

        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");

        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JobModel> updatedJobs = new ArrayList<>();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    updatedJobs.add(job);
                }
                jobAdapter.updateJobs(updatedJobs);

                Log.d("JobList", "Total jobs fetched: " + updatedJobs.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read jobs", error.toException());
            }
        });
    }
}
