package com.example.trackademic;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses;
    private ExpenseCategory category;
    private Runnable updateCallback;

    public ExpenseAdapter(List<Expense> expenses, ExpenseCategory category, Runnable updateCallback) {
        this.expenses = expenses;
        this.category = category;
        this.updateCallback = updateCallback;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(expenses.get(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionText;
        private TextView amountText;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.expenseDescription);
            amountText = itemView.findViewById(R.id.expenseAmount);

            itemView.setOnLongClickListener(v -> {
                showDeleteExpenseDialog(getAdapterPosition());
                return true;
            });
        }

        void bind(Expense expense) {
            descriptionText.setText(expense.getDescription());
            amountText.setText(String.format("$%.2f", expense.getAmount()));
        }

        private void showDeleteExpenseDialog(int position) {
            Expense expense = expenses.get(position);
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete the expense '" + expense.getDescription() +
                            "' ($" + String.format("%.2f", expense.getAmount()) + ")?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        double amount = expense.getAmount();
                        if (expenses.remove(expense)) {
                            category.removeExpense(expense);
                            notifyDataSetChanged();
                            updateCallback.run();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}