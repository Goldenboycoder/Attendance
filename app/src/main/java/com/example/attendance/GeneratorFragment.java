package com.example.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class GeneratorFragment extends Fragment {
    ImageView qrImage;
    Button start;
    String inputValue; //Text to transform to QR code
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> CourseIDs = new ArrayList<>();
    ArrayList<Course> courses = new ArrayList<>();
    private DatabaseReference mDatabase;
    Handler h = new Handler();
    int delay = 1000; //1 second = 1000 millisecond
    Runnable runnable;
    ProgressBar ProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generator, container, false);
        ProgressBar = v.findViewById(R.id.progressBar);
        loadCourses();
        spinner = v.findViewById(R.id.spinner);
        //Generate QR code
        qrImage = v.findViewById(R.id.QR_Image);
        start = v.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    inputValue = spinner.getSelectedItem().toString();
                }catch(Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error. Check Internet Connectivity.", Toast.LENGTH_SHORT).show();
                }
                //code relating to the generation process
                WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;
                qrgEncoder = new QRGEncoder(
                        inputValue, null,
                        QRGContents.Type.TEXT,
                        smallerDimension);
                try {
                    bitmap = qrgEncoder.encodeAsBitmap();
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    Log.v("GenerateQRCode", e.toString());
                }
            }
        });
        return v;
    }

    //Loads courses into the app
    private void loadCourses() {
        //Reading courses
        mDatabase = FirebaseDatabase.getInstance().getReference().child("courses");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //loop to go through all the child nodes of courses(which are the randomly generated keys)
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Store courses into arraylist
                    courses.add(uniqueKeySnapshot.getValue(Course.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        //start handler as fragment becomes visible
        h.postDelayed(runnable = new Runnable() {
            public void run() {
                //Check if data arrived from database and laod into spinner
                if (CourseIDs.size() == 0) {
                    for (int i = 0; i < courses.size(); i++) {
                        CourseIDs.add(courses.get(i).getId());
                    }
                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CourseIDs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    Log.d("task", "hi" + CourseIDs.size());
                    h.postDelayed(runnable, delay);
                    ProgressBar.setVisibility(ProgressBar.VISIBLE);
                } else {
                    h.removeCallbacks(runnable);
                    ProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        h.removeCallbacks(runnable); //stop handler when fragment not visible
        ProgressBar.setVisibility(ProgressBar.INVISIBLE);
        super.onPause();
    }

}
