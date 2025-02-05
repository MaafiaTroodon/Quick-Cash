package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Users userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        textView = findViewById(R.id.testView);
//
//        Firebase firebaseInstance = new Firebase();
//        userDatabase = new Users(firebaseInstance);
//
//        testCheckUsernameAndCreateUser();
    }

//    private void testCheckUsernameAndCreateUser() {
//        String testPassword = "Test2@123";
//        String testEmail = "testuser2@gmail.com";
//
//        userDatabase.checkIfEmailExists(testEmail, new Users.UsernameCallback() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResult(boolean exists) {
//                if (exists) {
//                    textView.setText("Error: Username already exists!");
//                    Toast.makeText(MainActivity.this, "Username is already taken!", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    userDatabase.createUser(testPassword, testEmail, new Users.UserCallback() {
//                        @Override
//                        public void onSuccess(String message) {
//                            textView.setText(message);
//                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onError(String error) {
//                            textView.setText("Error: " + error);
//                            Toast.makeText(MainActivity.this, "Signup failed: " + error, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onError(String error) {
//                textView.setText("Error: " + error);
//                Toast.makeText(MainActivity.this, "Error checking username: " + error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
}