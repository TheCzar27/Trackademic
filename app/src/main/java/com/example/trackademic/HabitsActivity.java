package com.example.trackademic;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class HabitsActivity extends BaseActivity {
    private LinearLayout habitList;
    private TextView tvNoHabits;
    private ArrayList<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_habits, (ViewGroup) findViewById(R.id.contentContainer));

        habitList = findViewById(R.id.habitList);
        tvNoHabits = new TextView(this);
        tvNoHabits.setText("No Habits");
        tvNoHabits.setTextSize(18);
        tvNoHabits.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        habits = loadHabits();
        Button btnAddHabit = findViewById(R.id.btnAddHabit);
        btnAddHabit.setOnClickListener(v -> showAddHabitDialog());

        for (Habit habit : habits) {
            habitList.addView(createHabitView(habit));
        }
        updateHabitListVisibility();
    }

    private ArrayList<Habit> loadHabits() {
        ArrayList<Habit> loadedHabits = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("HabitsPrefs", MODE_PRIVATE);
        int habitCount = prefs.getInt("habitCount", 0);

        for (int i = 0; i < habitCount; i++) {
            String name = prefs.getString("habit_name_" + i, "");
            long creationDate = prefs.getLong("habit_creation_" + i, 0);
            loadedHabits.add(new Habit(name, creationDate));
        }
        return loadedHabits;
    }

    private void saveHabits() {
        SharedPreferences prefs = getSharedPreferences("HabitsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putInt("habitCount", habits.size());

        for (int i = 0; i < habits.size(); i++) {
            Habit habit = habits.get(i);
            editor.putString("habit_name_" + i, habit.getName());
            editor.putLong("habit_creation_" + i, habit.creationDate);
        }
        editor.apply();
    }

    private void showAddHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.create_habit, null);
        builder.setView(dialogView);

        TextView etHabitName = dialogView.findViewById(R.id.etHabitName);
        Button btnSaveHabit = dialogView.findViewById(R.id.btnSaveHabit);

        AlertDialog dialog = builder.create();

        btnSaveHabit.setOnClickListener(v -> {
            String habitName = etHabitName.getText().toString().trim();
            if (habitName.isEmpty()) {
                Toast.makeText(HabitsActivity.this, "Please enter a habit name", Toast.LENGTH_SHORT).show();
            } else {
                addHabit(habitName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addHabit(String habitName) {
        Habit habit = new Habit(habitName, System.currentTimeMillis());
        habits.add(habit);
        habitList.addView(createHabitView(habit));
        updateHabitListVisibility();
        saveHabits();
    }

    private View createHabitView(Habit habit) {
        View habitView = LayoutInflater.from(this).inflate(R.layout.habit_item, null);

        // Set layout parameters with margins
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32); // 32dp bottom margin
        habitView.setLayoutParams(params);

        TextView tvHabitName = habitView.findViewById(R.id.HabitName);
        TextView tvHabitDays = habitView.findViewById(R.id.HabitDays);
        Button btnDeleteHabit = habitView.findViewById(R.id.DeleteHabit);

        tvHabitName.setText(habit.getName());
        tvHabitDays.setText(habit.getDays() + " Days");

        habitView.setOnClickListener(v -> showViewHabitDialog(habit));

        btnDeleteHabit.setOnClickListener(v -> {
            habits.remove(habit);
            habitList.removeView(habitView);
            updateHabitListVisibility();
            saveHabits();
            Toast.makeText(HabitsActivity.this, habit.getName() + " deleted", Toast.LENGTH_SHORT).show();
        });

        return habitView;
    }

    private void showViewHabitDialog(Habit habit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.habit_card, null);
        builder.setView(dialogView);

        TextView tvHabitTitle = dialogView.findViewById(R.id.tvHabitTitle);
        TextView tvHabitDays = dialogView.findViewById(R.id.tvHabitDays);
        Button btnEditHabit = dialogView.findViewById(R.id.btnEditHabit);

        tvHabitTitle.setText(habit.getName());
        tvHabitDays.setText(habit.getDays() + " Days");

        AlertDialog dialog = builder.create();

        btnEditHabit.setOnClickListener(v -> {
            Toast.makeText(HabitsActivity.this, "Edit functionality not implemented yet", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateHabitListVisibility() {
        if (habits.isEmpty()) {
            habitList.removeAllViews();
            habitList.addView(tvNoHabits);
        } else {
            habitList.removeView(tvNoHabits);
        }
    }

    private static class Habit {
        private final String name;
        private final long creationDate;

        public Habit(String name, long creationDate) {
            this.name = name;
            this.creationDate = creationDate;
        }

        public String getName() {
            return name;
        }

        public int getDays() {
            return (int) ((System.currentTimeMillis() - creationDate) / (1000 * 60 * 60 * 24));
        }
    }
}