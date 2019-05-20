package com.example.attendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CourseManagementFragment extends Fragment {
    private DatabaseReference mDatabase;
    ArrayList<Course> courses = new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton fab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        fab = view.findViewById(R.id.myFAB);
        fab.setEnabled(false);
        loadCourses(new MyCallback2() {
            @Override
            public void onCallback() {
                RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(new CourseAdapter(getActivity().getApplicationContext(), courses));

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreateCourseFragment nextFrag = CreateCourseFragment.newInstance(courses);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fcontent, nextFrag, null)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                fab.setEnabled(true);
            }
        });
        return view;
    }

    //Loads courses into the app
    private void loadCourses(final MyCallback2 myCallback) {
        //Reading courses
        mDatabase = FirebaseDatabase.getInstance().getReference().child("courses");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //reset courses incase returning from create course fragment
                    courses = new ArrayList<>();
                    //loop to go through all the child nodes of courses
                    for (final DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        //Store courses into arraylist
                        courses.add(new Course(uniqueKeySnapshot.child("id").getValue().toString(), uniqueKeySnapshot.child("name").getValue().toString()));
                    }
                }
                myCallback.onCallback();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public interface MyCallback2 {
        void onCallback();
    }
}
