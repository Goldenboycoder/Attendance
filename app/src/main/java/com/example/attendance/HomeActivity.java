package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    public final String Student_Name = "S_Name";
    public final String Student_ID = "S_ID";
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve Shared Preferences
        prefs = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        isAdmin =  prefs.getBoolean("isAdmin",false);

        //Set up the navigation drawer
        NavigationView nvDrawer;
        setContentView(R.layout.activity_homescreen);
        mDrawerLayout = findViewById(R.id.drawer);
        nvDrawer = findViewById(R.id.nav_view);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadMenuItems(nvDrawer);
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onMenuItemClicked(menuItem);
                return true;
            }
        });

        //Set Header TextView + Load Initial Fragment
        View headerView = nvDrawer.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.profile_name);
        Fragment initialFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(isAdmin) {
            username.setText("Administrator");
            initialFragment = new GeneratorFragment();
        }
        else {
            username.setText("User: " + prefs.getString(Student_Name, "N/A") + "\nID : " + prefs.getString(Student_ID, "N/A"));
            initialFragment = new ScannerFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.fcontent, initialFragment).addToBackStack(null).commit();
    }

    //Perform Menu Item Action
    public void onMenuItemClicked(MenuItem menuItem){
        Fragment myFragment = null;
        switch (menuItem.getItemId()){
            case 0:
                myFragment = new CreateCourseFragment();
                break;
            case 1:
                myFragment = new GeneratorFragment();
                break;
            case 2:
                //Change account status
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isAdmin", false);
                //Clear Account Profile
                editor.putString(Student_ID, null);
                editor.putString(Student_Name, null);
                //Commit changes & reload activity
                editor.commit();
                reload();
                Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                myFragment = new ProfileFragment();
                break;
            case 4 :
                myFragment = new ScannerFragment();
                break;
            case 5 :
                myFragment = new LoginFragment();
                break;
            default:
                myFragment = new ProfileFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        //Clear Fragment Backstack
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //Set Fragment
        if(myFragment != null)
            fragmentManager.beginTransaction().replace(R.id.fcontent,myFragment).addToBackStack(null).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    //Load Menu Items Based On Account Status
    public void loadMenuItems(NavigationView nv){
        Menu CustomMenu = nv.getMenu();
        if(isAdmin){
            CustomMenu.add(0,0,0,"Create Course");
            CustomMenu.add(0,1,0,"Generate QR code");
            CustomMenu.add(0,2,0,"Logout");

            CustomMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_create_course));
            CustomMenu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_take_attendance));
            CustomMenu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
        }
        else{
            CustomMenu.add(0,3,0,"Profile");
            CustomMenu.add(0,4,0,"QR Code Scanner");
            CustomMenu.add(0,5,0,"Admin Access");

            CustomMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_profile));
            CustomMenu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_scanner));
            CustomMenu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_login));
        }
    }

    //Reload activity
    public void reload(){
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
