package com.example.trackademic;

public class GradeCard {
    String name;
    double currentGrade;
    double desiredGrade;
    GradeCategory cat1;
    GradeCategory cat2;
    GradeCategory cat3;
    GradeCategory cat4;
    int finalExamWeight;
    double finalExamGrade;

    //constructor

    public GradeCard(String name, double currentGrade, double desiredGrade,
                       GradeCategory cat1, GradeCategory cat2, GradeCategory cat3, GradeCategory cat4,
                       int finalExamWeight, double finalExamGrade){
        this.name = name;
        this.currentGrade = currentGrade;
        this.desiredGrade = desiredGrade;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.cat4 = cat4;
        this.finalExamWeight = finalExamWeight;
        this.finalExamGrade = finalExamGrade;

    }

    //Getters
    public String getName() {return name;}
    public double getCurrentGrade() {return currentGrade;}
    public double getDesiredGrade() {return desiredGrade;}
    public GradeCategory getCat1() {return cat1;}
    public GradeCategory getCat2() {return cat2;}
    public GradeCategory getCat3() {return cat3;}
    public GradeCategory getCat4() {return cat4;}
    public int getFinalExamWeight() {return finalExamWeight;}
    public double getFinalExamGrade() {return finalExamGrade;}

    //Setters
    public void setName() {this.name = name;}
    public void setCurrentGrade() {this.currentGrade = currentGrade;}
    public void setDesiredGrade() {this.desiredGrade = desiredGrade;}
    public void setCat1() {this.cat1 = cat1;}
    public void setCat2() {this.cat2 = cat2;}
    public void setCat3() {this.cat3 = cat3;}
    public void setCat4() {this.cat4 = cat4;}
    public void setFinalExamWeight() {this.finalExamWeight = finalExamWeight;}
    public void setFinalExamGrade() {this.finalExamGrade = finalExamGrade;}

}
