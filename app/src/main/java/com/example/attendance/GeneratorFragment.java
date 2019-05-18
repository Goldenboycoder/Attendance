package com.example.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.support.constraint.Constraints.TAG;


public class GeneratorFragment extends Fragment {
    ImageView qrImage;
    Button start;
    Button update;
    String inputValue; //Text to transform to QR code
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> CourseIDs = new ArrayList<>();
    ArrayList<Course> courses = new ArrayList<>();
    ArrayList<String> sCourses=new ArrayList<>();
    private DatabaseReference mDatabase;
    private DatabaseReference seDatabase;
    ProgressBar ProgressBar;
    String cKey=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generator, container, false);
        ProgressBar = v.findViewById(R.id.progressBar);
        qrImage = v.findViewById(R.id.QR_Image);
        start = v.findViewById(R.id.start);
        spinner = v.findViewById(R.id.spinner);
        update=v.findViewById(R.id.btnupdateadapter);

        //Load courses
        loadCourses(new MyCallback() {
            @Override
            public void onCallback() {
                /*for (int i = 0; i < courses.size(); i++) {
                    CourseIDs.add(courses.get(i).getId());
                }*/
               /* adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sCourses);
                //created a customized layout for the selected item
               //adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item,sCourses);
                //created a customized item drop down layout
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), spinner.getAdapter().getCount()+"", Toast.LENGTH_SHORT).show();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity().getApplicationContext(), position+"", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/
                ProgressBar.setVisibility(View.INVISIBLE);
            }
        });


       /* for(int i=0;i<CourseIDs.size();i++) {
            loadSections(new MySectionCallback() {
                @Override
                public void onCallback(ArrayList<String> sections) {
                    for(int j=0;j<sections.size();j++){
                        sCourses.add(sections.get(j));
                        Toast.makeText(getActivity().getApplicationContext(),sections.get(j), Toast.LENGTH_SHORT).show();
                    }
                }
            },CourseIDs.get(i) );
        }*/

        //Generate QR code
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if automatic time is on
                if(Settings.Global.getInt(getActivity().getApplicationContext().getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1) {
                    try {
                        //Get system date
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        if(spinner.getSelectedItem()==null){
                            Toast.makeText(getActivity().getApplicationContext(), "Select a course", Toast.LENGTH_SHORT).show();
                        }
                        inputValue = spinner.getSelectedItem().toString() + "/" + date;
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error. Check Internet Connectivity.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    createEvent();
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
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Incorrect time/date", Toast.LENGTH_SHORT).show();
                }

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sCourses);
                //created a customized layout for the selected item
                //adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item,sCourses);
                //created a customized item drop down layout
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), spinner.getAdapter().getCount()+"", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
    /*public void updateadapter(View v){
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sCourses);
        //created a customized layout for the selected item
        //adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item,sCourses);
        //created a customized item drop down layout
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity().getApplicationContext(), spinner.getAdapter().getCount()+"", Toast.LENGTH_SHORT).show();
    }*/

    private void createEvent(){
        String[]parts=inputValue.split("/");
        String date=parts[1];
        String courseidS=parts[0];
        String[]subP=courseidS.split("-");
        String courseId=subP[0];
        String section=subP[1];
        mDatabase = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId).child("Section").child(section).child(date);
        seDatabase=FirebaseDatabase.getInstance().getReference().child("courses").child(courseId).child("Section").child(section);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(getActivity().getApplicationContext(),"date exists", Toast.LENGTH_SHORT).show();
                }
                else{
                    seDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot date : dataSnapshot.getChildren()){
                                if(date.exists()){
                                    copyRecord(date.getRef(),mDatabase);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(getActivity().getApplicationContext(),"date created", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void copyRecord(DatabaseReference fromPath,final DatabaseReference toPath){
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            Log.d(TAG,"Success");
                        }
                        else{
                            Log.d(TAG,"copy Failed");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }
    //load sections
    /*private void loadSections(final MySectionCallback mySectionCallback,String c){
            seDatabase=FirebaseDatabase.getInstance().getReference().child("courses").child(c).child("Section");
            final String course=c;
            seDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot sectionKey : dataSnapshot.getChildren()){
                            Section.add(course+"-"+sectionKey.getKey());
                            Toast.makeText(getActivity().getApplicationContext(),"loading saection", Toast.LENGTH_SHORT).show();
                        }
                        mySectionCallback.onCallback(Section);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }*/


    //Loads courses into the app
    private void loadCourses(final MyCallback myCallback) {
        //Reading courses

        mDatabase = FirebaseDatabase.getInstance().getReference().child("courses");

        final Pattern p=Pattern.compile("...\\d{3}");//eg: CSI300
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //loop to go through all the child nodes of courses(which are the randomly generated keys)
                    for (final DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        //Store courses into arraylist
                        //courses.add(uniqueKeySnapshot.getValue(Course.class));
                        if(p.matcher(uniqueKeySnapshot.getKey()).matches()){
                            //cKey=uniqueKeySnapshot.getKey();
                            //get Sections
                            seDatabase=FirebaseDatabase.getInstance().getReference().child("courses").child(uniqueKeySnapshot.getKey()).child("Section");
                            seDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot sectionKeySnapshot : dataSnapshot.getChildren()){
                                            sCourses.add(uniqueKeySnapshot.getKey()+"-"+sectionKeySnapshot.getKey());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                                //CourseIDs.add(uniqueKeySnapshot.getKey());
                        }
                        ProgressBar.setVisibility(ProgressBar.VISIBLE);
                        myCallback.onCallback();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface MySectionCallback{
        void onCallback(ArrayList<String> sections);
    }
    public interface MyCallback {
        void onCallback();
    }
}
