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
import com.subu.stjosephs.tollpay.Objects.User_C_Vehicles;
import com.subu.stjosephs.tollpay.R;


import java.util.List;

public class CustomClientAdapter extends ArrayAdapter<User_C_Vehicles>{

   private List<User_C_Vehicles> vehicle_details;
   private Activity context;

   public CustomClientAdapter(Activity context1, List<User_C_Vehicles> crossedVehicles) {
        super(context1,R.layout.client_list_item_view,crossedVehicles);
        this.context = context1;
        this.vehicle_details = crossedVehicles;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       LayoutInflater layoutInflater = context.getLayoutInflater();
        View client_view = layoutInflater.inflate(R.layout.client_list_item_view,null,true);

        TextView serial_number = client_view.findViewById(R.id.list_sn);
        TextView date = client_view.findViewById(R.id.list_date);
        TextView time = client_view.findViewById(R.id.list_time);
        TextView vehicle_number =  client_view.findViewById(R.id.list_number);
        TextView vehicle_amount =  client_view.findViewById(R.id.list_amount);
        TextView status = client_view.findViewById(R.id.list_status);

        serial_number.setText(Integer.toString(position+1));
        date.setText(vehicle_details.get(position).getDate());
        time.setText(vehicle_details.get(position).getTime());
        vehicle_number.setText(vehicle_details.get(position).getVehicle_number());
        vehicle_amount.setText(vehicle_details.get(position).getAmount());
        status.setText(vehicle_details.get(position).getStatus());
        return client_view;
    }
}
