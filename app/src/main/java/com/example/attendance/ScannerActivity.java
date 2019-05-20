package com.example.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Use rawResult.getText() to retrieve the result
        String[]parts=rawResult.getText().split("/");
        String date=parts[1];
        String courseidS=parts[0];
        String[]subP=courseidS.split("-");
        String courseId=subP[0];
        String section=subP[1];
        //udating firebase
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ATTENDANCE_APP_PREFS", Context.MODE_PRIVATE);
        String studentId=prefs.getString("S_ID","0");
        DatabaseReference mDatabase;
        mDatabase= FirebaseDatabase.getInstance().getReference().child("records").child(courseId).child(section).child(date).child(studentId);
        mDatabase.setValue(true);

        Toast.makeText(getApplicationContext(),rawResult.getText(),Toast.LENGTH_LONG).show();

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}

