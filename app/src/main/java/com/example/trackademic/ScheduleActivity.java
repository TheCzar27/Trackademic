package com.example.trackademic;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends BaseActivity {
    private FloatingActionButton fabAdd;
    private LinearLayout classContainer;
    private WeekView weekView;
    private List<ClassInfo> classList;
    private SharedPreferences prefs;
    private static final String CLASSES_KEY = "saved_classes";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        // Inflate schedule content into base layout
        FrameLayout contentContainer = findViewById(R.id.contentContainer);
        View scheduleContent = LayoutInflater.from(this).inflate(R.layout.activity_schedule, contentContainer, false);
        contentContainer.addView(scheduleContent);

        // Initialize views
        fabAdd = findViewById(R.id.fabAdd);
        classContainer = findViewById(R.id.classContainer);
        weekView = findViewById(R.id.weekView);

        // Get SharedPreferences
        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Setup FAB
        fabAdd.setOnClickListener(v -> showAddClassDialog());

        // Load classes
        loadClasses();
        updateNextClassInfo();
    }

    private void showTimePickerDialog(final EditText timeEdit) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hour12 = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                    timeEdit.setText(String.format(Locale.getDefault(), "%d:%02d %s",
                            hour12, minute, amPm));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);

        EditText nameEdit = dialogView.findViewById(R.id.editClassName);
        EditText descriptionEdit = dialogView.findViewById(R.id.editDescription);
        EditText startTimeEdit = dialogView.findViewById(R.id.startTimeEdit);
        EditText endTimeEdit = dialogView.findViewById(R.id.endTimeEdit);
        EditText professorEdit = dialogView.findViewById(R.id.editProfessor);

        // Get references to day checkboxes
        CheckBox mondayCheck = dialogView.findViewById(R.id.mondayCheck);
        CheckBox tuesdayCheck = dialogView.findViewById(R.id.tuesdayCheck);
        CheckBox wednesdayCheck = dialogView.findViewById(R.id.wednesdayCheck);
        CheckBox thursdayCheck = dialogView.findViewById(R.id.thursdayCheck);
        CheckBox fridayCheck = dialogView.findViewById(R.id.fridayCheck);

        startTimeEdit.setOnClickListener(v -> showTimePickerDialog(startTimeEdit));
        endTimeEdit.setOnClickListener(v -> showTimePickerDialog(endTimeEdit));

        builder.setView(dialogView)
                .setTitle("Add New Class")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameEdit.getText().toString();
                    String description = descriptionEdit.getText().toString();
                    String startTime = startTimeEdit.getText().toString();
                    String endTime = endTimeEdit.getText().toString();
                    String professor = professorEdit.getText().toString();

                    if (!name.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty()) {
                        // Collect all selected days
                        List<String> selectedDays = new ArrayList<>();
                        if (mondayCheck.isChecked()) selectedDays.add("Mon");
                        if (tuesdayCheck.isChecked()) selectedDays.add("Tues");
                        if (wednesdayCheck.isChecked()) selectedDays.add("Wed");
                        if (thursdayCheck.isChecked()) selectedDays.add("Thurs");
                        if (fridayCheck.isChecked()) selectedDays.add("Fri");

                        if (!selectedDays.isEmpty()) {
                            // Create one ClassInfo object with all days
                            String daysString = String.join(", ", selectedDays);
                            ClassInfo newClass = new ClassInfo(name, description, daysString,
                                    startTime, endTime, professor);
                            handleNewClass(newClass, selectedDays);
                        } else {
                            Toast.makeText(this, "Please select at least one day",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please fill in all required fields",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleNewClass(ClassInfo newClass, List<String> days) {
        if (classList == null) {
            classList = new ArrayList<>();
        }
        classList.add(newClass);
        saveClasses();

        //save class to Database
        databaseHelper.addNewClass(newClass);
        // Add single card view
        addClassView(newClass);

        // Get the color index for this class
        int colorIndex = classList.size() - 1;

        // Add to WeekView for each selected day
        int[] startTime = convertTimeString(newClass.getStartTime());
        int[] endTime = convertTimeString(newClass.getEndTime());

        // Add event for each day with the same color
        for (String day : days) {
            int dayColumn = getDayColumn(day);
            weekView.addEvent(newClass.getName(), dayColumn,
                    startTime[0], startTime[1],
                    endTime[0], endTime[1],
                    colorIndex); // Pass the same color index for all days
        }

        // Update next class info
        updateNextClassInfo();
    }

    private int[] convertTimeString(String timeString) {
        String[] parts = timeString.split(" ");
        String[] timeParts = parts[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        if (parts[1].equals("PM") && hour != 12) {
            hour += 12;
        } else if (parts[1].equals("AM") && hour == 12) {
            hour = 0;
        }

        return new int[]{hour, minute};
    }

    private void addClassView(ClassInfo classItem) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.class_card_layout,
                classContainer, false);

        TextView nameText = cardView.findViewById(R.id.className);
        TextView descText = cardView.findViewById(R.id.classDescription);
        TextView timeText = cardView.findViewById(R.id.classTime);
        TextView profText = cardView.findViewById(R.id.classProfessor);

        nameText.setText(classItem.getName());

        if (!classItem.getDescription().isEmpty()) {
            descText.setText(classItem.getDescription());
            descText.setVisibility(View.VISIBLE);
        }

        // Format time string to show all days
        String timeString = String.format("%s %s - %s",
                classItem.getDayOfWeek(), // This now contains all days
                classItem.getStartTime(),
                classItem.getEndTime());
        timeText.setText(timeString);
        timeText.setVisibility(View.VISIBLE);

        if (!classItem.getProfessor().isEmpty()) {
            profText.setText("Professor: " + classItem.getProfessor());
            profText.setVisibility(View.VISIBLE);
        }

        // Add long press listener
        cardView.setOnLongClickListener(v -> {
            showDeleteDialog(classItem, cardView);
            return true;
        });

        classContainer.addView(cardView);
    }

    private void showDeleteDialog(ClassInfo classItem, View cardView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Class")
                .setMessage("Are you sure you want to delete " + classItem.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from classList
                    classList.remove(classItem);

                    //Delete from database
                    databaseHelper.deleteClass(classItem);

                    // Remove card view
                    classContainer.removeView(cardView);
                    saveClasses();

                    // Clear and rebuild week view
                    weekView.removeAllEvents();
                    int colorIndex = 0; // Reset color index
                    for (ClassInfo cls : classList) {
                        // Split days string back into individual days
                        String[] days = cls.getDayOfWeek().split(", ");
                        int[] startTime = convertTimeString(cls.getStartTime());
                        int[] endTime = convertTimeString(cls.getEndTime());

                        // Add event for each day of this class
                        for (String day : days) {
                            int dayColumn = getDayColumn(day);
                            weekView.addEvent(cls.getName(), dayColumn,
                                    startTime[0], startTime[1],
                                    endTime[0], endTime[1],
                                    colorIndex);
                        }
                        colorIndex++; // Increment color index for next class
                    }
                    updateNextClassInfo();
                    Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ClassInfo findNextClass() {
        if (classList == null || classList.isEmpty()) return null;

        Calendar now = Calendar.getInstance();
        int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        String currentTime = String.format(Locale.getDefault(), "%d:%02d %s",
                now.get(Calendar.HOUR) == 0 ? 12 : now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                now.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

        ClassInfo nextClass = null;
        int daysUntilNext = 7; // Maximum days to look ahead
        String earliestTime = "11:59 PM";
        String nextDay = null;  // Store the specific next day

        // Check today and next 6 days
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            int checkDay = ((currentDayOfWeek - 1 + dayOffset) % 7) + 1;
            String checkDayName = getDayOfWeekName(checkDay);

            for (ClassInfo cls : classList) {
                if (cls == null || cls.getDayOfWeek() == null || cls.getStartTime() == null) continue;

                // Split the days string and check each day
                String[] classDays = cls.getDayOfWeek().split(", ");
                for (String classDay : classDays) {
                    String normalizedClassDay = normalizeDayName(classDay);
                    if (!normalizedClassDay.equals(checkDayName)) continue;

                    // For same day, check if class hasn't started yet
                    if (dayOffset == 0) {
                        if (compareTimeStrings(cls.getStartTime(), currentTime) <= 0) continue;

                        if (compareTimeStrings(cls.getStartTime(), earliestTime) < 0) {
                            nextClass = cls;
                            earliestTime = cls.getStartTime();
                            daysUntilNext = 0;
                            nextDay = classDay;  // Store the specific day
                        }
                    }
                    // For future days, take the earliest class of that day
                    else if (dayOffset < daysUntilNext) {
                        nextClass = cls;
                        earliestTime = cls.getStartTime();
                        daysUntilNext = dayOffset;
                        nextDay = classDay;  // Store the specific day
                        break;
                    }
                }

                // If we found a class today, no need to check future days
                if (daysUntilNext == 0) break;
            }
        }

        // Store the next day in the class info
        if (nextClass != null) {
            nextClass = new ClassInfo(
                    nextClass.getName(),
                    nextClass.getDescription(),
                    nextDay,  // Use the specific next day instead of all days
                    nextClass.getStartTime(),
                    nextClass.getEndTime(),
                    nextClass.getProfessor()
            );
        }

        return nextClass;
    }

    private void updateNextClassInfo() {
        TextView nextClassInfo = findViewById(R.id.nextClassInfo);
        if (nextClassInfo == null) return;

        ClassInfo nextClass = findNextClass();
        if (nextClass != null) {
            Calendar now = Calendar.getInstance();
            Calendar classDay = Calendar.getInstance();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int classDayOfWeek = getDayNumber(nextClass.getDayOfWeek());  // Now this will be the specific day

            // Calculate days until class
            int daysUntil = (classDayOfWeek - currentDayOfWeek + 7) % 7;
            String dayInfo;

            if (daysUntil == 0) {
                dayInfo = "Today";
            } else if (daysUntil == 1) {
                dayInfo = "Tomorrow";
            } else {
                dayInfo = "on " + nextClass.getDayOfWeek();  // Now shows specific day
            }

            String info = String.format("%s %s at %s with %s",
                    nextClass.getName(),
                    dayInfo,
                    nextClass.getStartTime(),
                    nextClass.getProfessor());
            nextClassInfo.setText(info);
        } else {
            nextClassInfo.setText("No upcoming classes scheduled");
        }
    }

    private int getDayNumber(String day) {
        day = day.toLowerCase();
        if (day.startsWith("mon")) return Calendar.MONDAY;
        if (day.startsWith("tue")) return Calendar.TUESDAY;
        if (day.startsWith("wed")) return Calendar.WEDNESDAY;
        if (day.startsWith("thu")) return Calendar.THURSDAY;
        if (day.startsWith("fri")) return Calendar.FRIDAY;
        return Calendar.SUNDAY; // Default, though shouldn't occur
    }

    private int getDayColumn(String day) {
        day = day.toLowerCase();
        if (day.startsWith("mon")) return 1;
        if (day.startsWith("tue")) return 2;
        if (day.startsWith("wed")) return 3;
        if (day.startsWith("thu")) return 4;
        if (day.startsWith("fri")) return 5;
        return 0;
    }

    private String getDayOfWeekName(int day) {
        switch (day) {
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            default: return "";
        }
    }

    private String normalizeDayName(String day) {
        day = day.toLowerCase();
        if (day.startsWith("mon")) return "Monday";
        if (day.startsWith("tue")) return "Tuesday";
        if (day.startsWith("wed")) return "Wednesday";
        if (day.startsWith("thu")) return "Thursday";
        if (day.startsWith("fri")) return "Friday";
        return day;
    }

    private int compareTimeStrings(String time1, String time2) {
        try {
            int[] t1 = convertTimeString(time1);
            int[] t2 = convertTimeString(time2);
            return (t1[0] * 60 + t1[1]) - (t2[0] * 60 + t2[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void loadClasses() {
        Gson gson = new Gson();
        String json = prefs.getString(CLASSES_KEY, null);
        Type type = new TypeToken<ArrayList<ClassInfo>>(){}.getType();
        classList = gson.fromJson(json, type);

        if (classList == null) {
            classList = new ArrayList<>();
        } else {
            for (int colorIndex = 0; colorIndex < classList.size(); colorIndex++) {
                ClassInfo classItem = classList.get(colorIndex);
                if (classItem != null) {
                    addClassView(classItem);
                    int[] startTime = convertTimeString(classItem.getStartTime());
                    int[] endTime = convertTimeString(classItem.getEndTime());

                    // Split the days string and add events for each day
                    String[] days = classItem.getDayOfWeek().split(", ");
                    for (String day : days) {
                        int dayColumn = getDayColumn(day);
                        weekView.addEvent(classItem.getName(), dayColumn,
                                startTime[0], startTime[1],
                                endTime[0], endTime[1],
                                colorIndex);
                    }
                }
            }
        }
    }

    private void saveClasses() {

        if (prefs != null && classList != null) {
            Gson gson = new Gson();
            String json = gson.toJson(classList);
            prefs.edit().putString(CLASSES_KEY, json).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update next class info when returning to the activity
        updateNextClassInfo();
    }
}