package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
    TextView name;
    TextView id;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    public final String Student_Name = "S_Name";
    public final String Student_ID = "S_ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        name = v.findViewById(R.id.editStudentName);
        id = v.findViewById(R.id.editStudentID);
        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        name.setText(prefs.getString(Student_Name, ""));
        id.setText(prefs.getString(Student_ID, ""));
        Button done = v.findViewById(R.id.btnDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Store name and id in shared prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Student_Name, name.getText().toString());
                editor.putString(Student_ID, id.getText().toString());
                editor.commit();
                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
