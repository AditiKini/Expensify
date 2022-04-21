package com.example.expensetrackmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mpass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;

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

    //Firebase

    private FirebaseAuth mAuth;

    private static int SPLASH_SCREEN = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        mDialog=new ProgressDialog(this);
        loginDetails();

        edtPassword = (EditText) findViewById(R.id.password_login);

        //Notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            String msg = "Failed";
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void loginDetails()
    {
        mEmail=findViewById(R.id.email_login);
        mpass=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.btn_login);
        mForgetPassword=findViewById(R.id.forget_password);
        mSignupHere=findViewById(R.id.signup_reg);

        btnLogin.setOnClickListener(new View.OnClickListener()
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

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        //Registration Activity

        mSignupHere.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

        //Reset Password Activity

        mForgetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));
            }
        });
    }
}