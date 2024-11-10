package com.example.trackademic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Initialize views
        bottomNav = findViewById(R.id.bottomNav);
        TextView studentNameText = findViewById(R.id.studentName);
        ImageButton menuButton = findViewById(R.id.menuButton);

        // Set student name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Student Name");
        studentNameText.setText(username);

        // Setup bottom navigation
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (shouldNavigate(item.getItemId())) {
                navigateToActivity(item.getItemId());
            }
            return true;
        });

        // Set selected nav item
        setSelectedNavItem();
    }

    private boolean shouldNavigate(int itemId) {
        // Don't navigate if we're already on the selected activity
        String currentClass = this.getClass().getSimpleName();

        if (itemId == R.id.nav_schedule && currentClass.equals("ScheduleActivity")) {
            return false;
        } else if (itemId == R.id.nav_todo && currentClass.equals("TodoActivity")) {
            return false;
        } else if (itemId == R.id.nav_finances && currentClass.equals("FinancesActivity")) {
            return false;
        } else if (itemId == R.id.nav_grades && currentClass.equals("GradesActivity")) {
            return false;
        } else if (itemId == R.id.nav_habits && currentClass.equals("HabitsActivity")) {
            return false;
        }
        return true;
    }

    private void navigateToActivity(int itemId) {
        Intent intent = null;

        if (itemId == R.id.nav_schedule) {
            intent = new Intent(this, ScheduleActivity.class);
        } else if (itemId == R.id.nav_todo) {
            intent = new Intent(this, TodoActivity.class);
        } else if (itemId == R.id.nav_finances) {
            intent = new Intent(this, FinancesActivity.class);
        } else if (itemId == R.id.nav_grades) {
            intent = new Intent(this, GradesActivity.class);
        } else if (itemId == R.id.nav_habits) {
            intent = new Intent(this, HabitsActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            finish(); // Close the current activity
        }
    }

    protected void setSelectedNavItem() {
        String currentClass = this.getClass().getSimpleName();

        if (currentClass.equals("ScheduleActivity")) {
            bottomNav.setSelectedItemId(R.id.nav_schedule);
        } else if (currentClass.equals("TodoActivity")) {
            bottomNav.setSelectedItemId(R.id.nav_todo);
        } else if (currentClass.equals("FinancesActivity")) {
            bottomNav.setSelectedItemId(R.id.nav_finances);
        } else if (currentClass.equals("GradesActivity")) {
            bottomNav.setSelectedItemId(R.id.nav_grades);
        } else if (currentClass.equals("HabitsActivity")) {
            bottomNav.setSelectedItemId(R.id.nav_habits);
        }
    }
}