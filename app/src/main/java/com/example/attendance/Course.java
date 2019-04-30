package com.example.attendance;

public class Course {
    private String id;
    private String name;
    private String section;

    public Course() {
    }

    public Course(String id, String name, String section) {
        this.id = id;
        this.name = name;
        this.section = section;
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
}
