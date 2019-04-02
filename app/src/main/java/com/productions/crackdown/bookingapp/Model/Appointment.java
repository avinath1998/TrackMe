package com.productions.crackdown.bookingapp.Model;

import java.util.Calendar;


public class Appointment {

    private String title;
    private String details;
    private Calendar date;
    private String id;
    private boolean isDone;

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    private static  int count = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Appointment(String title, String details, Calendar date) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.id = System.currentTimeMillis()+"";
        isDone = false;
    }

    public Appointment(String id,String title, String details, Calendar date,boolean isDone) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.id = id;
        this.isDone = isDone;
    }
}
