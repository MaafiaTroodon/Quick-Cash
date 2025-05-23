package com.example.quickcash.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.manager.JobDataManager;
import com.example.quickcash.manager.JobFilterManager;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.model.PreferEmployerModel;
import com.example.quickcash.util.JobAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SearcherDashboard extends AppCompatActivity implements JobAdapter.ButtonClickListener {
    public static final int FILTER_REQUEST_CODE = 1;
    private static final String CREDENTIALS_FILE_PATH = "key.json";
    private static final String PUSH_NOTIFICATION_ENDPOINT ="https://fcm.googleapis.com/v1/projects/quickcash-34895/messages:send";
    private RequestQueue requestQueue;
    private String token;
    private Set<String> initialJobKeys = new HashSet<>();
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<JobModel> jobList;
    public List<PreferEmployerModel> preferredJob;
    public List<String> prefemployer;
    private List<JobModel> fullJobList; // Stores all jobs for restoring after search
    private String currentUserEmail;
    private DatabaseReference jobsRef;
    private ChildEventListener jobsListener;
    private DatabaseReference usersRef;
    private ChildEventListener usersListener;
    private EditText searchInput;
    private Button searchButton;
    private Button filterButton;
    private TextView tvFilteredResults; // New TextView for showing applied filters
    private TextView locationText; // Added from US1-Location
    private Users users;
    private Button logoutButton, preferredJobButton, clearSearchButton;
    private FirebaseAuth auth;
    private JobFilterManager jobFilterManager;
    private JobDataManager jobDataManager;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcherdashboard);

        getFCMToken();
        jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    initialJobKeys.add(child.getKey());
                }
                Log.d("Firebase", "Initial keys loaded: " + initialJobKeys.size());

                addNewJobListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load initial keys", error.toException());
            }
        });

        currentUserEmail = (String) getIntent().getSerializableExtra("currentUser");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String sanitizedEmail = currentUserEmail.replace(".", ",");

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.d("User email!!", child.getKey());
                    if(child.getKey().equals(sanitizedEmail)) {
                        GenericTypeIndicator<List<PreferEmployerModel>> t = new GenericTypeIndicator<List<PreferEmployerModel>>() {};
                        preferredJob = child.child("preferredJob").getValue(t);

                        if (preferredJob != null) {
                            Log.d("Firebase", "Preferred jobs retrieved: " + preferredJob.size());
                            getPrefemployer();
                        } else {
                            Log.d("Firebase", "Preferred jobs is null");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("NewJobs")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("Subscribed msg!!!", msg);
                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI
        recyclerView = findViewById(R.id.recyclerView);
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        filterButton = findViewById(R.id.filterButton);
        tvFilteredResults = findViewById(R.id.tvFilteredResults);
        logoutButton = findViewById(R.id.LogOut);
        locationText = findViewById(R.id.locationText);

        // Initialize managers
        jobFilterManager = new JobFilterManager();
        jobDataManager = new JobDataManager();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        preferredJob = new ArrayList<>();
        prefemployer = new ArrayList<>();
        fullJobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList, currentUserEmail);
        recyclerView.setAdapter(jobAdapter);

        // Load jobs
        loadAllJobs();

        preferredJobButton = findViewById(R.id.PreferredList);
        auth = FirebaseAuth.getInstance();

        jobAdapter.setItemClickListener(this);

        Firebase firebase = new Firebase();
        users = new Users(firebase);

        checkLocationPermissions();

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (query.isEmpty()) {
                restoreFullList();
            } else {
                filterJobs(query);
            }
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
        jobAdapter.setItemClickListener(this);
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                        String address = getAddressFromCoordinates(latitude, longitude);
                        locationText.setText("Your Location: " + address);
                        fusedLocationClient.removeLocationUpdates(this); // Stop updates after getting the location
                        break;
                    }
                }
            }
        }, null);
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Build the address string
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i)).append("\n");
                }

                // Log the address
                Log.d("Geocoder", "Address fetched: " + addressBuilder.toString());

                return addressBuilder.toString().trim(); // Return the full address
            } else {
                Log.d("Geocoder", "No address found for coordinates: " + latitude + ", " + longitude);
            }
        } catch (IOException e) {
            Log.e("Geocoder", "Error fetching address using Geocoder", e);

            // Fallback to Google Maps Geocoding API
            return getAddressFromGoogleMapsAPI(latitude, longitude);
        }
        return "Unable to fetch address";
    }

    private String getAddressFromGoogleMapsAPI(double latitude, double longitude) {
        String apiKey = "AIzaSyBPfKk4y2T1T_tN1-QwB0dNKOQMtUHiaTM";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + apiKey;

        try {
            String json = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
            JSONObject response = new JSONObject(json);
            if (response.getString("status").equals("OK")) {
                return response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            }
        } catch (Exception e) {
            Log.e("Geocoder", "Error fetching address from Google Maps API", e);
        }
        return "Unable to fetch address";
    }

    public List<JobModel> getFilteredJobs() {
        return jobList;
    }

    private void loadAllJobs() {
        jobDataManager.loadAllJobs(new JobDataManager.JobDataCallback() {
            @Override
            public void onJobsLoaded(List<JobModel> jobs, List<String> jobIds) {  // Updated to include jobIds
                jobList.clear();
                fullJobList.clear();
                jobList.addAll(jobs);
                fullJobList.addAll(jobs);
                jobAdapter.updateJobs(jobList, jobIds);  // Updated to include jobIds
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SearcherDashboard.this, "Error loading jobs: " + error, Toast.LENGTH_SHORT).show();
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
        // Since we don't have IDs for filtered jobs, we'll need to find them
        List<String> filteredJobIds = new ArrayList<>();
        for (JobModel job : filteredJobs) {
            int index = fullJobList.indexOf(job);
            if (index >= 0 && index < jobAdapter.jobIds.size()) {
                filteredJobIds.add(jobAdapter.jobIds.get(index));
            }
        }
        jobAdapter.updateJobs(filteredJobs, filteredJobIds);
    }

    private void searchJobs() {
        String query = searchInput.getText().toString().trim();
        if (!query.isEmpty()) {
            List<JobModel> filteredJobs = jobFilterManager.filterJobs(fullJobList, query);
            // Get the corresponding job IDs for the filtered jobs
            List<String> filteredJobIds = getJobIdsForJobs(filteredJobs);
            jobAdapter.updateJobs(filteredJobs, filteredJobIds);
            clearSearchButton.setVisibility(View.VISIBLE);
        }
    }
    private List<String> getJobIdsForJobs(List<JobModel> jobs) {
        List<String> ids = new ArrayList<>();
        for (JobModel job : jobs) {
            int index = fullJobList.indexOf(job);
            if (index >= 0 && index < jobAdapter.jobIds.size()) {
                ids.add(jobAdapter.jobIds.get(index));
            }
        }
        return ids;
    }

    private void restoreFullList() {
        jobAdapter.updateJobs(fullJobList, jobAdapter.jobIds);
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
            List<String> filteredJobIds = new ArrayList<>();
            for (JobModel job : filteredJobs) {
                int index = fullJobList.indexOf(job);
                if (index >= 0 && index < jobAdapter.jobIds.size()) {
                    filteredJobIds.add(jobAdapter.jobIds.get(index));
                }
            }
            jobAdapter.updateJobs(filteredJobs, filteredJobIds);
        }
    }

    private void applyFilters(String jobTitle, String minSalary, String maxSalary, String company, String location) {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JobModel> filteredJobs = new ArrayList<>();
                List<String> filteredJobIds = new ArrayList<>();

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
                        filteredJobIds.add(jobSnapshot.getKey());
                    }
                }

                jobAdapter.updateJobs(filteredJobs, filteredJobIds);
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
        addToPreferredList(preferEmployerModel);
    }

    @Override
    public void onApplyClick(View view, int position) {
        String jobId = jobAdapter.getJobId(position);
        if (jobId == null) {
            Toast.makeText(this, "Error: Job ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JobDataManager jobDataManager = new JobDataManager();
        jobDataManager.applyForJob(jobId, currentUserEmail, new JobDataManager.ApplyCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SearcherDashboard.this,
                        "Application submitted!", Toast.LENGTH_SHORT).show();
                // Update the job locally
                JobModel job = jobAdapter.getItem(position);
                job.addApplicant(currentUserEmail);
                jobAdapter.notifyItemChanged(position);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SearcherDashboard.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDescriptionClick(View view, int position) {
        JobModel selectedItem = jobAdapter.getItem(position);
        LocalDate now = LocalDate.now();
        String addedTime = now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "-" + now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        PreferEmployerModel preferEmployerModel = new PreferEmployerModel(selectedItem, addedTime);
        addToPreferredList(preferEmployerModel);
    }

    protected void addToPreferredList(PreferEmployerModel preferEmployerModel) {
        if (!preferredJob.contains(preferEmployerModel)) {
            preferredJob.add(preferEmployerModel);
            Toast.makeText(this, preferEmployerModel.getCompany() + " Is added to preferred list", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This job is already in the list", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewJobListener() {
        jobsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String employerEmail = snapshot.child("employerEmail").getValue(String.class);
                JobModel jobModel = snapshot.getValue(JobModel.class);

                if (!initialJobKeys.contains(key) && prefemployer.contains(employerEmail)) {
                    Log.d("New Job!!", "Job updated (new): " + key);
                    initialJobKeys.add(key);

                    new Thread(() -> {
                        String accessToken = getAccessToken();
                        if (accessToken == null) {
                            Log.e("FCM", "Access token is null");
                            return;
                        }

                        String deviceToken = token;

                        JSONObject jsonPayload = new JSONObject();
                        try {
                            JSONObject messageObj = new JSONObject();
                            messageObj.put("token", deviceToken);

                            JSONObject notificationObj = new JSONObject();
                            notificationObj.put("title", "Job Updated");
                            notificationObj.put("body", employerEmail + "has posted new job");
                            messageObj.put("notification", notificationObj);

                            // Data part: Including jobModel JSON
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("job", jobModel.toJson());
                            messageObj.put("data", dataObj);

                            jsonPayload.put("message", messageObj);
                        } catch (Exception e) {
                            Log.e("FCM", "JSON Exception", e);
                            return;
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                PUSH_NOTIFICATION_ENDPOINT,
                                jsonPayload,
                                response -> Log.d("FCM", "Message sent successfully: " + response.toString()),
                                error -> Log.e("FCM", "Error sending message", error)
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; UTF-8");
                                headers.put("Authorization", "Bearer " + accessToken);
                                return headers;
                            }
                        };

                        runOnUiThread(() -> requestQueue.add(jsonObjectRequest));
                    }).start();

                } else {
//                    Log.d("Ignored Job!!", "Ignored existing job: " + key);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "ChildEventListener cancelled", error.toException());
            }
        };

        jobsRef.addChildEventListener(jobsListener);
    }

    protected void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                token = task.getResult();
                Log.i("My token!!!", token);
            }
        });
    }

    private String getAccessToken() {
        try {
            InputStream stream = getAssets().open(CREDENTIALS_FILE_PATH);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("FCM", "Failed to get access token", e);
            return null;
        }
    }

    private void getPrefemployer() {
        if (preferredJob != null) {
            for (PreferEmployerModel job : preferredJob) {
                if(!prefemployer.contains(job.getEmployerEmail())) {
                    prefemployer.add(job.getEmployerEmail());
                }
            }
        }
    }

}
