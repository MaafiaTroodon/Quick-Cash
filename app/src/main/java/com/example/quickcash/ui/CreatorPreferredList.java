package com.example.quickcash.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;

public class CreatorPreferredList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_preferred_list);

        // Create a TextView programmatically
        TextView textView = new TextView(this);
        textView.setText("Hello, World!");
        textView.setTextSize(24);
        textView.setPadding(50, 50, 50, 50);

        // Set the TextView as the content view
        setContentView(textView);
    }
}
