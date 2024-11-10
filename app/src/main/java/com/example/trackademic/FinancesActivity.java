package com.example.trackademic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class FinancesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the content container
        FrameLayout contentContainer = findViewById(R.id.contentContainer);

        // Inflate todo-specific layout into the container
        View todoContent = LayoutInflater.from(this).inflate(R.layout.activity_finances, contentContainer, false);
        contentContainer.addView(todoContent);

        // Put todo-specific initialization code here
    }
}