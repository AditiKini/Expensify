package com.example.expensetrackmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    //Firebase

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final String KEY = "edittextValue";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        updateProfile();
    }

    private void updateProfile() {
        TextView UserMail =findViewById(R.id.userEmail);
        EditText Username =findViewById(R.id.username);
        Button editbtn = findViewById(R.id.edt_btn);

        UserMail.setText(currentUser.getEmail());

        Username.setText(getValue());

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = Username.getText().toString();
                saveFromEditText(str);
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });
    }

    private String getValue() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String savedValue = sharedPref.getString(KEY, "");

        return savedValue;
    }

    private void saveFromEditText(String text) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY, text);
        editor.apply();
    }

    public void logout(View view) {
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY,"User Name");
        editor.apply();
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

}
