package com.example.scheduleapp;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.List;

public class GridUserAdapter extends ArrayAdapter<Userr> {
    public GridUserAdapter(Context context, List<Userr> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Userr user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_card, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());

        return convertView;
    }
}
