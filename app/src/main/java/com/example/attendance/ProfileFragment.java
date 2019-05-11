package com.example.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private EditText name;
    private AutoCompleteTextView id;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    public final String Student_Name = "S_Name";
    public final String Student_ID = "S_ID";
    private DatabaseReference mDatabase;
    private ArrayList<String> studentIDs = new ArrayList<>();
    private ProgressBar ProgressBar;
    CircleImageView profilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ProgressBar = v.findViewById(R.id.progressBar);
        name = v.findViewById(R.id.editStudentName);
        id = v.findViewById(R.id.editStudentID);

        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        Button save = v.findViewById(R.id.btnSave);
        final FloatingActionButton takePic = v.findViewById(R.id.btnTakePic);

        //Load all studentIds into the arraylist
        retrieveStudentIDs(new retrieveStudentIDsCallback() {
            @Override
            public void onCallback() {
                ProgressBar.setVisibility(getView().INVISIBLE);
            }
        });
        //AutoTextField set up
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, studentIDs);
        id.setThreshold(1);
        id.setAdapter(adapter);
        id.setTextColor(Color.DKGRAY);

        //If profile already created, disable editing
        if(prefs.getBoolean("profileCreated", false)) {
            id.setEnabled(false);
            save.setEnabled(false);
            name.setText(prefs.getString(Student_Name, ""));
            id.setText(prefs.getString(Student_ID, ""));
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if entered ID is valid
                if(studentIDs.contains(id.getText().toString())) {
                    //Store name and id in shared prefs
                    retrieveName(new retrieveNameCallback() {
                        @Override
                        public void onCallback(String value) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Student_Name, value);
                            editor.commit();
                            ProgressBar.setVisibility(View.INVISIBLE);
                            editor = prefs.edit();
                            editor.putString(Student_ID, id.getText().toString());
                            //Disable profile creation
                            editor.putBoolean("profileCreated", true);
                            editor.commit();
                            Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            //Reload Activity
                            if(getActivity() != null){
                                ((HomeActivity)getActivity()).reload();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Please choose a valid ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getActivity().getPackageManager())!=null) {
                    takePicture();
                }
                else
                    Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (resultCode == Activity.RESULT_OK) {
           if (requestCode == 333) {
               if (data != null) {
                   View view = getActivity().findViewById(R.id.profile_pic);
                   profilePic = view.findViewById(R.id.profile_pic);
                   //Set the image
                   profilePic.setImageURI(imageuri);
                   //store the image in firebase

               }
               else
                   Toast.makeText(getContext(),"data empty",Toast.LENGTH_SHORT).show();

           }
       }
    }
    Uri imageuri;
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pfp.jpg");
        imageuri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        //Remove Uri exposed error
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(intent, 333);
    }

    public interface retrieveStudentIDsCallback {
        void onCallback();
    }
    private void retrieveStudentIDs(final retrieveStudentIDsCallback myCallback) {
        //Read students node
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //loop to go through all student ids
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    //Store student ids into arraylist
                    studentIDs.add(uniqueKeySnapshot.getKey());
                    ProgressBar.setVisibility(View.VISIBLE);
                    myCallback.onCallback();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface retrieveNameCallback {
        void onCallback(String value);
    }
    private void retrieveName(final retrieveNameCallback myCallback){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(id.getText().toString());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProgressBar.setVisibility(View.VISIBLE);
                myCallback.onCallback(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
