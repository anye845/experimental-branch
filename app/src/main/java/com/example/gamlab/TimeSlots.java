package com.example.gamlab;

public class TimeSlots {
    private String time;


    private TimeSlots(){}

    private TimeSlots(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}