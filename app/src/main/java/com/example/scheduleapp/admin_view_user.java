package com.example.scheduleapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONObject;
import java.util.*;

public class admin_view_user extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword;
    Button btnUpdate, btnDelete, btnViewSchedule;
    int userId;
    String URL_GET = "http://10.0.2.2/schedule_api/get_user_info.php";
    String URL_UPDATE = "http://10.0.2.2/schedule_api/update_user.php";
    String URL_DELETE = "http://10.0.2.2/schedule_api/delete_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewSchedule = findViewById(R.id.btnViewSchedule);

        userId = getIntent().getIntExtra("user_id", -1);
        loadUserInfo();

        btnUpdate.setOnClickListener(v -> updateUser());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnViewSchedule.setOnClickListener(v -> {
            Intent i = new Intent(this, admin_view_schedule.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });
    }

    private void loadUserInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        edtName.setText(obj.getString("name"));
                        edtEmail.setText(obj.getString("email"));
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi server", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE,
                response -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // Trả kết quả về cho activity trước
                    setResult(RESULT_OK);
                    finish(); // Quay lại màn admin
                },
                error -> Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                m.put("name", name);
                m.put("email", email);
                m.put("password", password);
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }





    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá người dùng")
                .setMessage("Bạn có chắc muốn xoá người dùng này?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteUser())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteUser() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    Toast.makeText(this, "Đã xoá người dùng", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Lỗi xoá", Toast.LENGTH_SHORT).show()
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
