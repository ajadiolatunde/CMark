package com.phyrelinx.cp.cmark;

import org.json.JSONObject;

public class Event {
    //     eventstart
    String day,lenofstudent;
    JSONObject students;

    public Event(String day, JSONObject eve, String len){
        this.lenofstudent = len;
        this.students = eve;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLenofstudent() {
        return lenofstudent;
    }

    public void setLenofstudent(String lenofstudent) {
        this.lenofstudent = lenofstudent;
    }

    public JSONObject getStudents() {
        return students;
    }

    public void setStudents(JSONObject students) {
        this.students = students;
    }
}
