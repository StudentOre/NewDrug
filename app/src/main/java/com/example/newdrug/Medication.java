package com.example.newdrug;

public class Medication {
    private String name;
    private String time;
    private String note;

    public Medication(String name, String time, String note) {
        this.name = name;
        this.time = time;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getNote() {
        return note;
    }
}
