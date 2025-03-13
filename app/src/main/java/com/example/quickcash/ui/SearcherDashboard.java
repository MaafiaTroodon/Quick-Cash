// SearcherDashboardActivity.java
package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.R;
import com.example.quickcash.manager.JobDataManager;
import com.example.quickcash.manager.JobFilterManager;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.util.JobAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class SearcherDashboard extends AppCompatActivity {

    public static final int FILTER_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<JobModel> jobList;
    private List<JobModel> fullJobList;

    private EditText searchInput;
    private Button searchButton, filterButton, clearSearchButton, logoutButton;
    private TextView tvFilteredResults;

    private JobFilterManager jobFilterManager;
    private JobDataManager jobDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        // Initialize UI
        recyclerView = findViewById(R.id.recyclerView);
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        filterButton = findViewById(R.id.filterButton);
        tvFilteredResults = findViewById(R.id.tvFilteredResults);
        logoutButton = findViewById(R.id.LogOut);

        // Initialize managers
        jobFilterManager = new JobFilterManager();
        jobDataManager = new JobDataManager();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        fullJobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        // Load jobs
        loadAllJobs();

        // Set click listeners
        searchButton.setOnClickListener(v -> searchJobs());
        clearSearchButton.setOnClickListener(v -> restoreFullList());
        filterButton.setOnClickListener(v -> openFilterPage());
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadAllJobs() {
        jobDataManager.loadAllJobs(new JobDataManager.JobDataCallback() {
            @Override
            public void onJobsLoaded(List<JobModel> jobs) {
                jobList.clear();
                fullJobList.clear();
                jobList.addAll(jobs);
                fullJobList.addAll(jobs);
                jobAdapter.updateJobs(jobList);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void searchJobs() {
        String query = searchInput.getText().toString().trim();
        if (!query.isEmpty()) {
            List<JobModel> filteredJobs = jobFilterManager.filterJobs(fullJobList, query);
            jobAdapter.updateJobs(filteredJobs);
            clearSearchButton.setVisibility(View.VISIBLE);
        }
    }

    private void restoreFullList() {
        jobAdapter.updateJobs(fullJobList);
        clearSearchButton.setVisibility(View.GONE);
    }

    private void openFilterPage() {
        Intent intent = new Intent(SearcherDashboard.this, FilterPage.class);
        startActivityForResult(intent, FILTER_REQUEST_CODE);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SearcherDashboard.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String jobTitle = data.getStringExtra("jobTitle");
            String minSalary = data.getStringExtra("minSalary");
            String maxSalary = data.getStringExtra("maxSalary");
            String company = data.getStringExtra("company");
            String location = data.getStringExtra("location");

            // Display applied filters
            String filterSummary = "Filters Applied:\n"
                    + "Job Title: " + jobTitle + "\n"
                    + "Salary: " + minSalary + " - " + maxSalary + "\n"
                    + "Company: " + company + "\n"
                    + "Location: " + location;

            tvFilteredResults.setText(filterSummary);

            // Apply filters
            List<JobModel> filteredJobs = jobFilterManager.applyFilters(fullJobList, jobTitle, minSalary, maxSalary, company, location);
            jobAdapter.updateJobs(filteredJobs);
        }
    }
}
