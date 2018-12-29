package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void client(View view)
    {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
     public void user(View view)
     {
         startActivity(new Intent(MainActivity.this,LoginActivity.class));
     }
}
