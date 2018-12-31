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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subu.stjosephs.tollpay.Objects.CrossedVehicle;
import com.subu.stjosephs.tollpay.Objects.UserVehicle;
import com.subu.stjosephs.tollpay.Objects.Vehicles_Entry;
import com.subu.stjosephs.tollpay.adapters.CustomClientAdapter;
import com.subu.stjosephs.tollpay.adapters.CustomUserAdapter;
import com.subu.stjosephs.tollpay.common_variables.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivitty extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<Vehicles_Entry> register_vehicles_list;
    Vehicles_Entry matched_vehicle_entry;
    FirebaseAuth mAuth;
    ListView home_list;
    DatabaseReference mRef;
    UserVehicle matched_user_Vehicle;
    List<UserVehicle> user_Vehicle_List;
    List<CrossedVehicle> client_list;
    String last_crossed_vehicle_from_toll;

    List<String> demo;
    private String names[] = {"subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        if(Common.user_type.equals("user"))
        {
            setContentView(R.layout.activity_home);
            user_Vehicle_List = new ArrayList<>();
            home_list = (ListView)findViewById(R.id.home_list_view);
        }
        else if(Common.user_type.equals("client"))
        {
            setContentView(R.layout.activity_client_home);
            client_list = new ArrayList<>();
            register_vehicles_list = new ArrayList<>();
            home_list = (ListView)findViewById(R.id.client_home_list_view);
        }

        //here we end the list view code snippeds

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(HomeActivitty.this,LoginActivity.class));
        }

        if(Common.user_type.equals("user"))
        {
            String Uid = null;

            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null)
            {
                Uid = user.getUid();
            }
                mRef.child("User_Vehicles")
                    .child(Uid)
                     .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user_Vehicle_List.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        UserVehicle userVehicle = snapshot.getValue(UserVehicle.class);
                        user_Vehicle_List.add(userVehicle);
                    }
                    CustomUserAdapter customUserAdapter = new CustomUserAdapter(HomeActivitty.this,user_Vehicle_List);
                    home_list.setAdapter(customUserAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
        else if(Common.user_type.equals("client"))
        {
                mRef.child("Vehicles_Number")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    List<String> check_number_list = new ArrayList<>();
                    check_number_list.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                         check_number_list.add(snapshot.getValue().toString());
                    }
                   //here we get the last crossed vehicle number from camera

                    last_crossed_vehicle_from_toll = check_number_list.get(check_number_list.size()-1);

                    //check one

                    get_all_register_vehicles_list();
                    //find_the_crossed_vehicle_match();
                    //update_the_amount();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void get_all_register_vehicles_list()
    {
            mRef.child("Vehicles_Entry")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        register_vehicles_list.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            Vehicles_Entry vehicles_entry = snapshot.getValue(Vehicles_Entry.class);
                            register_vehicles_list.add(vehicles_entry);
                        }
                        find_the_crossed_vehicle_match();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    public void find_the_crossed_vehicle_match()
    {
        for(int count = 0; count<register_vehicles_list.size();count++)
        {
            Vehicles_Entry inner_use = register_vehicles_list.get(count);
            //check for matching vehicles
            if(last_crossed_vehicle_from_toll.equals(inner_use.getVehicle_number()))
            {
                matched_vehicle_entry = inner_use;

                    mRef.child("User_Vehicles")
                        .child(inner_use.getUid())
                        .child(inner_use.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                matched_user_Vehicle = dataSnapshot.getValue(UserVehicle.class);
                                update_the_amount();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }
        }
    }
    public void update_the_amount()
    {
        // This for the amount update code

        switch (matched_user_Vehicle.getU_vehicle_type()) {
            case "Car": {
                Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                mRef.child("User_Vehicles")
                        .child(matched_vehicle_entry.getUid())
                        .child(matched_vehicle_entry.getKey())
                        .child("u_amount")
                        .setValue(Integer.toString(update_amount).trim());
                //check this part of below code
                CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_type(),
                        Integer.toString(200));
                mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                break;
            }
            case "Van": {
                Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                mRef.child("User_Vehicles")
                        .child(matched_vehicle_entry.getUid())
                        .child(matched_vehicle_entry.getKey())
                        .child("u_amount")
                        .setValue(Integer.toString(update_amount).trim());
                //check this part of below code
                CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_type(),
                        Integer.toString(200));
                mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                break;
            }
            case "Auto": {
                Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                mRef.child("User_Vehicles")
                        .child(matched_vehicle_entry.getUid())
                        .child(matched_vehicle_entry.getKey())
                        .child("u_amount")
                        .setValue(Integer.toString(update_amount).trim());
                //check this part of below code
                CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_type(),
                        Integer.toString(200));
                mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                break;
            }
            case "Lorry": {
                Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                mRef.child("User_Vehicles")
                        .child(matched_vehicle_entry.getUid())
                        .child(matched_vehicle_entry.getKey())
                        .child("u_amount")
                        .setValue(Integer.toString(update_amount).trim());
                //check this part of below code
                CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_type(),
                        Integer.toString(200));
                mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                break;
            }
        }
        demo.clear();
        demo.add(matched_user_Vehicle.getU_phone_number());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1
                                        ,demo);
        home_list.setAdapter(arrayAdapter);
    }

    //        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1
    //                        ,demo);
    //                        home_list.setAdapter(arrayAdapter);
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_a_vehicle) {
            // Handle the camera action
            startActivity(new Intent(getApplicationContext(),FormActivity.class));
        } else if (id == R.id.nav_home) {
            Toast.makeText(getApplicationContext(),"You are already in that page",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
