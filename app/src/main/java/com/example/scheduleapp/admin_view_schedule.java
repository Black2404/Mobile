package com.example.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.*;

public class admin_view_schedule extends AppCompatActivity {

    ListView listView;
    TextView tvEmpty;
    List<Schedule> scheduleList = new ArrayList<>();
    int userId;
    String URL = "http://10.0.2.2/schedule_api/list_schedule.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_schedule);

        listView = findViewById(R.id.listView);
        tvEmpty = findViewById(R.id.tvEmpty); // TextView hiển thị khi rỗng
        userId = getIntent().getIntExtra("user_id", -1);

        loadSchedules();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Schedule s = scheduleList.get(position);
            Intent i = new Intent(this, edit_schedule.class);
            i.putExtra("schedule_id", s.getId());
            startActivity(i);
        });
    }

    private void loadSchedules() {
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        scheduleList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            scheduleList.add(new Schedule(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.getString("date"),
                                    obj.getString("time")
                            ));
                        }

                        if (scheduleList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            ScheduleAdapter adapter = new ScheduleAdapter(this, scheduleList);
                            listView.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi tải lịch", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("user_id", String.valueOf(userId));
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }
}
