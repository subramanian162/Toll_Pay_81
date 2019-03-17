package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.subu.stjosephs.tollpay.common_variables.Common;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
