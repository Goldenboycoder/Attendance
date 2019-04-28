package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    boolean admin;
    public final String Preference = "privateS";
    public final String Authentication = "isLoggedin";

    public final String Student_Name = "sname";
    public final String Student_ID = "S_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        admin = getIntent().getBooleanExtra("admin",false);
        NavigationView nvDrawer;
        if(admin){
            //for admin content
            setContentView(R.layout.activity_admin_home_screen);
            TextView profilename = findViewById(R.id.profile_name);
            profilename.setText("Administrator");
            mDrawerLayout = findViewById(R.id.Adrawer);
            nvDrawer = findViewById(R.id.Anav_view);
        }
        else {
            //for student content
            setContentView(R.layout.activity_student_home_screen);
            mDrawerLayout = findViewById(R.id.Sdrawer);
            nvDrawer = findViewById(R.id.Snav_view);
        }


        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);

       /* SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(Preference, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(Student_Name)&&sharedPreferences.contains(Student_ID)){
            TextView username=findViewById(R.id.profile_name);
            username.setText(sharedPreferences.getString(Student_Name,"User")+"  ID : "+sharedPreferences.getString(Student_ID,"xxxxxxx"));
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        //this can be done with activities instead of fragments (intent then startactivityforresult later receive in onactivityresult )
        Fragment myFragment = null;
        Class FragmentClass;
        switch (menuItem.getItemId()){
            case R.id.Sprofile :
                FragmentClass = StudentProfile.class;
                break;
            case R.id.Sscanner :
                FragmentClass = StudentScanner.class;
                break;
            case R.id.Slogout:
                //Handle user clicking logout
                Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                FragmentClass = LogoutScreen.class;
                break;
            case R.id.AcreateCourse:
                FragmentClass = AdminCreateCourse.class;
                break;
            case R.id.AshowAttendace:
                FragmentClass = AdminShowAttendance.class;
                break;
            case R.id.Alogout:
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Preference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Authentication, false);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                FragmentClass = LogoutScreen.class;
                break;

                default:
                    FragmentClass = LogoutScreen.class;
                    //should change
        }

        try {
            myFragment = (Fragment)FragmentClass.newInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();

        //f1 is the frame layout in admin_home_screen, f2 is for the student
        if(admin)
            fragmentManager.beginTransaction().replace(R.id.f1content,myFragment).commit();
        else
            fragmentManager.beginTransaction().replace(R.id.f2content,myFragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();

    }
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Preference, Context.MODE_PRIVATE);
                if(sharedPreferences.contains(Student_Name) && sharedPreferences.contains(Student_ID)){
                    TextView username = findViewById(R.id.profile_name);
                    username.setText(sharedPreferences.getString(Student_Name,"User")+"  ID : " + sharedPreferences.getString(Student_ID,"xxxxxxx"));
                }
                return true;
            }
        });
    }
}
