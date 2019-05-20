package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "ATTENDANCE_APP_PREFS";
    private static final int CAMERA_PERMISSION = 1;
    private static final int STORAGE_PERMISSION = 2;
    public final String Student_Name = "S_Name";
    public final String Student_ID = "S_ID";
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        //Retrieve Shared Preferences
        prefs = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean("isAdmin",false);
        //Check permissions
        if(checkAndRequestPermissions());
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
        ImageView pfp = headerView.findViewById(R.id.profile_pic);
        Fragment initialFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(isAdmin) {
            username.setText("Administrator");
            initialFragment = new GeneratorFragment();
        }
        else {
            //Disable scanning if user profile not created
            if(!(prefs.getBoolean("profileCreated", false))) {
                Menu menuNav = nvDrawer.getMenu();
                MenuItem scannerButton = menuNav.findItem(4);
                scannerButton.setVisible(false);
            }
            username.setText("User: " + prefs.getString(Student_Name, "N/A") + "\nID : " + prefs.getString(Student_ID, "N/A"));
            //load user pfp
            File pfpFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pfp.jpg");
            if (pfpFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(pfpFile.getAbsolutePath());
                pfp.setImageBitmap(myBitmap);
            }
            initialFragment = new ProfileFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.fcontent, initialFragment).addToBackStack(null).commit();
    }

    //Perform Menu Item Action
    public void onMenuItemClicked(MenuItem menuItem){
        Fragment myFragment = null;
        switch (menuItem.getItemId()){
            case 0:
                myFragment = new CourseManagementFragment();
                break;
            case 1:
                myFragment = new GeneratorFragment();
                break;
            case 2:
                //Change account status
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isAdmin", false);
                //Clear account profile
                editor.putString(Student_ID, null);
                editor.putString(Student_Name, null);
                //Delete pfp
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pfp.jpg");
                if(file.exists())
                    file.delete();
                //Enable new profile creation
                editor.putBoolean("profileCreated", false);
                //Commit changes & reload activity
                editor.commit();
                reload();
                Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                myFragment = new ProfileFragment();
                break;
            case 4 :
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivity(intent);
                break;
            case 5 :
                myFragment = new LoginFragment();
                break;
            case 6:
                myFragment=new Logs();
                break;
            default:
                myFragment = new ProfileFragment();
        }

        if(myFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //Clear Fragment Backstack
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //Set Fragment
            fragmentManager.beginTransaction().replace(R.id.fcontent, myFragment).addToBackStack(null).commit();
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    //Load Menu Items Based On Account Status
    public void loadMenuItems(NavigationView nv){
        Menu CustomMenu = nv.getMenu();
        if(isAdmin){
            CustomMenu.add(0,0,0,"Manage Courses");
            CustomMenu.add(0,1,0,"Generate QR code");
            CustomMenu.add(0,6,0,"Logs");
            CustomMenu.add(0,2,0,"Logout");

            CustomMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_create_course));
            CustomMenu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_take_attendance));
            CustomMenu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_logs));
            CustomMenu.getItem(3).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
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
    //Checks for camera permission
   @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
            case STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Please grant storage permission ", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
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


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
