package com.example.quickcash;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.ui.JobDetailsActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class JobDetailsActivityTest {
    @Rule
    public ActivityTestRule<JobDetailsActivity> activityRule = new ActivityTestRule<>(JobDetailsActivity.class, true, false);

    @Test
    public void testJobDetailsDisplayedCorrectly() {
        JobModel job = new JobModel("Title", "Description", "Location", "Type", "1000", "Company", "email@example.com");
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JobDetailsActivity.class);
        intent.putExtra("job", job);
        activityRule.launchActivity(intent);

        JobDetailsActivity activity = activityRule.getActivity();

        TextView jobTitle = activity.findViewById(R.id.jobTitle);
        TextView jobCompany = activity.findViewById(R.id.jobCompany);
        TextView jobLocation = activity.findViewById(R.id.jobLocation);
        TextView jobType = activity.findViewById(R.id.jobType);
        TextView jobSalary = activity.findViewById(R.id.jobSalary);
        TextView jobDescription = activity.findViewById(R.id.jobDescription);

        assertEquals("Title", jobTitle.getText().toString());
        assertEquals("Company", jobCompany.getText().toString());
        assertEquals("Location", jobLocation.getText().toString());
        assertEquals("Type", jobType.getText().toString());
        assertEquals("$1000", jobSalary.getText().toString());
        assertEquals("Description", jobDescription.getText().toString());
    }

    @Test
    public void testJobDetailsErrorHandling() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JobDetailsActivity.class);
        activityRule.launchActivity(intent);

        JobDetailsActivity activity = activityRule.getActivity();

        TextView jobTitle = activity.findViewById(R.id.jobTitle);

        assertEquals("Error: No Job Data", jobTitle.getText().toString());
    }

    @Test
    public void testNullJobDataHandling() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JobDetailsActivity.class);
        intent.putExtra("job", (JobModel) null);
        activityRule.launchActivity(intent);

        JobDetailsActivity activity = activityRule.getActivity();

        TextView jobTitle = activity.findViewById(R.id.jobTitle);

        assertEquals("Error: Job Not Found", jobTitle.getText().toString());
    }

    @Test
    public void testPartialJobDataHandling() {
        JobModel job = new JobModel("Title", null, "Location", null, "1000", "Company", null);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JobDetailsActivity.class);
        intent.putExtra("job", job);
        activityRule.launchActivity(intent);

        JobDetailsActivity activity = activityRule.getActivity();

        TextView jobTitle = activity.findViewById(R.id.jobTitle);
        TextView jobCompany = activity.findViewById(R.id.jobCompany);
        TextView jobLocation = activity.findViewById(R.id.jobLocation);
        TextView jobType = activity.findViewById(R.id.jobType);
        TextView jobSalary = activity.findViewById(R.id.jobSalary);
        TextView jobDescription = activity.findViewById(R.id.jobDescription);

        assertEquals("Title", jobTitle.getText().toString());
        assertEquals("Company", jobCompany.getText().toString());
        assertEquals("Location", jobLocation.getText().toString());
        assertEquals("", jobType.getText().toString()); // Expect empty string for null type
        assertEquals("$1000", jobSalary.getText().toString());
        assertEquals("", jobDescription.getText().toString()); // Expect empty string for null description
    }

    @Test
    public void testBackButtonFunctionality() {
        JobModel job = new JobModel("Title", "Description", "Location", "Type", "1000", "Company", "email@example.com");
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JobDetailsActivity.class);
        intent.putExtra("job", job);
        activityRule.launchActivity(intent);

        JobDetailsActivity activity = activityRule.getActivity();

        activity.runOnUiThread(() -> {
            Button backButton = activity.findViewById(R.id.backButton);
            backButton.performClick();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(activity.isFinishing());


    }
}
