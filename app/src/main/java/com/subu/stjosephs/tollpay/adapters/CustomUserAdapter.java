package com.subu.stjosephs.tollpay.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subu.stjosephs.tollpay.Objects.UserVehicle;
import com.subu.stjosephs.tollpay.R;

import java.util.List;

public class CustomUserAdapter extends ArrayAdapter<UserVehicle> {

    private Activity context;
    private List<UserVehicle> userVehicleList;


    public CustomUserAdapter(Activity context1, List<UserVehicle> list) {
        super(context1, R.layout.user_list_item_view,list);
        this.context = context1;
        this.userVehicleList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();

        View listItemView = layoutInflater.inflate(R.layout.user_list_item_view,null,true);

        //here we find the individual TextView id's

        TextView name = (TextView)listItemView.findViewById(R.id.item_name_id);
        TextView vehicle_number = (TextView)listItemView.findViewById(R.id.item_vehicle_number_id);
        TextView vehicle_type = (TextView) listItemView.findViewById(R.id.item_vehicle_type_id);
        TextView number = (TextView) listItemView.findViewById(R.id.item_phone_number_id);
        TextView amount = (TextView) listItemView.findViewById(R.id.item_rupees_id);

        //Here we assign the individual values to the TexxtView

        name.setText(userVehicleList.get(position).getU_name());
        vehicle_number.setText(userVehicleList.get(position).getU_vehicle_number());
        vehicle_type.setText(userVehicleList.get(position).getU_vehicle_type());
        number.setText(userVehicleList.get(position).getU_phone_number());
        amount.setText(userVehicleList.get(position).getU_amount());

        return listItemView;
    }
}