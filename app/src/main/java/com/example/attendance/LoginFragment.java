package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginFragment extends Fragment {
    EditText username;
    EditText password;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    public final String Authentication = "isAdmin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        username = v.findViewById(R.id.edtUsername);
        password = v.findViewById(R.id.editPassword);
        Button loginbtn = v.findViewById(R.id.btnLog);
        Button returnbtn = v.findViewById(R.id.btnReturn);
        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        if (prefs.contains(Authentication)) {
            if (prefs.getBoolean(Authentication, false)) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("admin", true);
                startActivity(intent);
            }
        }
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAdmin();
            }
        });

        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("admin",false);
                startActivity(intent);
            }
        });
        return v;
    }

    public void loginAdmin(){
        if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
            //Set account status to admin
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Authentication, true);
            editor.commit();
            //Reload Activity
            if(getActivity() != null){
                ((HomeActivity)getActivity()).reload();
            }
        }
        else{
            Toast.makeText(getActivity(),"Incorrect username or password", Toast.LENGTH_SHORT).show();
        }
    }
}
