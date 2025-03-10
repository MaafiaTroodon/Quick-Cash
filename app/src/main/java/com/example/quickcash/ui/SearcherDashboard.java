package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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
    private JobAdapter jobAdapter;
    private List<JobModel> jobList;
    private List<JobModel> fullJobList; // Stores all jobs for restoring after search

    private DatabaseReference jobsRef;

    private EditText searchInput;
    private Button searchButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);

        jobList = new ArrayList<>();
        fullJobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");

        loadAllJobs(); // Load all jobs initially

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (query.isEmpty()) {
                restoreFullList(); // Restore original job list when search is empty
            } else {
                filterJobs(query);
            }
        });
    }

    private void loadAllJobs() {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                fullJobList.clear(); // Clear previous data

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    if (job != null) {
                        jobList.add(job);
                        fullJobList.add(job);
                    }
                }
                jobAdapter.updateJobs(jobList);
                Log.d("JobList", "Total jobs loaded: " + jobList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load jobs", error.toException());
            }
        });
    }

    private void filterJobs(String searchQuery) {
        List<JobModel> filteredJobs = new ArrayList<>();
        for (JobModel job : fullJobList) {
            if (job.getTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    job.getCompany().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    job.getLocation().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredJobs.add(job);
            }
        }
        jobAdapter.updateJobs(filteredJobs);
    }

    private void restoreFullList() {
        jobAdapter.updateJobs(fullJobList);
    }
}
