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

import com.example.quickcash.core.Users;
import com.example.quickcash.model.PreferEmployeeModel;
import com.example.quickcash.model.UserModel;
import com.example.quickcash.ui.CreatorDashboard;
import com.example.quickcash.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
            // User is not logged in, redirect to login screen
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
                Log.d("FirebaseData", "Snapshot: " + snapshot.toString()); // Log the entire snapshot

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String keyEmail = userSnapshot.getKey();
                    if (keyEmail != null) {
                        String userEmail = keyEmail.replace(",", ".");
                        UserModel user = userSnapshot.getValue(UserModel.class);

                        if (user != null) {
                            user.setEmail(userEmail); // Set correct email
                            Log.d("FirebaseData", "User: " + user.getUsername() + ", Role: " + user.getRole()); // Log each user

                            if ("Searcher".equals(user.getRole())) {
                                employeeList.add(user);
                                Log.d("FirebaseData", "Added Searcher: " + user.getUsername()); // Log added searchers
                            }
                        }
                    }
                }
                employeeAdapter.updateEmployees(employeeList);
                Log.d("EmployeeList", "Total employees fetched: " + employeeList.size());
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
                        Log.d("PreferredEmployees", "Existing Preferred Employee: " + employee.getEmployeeName());
                    }
                }

                PreferEmployeeModel newPreferredEmployee = new PreferEmployeeModel(
                        selectedEmployee.getEmail(),
                        selectedEmployee.getUsername(),
                        String.valueOf(System.currentTimeMillis())
                );

                if (!preferredEmployees.contains(newPreferredEmployee)) {
                    preferredEmployees.add(newPreferredEmployee);
                    usersRef.child(sanitizedEmail).child("preferredEmployees").setValue(preferredEmployees)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PreferredEmployees", "Added to preferred employees: " + newPreferredEmployee.getEmployeeName());
                                    Toast.makeText(CreatorEmployeeList.this, "Added to preferred employees", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("PreferredEmployees", "Failed to add to preferred employees");
                                    Toast.makeText(CreatorEmployeeList.this, "Failed to add to preferred employees", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Log.d("PreferredEmployees", "Employee already in preferred list: " + newPreferredEmployee.getEmployeeName());
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