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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    List<Vehicles_Entry> summa;
    Vehicles_Entry vehicles_entryList;
    private FirebaseAuth mAuth;
    ListView home_list;
    List<UserVehicle> userVehicleList;
    List<String> client_list;
    List<String> temp;
    String c_vehicle;
    private String names[] = {"subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(Common.user_type.equals("user"))
        {
            setContentView(R.layout.activity_home);
            userVehicleList = new ArrayList<>();
        }
        else if(Common.user_type.equals("client"))
        {
            setContentView(R.layout.activity_client_home);
            client_list = new ArrayList<>();
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

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null)
            {
                Uid = user.getUid();
            }

            userVehicleList.clear();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                    .child("User_Vehicles")
                    .child(Uid);
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        UserVehicle userVehicle = snapshot.getValue(UserVehicle.class);
                        userVehicleList.add(userVehicle);
                    }
                    home_list = (ListView)findViewById(R.id.home_list_view);
                    CustomUserAdapter customUserAdapter = new CustomUserAdapter(HomeActivitty.this,userVehicleList);
                    home_list.setAdapter(customUserAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
        else if(Common.user_type.equals("client"))
        {
            client_list.clear();
            DatabaseReference number_Ref = FirebaseDatabase.getInstance().getReference()
                    .child("Vehicles_Number");
            number_Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    List<String> check_number_list = new ArrayList<>();
                    check_number_list.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                         check_number_list.add(snapshot.getValue().toString());
                    }
                    c_vehicle = check_number_list.get(check_number_list.size()-1);
                    get_all_vehicles();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void get_all_vehicles()
    {
        FirebaseDatabase.getInstance().getReference()
                .child("Vehicles_Entry")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        summa = new ArrayList<>();
                        summa.clear();

                      for(DataSnapshot snapshot : dataSnapshot.getChildren())
                      {
                          Vehicles_Entry vehicles_entry = snapshot.getValue(Vehicles_Entry.class);
                          summa.add(vehicles_entry);
                          if(c_vehicle.equals(vehicles_entry.getVehicle_number()))
                          {
                              vehicles_entryList = vehicles_entry;
                              break;
                          }
                      }
                          FirebaseDatabase.getInstance().getReference()
                                  .child("User_Vehicles")
                                  .child(vehicles_entryList.getUid())
                                  .child(vehicles_entryList.getKey())
                                  .addValueEventListener(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       UserVehicle userVehicle = dataSnapshot.getValue(UserVehicle.class);

                                          home_list = (ListView)findViewById(R.id.client_home_list_view);
                                          //  CustomClientAdapter customClientAdapter = new CustomClientAdapter(HomeActivitty.this
                                          // ,summa);
                                        //  ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,Arrays.asList(c_vehicle));


                                       if(userVehicle.getU_vehicle_type().equals("Car"))
                                       {
                                           List<UserVehicle> vList= new ArrayList<>();
                                           vList.clear();
                                           vList.add(userVehicle);
                                           CustomUserAdapter customUserAdapter = new CustomUserAdapter(HomeActivitty.this,vList);
                                           home_list.setAdapter(customUserAdapter);

                                           long amount = Long.parseLong(userVehicle.getU_amount());
                                           FirebaseDatabase.getInstance().getReference()
                                                   .child("User_Vehicles")
                                                   .child(vehicles_entryList.getUid())
                                                   .child(vehicles_entryList.getKey())
                                                   .child("u_amount")
                                                   .setValue(Long.toString(amount));
                                       }
                                      }
                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                      }
                                  });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

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
