package com.example.trackademic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegUsername;
    private EditText editTextRegPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextMajor;
    private EditText editTextGraduationYear;
    private Button buttonRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        editTextRegUsername = findViewById(R.id.editTextRegUsername);
        editTextRegPassword = findViewById(R.id.editTextRegPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMajor = findViewById(R.id.editTextMajor);
        editTextGraduationYear = findViewById(R.id.editTextGraduationYear);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextRegUsername.getText().toString().trim();
                String password = editTextRegPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String major = editTextMajor.getText().toString().trim();
                String gradYearStr = editTextGraduationYear.getText().toString().trim();

                if (validateInput(username, password, confirmPassword, fullName, email, major, gradYearStr)) {
                    if (databaseHelper.isUserExists(username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int graduationYear = Integer.parseInt(gradYearStr);

                    // First create the user
                    long userId = databaseHelper.addUser(username, password);
                    if (userId != -1) {
                        // Then create their profile
                        if (databaseHelper.addProfile(userId, fullName, email, major, graduationYear)) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to create profile", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateInput(String username, String password, String confirmPassword,
                                  String fullName, String email, String major, String gradYearStr) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                fullName.isEmpty() || email.isEmpty() || major.isEmpty() || gradYearStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int graduationYear = Integer.parseInt(gradYearStr);
            if (graduationYear < 2024 || graduationYear > 2100) {
                Toast.makeText(this, "Please enter a valid graduation year", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid graduation year", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}