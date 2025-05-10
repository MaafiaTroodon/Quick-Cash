package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.PreferEmployerModel;
import com.example.quickcash.model.PreferEmployeeModel;
import com.example.quickcash.model.UserModel;
import com.example.quickcash.util.EmployeeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreatorEmployeeList extends AppCompatActivity implements EmployeeAdapter.ButtonClickListener {

    private FirebaseAuth auth;
    private Users users;
    private DatabaseReference usersRef;
    private RecyclerView recyclerView;
    private Button backButton;
    public EmployeeAdapter employeeAdapter;
    private List<UserModel> employeeList;
    public String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_employee_list);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(true);
        backButton = findViewById(R.id.Back);

        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);
        employeeAdapter.setItemClickListener(this);

        Firebase firebase = new Firebase();
        users = new Users(firebase);

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String sanitizedEmail = currentUserEmail.replace(".", ",");

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();
                List<UserModel> allUsers = new ArrayList<>();

                // Collect all user data first
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String keyEmail = userSnapshot.getKey();
                    if (keyEmail != null) {
                        String userEmail = keyEmail.replace(",", ".");
                        UserModel user = userSnapshot.getValue(UserModel.class);
                        if (user != null) {
                            user.setEmail(userEmail);
                            allUsers.add(user);
                        }
                    }
                }

                List<PreferEmployeeModel> preferredEmployees = new ArrayList<>();
                for (UserModel u : allUsers) {
                    if (u.getEmail().equals(currentUserEmail)) {
                        preferredEmployees = u.getPreferredEmployees();
                        break;
                    }
                }

                for (UserModel user : allUsers) {
                    if ("Searcher".equals(user.getRole())) {
                        int score = 0;
                        for (PreferEmployerModel job : user.getPreferredJob()) {
                            if (currentUserEmail.equals(job.getEmployerEmail())) {
                                score += 2; // Prior work history
                            }
                        }
                        for (PreferEmployeeModel emp : preferredEmployees) {
                            if (emp.getEmployeeEmail().equals(user.getEmail())) {
                                score += 1; // Employee marked this employer preferred
                            }
                        }
                        user.setPassword(String.valueOf(score)); // Store score temporarily
                        employeeList.add(user);
                    }
                }

                // Sort employees by score descending (rating placeholder is 0)
                Collections.sort(employeeList, (u1, u2) -> {
                    int score1 = Integer.parseInt(u1.getPassword());
                    int score2 = Integer.parseInt(u2.getPassword());
                    return Integer.compare(score2, score1);
                });

                employeeAdapter.updateEmployees(employeeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read employees", error.toException());
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreatorEmployeeList.this, CreatorDashboard.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        UserModel selectedEmployee = employeeAdapter.getItem(position);
        addToPreferredEmployees(selectedEmployee);
    }

    protected void addToPreferredEmployees(UserModel selectedEmployee) {
        String sanitizedEmail = currentUserEmail.replace(".", ",");
        usersRef.child(sanitizedEmail).child("preferredEmployees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PreferEmployeeModel> preferredEmployees = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                        PreferEmployeeModel employee = employeeSnapshot.getValue(PreferEmployeeModel.class);
                        preferredEmployees.add(employee);
                    }
                }

                PreferEmployeeModel newPreferredEmployee = new PreferEmployeeModel(
                        selectedEmployee.getEmail(),
                        selectedEmployee.getUsername(),
                        String.valueOf(System.currentTimeMillis())
                );

                if (!preferredEmployees.contains(newPreferredEmployee)) {
                    preferredEmployees.add(newPreferredEmployee);
                    usersRef.child(sanitizedEmail).child("preferredEmployees").setValue(preferredEmployees);
                } else {
                    Toast.makeText(CreatorEmployeeList.this, "Employee already in preferred list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read preferred employees", error.toException());
            }
        });
    }
}
