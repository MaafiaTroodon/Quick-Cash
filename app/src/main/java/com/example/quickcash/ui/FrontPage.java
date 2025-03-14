package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;

public class FrontPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(FrontPage.this, CreateAccount.class));
            finish();
        }, 3000);
    }
}
