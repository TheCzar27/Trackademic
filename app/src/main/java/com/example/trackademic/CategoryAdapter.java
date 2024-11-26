package com.example.trackademic;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<ExpenseCategory> categories;
    private Runnable updateCallback;
    private Context context;

    public CategoryAdapter(List<ExpenseCategory> categories, Runnable updateCallback) {
        this.categories = categories;
        this.updateCallback = updateCallback;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (position < categories.size()) {
            holder.bind(categories.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final TextView categoryTotal;
        private final RecyclerView expensesRecyclerView;
        private final Button addExpenseButton;
        private ExpenseAdapter expenseAdapter;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryTotal = itemView.findViewById(R.id.categoryTotal);
            expensesRecyclerView = itemView.findViewById(R.id.expensesRecyclerView);
            addExpenseButton = itemView.findViewById(R.id.addExpenseButton);

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteCategoryDialog(position);
                }
                return true;
            });
        }

        void bind(ExpenseCategory category) {
            try {
                categoryName.setText(category.getName());
                categoryTotal.setText(String.format("$%.2f", category.getTotal()));

                expenseAdapter = new ExpenseAdapter(category.getExpenses(), category, this::updateCategoryTotal);
                expensesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                expensesRecyclerView.setAdapter(expenseAdapter);

                addExpenseButton.setOnClickListener(v -> showAddExpenseDialog(category));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void showAddExpenseDialog(ExpenseCategory category) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null);

            EditText descriptionInput = dialogView.findViewById(R.id.expenseDescription);
            EditText amountInput = dialogView.findViewById(R.id.expenseAmount);

            builder.setTitle("Add Expense")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String description = descriptionInput.getText().toString();
                        try {
                            double amount = Double.parseDouble(amountInput.getText().toString());
                            Expense expense = new Expense(description, amount);
                            category.addExpense(expense);
                            expenseAdapter.notifyDataSetChanged();
                            updateCategoryTotal();
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showDeleteCategoryDialog(int position) {
            try {
                ExpenseCategory category = categories.get(position);
                new AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete the category '" + category.getName() + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            categories.remove(position);
                            notifyItemRemoved(position);
                            if (position < categories.size()) {
                                notifyItemRangeChanged(position, categories.size() - position);
                            }
                            updateCallback.run();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error showing delete dialog", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateCategoryTotal() {
            try {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < categories.size()) {
                    categoryTotal.setText(String.format("$%.2f", categories.get(position).getTotal()));
                    updateCallback.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}