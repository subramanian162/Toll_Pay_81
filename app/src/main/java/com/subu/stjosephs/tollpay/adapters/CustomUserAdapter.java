package com.subu.stjosephs.tollpay.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subu.stjosephs.tollpay.Objects.User_R_Vehicles;
import com.subu.stjosephs.tollpay.R;

import java.util.List;

public class CustomUserAdapter extends ArrayAdapter<User_R_Vehicles> {

    private Activity context;
    private List<User_R_Vehicles> userVehicleList;


    public CustomUserAdapter(Activity context1, List<User_R_Vehicles> list) {
        super(context1, R.layout.user_list_item_view,list);
        this.context = context1;
        this.userVehicleList = list;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();

        View client_view = layoutInflater.inflate(R.layout.client_list_item_view,null,true);

        //here we find the individual TextView id's


        TextView serial_number = client_view.findViewById(R.id.list_sn);
        TextView date = client_view.findViewById(R.id.list_date);
        TextView time = client_view.findViewById(R.id.list_time);
        TextView vehicle_number =  client_view.findViewById(R.id.list_number);
        TextView vehicle_amount =  client_view.findViewById(R.id.list_amount);
        TextView status = client_view.findViewById(R.id.list_status);

        //Here we assign the individual values to the TexxtView
        serial_number.setText(Integer.toString(position+1));
        vehicle_number.setText(userVehicleList.get(position).getU_vehicle_number());

        return client_view;
    }
}