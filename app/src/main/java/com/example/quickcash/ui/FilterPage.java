package com.example.quickcash.ui;

import android.content.Intent;
import com.example.quickcash.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class FilterPage extends AppCompatActivity {

    private EditText etJobTitle, etMinSalary, etMaxSalary, etCompany, etLocation;
    private Button btnApplyFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        etJobTitle = findViewById(R.id.etJobTitle);
        etMinSalary = findViewById(R.id.etMinSalary);
        etMaxSalary = findViewById(R.id.etMaxSalary);
        etCompany = findViewById(R.id.etCompany);
        etLocation = findViewById(R.id.etLocation);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);

        btnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String jobTitle = etJobTitle.getText().toString();
                String minSalary = etMinSalary.getText().toString();
                String maxSalary = etMaxSalary.getText().toString();
                String company = etCompany.getText().toString();
                String location = etLocation.getText().toString();

                // Pass data back to SearcherDashboard
                Intent intent = new Intent();
                intent.putExtra("jobTitle", jobTitle);
                intent.putExtra("minSalary", minSalary);
                intent.putExtra("maxSalary", maxSalary);
                intent.putExtra("company", company);
                intent.putExtra("location", location);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
