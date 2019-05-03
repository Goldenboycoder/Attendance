package com.example.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class CreateCourseFragment extends Fragment {
    private DatabaseReference mDatabase;
    EditText courseId;
    EditText courseName;
    EditText courseSection;
    Uri uriData;

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
        Button importbtn = v.findViewById(R.id.importBtn);
        importbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        return v;
    }

    private void AddCourse(String id, String name, String section) {
        mDatabase = FirebaseDatabase.getInstance().getReference("courses");
        String courseID = mDatabase.push().getKey(); //Create new empty course node with unique ID
        //Course course = new Course(id,name,section);
        try {
            //mDatabase.child(courseID).setValue(course); //Add course to database
            Toast.makeText(getActivity().getApplicationContext(), "Course Created Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // To search for all documents available via installed storage providers,
        intent.setType("*/*");

        startActivityForResult(intent, 1);
    }

    private static void readExcelFile(Context context, Uri u) {

        try {
            //URI to file
            File selectedFile = new File(u.toString());

            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(selectedFile);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d("lol", "Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context,"Please choose valid excel file",Toast.LENGTH_SHORT).show();
        }
        return;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uriData = resultData.getData();
                Log.d("uri", "uri:" + uriData);
                readExcelFile(getActivity().getApplicationContext(), uriData);
            }
        }
    }
}
