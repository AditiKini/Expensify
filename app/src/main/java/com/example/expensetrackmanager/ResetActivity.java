package com.example.expensetrackmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private Button forgetBtn;
    private EditText txtEmail;
    private String email;
    private TextView Login;

    private ProgressDialog mDialog;
    //Firebase

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mAuth=FirebaseAuth.getInstance();

        txtEmail=findViewById(R.id.email_reset);
        forgetBtn=findViewById(R.id.btn_Forget_Password);
        Login=findViewById(R.id.back_to_login);

        mDialog=new ProgressDialog(this);

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void validateData()
    {
        email=txtEmail.getText().toString().trim();
        String emailPattern = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(TextUtils.isEmpty(email))
        {
            txtEmail.setError("Email Required");
            return;
        }
        if(!email.matches(emailPattern))
        {
            txtEmail.setError("Email is Invalid");
            return;
        }
        else
        {
            mDialog.setMessage("Processing");
            mDialog.show();
            forgetpass();
        }


    }

    private void forgetpass()
    {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mDialog.dismiss();
                    Toast.makeText(ResetActivity.this,"Check your Email ",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetActivity.this,MainActivity.class));
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(ResetActivity.this,"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}