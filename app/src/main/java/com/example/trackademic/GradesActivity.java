package com.example.trackademic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class GradesActivity extends BaseActivity {
    private SharedPreferences prefs;

    //for rounding double to 2 decimal places
    private static final DecimalFormat df = new DecimalFormat("#.##");

    //Variables for all views
    TextView classNameTV;
    TextView overallGradeTV;
    EditText desiredGradeET;
    EditText cat1NameET;
    EditText cat1WeightET;
    EditText cat1GradeET;
    EditText cat2NameET;
    EditText cat2WeightET;
    EditText cat2GradeET;
    EditText cat3NameET;
    EditText cat3WeightET;
    EditText cat3GradeET;
    EditText cat4NameET;
    EditText cat4WeightET;
    EditText cat4GradeET;
    EditText finalExamWeightET;
    TextView finalExamGradeTV;
    Button calcBtn;

    //variables to store current values of EditText fields
    public int c1weight;
    public double c1grade;
    public int c2weight;
    public double c2grade;
    public int c3weight;
    public double c3grade;
    public int c4weight;
    public double c4grade;
    public int finExamWeight;
    public double desGrade;
    public double curGrade;
    public double finExamGrade;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the content container
        FrameLayout contentContainer = findViewById(R.id.contentContainer);

        // Inflate grades-specific layout into the container
        View gradesContent = LayoutInflater.from(this).inflate(R.layout.activity_grades, contentContainer, false);
        contentContainer.addView(gradesContent);

        // Put todo-specific initialization code here


        //Get SharedPreferences
        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        //Get views by ID
        classNameTV = findViewById(R.id.className);
        overallGradeTV = findViewById(R.id.overallGrade);
        desiredGradeET = (EditText) findViewById(R.id.desiredGrade);
        cat1NameET = findViewById(R.id.cat1Name);
        cat1WeightET = findViewById(R.id.cat1Weight);
        cat1GradeET = findViewById(R.id.cat1Grade);
        cat2NameET = findViewById(R.id.cat2Name);
        cat2WeightET = findViewById(R.id.cat2Weight);
        cat2GradeET = findViewById(R.id.cat2Grade);
        cat3NameET = findViewById(R.id.cat3Name);
        cat3WeightET = findViewById(R.id.cat3Weight);
        cat3GradeET = findViewById(R.id.cat3Grade);
        cat4NameET = findViewById(R.id.cat4Name);
        cat4WeightET = findViewById(R.id.cat4Weight);
        cat4GradeET = findViewById(R.id.cat4Grade);
        finalExamWeightET = findViewById(R.id.finalExamWeight);
        finalExamGradeTV = findViewById(R.id.finalExamGrade);
        calcBtn = findViewById(R.id.calcBtn);

        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVariables();

                curGrade = calculateCurrentGrade();
                finExamGrade = calculateFinalExamGrade();

                overallGradeTV.setText(df.format(curGrade)+"%");
                finalExamGradeTV.setText(df.format(finExamGrade)+"%");
            }
        });
    }



    public double calculateCurrentGrade() {

        double c1GradeContr = (c1weight /(100.0-finExamWeight)) * c1grade;
        double c2GradeContr = (c2weight /(100.0-finExamWeight)) * c2grade;
        double c3GradeContr = (c3weight /(100.0-finExamWeight)) * c3grade;
        double c4GradeContr = (c4weight /(100.0-finExamWeight)) * c4grade;

        double currentGrade = c1GradeContr + c2GradeContr + c3GradeContr + c4GradeContr;

        return currentGrade;
    }

    public double calculateFinalExamGrade(){
        double finalExamGrade = 0;
        double c1GradeContr = (c1weight /100.0) * c1grade;
        double c2GradeContr = (c2weight /100.0) * c2grade;
        double c3GradeContr = (c3weight /100.0) * c3grade;
        double c4GradeContr = (c4weight /100.0) * c4grade;
        double remainGradeNeeded = desGrade - c1GradeContr - c2GradeContr - c3GradeContr - c4GradeContr;

        finalExamGrade = remainGradeNeeded / (finExamWeight/100.0);

        return finalExamGrade;

    }

    public double stringToDouble(String numString){
        double doubleNum;
        if (numString.equals("")){
            doubleNum = 0;
        }
        else{
            doubleNum = Double.parseDouble(numString);
        }
        return doubleNum;
    }

    public void updateVariables(){
        String c1weightStr = cat1WeightET.getText().toString();
        c1weight = (int) stringToDouble(c1weightStr);
        String c1gradeStr = cat1GradeET.getText().toString();
        c1grade = stringToDouble(c1gradeStr);

        String c2weightStr = cat2WeightET.getText().toString();
        c2weight = (int) stringToDouble(c2weightStr);
        String c2gradeStr = cat2GradeET.getText().toString();
        c2grade = stringToDouble(c2gradeStr);

        String c3weightStr = cat3WeightET.getText().toString();
        c3weight = (int) stringToDouble(c3weightStr);
        String c3gradeStr = cat3GradeET.getText().toString();
        c3grade = stringToDouble(c3gradeStr);

        String c4weightStr = cat4WeightET.getText().toString();
        c4weight = (int) stringToDouble(c4weightStr);
        String c4gradeStr = cat4GradeET.getText().toString();
        c4grade = stringToDouble(c4gradeStr);

        String finExamWeightStr = finalExamWeightET.getText().toString();
        finExamWeight = (int) stringToDouble(finExamWeightStr);

        String desGradeStr = desiredGradeET.getText().toString();
        desGrade = stringToDouble(desGradeStr);
    }



}