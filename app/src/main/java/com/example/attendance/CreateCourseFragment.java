package com.example.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hpsf.Section;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class CreateCourseFragment extends Fragment implements Serializable {
    private DatabaseReference mDatabase;
    private DatabaseReference sDatabase;
    EditText courseId;
    EditText courseName;
    Uri uriData;
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Course> courses = new ArrayList<>();
    private TextInputLayout inputLayoutCourseID,inputLayoutCourseName,inputLayoutCourseSection;
    private static final int STORAGE_PERMISSION = 2;
    int totalsections;
    boolean exists = false;

    public static CreateCourseFragment newInstance (ArrayList<Course> courses) {
        CreateCourseFragment nf = new CreateCourseFragment();
        Bundle args = new Bundle();
        args.putSerializable("courses", courses);
        nf.setArguments(args);
        return nf;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_course, container, false);
        this.courses = (ArrayList<Course>)getArguments().getSerializable("courses");
        getArguments().remove("courses");
        Button add = v.findViewById(R.id.CreateCourseBtn);
        courseId = v.findViewById(R.id.CourseIdET);
        courseName = v.findViewById(R.id.CourseNameET);

        inputLayoutCourseID=v.findViewById(R.id.input_layout_courseid);
        inputLayoutCourseName=v.findViewById(R.id.input_layout_coursename);

        courseId.addTextChangedListener(new MyTextWatcher(courseId));
        courseName.addTextChangedListener(new MyTextWatcher(courseName));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = 0;
                for(Course c : courses) {
                    if(c.getId().equals(courseId.getText().toString())) {
                        val = 1;
                        Toast.makeText(getActivity().getApplicationContext(), "already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                if(val == 0) {
                    AddCourse(courseId.getText().toString(), courseName.getText().toString(), "A");
                    getFragmentManager().popBackStack();
                }
                else{
                    getNbOfSections(new MySectionCallback2() {
                        @Override
                        public void onCallback() {
                            if(totalsections <= 26)
                                AddCourse(courseId.getText().toString(), courseName.getText().toString(), "" + (char)(totalsections + 65));
                            getFragmentManager().popBackStack();
                        }
                    }, courseId.getText().toString());
                }
            }
        });
        Button importbtn = v.findViewById(R.id.importBtn);
        importbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        return v;
    }

    private class MyTextWatcher implements TextWatcher{
        private View view;

        private MyTextWatcher(View view){
            this.view=view;
        }

        private void requestFocus(View view) {
            if (view.requestFocus()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }

        private boolean validateID() {
            if (courseId.getText().toString().trim().isEmpty()) {
                inputLayoutCourseID.setError(getString(R.string.err_msg_id));
                requestFocus(courseId);
                return false;
            } else {
                inputLayoutCourseID.setErrorEnabled(false);
            }

            return true;
        }

        private boolean validateName() {
            if (courseName.getText().toString().trim().isEmpty()) {
                inputLayoutCourseName.setError(getString(R.string.err_msg_name));
                requestFocus(courseName);
                return false;
            } else {
                inputLayoutCourseName.setErrorEnabled(false);
            }

            return true;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.CourseIdET:
                    validateID();
                    break;
                case R.id.CourseNameET:
                    validateName();
                    break;
            }
        }
    }

    private void AddCourse(String id, String name, String section) {
        mDatabase = FirebaseDatabase.getInstance().getReference("courses");
        Course course;
        //String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        try {
            if(students.size() == 0 || id.equals("") || name.equals("") || section.equals(""))
                Toast.makeText(getActivity().getApplicationContext(), "Please provide all required data", Toast.LENGTH_SHORT).show();
            else {
                course = new Course(id, name);
                AddStudents(students); //Adds all students imported to the student node
                if(section.equals("A"))
                    mDatabase.child(course.getId()).setValue(course); //Add course to course node
                DatabaseReference seDatabase;
                seDatabase = mDatabase.child(course.getId()).child("Section").child(section);
                for(int i = 0; i < students.size(); i++) {
                    seDatabase.child("" + i).setValue(students.get(i).getId());
                }
                Toast.makeText(getActivity().getApplicationContext(), "Course Created Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddStudents(ArrayList<Student> students) {
        for(Student s : students) {
            try {
                sDatabase = FirebaseDatabase.getInstance().getReference("students").child(s.getId());
                DatabaseReference eDatabase = FirebaseDatabase.getInstance().getReference("students");
                sDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            exists = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(!exists) {
                    eDatabase.child(s.getId()).setValue(s);
                }
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getNbOfSections(final MySectionCallback2 mySectionCallback, String id){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("courses").child(id).child("Section");
        totalsections = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    totalsections = (int)(dataSnapshot.getChildrenCount());
                    Toast.makeText(getActivity().getApplicationContext(), "values : "+ totalsections , Toast.LENGTH_SHORT).show();
                    mySectionCallback.onCallback();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface MySectionCallback2 {
        void onCallback();
    }

    private void performFileSearch() {
        isStoragePermissionGranted();
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // To search for all documents available via installed storage providers,
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    public void readExcelFile() {
        //isStoragePermissionGranted();
        DataFormatter fmt = new DataFormatter();
        try {
            Log.d("urid", "readExcelFile: " + uriData);
            // Creating Input Stream from uri
            Toast.makeText(getActivity(),uriData.getPath(),Toast.LENGTH_LONG).show();
            FileInputStream myInput = new FileInputStream(new File(uriData.getPath()));
            Toast.makeText(getActivity(),uriData.getPath(),Toast.LENGTH_LONG).show();
            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            //Used to iterate through cells
            Iterator rowIter = mySheet.rowIterator();

            //Empty attributes to use for student
            String name;
            String id;
            String image = "";
            int cellCount; //Cell count
            Student student;
            while (rowIter.hasNext()) {
                //reset values
                cellCount = 0; id = ""; name = "";
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    //Convert cell to a string
                    String cellValue = fmt.formatCellValue(myCell);
                    switch (cellCount) {
                        case 0:
                            id = cellValue;
                            break;
                        case 1:
                            name = cellValue;
                            break;
                        default:
                    }
                    cellCount++;
                }
                //Check if row is valid
                if(!name.equals("") && !id.equals("")) {
                    //Create a student instance
                    student = new Student(name, id, image);
                    students.add(student);
                }
            }
        } catch (Exception e) {
            //Toast.makeText(getActivity(),"Please choose a .xls file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return;
    }
    //Convert content uri to a file path
    private String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    //Check storage permissions
    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user will return a URI
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uriData = resultData.getData();
                readExcelFile();
            }
        }else{
            Toast.makeText(getActivity(), "Exited",Toast.LENGTH_SHORT).show();
        }
    }
}
