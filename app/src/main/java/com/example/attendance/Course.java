package com.example.attendance;

import java.util.ArrayList;

public class Course {
    private String id;
    private String name;
    private String section;
    private ArrayList<Student> students;

    public Course() {
    }

    public Course(String id, String name, String section, ArrayList<Student> students) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.students = students;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }
}
