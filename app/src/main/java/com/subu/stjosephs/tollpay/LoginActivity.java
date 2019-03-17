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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subu.stjosephs.tollpay.common_variables.Common;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    protected String s_email;
    protected String s_password;
    private ProgressBar sign_in_progress;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.sign_in_email_id);
        password = findViewById(R.id.sign_in_password_id);
        sign_in_progress = findViewById(R.id.sign_in_progress_bar_id);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Common.user_type.equals("user"))
        {
            if(firebaseUser!=null)
            {
                sign_in_progress.setVisibility(View.VISIBLE);
                reference = "User_ids";
                start_email_check(reference);
            }
        }
        else if(Common.user_type.equals("client"))
        {
            if(firebaseUser!=null)
            {
                sign_in_progress.setVisibility(View.VISIBLE);
                reference = "Client_ids";
                start_email_check(reference);
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
                      reference = "User_ids";
                      start_email_check(reference);
                  }
                  else if(Common.user_type.equals("client"))
                  {
                      reference = "Client_ids";
                      start_email_check(reference);
                  }
              }
              else
              {
                  sign_in_progress.setVisibility(View.GONE);
                  Toast.makeText(getApplicationContext(),Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_SHORT).show();
              }
            }
        });
    }


    public void start_email_check(String  myref)
    {
        // Read from the database
        FirebaseDatabase.getInstance().getReference("Mail_ids")
                .child(myref)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int check = 1;
                        String current_user_mail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                        for(DataSnapshot email_snapshot : dataSnapshot.getChildren())
                        {
                            String email = email_snapshot.getValue(String.class);

                            if(email != null && email.equals(current_user_mail))
                            {
                                check++;
                                sign_in_progress.setVisibility(View.GONE);
                                finish();
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }
                        }
                        if(check==1)
                        {
                            if(Common.user_type.equals("user"))
                            {
                                sign_in_progress.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "You first logout the Client account", Toast.LENGTH_SHORT).show();
                            }
                            else if(Common.user_type.equals("client"))
                            {
                                sign_in_progress.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "You first logout the User account", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void sign_up_activity(View view)
    {
        finish();
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }
}
