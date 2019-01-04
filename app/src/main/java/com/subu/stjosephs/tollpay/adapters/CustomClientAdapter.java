package com.subu.stjosephs.tollpay.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subu.stjosephs.tollpay.Objects.CrossedVehicle;
import com.subu.stjosephs.tollpay.Objects.Vehicles_Entry;
import com.subu.stjosephs.tollpay.R;

import java.util.List;

public class CustomClientAdapter extends ArrayAdapter<CrossedVehicle>{

   private List<CrossedVehicle> vehicle_details;
   private Activity context;

    public CustomClientAdapter(Activity context1, List<CrossedVehicle> crossedVehicles) {
        super(context1,R.layout.client_list_item_view,crossedVehicles);
        this.context = context1;
        this.vehicle_details = crossedVehicles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View client_view = layoutInflater.inflate(R.layout.client_list_item_view,null,true);

        TextView serial_number = client_view.findViewById(R.id.client_list_serial_number);
        TextView vehicle_number =  client_view.findViewById(R.id.client_list_vehicle_number);
        TextView vehicle_amount =  client_view.findViewById(R.id.client_list_amount);

        serial_number.setText(Integer.toString(position));
        vehicle_number.setText(vehicle_details.get(position).getCrossed_vehicle_number());
        vehicle_amount.setText(context.getString(R.string.plus)+vehicle_details.get(position).getGetCrossed_vehicle_amount());

        return client_view;
    }
}
