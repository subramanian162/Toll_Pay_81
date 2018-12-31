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

import com.subu.stjosephs.tollpay.Objects.Vehicles_Entry;
import com.subu.stjosephs.tollpay.R;

import java.util.List;

public class CustomClientAdapter extends ArrayAdapter<Vehicles_Entry>{

   private List<Vehicles_Entry> client_details;
   private Activity context;

    public CustomClientAdapter(Activity context1, List<Vehicles_Entry> names) {
        super(context1,R.layout.client_list_item_view,names);
        this.context = context1;
        this.client_details = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View client_view = layoutInflater.inflate(R.layout.client_list_item_view,null,true);

        TextView vehicle_number = (TextView) client_view.findViewById(R.id.client_list_vehicle_number);

        vehicle_number.setText(client_details.get(position).getVehicle_number());

        return client_view;
    }
}
