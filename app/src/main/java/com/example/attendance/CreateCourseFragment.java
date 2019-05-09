package com.example.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class CreateCourseFragment extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference sDatabase;
    EditText courseId;
    EditText courseName;
    EditText courseSection;
    Uri uriData;
    ArrayList<Student> students = new ArrayList<>();
    //private EditText inputCourseId,inputCourseName,inputCourseSection;
    private TextInputLayout inputLayoutCourseID,inputLayoutCourseName,inputLayoutCourseSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_course, container, false);
        Button add = v.findViewById(R.id.CreateCourseBtn);
        courseId = v.findViewById(R.id.CourseIdET);
        courseName = v.findViewById(R.id.CourseNameET);
        courseSection = v.findViewById(R.id.CourseSectionET);

        inputLayoutCourseID=v.findViewById(R.id.input_layout_courseid);
        inputLayoutCourseName=v.findViewById(R.id.input_layout_coursename);
        inputLayoutCourseSection=v.findViewById(R.id.input_layout_coursesection);

        courseId.addTextChangedListener(new MyTextWatcher(courseId));
        courseName.addTextChangedListener(new MyTextWatcher(courseName));
        courseSection.addTextChangedListener(new MyTextWatcher(courseSection));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCourse(courseId.getText().toString(), courseName.getText().toString(), courseSection.getText().toString());
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
        private boolean validateSection() {
            if (courseSection.getText().toString().trim().isEmpty()) {
                inputLayoutCourseSection.setError(getString(R.string.err_msg_section));
                requestFocus(courseSection);
                return false;
            } else {
                inputLayoutCourseSection.setErrorEnabled(false);
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
                    validateName();
                    break;
                case R.id.CourseNameET:
                    validateName();
                    break;
                case R.id.CourseSectionET:
                    validateSection();
                    break;
            }
        }
    }



    private void AddCourse(String id, String name, String section) {
        mDatabase = FirebaseDatabase.getInstance().getReference("courses");
        String courseID = mDatabase.push().getKey(); //Create new empty course node with unique ID
        Course course;
        try {
            if(students.size() == 0 || id.equals("") || name.equals("") || section.equals(""))
                Toast.makeText(getActivity().getApplicationContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
            else {
                course = new Course(id, name, section, students);
                AddStudents(students); //Adds all students imported to the student node
                mDatabase.child(courseID).setValue(course); //Add course to course node
                Toast.makeText(getActivity().getApplicationContext(), "Course Created Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddStudents(ArrayList<Student> students) {
        sDatabase = FirebaseDatabase.getInstance().getReference("students");
        for(Student s : students) {
            try {
                sDatabase.child(s.getId()).setValue(s.getName());
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // To search for all documents available via installed storage providers,
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    public void readExcelFile(Context context, Uri u) {
        isStoragePermissionGranted();
        DataFormatter fmt = new DataFormatter();
        try {
            // Creating Input Stream from uri
            FileInputStream myInput = new FileInputStream(new File(getPath(u)));
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
            Toast.makeText(context,"Please choose a .xls file",Toast.LENGTH_SHORT).show();
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                readExcelFile(getActivity().getApplicationContext(), uriData);
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Exited",Toast.LENGTH_SHORT).show();
        }
    }
}
