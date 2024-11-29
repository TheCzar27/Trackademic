package com.example.trackademic;

public class ClassInfo {
    private String name;
    private String description;
    private String dayOfWeek;    // e.g., "Monday"
    private String startTime;    // e.g., "14:30"
    private String endTime;      // e.g., "16:00"
    private String professor;

    public ClassInfo(String name, String description, String dayOfWeek,
                     String startTime, String endTime, String professor) {
        this.name = name != null ? name : "";
        this.description = description != null ? description : "";
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek : "";
        this.startTime = startTime != null ? startTime : "";
        this.endTime = endTime != null ? endTime : "";
        this.professor = professor != null ? professor : "";
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getProfessor() { return professor; }

    // Setters
    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek : "";
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime != null ? startTime : "";
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime != null ? endTime : "";
    }

    public void setProfessor(String professor) {
        this.professor = professor != null ? professor : "";
    }
}