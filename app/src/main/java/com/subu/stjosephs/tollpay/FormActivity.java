package com.subu.stjosephs.tollpay;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.subu.stjosephs.tollpay.Objects.UserVehicle;
import com.subu.stjosephs.tollpay.Objects.Vehicles_Entry;

import java.util.Arrays;

public class FormActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    FirebaseAuth mAuth;
    private EditText editText_name;
    private EditText editText_vehicle_number;
    private EditText editText_phone_number;
    private EditText editText_amount;
    String form_name;
    String form_vehicle_number;
    String form_vehicle_type;
    String form_phone_number;
    String form_amount;
    String key;
    private String vehicle_type[] = {"Van","Car","Auto","Lorry"};
    Dialog dialog;
    private Button ok_btn;
    UserVehicle userVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //This is general text and button initializer

        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);
        editText_name =  findViewById(R.id.form_full_name_id);
        editText_vehicle_number = findViewById(R.id.form_vehicle_id);
        editText_phone_number = findViewById(R.id.form_phone_id);
        editText_amount = findViewById(R.id.form_amount_id);

        //This below line of code for the spinner  initializer

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


    public void pay()
    {
     FirebaseDatabase.getInstance().getReference()
     .child("Vehicles_Number")
     .push().setValue(editText_vehicle_number.getText().toString());
    }
    public void vehicleEntry()
    {
        Vehicles_Entry vehicles_entry = new Vehicles_Entry(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                form_vehicle_number,key);
        FirebaseDatabase.getInstance().getReference()
                .child("Vehicles_Entry")
                .push().setValue(vehicles_entry);
    }
    public void payAmount(View view)
    {
        pay();
        //Here we get the form details and wrap it in a single userVehicle object

        form_name = editText_name.getText().toString();
        form_vehicle_number = editText_vehicle_number.getText().toString();
        form_phone_number = editText_phone_number.getText().toString();
        form_amount = editText_amount.getText().toString();
        userVehicle = new UserVehicle(form_name,form_vehicle_number,form_vehicle_type,form_phone_number,form_amount);

        //Here we sent those wrapping userVehicle object in to firebase database under the User_Vehicles path.

        DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference()
                .child("User_Vehicles")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        key = mRef.push().getKey();

        vehicleEntry();

        mRef.child(key).setValue(userVehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    //This is used to display the custom on completion dialogue
                    dialog.setContentView(R.layout.pay_sucess_dialogue);
                    ok_btn =  dialog.findViewById(R.id.ok);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            editText_name.setText("");
                            editText_vehicle_number.setText("");
                            editText_amount.setText("");
                            editText_phone_number.setText("");
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });
    }

    //This is for the navigation button control method
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
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
