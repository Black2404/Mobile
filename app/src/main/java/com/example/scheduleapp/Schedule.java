package com.example.scheduleapp;

public class Schedule {
    private int id;
    private String title, date, time, description;

    public Schedule(int id, String title, String date, String time, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public Schedule(int id, String title, String date, String time) {
        this(id, title, date, time, "");
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
}