package com.subu.stjosephs.tollpay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class FormActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private String vehicle_type[] = {"Van","Car","Auto","Lorry"};
    Dialog sucess_dialogue;
    Button ok_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        sucess_dialogue = new Dialog(this);


                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner vehicle_type_spinner = (Spinner) findViewById(R.id.form_vehicle_type_spinner_id);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item, Arrays.asList(vehicle_type));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_type_spinner.setAdapter(arrayAdapter);

        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),item,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void payAmount(View view)
    {
        sucess_dialogue.setContentView(R.layout.pay_sucess_dialogue);
        ok_btn = (Button) sucess_dialogue.findViewById(R.id.ok);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sucess_dialogue.dismiss();
            }
        });
        sucess_dialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sucess_dialogue.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_a_vehicle) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(),"You are already in that page",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_home) {
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivitty.class));

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
