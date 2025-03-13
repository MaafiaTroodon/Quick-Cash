package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.model.JobModel;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView jobTitle, jobCompany, jobLocation, jobType, jobSalary, jobDescription;
    private Button backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        jobTitle = findViewById(R.id.jobTitle);
        jobCompany = findViewById(R.id.jobCompany);
        jobLocation = findViewById(R.id.jobLocation);
        jobType = findViewById(R.id.jobType);
        jobSalary = findViewById(R.id.jobSalary);
        jobDescription = findViewById(R.id.jobDescription);
        backButton = findViewById(R.id.backButton);

        // Back button logic
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // ✅ This will close JobDetailsActivity and return to the previous screen
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("job")) {
            JobModel job = intent.getParcelableExtra("job");

            if (job != null) {
                jobTitle.setText(job.getTitle());
                jobCompany.setText(job.getCompany());
                jobLocation.setText(job.getLocation());
                jobType.setText(job.getType());
                jobSalary.setText("$" + job.getSalaryText());
                jobDescription.setText(job.getDescription());
            } else {
                jobTitle.setText("Error: Job Not Found");
            }
        } else {
            jobTitle.setText("Error: No Job Data");
        }
    }
}
