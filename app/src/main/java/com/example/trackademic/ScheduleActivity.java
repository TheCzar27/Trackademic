package com.example.trackademic;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAdd;
    private LinearLayout classContainer;
    private List<ClassInfo> classList; // Renamed to ClassInfo to avoid confusion with java.lang.Class
    private SharedPreferences prefs;
    private static final String CLASSES_KEY = "saved_classes";

    // Inner class for storing class information
    private static class ClassInfo {
        private String name;
        private String description;
        private String time;
        private String professor;

        public ClassInfo(String name, String description, String time, String professor) {
            this.name = name;
            this.description = description;
            this.time = time;
            this.professor = professor;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getTime() { return time; }
        public String getProfessor() { return professor; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialize views
        bottomNav = findViewById(R.id.bottomNav);
        fabAdd = findViewById(R.id.fabAdd);
        classContainer = findViewById(R.id.classContainer);

        // Set student name from SharedPreferences
        TextView studentName = findViewById(R.id.studentName);
        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Student Name");
        studentName.setText(username);

        // Load saved classes
        loadClasses();

        // Setup bottom navigation
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_schedule) {
                return true;
            } else if (itemId == R.id.nav_todo) {
                startActivity(new Intent(this, TodoActivity.class));
                return true;
            } else if (itemId == R.id.nav_finances) {
                startActivity(new Intent(this, FinancesActivity.class));
                return true;
            } else if (itemId == R.id.nav_grades) {
                startActivity(new Intent(this, GradesActivity.class));
                return true;
            } else if (itemId == R.id.nav_habits) {
                startActivity(new Intent(this, HabitsActivity.class));
                return true;
            }
            return false;
        });

        // Setup FAB
        fabAdd.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_add_class_dialog, null);

        EditText nameEdit = dialogView.findViewById(R.id.editClassName);
        EditText descriptionEdit = dialogView.findViewById(R.id.editDescription);
        EditText timeEdit = dialogView.findViewById(R.id.editTime);
        EditText professorEdit = dialogView.findViewById(R.id.editProfessor);

        builder.setView(dialogView)
                .setTitle("Add New Class")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameEdit.getText().toString();
                    String description = descriptionEdit.getText().toString();
                    String time = timeEdit.getText().toString();
                    String professor = professorEdit.getText().toString();

                    if (!name.isEmpty()) {
                        addClass(new ClassInfo(name, description, time, professor));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addClass(ClassInfo newClass) {
        classList.add(newClass);
        saveClasses();
        addClassView(newClass);
    }

    private void addClassView(ClassInfo classItem) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.class_card_layout, classContainer, false);

        TextView nameText = cardView.findViewById(R.id.className);
        TextView descText = cardView.findViewById(R.id.classDescription);
        TextView timeText = cardView.findViewById(R.id.classTime);
        TextView profText = cardView.findViewById(R.id.classProfessor);
        View deleteBtn = cardView.findViewById(R.id.deleteButton);

        nameText.setText(classItem.getName());

        if (!classItem.getDescription().isEmpty()) {
            descText.setText(classItem.getDescription());
            descText.setVisibility(View.VISIBLE);
        }

        if (!classItem.getTime().isEmpty()) {
            timeText.setText("Time: " + classItem.getTime());
            timeText.setVisibility(View.VISIBLE);
        }

        if (!classItem.getProfessor().isEmpty()) {
            profText.setText("Professor: " + classItem.getProfessor());
            profText.setVisibility(View.VISIBLE);
        }

        deleteBtn.setOnClickListener(v -> {
            classList.remove(classItem);
            classContainer.removeView(cardView);
            saveClasses();
        });

        classContainer.addView(cardView);
    }

    private void loadClasses() {
        Gson gson = new Gson();
        String json = prefs.getString(CLASSES_KEY, null);
        Type type = new TypeToken<ArrayList<ClassInfo>>(){}.getType();
        classList = gson.fromJson(json, type);

        if (classList == null) {
            classList = new ArrayList<>();
        } else {
            for (ClassInfo classItem : classList) {
                addClassView(classItem);
            }
        }
    }

    private void saveClasses() {
        Gson gson = new Gson();
        String json = gson.toJson(classList);
        prefs.edit().putString(CLASSES_KEY, json).apply();
    }
}