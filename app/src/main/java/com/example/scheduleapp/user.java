package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class user extends AppCompatActivity {

    TextView tvGreeting, tvNextSchedule, tvStats;
    Button btnInfo, btnAddSchedule, btnViewSchedule, btnLogout;

    ImageButton btnChatAI;
    int userId;
    String userName;

    private static final int REQUEST_EDIT_INFO = 1;
    private static final int REQUEST_VIEW_SCHEDULE = 2;
    private static final int REQUEST_ADD_SCHEDULE = 3;
    private static final String API_URL = "http://10.0.2.2/schedule_api/get_schedule_summary.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvNextSchedule = findViewById(R.id.tvNextSchedule);
        tvStats = findViewById(R.id.tvStats);
        btnInfo = findViewById(R.id.btnInfo);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnViewSchedule = findViewById(R.id.btnViewSchedule);
        btnLogout = findViewById(R.id.btnLogout);
        btnChatAI = findViewById(R.id.btnChatAI);

        userId = getIntent().getIntExtra("user_id", -1);
        userName = getIntent().getStringExtra("user_name");

        if (userName == null || userName.isEmpty()) {
            userName = "User";
        }

        tvGreeting.setText("Xin chào, " + userName);
        fetchScheduleSummary();

        btnInfo.setOnClickListener(v -> {
            Intent i = new Intent(this, user_info.class);
            i.putExtra("user_id", userId);
            startActivityForResult(i, REQUEST_EDIT_INFO);
        });

        btnAddSchedule.setOnClickListener(v -> {
            Intent i = new Intent(this, add_schedule.class);
            i.putExtra("user_id", userId);
            startActivityForResult(i, REQUEST_ADD_SCHEDULE);
        });

        btnViewSchedule.setOnClickListener(v -> {
            Intent i = new Intent(this, view_schedule.class);
            i.putExtra("user_id", userId);
            startActivityForResult(i, REQUEST_VIEW_SCHEDULE);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("user_session", MODE_PRIVATE);
            pref.edit().clear().apply();
            startActivity(new Intent(this, login.class));
            finish();
        });

        // ⚡ Xử lý nút Chat với AI
        btnChatAI.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void fetchScheduleSummary() {
        String url = API_URL + "?user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject next = response.getJSONObject("next_schedule");
                        JSONObject stats = response.getJSONObject("stats");

                        String title = next.getString("title");
                        String date = next.getString("date");
                        String time = next.getString("time");

                        tvNextSchedule.setText("Lịch tiếp theo: " + title + " - " + time + " ngày " + date);
                        tvStats.setText("Tổng số lịch: " + stats.getInt("total") +
                                " | Hôm nay: " + stats.getInt("today") +
                                " | Tuần này: " + stats.getInt("week"));

                    } catch (Exception e) {
                        tvNextSchedule.setText("Không có lịch sắp tới");
                        tvStats.setText("");
                    }
                },
                error -> {
                    tvNextSchedule.setText("Lỗi khi tải lịch");
                    tvStats.setText("");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_INFO && resultCode == RESULT_OK) {
            String updatedName = data.getStringExtra("updated_name");
            if (updatedName != null && !updatedName.isEmpty()) {
                userName = updatedName;
                tvGreeting.setText("Xin chào, " + userName);
            }
        } else if ((requestCode == REQUEST_VIEW_SCHEDULE || requestCode == REQUEST_ADD_SCHEDULE)
                && resultCode == RESULT_OK) {
            fetchScheduleSummary();
        }
    }
}
