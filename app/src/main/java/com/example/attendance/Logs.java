package com.example.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private DatabaseReference sDatabase;
    Button fetch;

    List<Student> students = new ArrayList<>();
    List<Student> filteredStudents=new ArrayList<>();
    List<Boolean> attended = new ArrayList<>();
    List<Boolean> filteredAttended = new ArrayList<>();
    Boolean isFiltered=false;
    TextView absenses;
    RecyclerView recyclerView;
    StudentAdapter suAdapter;
    StudentAdapter filteredAdapter;
    PopupWindow popupWindow;
    ConstraintLayout constraintLayout;

    Button closePop;
    TextView namePop;
    TextView idPop;
    CircleImageView popProfile;
    StorageReference storageRef;
    ProgressBar progressBar;
    Switch Spresent;
    EditText search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logs, container, false);
        progressBar = v.findViewById(R.id.progressBar2);
        constraintLayout=v.findViewById(R.id.frameLayout3);
        absenses = v.findViewById(R.id.absenses);
        spCourse=v.findViewById(R.id.spCourse);
        spSection=v.findViewById(R.id.spSection);
        search=v.findViewById(R.id.edSearch);
        spDate=v.findViewById(R.id.spDate);
        fetch=v.findViewById(R.id.btnGet);
        recyclerView=v.findViewById(R.id.recycler_view);
        fetch.setEnabled(false);
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
                        dAdapter=new ArrayAdapter(getContext(), R.layout.spinner_item, date);
                        dAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        spDate.setAdapter(dAdapter);
                        fetch.setEnabled(true);
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
                loadStudents(new MyStudentsCallback() {
                    @Override
                    public void onCallback() {
                        suAdapter=new StudentAdapter(students, attended,spCourse.getSelectedItem().toString(),spSection.getSelectedItem().toString(),spDate.getSelectedItem().toString(), Logs.this);
                        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(suAdapter);
                        //set total absentees
                        setAbsenses();
                    }
                },spCourse.getSelectedItem().toString(),spSection.getSelectedItem().toString(),spDate.getSelectedItem().toString());

            }
        });
       recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
           Student student;
           @Override
           public void onClick(View view, int position) {
               student=students.get(position);
           }

           @Override
           public void onLongClick(View view, final int position) {
               progressBar.setVisibility(View.VISIBLE);
               if(isFiltered){
                   student=filteredStudents.get(position);
               }
               else{
                    student=students.get(position);
               }
               Toast.makeText(getContext(),student.getId(),Toast.LENGTH_LONG).show();
               storageRef=FirebaseStorage.getInstance().getReferenceFromUrl("gs://attendanceapp-c1175.appspot.com/").child("students_pics").child(student.getId()+".jpg");
               LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               final View customView=layoutInflater.inflate(R.layout.popup,null);
               closePop=customView.findViewById(R.id.btnClose);
               namePop=customView.findViewById(R.id.popName);
               Spresent=customView.findViewById(R.id.swPresent);
               mDatabase=FirebaseDatabase.getInstance().getReference().child("records").child(spCourse.getSelectedItem().toString()).child(spSection.getSelectedItem().toString()).child(spDate.getSelectedItem().toString()).child(student.getId());
               mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       Spresent.setChecked((Boolean) dataSnapshot.getValue());

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
               Spresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       mDatabase.setValue(isChecked);
                       if(isFiltered){

                       }
                       else{

                       }
                       suAdapter.updateAttended(position,isChecked);
                   }
               });
               idPop=customView.findViewById(R.id.popID);
               namePop.setText(student.getName());
               idPop.setText(student.getId());
               popProfile=customView.findViewById(R.id.pop_profile);
               final long TWO_Megabyte=(1024*1024)*5;
               storageRef.getBytes(TWO_Megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                   @Override
                   public void onSuccess(byte[] bytes) {
                       Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                       Matrix matrix=new Matrix();
                       matrix.postRotate(270);
                       popProfile.setImageBitmap(Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true));
                       popupWindow=new PopupWindow(customView,500, 1100);
                       popupWindow.showAtLocation(constraintLayout, Gravity.CENTER,0,0);
                       progressBar.setVisibility(View.INVISIBLE);
                   }

               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getContext(),"Profile picture unavailable",Toast.LENGTH_LONG).show();
                       progressBar.setVisibility(View.INVISIBLE);
                   }
               });
               closePop.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       popupWindow.dismiss();
                       suAdapter.notifyItemChanged(position);
                   }
               });
           }
       }));
       search.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
                filter(s.toString());
           }
       });


        return v;
    }
    private void filter(String text){
        List<Student> filteredList=new ArrayList<>();
        List<Boolean> filteredAttend = new ArrayList<>();

        for(int i=0;i<students.size();i++){
            if(students.get(i).getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(students.get(i));
                filteredAttend.add(attended.get(i));

            }
        }

        if(!filteredList.isEmpty()){
            filteredStudents=filteredList;
            filteredAttended=filteredAttend;
            isFiltered=true;
            filteredAdapter=new StudentAdapter(filteredStudents,filteredAttended,spCourse.getSelectedItem().toString(),spSection.getSelectedItem().toString(),spDate.getSelectedItem().toString(), Logs.this);
            recyclerView.swapAdapter(filteredAdapter,false);
        }
        else
            isFiltered=false;
        //suAdapter.notifyDataSetChanged();
    }

    private void loadCourses(final MyCourseCallback myCourseCallback){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("records");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot CourseID : dataSnapshot.getChildren()){
                        courseId.add(CourseID.getKey());
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
        mDatabase= FirebaseDatabase.getInstance().getReference().child("records").child(c);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot SectionKey : dataSnapshot.getChildren()){
                        section.add(SectionKey.getKey());
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
        mDatabase= FirebaseDatabase.getInstance().getReference().child("records").child(c).child(s);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot DateKey : dataSnapshot.getChildren()){
                        date.add(DateKey.getKey());
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
    private void loadStudents(final MyStudentsCallback myStudentsCallback,String c,String s,String d){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("records").child(c).child(s).child(d);
        sDatabase = FirebaseDatabase.getInstance().getReference().child("students");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(final DataSnapshot StudentKey : dataSnapshot.getChildren()){
                        attended.add((Boolean)StudentKey.getValue());
                        sDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                students.add(dataSnapshot.child(StudentKey.getKey()).getValue(Student.class));
                                suAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        Toast.makeText(getActivity().getApplicationContext(),"loading Students", Toast.LENGTH_SHORT).show();
                    }
                    myStudentsCallback.onCallback();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface MyStudentsCallback {
        void onCallback();
    }

    public void setAbsenses() {
        int count = 0;
        for(int i = 0; i < attended.size(); i++) {
            if(!attended.get(i)) {
                count ++;
            }
        }
        absenses.setText("Total Absentees: " + count);
    }
}

