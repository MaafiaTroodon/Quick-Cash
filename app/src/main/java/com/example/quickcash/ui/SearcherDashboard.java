package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.model.PreferEmployerModel;
import com.example.quickcash.util.JobAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearcherDashboard extends AppCompatActivity implements JobAdapter.ButtonClickListener {
    public static final int FILTER_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<JobModel> jobList;
    public List<PreferEmployerModel> preferredJob;
    private List<JobModel> fullJobList; // Stores all jobs for restoring after search
    private String currentUserEmail;
    private DatabaseReference jobsRef;
    private DatabaseReference usersRef;
    private EditText searchInput;
    private Button searchButton;
    private Button filterButton;
    private TextView tvFilteredResults; // New TextView for showing applied filters
    private TextView locationText; // Added from US1-Location
    private Users users;
    private Button logoutButton, preferredJobButton;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        filterButton = findViewById(R.id.filterButton);
        tvFilteredResults = findViewById(R.id.tvFilteredResults);
        locationText = findViewById(R.id.locationText); // Added from US1-Location

        jobList = new ArrayList<>();
        preferredJob = new ArrayList<>();
        fullJobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");

        loadAllJobs(); // Load all jobs initially

        logoutButton = findViewById(R.id.LogOut);
        preferredJobButton = findViewById(R.id.PreferredList);
        auth = FirebaseAuth.getInstance();

        currentUserEmail = (String) getIntent().getSerializableExtra("currentUser");

        Firebase firebase = new Firebase();
        users = new Users(firebase);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String sanitizedEmail = currentUserEmail.replace(".", ",");
        usersRef.child(sanitizedEmail).child("preferredJob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    PreferEmployerModel job = jobSnapshot.getValue(PreferEmployerModel.class);
                    preferredJob.add(job);
                }
                Log.d("JobList", "Total jobs fetched: " + preferredJob.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read jobs", error.toException());
            }
        });

        // Retrieve location data from Intent
        double latitude = getIntent().getDoubleExtra("latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Convert coordinates to address
        String address = getAddressFromCoordinates(latitude, longitude);

        // Display address
        locationText.setText("Your Location: " + address);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                filterJobs(query);
                clearSearchButton.setVisibility(View.VISIBLE);
            }
        });

        clearSearchButton.setOnClickListener(v -> {
            restoreFullList();
            clearSearchButton.setVisibility(View.GONE); // Hide "Clear Search"
        });


        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearcherDashboard.this, FilterPage.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(SearcherDashboard.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SearcherDashboard.this, LoginActivity.class));
            finish();
        });

        preferredJobButton.setOnClickListener(v -> {
            PreferEmployerModel isTempIn = new PreferEmployerModel();
            if (preferredJob.size() >= 2 && preferredJob.contains(isTempIn)) {
                preferredJob.remove(isTempIn);
            }
            Log.d("Current User Email!", currentUserEmail + "!");
            users.setPreferredList(currentUserEmail, preferredJob, new Users.UserCallback() {
                @Override
                public void onSuccess(String message) {
                    Intent intent = new Intent(SearcherDashboard.this, SearcherPreferredListDashboard.class);
                    intent.putExtra("currentUserEmail", currentUserEmail);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SearcherDashboard.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address";
    }
    public List<JobModel> getFilteredJobs() {
        return jobList;
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
                jobAdapter.updateJobs(jobList); // Update the adapter with all jobs
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
        jobAdapter.updateJobs(filteredJobs); // Update the adapter with filtered jobs
    }

    private void restoreFullList() {
        jobAdapter.updateJobs(fullJobList); // Restore original list
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String jobTitle = data.getStringExtra("jobTitle");
            String minSalary = data.getStringExtra("minSalary");
            String maxSalary = data.getStringExtra("maxSalary");
            String company = data.getStringExtra("company");
            String location = data.getStringExtra("location");

            // Display applied filters in TextView
            String filterSummary = "Filters Applied:\n"
                    + "Job Title: " + jobTitle + "\n"
                    + "Salary: " + minSalary + " - " + maxSalary + "\n"
                    + "Company: " + company + "\n"
                    + "Location: " + location;

            tvFilteredResults.setText(filterSummary);

            applyFilters(jobTitle, minSalary, maxSalary, company, location);
        }
    }

    private void applyFilters(String jobTitle, String minSalary, String maxSalary, String company, String location) {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JobModel> filteredJobs = new ArrayList<>();

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    if (job == null) continue;

                    boolean matches = true;

                    if (!jobTitle.isEmpty() && !job.getTitle().toLowerCase().contains(jobTitle.toLowerCase())) {
                        matches = false;
                    }

                    if (!company.isEmpty() && !job.getCompany().toLowerCase().contains(company.toLowerCase())) {
                        matches = false;
                    }

                    if (!location.isEmpty() && !job.getLocation().toLowerCase().contains(location.toLowerCase())) {
                        matches = false;
                    }

                    if (!minSalary.isEmpty() || !maxSalary.isEmpty()) {
                        try {
                            double jobSalary = Double.parseDouble(job.getSalaryText());
                            double min = minSalary.isEmpty() ? 0 : Double.parseDouble(minSalary);
                            double max = maxSalary.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxSalary);

                            if (jobSalary < min || jobSalary > max) {
                                matches = false;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("SalaryFilter", "Invalid salary format", e);
                        }
                    }

                    if (matches) {
                        filteredJobs.add(job);
                    }
                }

                jobAdapter.updateJobs(filteredJobs); // Update adapter with filtered jobs
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch filtered jobs", error.toException());
            }
        });
    }

    /*Add selected job to Preferred list when Add to preferred list button is clicked*/
    @Override
    public void onItemClick(View view, int position) {
        JobModel selectedItem = jobAdapter.getItem(position);
        LocalDate now = LocalDate.now();
        String addedTime = now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "-" + now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        PreferEmployerModel preferEmployerModel = new PreferEmployerModel(selectedItem, addedTime);
    //    Toast.makeText(this, selectedItem.getCompany(), Toast.LENGTH_SHORT).show();
        addToPreferredList(preferEmployerModel);
    }

    @Override
    public void onDescriptionClick(View view, int position) {
        JobModel selectedItem = jobAdapter.getItem(position);
        LocalDate now = LocalDate.now();
        String addedTime = now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "-" + now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        PreferEmployerModel preferEmployerModel = new PreferEmployerModel(selectedItem, addedTime);
    //    Toast.makeText(this, selectedItem.getCompany(), Toast.LENGTH_SHORT).show();
        addToPreferredList(preferEmployerModel);

    }

    protected void addToPreferredList(PreferEmployerModel preferEmployerModel) {
        if(!preferredJob.contains(preferEmployerModel)) {
            preferredJob.add(preferEmployerModel);
            Toast.makeText(this, preferEmployerModel.getCompany() + " Is added to preferred list", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This job is already in the list", Toast.LENGTH_SHORT).show();
        }
    }
}
