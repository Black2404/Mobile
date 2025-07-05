package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;
import com.example.scheduleapp.Userr;

public class admin extends AppCompatActivity {

    GridView gridView;
    TextView tvGreeting;
    Button btnLogout;
    List<Userr> userList = new ArrayList<>();
    String LIST_USERS_URL = "http://10.0.2.2/schedule_api/list_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        gridView = findViewById(R.id.gridUsers);
        tvGreeting = findViewById(R.id.tvGreeting);
        btnLogout = findViewById(R.id.btnLogout);

        tvGreeting.setText("Xin chào, Admin");

        loadUsers();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Userr user = userList.get(position);
            Intent i = new Intent(this, admin_view_user.class);
            i.putExtra("user_id", user.getId());
            i.putExtra("user_name", user.getName());
            startActivityForResult(i, 123); // Bắt kết quả sau khi chỉnh sửa/xoá
        });

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply();
            Intent i = new Intent(this, login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void loadUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, LIST_USERS_URL,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        userList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            userList.add(new Userr(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("email")
                            ));
                        }
                        GridUserAdapter adapter = new GridUserAdapter(this, userList);
                        gridView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi tải người dùng", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // Xử lý kết quả khi quay về từ admin_view_user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            // Nếu user đã cập nhật hoặc xoá → reload danh sách
            loadUsers();
        }
    }
}
