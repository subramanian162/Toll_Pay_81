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
import com.google.firebase.database.FirebaseDatabase;
import com.subu.stjosephs.tollpay.Objects.User;
import com.subu.stjosephs.tollpay.common_variables.Common;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText new_password;
    private EditText confirm_password;
    private String s_email;
    private FirebaseAuth mAuth;
    private ProgressBar sign_up_progress;
    private User update_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.sign_up_email_id);
        new_password = findViewById(R.id.sign_up_password_id);
        confirm_password = findViewById(R.id.confirm_password_id);
        sign_up_progress = findViewById(R.id.sign_up_progress_bar_id);

        mAuth = FirebaseAuth.getInstance();

    }

    public void sign_up_user(View view)
    {
        s_email = email.getText().toString().trim();
        String s_password = new_password.getText().toString().trim();
        String c_password = confirm_password.getText().toString().trim();

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
            new_password.setError("Password is required.");
            new_password.requestFocus();
            return;
        }
        if(s_password.length() < 6)
        {
            new_password.setError("Minimum length of the password is 6");
            new_password.requestFocus();
            return;
        }
        if(!s_password.equals(c_password))
        {
            confirm_password.setError("Check the passwords");
            new_password.setError("Check the passwords");
            confirm_password.requestFocus();
            new_password.requestFocus();
            return;
        }

        sign_up_progress.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(s_email, s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    update_user  = new User(s_email);
                    if(Common.user_type.equals("client"))
                    {
                        FirebaseDatabase.getInstance().getReference("Clients")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(update_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sign_up_progress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Your Client account added successfully", Toast.LENGTH_LONG).show();
                                    finish();
                                    Intent intent = new Intent(SignUpActivity.this,HomeActivitty.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    //display a failure message
                                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else if(Common.user_type.equals("user"))
                    {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(update_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sign_up_progress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Your User account added successfully", Toast.LENGTH_LONG).show();
                                    finish();
                                    Intent intent = new Intent(SignUpActivity.this,HomeActivitty.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    //display a failure message
                                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sign_in_activity(View view)
    {
        finish();
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
    }
}
