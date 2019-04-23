package com.example.attendance;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity {
    private DrawerLayout mDrrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mDrrawerLayout=findViewById(R.id.drawer);
        mToggle=new ActionBarDrawerToggle(this,mDrrawerLayout,R.string.open,R.string.close);
        mDrrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nvDrawer=findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment=null;
        Class FragmentClass;
        switch (menuItem.getItemId()){
            case R.id.profile :
                FragmentClass=Profile.class;
                break;
            case R.id.scanner :
                FragmentClass=Scanner.class;
                break;
            case R.id.logout:
                //Handle user clicking logout
                Toast.makeText(getApplicationContext(),"Logged out",Toast.LENGTH_SHORT).show();
                FragmentClass=Profile.class;
                break;
                default:
                    FragmentClass=Profile.class;
        }

        try {
            myFragment=(Fragment)FragmentClass.newInstance();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.f1content,myFragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrrawerLayout.closeDrawers();

    }
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }


}
