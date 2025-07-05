package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class user_info extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword;
    Button btnUpdate, btnDelete;
    int userId;

    String GET_URL = "http://10.0.2.2/schedule_api/get_user_info.php";
    String UPDATE_URL = "http://10.0.2.2/schedule_api/update_user.php";
    String DELETE_URL = "http://10.0.2.2/schedule_api/delete_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        userId = getIntent().getIntExtra("user_id", -1);

        loadUserInfo();

        btnUpdate.setOnClickListener(v -> updateUserInfo());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá tài khoản này?")
                    .setPositiveButton("Xoá", (dialog, which) -> deleteUser())
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    private void loadUserInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        edtName.setText(obj.getString("name"));
                        edtEmail.setText(obj.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateUserInfo() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim(); // có thể rỗng

        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    // Gửi tên mới về user.java để hiển thị lại lời chào
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_name", name);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                error -> Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                m.put("name", name);
                m.put("email", email);
                m.put("password", password); // có thể rỗng
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void deleteUser() {
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL,
                response -> {
                    Toast.makeText(this, "Tài khoản đã được xoá", Toast.LENGTH_SHORT).show();

                    // Xoá session và quay về login
                    SharedPreferences pref = getSharedPreferences("user_session", MODE_PRIVATE);
                    pref.edit().clear().apply();

                    Intent i = new Intent(this, login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                },
                error -> Toast.makeText(this, "Lỗi khi xoá tài khoản", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
