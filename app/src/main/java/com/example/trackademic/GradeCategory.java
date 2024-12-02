package com.example.trackademic;

public class GradeCategory {
    String name = "";
    int weight = 0;
    double grade = 0 ;

    public GradeCategory (String name, int weight, double grade){
        this.name = (name != null) ? name : "";
        this.weight = weight;
        this.grade = grade;

    }

    //Getters
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public double getGrade() { return grade; }

    //Setters
    public void setName(String name) { this.name = (name != null) ? name : ""; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setGrade(double grade) { this.grade = grade; }




}
