package com.subu.stjosephs.tollpay;

import android.content.Intent;
import android.os.Bundle;
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

import com.subu.stjosephs.tollpay.adapters.CustomClientAdapter;
import com.subu.stjosephs.tollpay.adapters.CustomUserAdapter;
import com.subu.stjosephs.tollpay.common_variables.Common;

import java.util.Arrays;
import java.util.List;

public class HomeActivitty extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView home_list;
    private List<String> list;
    private String names[] = {"subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh",
            "subramanian","suresh","dinesh","ramesh","subramanian","suresh","dinesh","ramesh"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Common.user_type.equals("client"))
        {
            setContentView(R.layout.activity_client_home);

            //list view display the names inthe list view

            list = Arrays.asList(names);
            home_list = (ListView)findViewById(R.id.client_home_list_view);
            if(!list.isEmpty())
            {
                //   CustomUserAdapter customArrayAdapter = new CustomUserAdapter(HomeActivitty.this,names);
                CustomClientAdapter customClientAdapter = new CustomClientAdapter(HomeActivitty.this,list);

                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
                home_list.setAdapter(customClientAdapter);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "check the array", Toast.LENGTH_SHORT).show();
            }

        }
        else if(Common.user_type.equals("user"))
        {
            setContentView(R.layout.activity_home);

            list = Arrays.asList(names);
            home_list = (ListView)findViewById(R.id.home_list_view);
            if(!list.isEmpty())
            {
                  CustomUserAdapter customUserAdapter = new CustomUserAdapter(HomeActivitty.this,names);
               // CustomClientAdapter customClientAdapter = new CustomClientAdapter(HomeActivitty.this,list);

                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
                home_list.setAdapter(customUserAdapter);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "check the array", Toast.LENGTH_SHORT).show();
            }

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
