package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
            demo =new ArrayList<>();
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
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(HomeActivitty.this, LoginActivity.class));
        }

        if (Common.user_type.equals("user")) {
            String Uid = null;

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Uid = user.getUid();
            }
            mRef.child("User_Register_Vehicles")
                    .child(Uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user_Vehicle_List.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                UserVehicle userVehicle = snapshot.getValue(UserVehicle.class);
                                user_Vehicle_List.add(userVehicle);
                            }
                            CustomUserAdapter customUserAdapter = new CustomUserAdapter(HomeActivitty.this, user_Vehicle_List);
                            home_list.setAdapter(customUserAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

        }
        else if (Common.user_type.equals("client")) {

            mRef.child("Vehicles_Number")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            //here we find the last crossed vehicle number from the data base

                            last_crossed_vehicle_from_toll = dataSnapshot.getValue().toString();

                            //here we need to find the correct match vehicle root

                            mRef.child("Common_Vehicles_list")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                            {
                                                Vehicles_Entry vehicles_entry = snapshot.getValue(Vehicles_Entry.class);

                                                //check for matching vehicles from the User_Vehicles entry

                                                if(last_crossed_vehicle_from_toll.equals(vehicles_entry.getVehicle_number()))
                                                {

                                                    //this is for later use

                                                    matched_vehicle_entry = vehicles_entry;

                                                    mRef.child("User_Register_Vehicles")
                                                            .child(vehicles_entry.getUid())
                                                            .child(vehicles_entry.getKey())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                    //Here we find the matched user vehicle from the data base

                                                                    matched_user_Vehicle = dataSnapshot.getValue(UserVehicle.class);

                                                                    //here we update the amount details

                                                                    switch (matched_user_Vehicle.getU_vehicle_type()) {
                                                                        case "Car": {
                                                                            Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                                                                            mRef.child("User_Register_Vehicles")
                                                                                    .child(matched_vehicle_entry.getUid())
                                                                                    .child(matched_vehicle_entry.getKey())
                                                                                    .child("u_amount")
                                                                                    .setValue(Integer.toString(update_amount).trim());
                                                                            //check this part of below code
                                                                            CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_number(),
                                                                                    Integer.toString(200));
                                                                            mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                                                                            break;
                                                                        }
                                                                        case "Van": {
                                                                            Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                                                                            mRef.child("User_Register_Vehicles")
                                                                                    .child(matched_vehicle_entry.getUid())
                                                                                    .child(matched_vehicle_entry.getKey())
                                                                                    .child("u_amount")
                                                                                    .setValue(Integer.toString(update_amount).trim());
                                                                            //check this part of below code
                                                                            CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_number(),
                                                                                    Integer.toString(200));
                                                                            mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                                                                            break;
                                                                        }
                                                                        case "Auto": {
                                                                            Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                                                                            mRef.child("User_Register_Vehicles")
                                                                                    .child(matched_vehicle_entry.getUid())
                                                                                    .child(matched_vehicle_entry.getKey())
                                                                                    .child("u_amount")
                                                                                    .setValue(Integer.toString(update_amount).trim());
                                                                            //check this part of below code
                                                                            CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_number(),
                                                                                    Integer.toString(200));
                                                                            mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                                                                            break;
                                                                        }
                                                                        case "Lorry": {
                                                                            Integer update_amount = Integer.parseInt(matched_user_Vehicle.getU_amount()) - 200;
                                                                            mRef.child("User_Register_Vehicles")
                                                                                    .child(matched_vehicle_entry.getUid())
                                                                                    .child(matched_vehicle_entry.getKey())
                                                                                    .child("u_amount")
                                                                                    .setValue(Integer.toString(update_amount).trim());
                                                                            //check this part of below code
                                                                            CrossedVehicle crossedVehicle = new CrossedVehicle(matched_user_Vehicle.getU_vehicle_number(),
                                                                                    Integer.toString(200));
                                                                            mRef.child("Crossed_Vehicles").push().setValue(crossedVehicle);
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                    });
                        }
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

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
