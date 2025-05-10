package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.core.Users.LoadPreferredListCallback;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.PreferEmployeeModel;
import com.example.quickcash.util.PrefEmployerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class CreatorPreferredList extends AppCompatActivity {

    private RecyclerView preferredListRecyclerView;
    private PrefEmployerAdapter adapter;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_preferred_list);

        // Set up the back button to route to CreatorDashboard
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorPreferredList.this, CreatorDashboard.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView and set a layout manager
        preferredListRecyclerView = findViewById(R.id.preferredListRecyclerView);
        preferredListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase and Users instance
        users = new Users(new Firebase());

        // Retrieve the currently logged in user using FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userEmail = currentUser.getEmail();

        // Load the user's preferred list
        users.loadPreferredList(userEmail, new LoadPreferredListCallback() {
            @Override
            public void onSuccess(List<PreferEmployeeModel> preferredList) {
                if (preferredList.isEmpty()) {
                    Toast.makeText(CreatorPreferredList.this, "No preferred employees found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Create the adapter with the list of employees and set it to the RecyclerView
                adapter = new PrefEmployerAdapter(preferredList);
                preferredListRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreatorPreferredList.this, "Error loading preferred list: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
