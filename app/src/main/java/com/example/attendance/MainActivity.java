package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    public final String Preference="privateS";
    public final String Authentication="isLogedin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=findViewById(R.id.edtUsername);
        password=findViewById(R.id.editPassword);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(Preference, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(Authentication,false)){
            Intent intent=new Intent(getApplicationContext(),AdminHomeScreen.class);
            startActivity(intent);
        }
    }
    public void studentAcess(View v){
        Intent intent=new Intent(getApplicationContext(),StudentHomeScreen.class);
        startActivity(intent);
    }

    public void loginAdmin(View v){
        if(username.getText().toString().equals("admin")&&password.getText().toString().equals("admin")){
            Intent intent=new Intent(getApplicationContext(),AdminHomeScreen.class);
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(Preference, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(Authentication,true);
            editor.commit();
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to login retry",Toast.LENGTH_SHORT).show();
        }
    }
}
