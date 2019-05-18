package com.example.attendance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Logs extends Fragment {

    ArrayList<String> courseId=new ArrayList<>();
    ArrayList<String> section=new ArrayList<>();
    ArrayList<String> date=new ArrayList<>();

    Spinner spCourse;
    Spinner spSection;
    Spinner spDate;

    ArrayAdapter<String>cAdapter;
    ArrayAdapter<String>sAdapter;
    ArrayAdapter<String>dAdapter;

    private DatabaseReference mDatabase;
    Button fetch;

    ArrayList<Student> students=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_logs, container, false);
        spCourse=v.findViewById(R.id.spCourse);
        spSection=v.findViewById(R.id.spSection);
        spDate=v.findViewById(R.id.spDate);
        fetch=v.findViewById(R.id.btnGet);
        loadCourses(new MyCourseCallback() {
            @Override
            public void onCallback() {
                cAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, courseId);
                cAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spCourse.setAdapter(cAdapter);

            }
        });
        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section=new ArrayList<>();
                loadSection(new MySectionCallback() {
                    @Override
                    public void onCallback() {
                        sAdapter=new ArrayAdapter<String>(getContext(), R.layout.spinner_item, section);
                        sAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        spSection.setAdapter(sAdapter);
                    }
                },spCourse.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date=new ArrayList<>();
                loadDates(new MyDatesCallback() {
                    @Override
                    public void onCallback() {
                        dAdapter=new ArrayAdapter<String>(getContext(), R.layout.spinner_item, date);
                        dAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        spDate.setAdapter(dAdapter);
                    }
                },spCourse.getSelectedItem().toString(),spSection.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                students=new ArrayList<>();

            }
        });

        return v;
    }

    private void loadCourses(final MyCourseCallback myCourseCallback){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("courses");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot CourseKey : dataSnapshot.getChildren()){
                        courseId.add(CourseKey.getKey());
                        cAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplicationContext(),"loading courses", Toast.LENGTH_SHORT).show();
                    }
                    myCourseCallback.onCallback();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface MyCourseCallback {
        void onCallback();
    }
    private void loadSection(final MySectionCallback mySectionCallback,String c){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("courses").child(c).child("Section");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot SectionKey : dataSnapshot.getChildren()){
                        section.add(SectionKey.getKey());
                        sAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplicationContext(),"loading sections", Toast.LENGTH_SHORT).show();
                    }
                    mySectionCallback.onCallback();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface MySectionCallback {
        void onCallback();
    }
    private void loadDates(final MyDatesCallback myDatesCallback,String c,String s){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("courses").child(c).child("Section").child(s);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot DateKey : dataSnapshot.getChildren()){
                        date.add(DateKey.getKey());
                        dAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplicationContext(),"loading dates", Toast.LENGTH_SHORT).show();
                    }
                    myDatesCallback.onCallback();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface MyDatesCallback {
        void onCallback();
    }
}

