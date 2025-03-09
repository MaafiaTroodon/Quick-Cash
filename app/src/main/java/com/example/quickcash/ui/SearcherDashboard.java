package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.util.JobAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class SearcherDashboard extends AppCompatActivity {
    private Button logoutButton, preferredJobButton;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    public JobAdapter jobAdapter;
    private List<JobModel> jobList;
    public List<JobModel> preferredJob;

    private DatabaseReference jobsRef;
    private Users users;

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

        auth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.LogOut);
        preferredJobButton = findViewById(R.id.PreferredList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false); // Ensures RecyclerView measures dynamically
        recyclerView.setNestedScrollingEnabled(true); // Enables scrolling

        jobList = new ArrayList<>();
        preferredJob = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        Firebase firebase = new Firebase();
        users = new Users(firebase);

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

        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to log out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        auth.signOut();
                        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                    .show();
        });

        preferredJobButton.setOnClickListener(v -> {
            String currentUserEmail = (String) getIntent().getSerializableExtra("currentUser");
            Log.d("Current User Email!", currentUserEmail + "!");
            Intent intent = new Intent(SearcherDashboard.this, SearcherPreferredListDashboard.class);
            intent.putExtra("currentUserEmail",  currentUserEmail);
            startActivity(intent);
        });
    }
}
