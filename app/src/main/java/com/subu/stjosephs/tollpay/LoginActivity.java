package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.subu.stjosephs.tollpay.common_variables.Common;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    public String s_email,s_password;
    public FirebaseAuth mAuth;
    private ProgressBar sign_in_progress;
    public FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.sign_in_email_id);
        password = (EditText)findViewById(R.id.sign_in_password_id);
        sign_in_progress = (ProgressBar)findViewById(R.id.sign_in_progress_bar_id);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Common.current_user = user;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Common.current_user!=null)
        {
            finish();
            startActivity(new Intent(LoginActivity.this,HomeActivitty.class));
        }
    }

    public void sign_in_user(View view)
    {
        s_email = email.getText().toString();
        s_password = password.getText().toString();

        if(s_email.isEmpty())
        {
            email.setError("Email is required.");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(s_email).matches())
        {
            email.setError("Please enter a valid email id");
            email.requestFocus();
            return;
        }
        if(s_password.isEmpty())
        {
            password.setError("Password is required.");
            password.requestFocus();
            return;
        }
        if(s_password.length() < 6)
        {
            password.setError("Minimum length of the password is 6");
            password.requestFocus();
            return;
        }

        sign_in_progress.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(s_email,s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              sign_in_progress.setVisibility(View.GONE);
              if(task.isSuccessful())
              {
                  finish();
                  Intent intent = new Intent(LoginActivity.this,HomeActivitty.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);
              }
              else
              {
                  Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

    public void sign_up_activity(View view)
    {
        finish();
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }
}
