package com.example.expensetrackmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mpass;
    private Button btnReg;
    private TextView mSignin;

    private ProgressDialog mDialog;

    //Show Password


    private android.widget.EditText edtPassword;

    //Password Pattern

    private static final Pattern PASSWORD_PATTERN=
            Pattern.compile("^"+
                    "(?=.*[0-9])"+      //at least 1 digit
                    "(?=.*[a-z])"+      //at least 1 lower case Letter
                    "(?=.*[A-Z])"+      //at least 1 upper case Letter
                    "(?=.*[@#%^&+=])"+  //at least 1 special case Letter
                    //"(?=\\s+$)"+        //no white spaces
                    ".{8,}"+            //at least 8 characters
                    "$");

    //Firebae..

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);
        registration();

        edtPassword = (EditText) findViewById(R.id.password_reg);

    }

    private void registration()
    {
        mEmail=findViewById(R.id.email_reg);
        mpass=findViewById(R.id.password_reg);
        btnReg=findViewById(R.id.btn_reg);
        mSignin=findViewById(R.id.signin_here);

        btnReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email=mEmail.getText().toString().trim();
                String pass=mpass.getText().toString().trim();
                String emailPattern = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email Required");
                    return;
                }
                if(!email.matches(emailPattern))
                {
                    mEmail.setError("Email is Invalid");
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    mpass.setError("Password Required");
                    return;
                }
                if(!PASSWORD_PATTERN.matcher(pass).matches())
                {
                    mpass.setError("Password Too Weak Include At least One Upper Case, Special Character & Number");
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        mSignin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}