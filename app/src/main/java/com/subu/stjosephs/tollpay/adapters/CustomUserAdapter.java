package com.subu.stjosephs.tollpay.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subu.stjosephs.tollpay.R;

public class CustomUserAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] names;


    public CustomUserAdapter(Activity context1, String[] names) {
        super(context1, R.layout.user_list_item_view,names);
        this.context = context1;
        this.names = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View listItemView = layoutInflater.inflate(R.layout.user_list_item_view,null,true);
        TextView nameText = (TextView)listItemView.findViewById(R.id.item_name_id);
        nameText.setText(names[position]);
        return listItemView;
    }
}