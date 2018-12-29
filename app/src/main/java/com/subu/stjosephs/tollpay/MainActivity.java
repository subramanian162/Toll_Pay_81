package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.subu.stjosephs.tollpay.common_variables.Common;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }
    public void client(View view)
    {
        Common.user_type = "client";
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
     public void user(View view)
     {
         Common.user_type = "user";
         startActivity(new Intent(MainActivity.this,LoginActivity.class));
     }
}
