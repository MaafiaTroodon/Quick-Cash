package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.model.JobModel;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView jobTitle, jobCompany, jobLocation, jobType, jobSalary, jobDescription;

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
            }
        }
    }
}
