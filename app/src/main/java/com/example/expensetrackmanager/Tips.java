package com.example.expensetrackmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Tips extends AppCompatActivity {
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips);

        getSupportActionBar().setTitle("Tips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}

