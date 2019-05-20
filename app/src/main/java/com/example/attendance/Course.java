package com.example.attendance;

public class Course {
    private String ID;
    private String Name;

    public Course() {
    }

    public Course(String id, String name) {
        this.ID = id;
        this.Name = name;

    }

    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }
}
