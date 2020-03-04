package com.example.spielen;

public class Event {
    private String name,time,date;

    public Event(String name, String time, String date) {
        this.name = name;
        this.time = time;
        this.date = date;
    }

    public Event(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
