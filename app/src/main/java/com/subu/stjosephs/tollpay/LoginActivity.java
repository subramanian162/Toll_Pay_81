package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subu.stjosephs.tollpay.Objects.User;
import com.subu.stjosephs.tollpay.common_variables.Common;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    public String s_email,s_password;
    public FirebaseAuth mAuth;
    private ProgressBar sign_in_progress;
    public FirebaseUser user;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.sign_in_email_id);
        password = (EditText)findViewById(R.id.sign_in_password_id);
        sign_in_progress = (ProgressBar)findViewById(R.id.sign_in_progress_bar_id);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Common.user_type.equals("user"))
        {
            if(mAuth.getCurrentUser()!=null)
            {
                sign_in_progress.setVisibility(View.VISIBLE);
                myRef = FirebaseDatabase.getInstance().getReference("Users");
                start_email_check(myRef);
            }
        }
        else if(Common.user_type.equals("client"))
        {
            if(mAuth.getCurrentUser()!=null)
            {
                sign_in_progress.setVisibility(View.VISIBLE);
                myRef = FirebaseDatabase.getInstance().getReference("Clients");
                start_email_check(myRef);
            }
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

              if(task.isSuccessful())
              {

                  if(Common.user_type.equals("user"))
                  {
                      myRef = FirebaseDatabase.getInstance().getReference("Users");
                      email_check();
                  }
                  else if(Common.user_type.equals("client"))
                  {
                      myRef = FirebaseDatabase.getInstance().getReference("Clients");
                      email_check();
                  }
              }
              else
              {
                  Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

    public void email_check()
    {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int cross_check = 1;
                for(DataSnapshot email_snapshot : dataSnapshot.getChildren() )
                {
                    User user_mail = email_snapshot.getValue(User.class);

                    if(user_mail.getEmail_id().equals(null))
                    {
                        cross_check = 2;
                        sign_in_progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"The user branch has no users",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else if (user_mail.getEmail_id().equals(s_email))
                    {
                        cross_check = 3;
                        sign_in_progress.setVisibility(View.GONE);
                        finish();
                        Intent intent = new Intent(LoginActivity.this, HomeActivitty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }
                }
                sign_in_progress.setVisibility(View.GONE);
                if(cross_check==1)
                {
                    Toast.makeText(LoginActivity.this, "Enter the correct id", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void start_email_check(DatabaseReference  myref)
    {
        // Read from the database

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int cross_check = 1;
                for(DataSnapshot email_snapshot : dataSnapshot.getChildren() )
                {
                    User user_mail = email_snapshot.getValue(User.class);

                    if(user_mail.getEmail_id().equals(null))
                    {
                        cross_check = 2;
                                sign_in_progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"The user branch has no users",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else if (user_mail.getEmail_id().equals(mAuth.getCurrentUser().getEmail().toString().trim()))
                    {
                        cross_check = 3;
                        sign_in_progress.setVisibility(View.GONE);
                        finish();
                        Intent intent = new Intent(LoginActivity.this, HomeActivitty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }
                }
                sign_in_progress.setVisibility(View.GONE);
                if(cross_check==1)
                {
                    if(Common.user_type.equals("user"))
                    {
                        Toast.makeText(LoginActivity.this, "First Log out your client account ", Toast.LENGTH_SHORT).show();
                    }
                    else if(Common.user_type.equals("client"))
                    {
                        Toast.makeText(LoginActivity.this, "First Log out your user account ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void sign_up_activity(View view)
    {
        finish();
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }
}
