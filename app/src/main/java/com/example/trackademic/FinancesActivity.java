package com.example.trackademic;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FinancesActivity extends BaseActivity {
    private static final String PREFS_NAME = "FinancesPrefs";
    private static final String CATEGORIES_KEY = "categories";

    private static final int[] CATEGORY_COLORS = {
            Color.rgb(46, 204, 113),  // Green
            Color.rgb(52, 152, 219),  // Blue
            Color.rgb(155, 89, 182),  // Purple
            Color.rgb(231, 76, 60),   // Red
            Color.rgb(241, 196, 15),  // Yellow
            Color.rgb(230, 126, 34),  // Orange
            Color.rgb(52, 73, 94),    // Dark Blue
            Color.rgb(149, 165, 166), // Gray
            Color.rgb(211, 84, 0),    // Dark Orange
            Color.rgb(192, 57, 43)    // Dark Red
    };

    private List<ExpenseCategory> categories;
    private CategoryAdapter categoryAdapter;
    private PieChart pieChart;
    private TextView totalExpensesText;
    private double totalExpenses;
    private Gson gson;
    private int currentColorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentContainer = findViewById(R.id.contentContainer);
        View financesContent = LayoutInflater.from(this).inflate(R.layout.activity_finances, contentContainer, false);
        contentContainer.addView(financesContent);

        gson = new Gson();
        initializeViews();
        loadData();
        setupPieChart();
        setupRecyclerView();
        setupFAB();
    }

    private void initializeViews() {
        pieChart = findViewById(R.id.pieChart);
        totalExpensesText = findViewById(R.id.totalExpensesText);
        categories = new ArrayList<>();
        totalExpenses = 0.0;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String categoriesJson = sharedPreferences.getString(CATEGORIES_KEY, null);

        if (categoriesJson != null) {
            Type type = new TypeToken<List<ExpenseCategory>>(){}.getType();
            categories = gson.fromJson(categoriesJson, type);

            if (!categories.isEmpty()) {
                currentColorIndex = categories.size() % CATEGORY_COLORS.length;
            }

            updateTotalAndChart();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String categoriesJson = gson.toJson(categories);
        editor.putString(CATEGORIES_KEY, categoriesJson);
        editor.apply();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        updatePieChart();
    }

    private void setupRecyclerView() {
        RecyclerView categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoryAdapter = new CategoryAdapter(categories, this::updateTotalAndChart);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupFAB() {
        FloatingActionButton addCategoryFab = findViewById(R.id.addCategoryFab);
        addCategoryFab.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (ExpenseCategory category : categories) {
            double total = category.getTotal();
            if (total > 0) {
                entries.add(new PieEntry((float) total, category.getName()));
                colors.add(category.getColor());
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle("Add Category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String categoryName = input.getText().toString();
                    if (!categoryName.isEmpty()) {
                        int color = CATEGORY_COLORS[currentColorIndex];
                        currentColorIndex = (currentColorIndex + 1) % CATEGORY_COLORS.length;

                        categories.add(new ExpenseCategory(categoryName, color));
                        categoryAdapter.notifyDataSetChanged();
                        updateTotalAndChart();
                        saveData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTotalAndChart() {
        totalExpenses = categories.stream()
                .mapToDouble(ExpenseCategory::getTotal)
                .sum();
        totalExpensesText.setText(String.format("$%.2f", totalExpenses));
        updatePieChart();
        saveData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    public List<ExpenseCategory> getCategories() {
        return categories;
    }
}