package com.example.scheduleapp;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.*;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    public ScheduleAdapter(Context context, List<Schedule> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Schedule schedule = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        tvTitle.setText(schedule.getTitle());
        tvDate.setText(schedule.getDate());
        tvTime.setText(schedule.getTime());

        return convertView;
    }
}
