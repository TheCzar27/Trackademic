package com.example.trackademic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoActivity extends AppCompatActivity {

    private LinearLayout todoList;
    private TextView tvNoItems;
    private ArrayList<ToDoItem> todos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Initialize views
        todoList = findViewById(R.id.todoList);
        todos = new ArrayList<>();
        tvNoItems = new TextView(this);
        tvNoItems.setText("No items to-do");
        tvNoItems.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvNoItems.setTextSize(18);

        // Floating button for adding To-Do items
        Button btnAddTodo = findViewById(R.id.btnAddTodo);
        btnAddTodo.setOnClickListener(v -> showAddTodoPopup());

        updateTodoListVisibility();
    }


    private void showAddTodoPopup() {
        // Inflate the popup view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.add_todo, null);
        builder.setView(popupView);

        // Initialize popup fields
        EditText etTodoName = popupView.findViewById(R.id.etTodoName);
        EditText etTodoClass = popupView.findViewById(R.id.etTodoClass);
        EditText etTodoDueDate = popupView.findViewById(R.id.etTodoDueDate);
        Button btnAddTodo = popupView.findViewById(R.id.btnAddTodo);

        AlertDialog dialog = builder.create();

        // Add To-Do button functionality
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

        View todoItemView = createTodoItemView(item);
        todoList.addView(todoItemView);

        updateTodoListVisibility();
    }


    private View createTodoItemView(ToDoItem item) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.todo_item, null);

        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDetails = itemView.findViewById(R.id.tvDetails);
        Button btnDelete = itemView.findViewById(R.id.btnDelete);

        tvName.setText(item.getName());
        String dueDateText = "Due: " + item.getDueDate();
        if (isOverdue(item.getDueDate())) {
            dueDateText += " (Overdue)";
        }
        tvDetails.setText(dueDateText);

        // Delete button functionality
        btnDelete.setOnClickListener(v -> {
            todos.remove(item);
            todoList.removeView(itemView);
            updateTodoListVisibility();
        });

        return itemView;
    }

    private boolean isOverdue(String dueDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date due = sdf.parse(dueDate);
            return due.before(Calendar.getInstance().getTime());
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

