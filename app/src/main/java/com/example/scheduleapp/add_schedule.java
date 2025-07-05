package com.example.scheduleapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;

public class add_schedule extends AppCompatActivity {

    EditText edtTitle, edtDate, edtTime, edtDescription;
    Button btnAdd;
    int userId;
    String ADD_URL = "http://10.0.2.2/schedule_api/add_schedule.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        edtTitle = findViewById(R.id.edtTitle);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        edtDescription = findViewById(R.id.edtDescription);
        btnAdd = findViewById(R.id.btnAdd);

        userId = getIntent().getIntExtra("user_id", -1);

        edtDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    edtDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        edtTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) ->
                    edtTime.setText(String.format("%02d:%02d", h, m)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        btnAdd.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String date = edtDate.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, ADD_URL,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("success")) {
                                Toast.makeText(this, "Thêm lịch thành công", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(this, "Lỗi: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Lỗi phản hồi JSON", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
            ) {
                protected Map<String, String> getParams() {
                    Map<String, String> m = new HashMap<>();
                    m.put("user_id", String.valueOf(userId));
                    m.put("title", title);
                    m.put("date", date);
                    m.put("time", time);
                    m.put("description", description);
                    return m;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}
