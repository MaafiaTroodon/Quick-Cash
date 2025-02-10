package com.example.quickcash.ui;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickcash.R;
import com.example.quickcash.core.Users;
import com.example.quickcash.database.Firebase;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private Users userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FirebaseApp.initializeApp(this);
        Firebase firebaseInstance = new Firebase();
        userDatabase = new Users(firebaseInstance);

        EditText usernameEditText = findViewById(R.id.userName);
        EditText emailEditText = findViewById(R.id.Email);
        EditText passwordEditText = findViewById(R.id.Password);
        Button confirmButton = findViewById(R.id.buttonCreateAccount);

        confirmButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            testCheckUsernameAndCreateUser(username, email, password);
        });
    }

    private void testCheckUsernameAndCreateUser(String username, String email, String password) {
        userDatabase.checkIfEmailExists(email, new Users.UsernameCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResult(boolean exists) {
                if (exists) {
                    Toast.makeText(MainActivity.this, "Email already exists!", Toast.LENGTH_LONG).show();
                    Log.d("TestActivity", "Email already exists.");
                } else {
                    userDatabase.createUser(username, password, email, new Users.UserCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(MainActivity.this, "User created successfully!", Toast.LENGTH_LONG).show();
                            Log.d("TestActivity", "User created: " + message);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MainActivity.this, "Error creating user: " + error, Toast.LENGTH_LONG).show();
                            Log.e("TestActivity", "Error: " + error);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error checking email: " + error, Toast.LENGTH_LONG).show();
                Log.e("TestActivity", "Error checking email: " + error);
            }
        });
    }
}
