package com.example.attendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateCourseFragment extends Fragment {
    private DatabaseReference mDatabase;
    EditText courseId;
    EditText courseName;
    EditText courseSection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_course, container, false);
        Button add = v.findViewById(R.id.CreateCourseBtn);
        courseId = v.findViewById(R.id.CourseIdET);
        courseName = v.findViewById(R.id.CourseNameET);
        courseSection = v.findViewById(R.id.CourseSectionET);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCourse(courseId.getText().toString(), courseName.getText().toString(), courseSection.getText().toString());
            }
        });
        return v;
    }
    private void AddCourse(String id, String name, String section){
        mDatabase = FirebaseDatabase.getInstance().getReference("courses");
        String courseID = mDatabase.push().getKey(); //Create new empty course node with unique ID
        Course course = new Course(id,name,section);
        try {
            mDatabase.child(courseID).setValue(course); //Add course to database
            Toast.makeText(getActivity().getApplicationContext(), "Course Created Successfully", Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
