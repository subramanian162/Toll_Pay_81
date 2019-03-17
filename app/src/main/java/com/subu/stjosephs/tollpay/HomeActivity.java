package com.subu.stjosephs.tollpay;

import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subu.stjosephs.tollpay.Objects.CrossedVehicle;
import com.subu.stjosephs.tollpay.Objects.Register_Vehicles;
import com.subu.stjosephs.tollpay.Objects.Toll_Entry;
import com.subu.stjosephs.tollpay.Objects.User_C_Vehicles;
import com.subu.stjosephs.tollpay.adapters.CustomClientAdapter;
import com.subu.stjosephs.tollpay.common_variables.Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected boolean vehicle_not_found;
    protected ListView home_list;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mRef;
    protected String current_user_key;
    protected List<User_C_Vehicles> user_Crossed_Vehicle_List;
    protected List<User_C_Vehicles> client_crossed_Vehicle_list;

    protected String client_id="DythdPJN14QZtHp7JMinarbzcwf2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        current_user_key = mAuth.getUid();

        if(Common.user_type.equals("user"))
        {
            setContentView(R.layout.activity_home);
            user_Crossed_Vehicle_List = new ArrayList<>();
            home_list = findViewById(R.id.home_list_view);
        }
        else if(Common.user_type.equals("client"))
        {
            setContentView(R.layout.activity_client_home);
            client_crossed_Vehicle_list = new ArrayList<>();
            home_list = findViewById(R.id.client_home_list_view);
        }

        //here we end the list view code snippets

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

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }


        final DatabaseReference toll_entry_reference = mRef.child("Clients")
                .child(client_id)
                .child("Toll Entry");

        toll_entry_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if new vehicles cross the toll this method is call
                final Toll_Entry tollEntry = dataSnapshot.getValue(Toll_Entry.class);

                if (tollEntry != null && tollEntry.getPayment_status().equals("Not Paid"))
                {
                    //If the user status is not paid mean then this lines of code is work
                    DatabaseReference register_vehicle_reference = mRef.child("Register_Vehicles");

                    register_vehicle_reference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //here we get all the user register vehicles

                            vehicle_not_found = true;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                final Register_Vehicles register_vehicles =
                                        snapshot.getValue(Register_Vehicles.class);
                                //Here we check the toll crossed vehicle number
                                // with the already registered user vehicle number

                                if (register_vehicles != null &&
                                        tollEntry.getToll_crossed_vehicle_from_camer().
                                                equals(register_vehicles.getU_vehicle_number())) {
                                    //If we get a matched vehicle number then the user will have an
                                    // sufficient amount to pay or not
                                    final DatabaseReference amount_ref = mRef.child("Users")
                                            .child(register_vehicles.getU_key())
                                            .child("Amount");

                                    vehicle_not_found=false;

                                    amount_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int amount = Integer.parseInt(dataSnapshot.getValue(String.class));


                                            switch (register_vehicles.getU_vehicle_type()) {
                                                case "Light Motor Vehicles":
                                                    if (amount >= 200) {
                                                        //if suffiecient amount is present mean then
                                                        // we will minus the amount
                                                        // and update the new amount to the database
                                                        //and then change the toll entry vehicle status
                                                        //as paid

                                                        amount_ref.setValue(Integer.toString(amount - 200));
                                                        //after updating the amount we need to update the payment status

                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Paid")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Calendar calendar = Calendar.getInstance();
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format =
                                                                        new SimpleDateFormat("dd-MMM-yyy");
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format =
                                                                        new SimpleDateFormat("hh:mm:ss");
                                                                String date = date_format.format(calendar.getTime());
                                                                String time = time_format.format(calendar.getTime());

                                                                final User_C_Vehicles user_c_vehicles =
                                                                        new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                                "200", "Paid");

                                                                mRef.child("Users")
                                                                        .child(register_vehicles.getU_key())
                                                                        .child("User_C_Vehicles")
                                                                        .push()
                                                                        .setValue(user_c_vehicles)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                mRef.child("Clients")
                                                                                        .child(client_id)
                                                                                        .child("Client_C_Vehicles")
                                                                                        .push()
                                                                                        .setValue(user_c_vehicles)
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(Common.user_type.equals("client"))
                                                                                                    display_client_list_items();
                                                                                                else if(Common.user_type.equals("user"))
                                                                                                    display_user_list_items();
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                        break;
                                                    } else {
                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Insufficient Amount");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Insufficient Amount");
                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);

                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    }
                                                case "Light Commercial Vehicles":
                                                    if (amount >= 200) {
                                                        //if suffiecient amount is present mean then
                                                        // we will minus the amount
                                                        // and update the new amount to the database
                                                        //and then change the toll entry vehicle status
                                                        //as paid

                                                        amount_ref.setValue(Integer.toString(amount - 200));
                                                        //after updating the amount we need to update the payment status

                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Paid");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Paid");

                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    } else {
                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Insufficient Amount");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Insufficient Amount");
                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);

                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    }
                                                case "Buses/Trucks":
                                                    if (amount >= 200) {
                                                        //if suffiecient amount is present mean then
                                                        // we will minus the amount
                                                        // and update the new amount to the database
                                                        //and then change the toll entry vehicle status
                                                        //as paid

                                                        amount_ref.setValue(Integer.toString(amount - 200));
                                                        //after updating the amount we need to update the payment status

                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Paid");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Paid");

                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    } else {
                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Insufficient Amount");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Insufficient Amount");
                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);

                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    }
                                                case "Multi-Axle Vehicles":
                                                    if (amount >= 200) {
                                                        //if suffiecient amount is present mean then
                                                        // we will minus the amount
                                                        // and update the new amount to the database
                                                        //and then change the toll entry vehicle status
                                                        //as paid

                                                        amount_ref.setValue(Integer.toString(amount - 200));
                                                        //after updating the amount we need to update the payment status

                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Paid");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Paid");

                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    } else {
                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Insufficient Amount");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Insufficient Amount");
                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);

                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    }
                                                case "Over sized Vehicles":
                                                    if (amount >= 200) {
                                                        //if suffiecient amount is present mean then
                                                        // we will minus the amount
                                                        // and update the new amount to the database
                                                        //and then change the toll entry vehicle status
                                                        //as paid

                                                        amount_ref.setValue(Integer.toString(amount - 200));
                                                        //after updating the amount we need to update the payment status

                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Paid");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Paid");

                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    } else {
                                                        toll_entry_reference.child(tollEntry.getToll_entry_key())
                                                                .child("payment_status")
                                                                .setValue("Insufficient Amount");

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyy");
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat time_format = new SimpleDateFormat("hh:mm:ss");
                                                        String date = date_format.format(calendar.getTime());
                                                        String time = time_format.format(calendar.getTime());

                                                        User_C_Vehicles user_c_vehicles =
                                                                new User_C_Vehicles(date, time, register_vehicles.getU_vehicle_number(),
                                                                        "200", "Insufficient Amount");
                                                        mRef.child("Users")
                                                                .child(register_vehicles.getU_key())
                                                                .child("User_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);

                                                        mRef.child("Clients").child(client_id)
                                                                .child("Client_C_Vehicles")
                                                                .push()
                                                                .setValue(user_c_vehicles);
                                                        break;
                                                    }
                                            }
                                            if(Common.user_type.equals("client"))
                                            {
                                                display_client_list_items();
                                            }
                                            else if(Common.user_type.equals("user"))
                                            {
                                                display_user_list_items();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                            if(vehicle_not_found)
                            {
                                if(Common.user_type.equals("client"))
                                Toast.makeText(getApplicationContext(), "The Last Crossed Vehicle "+
                                                tollEntry.getToll_crossed_vehicle_from_camer()+" is not" +
                                                " found in the data base",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    if(Common.user_type.equals("client"))display_client_list_items();
                    else if(Common.user_type.equals("user"))display_user_list_items();
                }
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

        if(Common.user_type.equals("user"))
        {
            display_user_list_items();
        }
        else if(Common.user_type.equals("client"))
        {
            display_client_list_items();
        }

    }

    public void display_user_list_items()
    {
        mRef.child("Users").child(mAuth.getCurrentUser().getUid())
                .child("User_C_Vehicles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user_Crossed_Vehicle_List.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            User_C_Vehicles user_c_vehicles = snapshot.getValue(User_C_Vehicles.class);
                            user_Crossed_Vehicle_List.add(user_c_vehicles);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        CustomClientAdapter adapter = new CustomClientAdapter(HomeActivity.this,user_Crossed_Vehicle_List);
        home_list.setAdapter(adapter);
    }

    public void display_client_list_items()
    {
        mRef.child("Clients").child(current_user_key)
                .child("Client_C_Vehicles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        client_crossed_Vehicle_list.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            User_C_Vehicles user_c_vehicles = snapshot.getValue(User_C_Vehicles.class);
                            client_crossed_Vehicle_list.add(user_c_vehicles);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        CustomClientAdapter adapter = new CustomClientAdapter(HomeActivity.this,client_crossed_Vehicle_list);
        home_list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_a_vehicle) {
            startActivity(new Intent(getApplicationContext(),FormActivity.class));
        } else if (id == R.id.nav_home) {
            Toast.makeText(getApplicationContext(),"You are already in that page",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {

            String new_key = Objects.requireNonNull(FirebaseDatabase.getInstance().
                    getReference("Clients")
                    .child(Objects.requireNonNull(client_id))
                    .child("Toll Entry")
                    .push().getKey());

            Toll_Entry tollEntry = new Toll_Entry("TN241637","Not Paid",new_key);

            FirebaseDatabase.getInstance().getReference("Clients")
                    .child(client_id)
                    .child("Toll Entry")
                    .child(new_key)
                    .setValue(tollEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete())
                        Toast.makeText(getApplicationContext(),"Entry added", Toast.LENGTH_SHORT).show();
                }
            });
        } else if(id == R.id.nav_share) {

            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"Your Authority Key is "+Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            startActivity(Intent.createChooser(i,"Choose One"));

        } else if(id == R.id.nav_logout) {
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.home_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.balance:
                Intent intent = new Intent(HomeActivity.this,AmountActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
