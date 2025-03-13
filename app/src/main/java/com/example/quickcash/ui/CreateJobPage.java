package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quickcash.R;
import com.example.quickcash.core.Jobs;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.validator.JobValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class CreateJobPage extends AppCompatActivity {

    private EditText jobTitle, jobDescription, jobLocation, salary, companyName;
    private Spinner jobType;
    private Button submitButton;
    private Jobs jobs;
    private JobValidator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jobposting);

        jobTitle = findViewById(R.id.jobTitle);
        jobDescription = findViewById(R.id.jobDescription);
        jobType = findViewById(R.id.jobType);
        salary = findViewById(R.id.salary);
        jobLocation = findViewById(R.id.jobLocation);
        companyName = findViewById(R.id.companyName);
        submitButton = findViewById(R.id.submitButton);

        jobs = new Jobs(new Firebase());
        validator = new JobValidator();

        submitButton.setOnClickListener(v -> submitJob());
    }

    private void submitJob() {
        String title = jobTitle.getText().toString().trim();
        String description = jobDescription.getText().toString().trim();
        String location = jobLocation.getText().toString().trim();
        String type = jobType.getSelectedItem().toString();
        String salaryText = salary.getText().toString().trim();
        String company = companyName.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You need to log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = user.getEmail();

        // ✅ Validation: Stay on the same page if validation fails
        if (!validator.isValidTitle(title)) {
            jobTitle.setError("Job title is required");
            jobTitle.requestFocus();
            return;
        }
        if (!validator.isValidDescription(description)) {
            jobDescription.setError("Job description must be at least 150 characters");
            jobDescription.requestFocus();
            return;
        }
        if (!validator.isValidLocation(location)) {
            jobLocation.setError("Job location is required");
            jobLocation.requestFocus();
            return;
        }
        if (!validator.isValidType(type)) {
            Toast.makeText(this, "Please select a valid job type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validator.isValidSalary(salaryText)) {
            salary.setError("Invalid salary format");
            salary.requestFocus();
            return;
        }
        if (!validator.isValidCompany(company)) {
            companyName.setError("Company name is required");
            companyName.requestFocus();
            return;
        }

        // Create Job and Navigate on Success
        JobModel jobModel = new JobModel(title, description, location, type, salaryText, company, userEmail);

        jobs.createJob(jobModel, new Jobs.JobCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(CreateJobPage.this, message, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateJobPage.this, CreatorDashboard.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateJobPage.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
