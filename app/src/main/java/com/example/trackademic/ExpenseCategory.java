package com.example.trackademic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategory implements Serializable {
    private String name;
    private List<Expense> expenses;
    private double total;
    private int color;  // New field for color

    public ExpenseCategory(String name, int color) {
        this.name = name;
        this.expenses = new ArrayList<>();
        this.total = 0.0;
        this.color = color;
    }

    public String getName() { return name; }
    public List<Expense> getExpenses() { return expenses; }
    public int getColor() { return color; }
    public double getTotal() {
        recalculateTotal();
        return total;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        total += expense.getAmount();
    }

    public boolean removeExpense(Expense expense) {
        boolean removed = expenses.remove(expense);
        if (removed) {
            recalculateTotal();
        }
        return removed;
    }

    private void recalculateTotal() {
        total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}