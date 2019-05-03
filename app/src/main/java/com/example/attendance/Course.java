package com.example.attendance;

public class Course {
    private String id;
    private String name;
    private String section;
    private Student[] students;

    public Course() {
    }

    public Course(String id, String name, String section, Student[] students) {
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

    public Student[] getStudents() {
        return students;
    }

    public void setStudents(Student[] students) {
        this.students = students;
    }
}
