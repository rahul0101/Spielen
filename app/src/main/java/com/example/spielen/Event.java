package com.example.spielen;


import com.google.firebase.Timestamp;

public class Event {
    private String name;
    private Timestamp time;

    public Event(String name, Timestamp time) {
        this.name = name;
        this.time = time;
    }

    public Event(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
