package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.util.PrefAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearcherPreferredListDashboard extends AppCompatActivity implements PrefAdapter.ButtonClickListener {

    private FirebaseAuth auth;
    private Users users;
    private DatabaseReference usersRef;
    private RecyclerView recyclerView;
    private Button backButton;
    public PrefAdapter prefAdapter;
    private List<JobModel> prefList;
    public String currentUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searcher_preferred_list_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false); // Ensures RecyclerView measures dynamically
        recyclerView.setNestedScrollingEnabled(true); // Enables scrolling
        backButton = findViewById(R.id.Back);

        prefList = new ArrayList<>();
        prefAdapter = new PrefAdapter(prefList);
        recyclerView.setAdapter(prefAdapter);
        prefAdapter.setItemClickListener(this);

        Firebase firebase = new Firebase();
        users = new Users(firebase);

        currentUserEmail = (String) getIntent().getSerializableExtra("currentUserEmail");
        Log.d("Current User Email!", currentUserEmail + "!");


        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String sanitizedEmail = currentUserEmail.replace(".", ",");

        usersRef.child(sanitizedEmail).child("preferredJob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    prefList.add(job);
                }
                prefAdapter.updateJobs(prefList);
                Log.d("JobList", "Total jobs fetched: " + prefList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read jobs", error.toException());
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearcherPreferredListDashboard.this, SearcherDashboard.class);
            intent.putExtra("currentUser", currentUserEmail);
            startActivity(intent);
        });
    }

    /*Remove selected job to Preferred list when unprefer button is clicked*/
    @Override
    public void onItemClick(View view, int position) {
        JobModel selectedItem = prefAdapter.getItem(position);
//        Toast.makeText(this, selectedItem.getCompany(), Toast.LENGTH_SHORT).show();
        removeToPreferredList(selectedItem);
    }

    protected void removeToPreferredList(JobModel selectedItem) {
        if(prefList.contains(selectedItem)) {
            Toast.makeText(this, selectedItem.getTitle() + " Is removed to preferred list", Toast.LENGTH_SHORT).show();
            prefList.remove(selectedItem);

            users.setPreferredList(currentUserEmail, prefList, new Users.UserCallback() {
                @Override
                public void onSuccess(String message) {
                    recreate();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SearcherPreferredListDashboard.this, error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "This job is not in the list", Toast.LENGTH_SHORT).show();
        }
    }
}