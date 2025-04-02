package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;

import java.util.List;

public class EmployeeListPage extends AppCompatActivity {

    private LinearLayout employeeListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        employeeListContainer = findViewById(R.id.employeeListContainer);

        Button backToDashboardButton = findViewById(R.id.backToDashboardButton);
        backToDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListPage.this, CreatorDashboard.class);
            startActivity(intent);
            finish();
        });



        Firebase firebase = new Firebase();
        Users users = new Users(firebase);

        users.getApplicantsForMyJobs(firebase, new Users.ApplicantsCallback() {
            @Override
            public void onSuccess(List<String> applicantEmails) {
                if (applicantEmails.isEmpty()) {
                    showMessage("No applicants found.");
                    return;
                }

                for (String email : applicantEmails) {
                    addApplicantToLayout(email);
                }
            }

            @Override
            public void onError(String error) {
                showMessage("Error: " + error);
            }
        });
    }

    private void addApplicantToLayout(String email) {
        // TextView for email
        TextView emailText = new TextView(this);
        emailText.setText(email);
        emailText.setTextSize(18);
        emailText.setPadding(0, 16, 0, 8);

        // Pay button
        Button payButton = new Button(this);
        payButton.setText("Pay Employee");
        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListPage.this, Paypal.class);
            intent.putExtra("employeeEmail", email); // Optional: pass employee email
            startActivity(intent);
        });

        // Add both views to layout
        employeeListContainer.addView(emailText);
        employeeListContainer.addView(payButton);
    }


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
