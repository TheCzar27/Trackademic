package com.example.trackademic;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoActivity extends BaseActivity {
    private LinearLayout todoList;
    private TextView tvNoItems;
    private ArrayList<ToDoItem> todos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_todo, (ViewGroup) findViewById(R.id.contentContainer));

        todoList = findViewById(R.id.todoList);
        todos = loadTodos();
        tvNoItems = new TextView(this);
        tvNoItems.setText("No items to-do");
        tvNoItems.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvNoItems.setTextSize(18);

        Button btnAddTodo = findViewById(R.id.btnAddTodo);
        btnAddTodo.setOnClickListener(v -> showAddTodoPopup());

        for (ToDoItem todo : todos) {
            todoList.addView(createTodoItemView(todo));
        }
        updateTodoListVisibility();
    }

    private ArrayList<ToDoItem> loadTodos() {
        ArrayList<ToDoItem> loadedTodos = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("TodoPrefs", MODE_PRIVATE);
        int todoCount = prefs.getInt("todoCount", 0);

        for (int i = 0; i < todoCount; i++) {
            String name = prefs.getString("todo_name_" + i, "");
            String className = prefs.getString("todo_class_" + i, "");
            String dueDate = prefs.getString("todo_date_" + i, "");
            loadedTodos.add(new ToDoItem(name, className, dueDate));
        }
        return loadedTodos;
    }

    private void saveTodos() {
        SharedPreferences prefs = getSharedPreferences("TodoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putInt("todoCount", todos.size());

        for (int i = 0; i < todos.size(); i++) {
            ToDoItem todo = todos.get(i);
            editor.putString("todo_name_" + i, todo.getName());
            editor.putString("todo_class_" + i, todo.getClassName());
            editor.putString("todo_date_" + i, todo.getDueDate());
        }
        editor.apply();
    }

    private void showAddTodoPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.add_todo, todoList, false);
        builder.setView(popupView);

        EditText etTodoName = popupView.findViewById(R.id.etTodoName);
        EditText etTodoClass = popupView.findViewById(R.id.etTodoClass);
        EditText etTodoDueDate = popupView.findViewById(R.id.etTodoDueDate);
        Button btnAddTodo = popupView.findViewById(R.id.btnAddTodo);

        AlertDialog dialog = builder.create();

        btnAddTodo.setOnClickListener(v -> {
            String name = etTodoName.getText().toString().trim();
            String className = etTodoClass.getText().toString().trim();
            String dueDate = etTodoDueDate.getText().toString().trim();

            if (name.isEmpty() || className.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(TodoActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                addTodoItem(name, className, dueDate);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addTodoItem(String name, String className, String dueDate) {
        ToDoItem item = new ToDoItem(name, className, dueDate);
        todos.add(item);
        todoList.addView(createTodoItemView(item));
        updateTodoListVisibility();
        saveTodos();
    }

    private View createTodoItemView(ToDoItem item) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.todo_item, todoList, false);

        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDetails = itemView.findViewById(R.id.tvDetails);
        Button btnDelete = itemView.findViewById(R.id.btnDelete);

        tvName.setText(item.getName());

        String detailsText = "Class: " + item.getClassName() + " | Due: " + item.getDueDate();
        if (isOverdue(item.getDueDate())) {
            detailsText += " (Overdue)";
        }
        tvDetails.setText(detailsText);

        btnDelete.setOnClickListener(v -> {
            todos.remove(item);
            todoList.removeView(itemView);
            updateTodoListVisibility();
            saveTodos();
        });

        return itemView;
    }

    private boolean isOverdue(String dueDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date due = sdf.parse(dueDate);
            Date currentTime = Calendar.getInstance().getTime();

            return due != null && due.before(currentTime);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateTodoListVisibility() {
        if (todos.isEmpty()) {
            todoList.removeAllViews();
            todoList.addView(tvNoItems);
        } else {
            todoList.removeView(tvNoItems);
        }
    }

    private static class ToDoItem {
        private final String name;
        private final String className;
        private final String dueDate;

        public ToDoItem(String name, String className, String dueDate) {
            this.name = name;
            this.className = className;
            this.dueDate = dueDate;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }

        public String getDueDate() {
            return dueDate;
        }
    }
}