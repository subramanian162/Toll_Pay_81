package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.subu.stjosephs.tollpay.Objects.Register_Vehicles;
import com.subu.stjosephs.tollpay.Objects.User_R_Vehicles;
import com.subu.stjosephs.tollpay.common_variables.Common;

import java.util.Arrays;
import java.util.Objects;

public class FormActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    protected EditText editText_vehicle_number;
    protected EditText editText_vehicle_state;
    protected EditText editText_vehicle_country;
    protected String form_vehicle_number;
    protected String form_vehicle_type;
    protected ProgressBar progressBar;
    private String vehicle_type[] = {"Light Motor Vehicles","Light Commercial Vehicles","Buses/Trucks","Multi-Axle Vehicles","Over sized Vehicles"};
    protected Button ok_btn;
    protected String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        key = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        ok_btn = findViewById(R.id.pay_btn);
        editText_vehicle_number = findViewById(R.id.form_vehicle_id);
        editText_vehicle_state = findViewById(R.id.form_vehicle_state);
        editText_vehicle_country = findViewById(R.id.form_vehicle_country);
        progressBar = findViewById(R.id.form_progress_bar_id);

        Spinner vehicle_type_spinner =  findViewById(R.id.form_vehicle_type_spinner_id);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item, Arrays.asList(vehicle_type));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_type_spinner.setAdapter(arrayAdapter);

        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                form_vehicle_type = parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.user_type.equals("user"))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    form_vehicle_number = editText_vehicle_number.getText().toString().trim();
                    String form_vehicle_state = editText_vehicle_state.getText().toString().trim();
                    String form_vehicle_country = editText_vehicle_country.getText().toString().trim();

                    if(!form_vehicle_number.equals("") && !form_vehicle_state.equals("") && !form_vehicle_country.equals(""))
                    {
                        User_R_Vehicles userVehicle = new User_R_Vehicles(form_vehicle_number,form_vehicle_type);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(key)
                                .child("User_R_Vehicles")
                                .push()
                                .setValue(userVehicle)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete())
                                        {
                                            Register_Vehicles register_vehicles =
                                                    new Register_Vehicles(form_vehicle_number,form_vehicle_type,key);
                                            FirebaseDatabase.getInstance().getReference("Register_Vehicles")
                                                    .push()
                                                    .setValue(register_vehicles)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isComplete())
                                                            {
                                                                editText_vehicle_number.setText("");
                                                                editText_vehicle_country.setText("");
                                                                editText_vehicle_state.setText("");
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(getApplicationContext(),"Your Vehicle is added Successfully",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else if(task.isCanceled())
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "You canceled the process", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(),"Try Later...",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Must Fill all details",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Common.user_type.equals("client"))
                {
                    Toast.makeText(getApplicationContext(),"This feature is not for you",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // This is for the toolbar initialisation code sniffet

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //This is for the navigation button control method
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_a_vehicle) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(),"You are already in that page",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_home) {
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
