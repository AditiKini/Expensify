package com.example.expensetrackmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    //Fragment

    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    //Firebase

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final String KEY = "edittextValue";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.Profile) {
            Intent i1 = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(i1);
        }
        else if(id==R.id.About) {
            Intent i2 = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(i2);
        }
        else if(id==R.id.Expense_Graph) {
            Intent i3 = new Intent(getApplicationContext(),AnalyticsActivity.class);
            startActivity(i3);
        }
        else if(id==R.id.Income_Graph) {
            Intent i4 = new Intent(getApplicationContext(),IncomeAnalyticActivity.class);
            startActivity(i4);
        }
        else if(id==R.id.Share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String Body="Download this App";
            String s1="https://easyupload.io/bxbl5j";
            intent.putExtra(Intent.EXTRA_TEXT,Body);
            intent.putExtra(Intent.EXTRA_TEXT,s1);
            startActivity(Intent.createChooser(intent,"Share Using"));
        }
        else if(id==R.id.Tips){
            Intent i5=new Intent(getApplicationContext(),Tips.class);
            startActivity(this,HomeActivity.,Tips.class);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar =(Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Track Manager");
        setSupportActionBar(toolbar);

        //Firebase

        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser();

        bottomNavigationView=findViewById(R.id.bottomNavigationBar);
        frameLayout=findViewById(R.id.main_frame);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashBoardFragment=new DashBoardFragment();
        incomeFragment=new IncomeFragment();
        expenseFragment=new ExpenseFragment();

        setFragment(dashBoardFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        setFragment(dashBoardFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.blue_500);
                        return true;

                    case R.id.income:
                        setFragment(incomeFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.blue_500);
                        return true;

                    case R.id.expense:
                        setFragment(expenseFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.blue_500);
                        return true;

                    default:
                        return false;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);

        if(drawerLayout.isDrawerOpen(GravityCompat.END))
        {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else
        {
            super.onBackPressed();
        }

    }

    public void displaySelectedListener(int itemid)
    {
        Fragment fragment=null;

        switch (itemid)
        {
            case R.id.dashboard:
                fragment=new DashBoardFragment();
                break;

            case R.id.income:
                fragment=new IncomeFragment();
                break;

            case R.id.expense:
                fragment=new ExpenseFragment();
                break;

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

        }

        if(fragment!=null)
        {
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

    }

    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

}