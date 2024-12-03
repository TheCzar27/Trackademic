package com.example.trackademic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HabitsActivity extends AppCompatActivity {

    private LinearLayout habitList;
    private TextView tvNoHabits;
    private ArrayList<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);


        habitList = findViewById(R.id.habitList);
        tvNoHabits = new TextView(this);
        tvNoHabits.setText("No Habits");
        tvNoHabits.setTextSize(18);
        tvNoHabits.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        habits = new ArrayList<>();
        Button btnAddHabit = findViewById(R.id.btnAddHabit);


        btnAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHabitDialog();
            }
        });

        updateHabitListVisibility();
    }


    private void showAddHabitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.create_habit, null);
        builder.setView(dialogView);

        TextView etHabitName = dialogView.findViewById(R.id.etHabitName);
        Button btnSaveHabit = dialogView.findViewById(R.id.btnSaveHabit);

        AlertDialog dialog = builder.create();

        btnSaveHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String habitName = etHabitName.getText().toString().trim();
                if (habitName.isEmpty()) {
                    Toast.makeText(HabitsActivity.this, "Please enter a habit name", Toast.LENGTH_SHORT).show();
                } else {
                    addHabit(habitName);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    /**
     *
     * @param habitName The name of the habit.
     */
    private void addHabit(String habitName) {
        Habit habit = new Habit(habitName, 0);
        habits.add(habit);

        // Add the habit to the UI
        View habitItem = createHabitView(habit);
        habitList.addView(habitItem);

        updateHabitListVisibility();
    }

    /**
     *
     * @param habit The habit object.
     * @return The view representing the habit.
     */
    private View createHabitView(Habit habit) {
        View habitView = LayoutInflater.from(this).inflate(R.layout.habit_item, null);

        TextView tvHabitName = habitView.findViewById(R.id.HabitName);
        TextView tvHabitDays = habitView.findViewById(R.id.HabitDays);
        Button btnDeleteHabit = habitView.findViewById(R.id.DeleteHabit);

        tvHabitName.setText(habit.getName());
        tvHabitDays.setText(habit.getDays() + " Days");


        habitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewHabitDialog(habit);
            }
        });


        btnDeleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habits.remove(habit);
                habitList.removeView(habitView);
                updateHabitListVisibility();
                Toast.makeText(HabitsActivity.this, habit.getName() + " deleted", Toast.LENGTH_SHORT).show();
            }
        });

        return habitView;
    }

    /**
     *
     * @param habit The habit to view/edit.
     */
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

        btnEditHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HabitsActivity.this, "Edit functionality not implemented yet", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
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
        private String name;
        private int days;

        public Habit(String name, int days) {
            this.name = name;
            this.days = days;
        }

        public String getName() {
            return name;
        }

        public int getDays() {
            return days;
        }
    }
}
