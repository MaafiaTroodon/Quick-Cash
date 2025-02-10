package com.example.quickcash.ui;
import com.example.quickcash.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.database.Firebase;
import com.example.quickcash.core.Users;


public class CreateAccount extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Firebase firebaseInstance = new Firebase();
        Users userDatabase = new Users(firebaseInstance);
    }
}
