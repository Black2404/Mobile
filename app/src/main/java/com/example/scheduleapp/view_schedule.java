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

public class view_schedule extends AppCompatActivity {

    ListView listView;
    TextView tvEmpty;
    EditText etSearch;
    ImageButton btnSearch;

    List<Schedule> scheduleList = new ArrayList<>();
    int userId;
    String LIST_URL = "http://10.0.2.2/schedule_api/list_schedule.php";
    String SEARCH_URL = "http://10.0.2.2/schedule_api/search_schedule.php";

    static final int REQUEST_EDIT_SCHEDULE = 100;
    boolean scheduleChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        listView = findViewById(R.id.listView);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        userId = getIntent().getIntExtra("user_id", -1);

        loadSchedules();

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchSchedule(keyword);
            } else {
                loadSchedules(); // nếu ô trống, hiển thị toàn bộ
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Schedule s = scheduleList.get(position);
            Intent i = new Intent(this, edit_schedule.class);
            i.putExtra("schedule_id", s.getId());
            startActivityForResult(i, REQUEST_EDIT_SCHEDULE);
        });
    }

    private void loadSchedules() {
        StringRequest request = new StringRequest(Request.Method.POST, LIST_URL,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        scheduleList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Schedule s = new Schedule(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.getString("date"),
                                    obj.getString("time")
                            );
                            scheduleList.add(s);
                        }

                        updateListView();

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

    private void searchSchedule(String keyword) {
        StringRequest request = new StringRequest(Request.Method.POST, SEARCH_URL,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        scheduleList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Schedule s = new Schedule(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.getString("date"),
                                    obj.getString("time")
                            );
                            scheduleList.add(s);
                        }

                        updateListView();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("user_id", String.valueOf(userId));
                m.put("keyword", keyword);
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateListView() {
        if (scheduleList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ScheduleAdapter adapter = new ScheduleAdapter(this, scheduleList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_SCHEDULE && resultCode == RESULT_OK) {
            scheduleChanged = true;
            loadSchedules();
        }
    }

    @Override
    public void finish() {
        if (scheduleChanged) {
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
