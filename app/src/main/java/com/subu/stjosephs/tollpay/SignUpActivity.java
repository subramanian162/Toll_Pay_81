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

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText mobile_number;
    private EditText email;
    private EditText new_password;
    private EditText confirm_password;

    private String s_email;
    private String s_name;
    private String s_mobile_number;
    protected String s_new_password;
    protected String s_confirm_password;
    private ProgressBar sign_up_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.sign_up_name_id);
        mobile_number = findViewById(R.id.sign_up_mobile_number_id);
        email = findViewById(R.id.sign_up_email_id);
        new_password = findViewById(R.id.sign_up_new_password_id);
        confirm_password = findViewById(R.id.sign_up_confirm_password_id);
        sign_up_progress = findViewById(R.id.sign_up_progress_bar_id);
    }

    //This method is for the sign_Up_a_new_user to our data base
    // And also collect the user details like name phone number and mail id and store it under User_details if
    // it is a user or else store it in the Client_details

    public void sign_up_user(View view)
    {
        s_name = name.getText().toString().trim();
        s_mobile_number = mobile_number.getText().toString().trim();
        s_email = email.getText().toString().trim();
        s_new_password = new_password.getText().toString().trim();
        s_confirm_password = confirm_password.getText().toString().trim();

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
        if(s_new_password.isEmpty())
        {
            new_password.setError("Password is required.");
            new_password.requestFocus();
            return;
        }
        if(s_new_password.length() < 6)
        {
            new_password.setError("Minimum length of the password is 6");
            new_password.requestFocus();
            return;
        }
        if(!s_new_password.equals(s_confirm_password))
        {
            confirm_password.setError("Check the passwords");
            new_password.setError("Check the passwords");
            confirm_password.requestFocus();
            new_password.requestFocus();
            return;
        }

        sign_up_progress.setVisibility(View.VISIBLE);

        //here we create an account for the new details which is enter by the user

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(s_email, s_new_password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    User update_user  = new User(s_name,s_mobile_number,s_email);
                    switch (Common.user_type) {
                        case "client":
                            FirebaseDatabase.getInstance().getReference("Clients")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .child("Client_Details")
                                    .setValue(update_user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sign_up_progress.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("Mail_ids")
                                                .child("Client_ids")
                                                .push()
                                                .setValue(s_email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                      if(task.isComplete())
                                                      {
                                                          FirebaseDatabase.getInstance().getReference().
                                                                  child("Clients")
                                                                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                  .child("Client_C_Vehicles")
                                                                  .setValue("");
                                                          FirebaseDatabase.getInstance().getReference("Clients")
                                                                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                  .child("Toll Entry")
                                                                  .setValue("");
                                                          Toast.makeText(SignUpActivity.this, "Your Client account added successfully", Toast.LENGTH_LONG).show();
                                                          Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                                          finish();
                                                          startActivity(intent);
                                                      }
                                                      else {
                                                          sign_up_progress.setVisibility(View.GONE);
                                                          Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                                                      }
                                                    }
                                                });
                                    } else {
                                        //display a failure message
                                        sign_up_progress.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            break;
                        case "user":
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .child("User_details")
                                    .setValue(update_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sign_up_progress.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("Mail_ids")
                                                .child("User_ids")
                                                .push()
                                                .setValue(s_email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isComplete())
                                                        {
                                                            FirebaseDatabase.getInstance().getReference("Users")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child("Amount")
                                                                    .setValue("0");
                                                            FirebaseDatabase.getInstance().getReference("Users")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child("User_C_Vehicles")
                                                                    .setValue("");

                                                            Toast.makeText(SignUpActivity.this, "Your User account added successfully", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                                            finish();
                                                            startActivity(intent);
                                                        }
                                                        else {
                                                            sign_up_progress.setVisibility(View.GONE);
                                                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        //display a failure message
                                        sign_up_progress.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            break;
                        default:
                            sign_up_progress.setVisibility(View.GONE);
                            finish();
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            Toast.makeText(SignUpActivity.this, "You are not a valid person", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                else
                {
                    sign_up_progress.setVisibility(View.GONE);
                    finish();
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    Toast.makeText(getApplicationContext(),Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_SHORT).show();
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
