package com.example.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private EditText name;
    private AutoCompleteTextView id;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    public final String Student_Name = "S_Name";
    public final String Student_ID = "S_ID";
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private String profilePictureURL="";
    public final String STUDENT_PROFILE_PICTURES_LOCATION="students_pics";
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
               View view = getActivity().findViewById(R.id.profile_pic);
               profilePic = view.findViewById(R.id.profile_pic);
               /*String path=ImagePath.getPath(getActivity(),imageuri);
               Toast.makeText(getActivity(),path,Toast.LENGTH_SHORT).show();
               Bitmap photo=null;
               try {
                   InputStream imageStream = getActivity().openFileInput(path);
                   photo = BitmapFactory.decodeStream(imageStream);
                   imageStream.close();
               }catch (Exception e){}*/
               Log.d("imageuri:", imageuri.toString());

               //Set the image
               profilePic.setImageURI(imageuri);
               //profilePic.setImageBitmap(photo);
               Toast.makeText(getActivity(),imageuri.toString(),Toast.LENGTH_SHORT).show();
               //store the image in firebase
               uploadPp();

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

    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri",imageuri.toString());

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            imageuri=Uri.parse(savedInstanceState.getString("uri"));
        }
    }*/

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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(id.getText().toString()).child("name");
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
    public void uploadPp(){
        mStorageRef= FirebaseStorage.getInstance().getReference("students_pics");
        mDatabase=FirebaseDatabase.getInstance().getReference();

        if(imageuri!=null){
            final StorageReference pictureRef=mStorageRef.child(id.getText().toString()+".jpg");
            pictureRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //code to run if upload was a success
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressBar.setProgress(0);
                                    ProgressBar.setVisibility(View.INVISIBLE);
                                }
                            },5000);
                            Toast.makeText(getContext(),"Upload Successful",Toast.LENGTH_LONG).show();
                            //code to set Url for student pp
                            profilePictureURL=pictureRef.getDownloadUrl().toString();
                            mDatabase.child("students").child(id.getText().toString()).child("imageURL").setValue(profilePictureURL);



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //code to run if upload failed
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //code to do when upload is in progress
                            double progress=(100* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            ProgressBar.setVisibility(getView().VISIBLE);
                            ProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(),"Profile pic not taken ",Toast.LENGTH_SHORT).show();
        }

    }
}
