package com.example.scheduleapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;

public class edit_schedule extends AppCompatActivity {

    EditText edtTitle, edtDate, edtTime, edtDescription;
    Button btnUpdate, btnDelete;
    int scheduleId;
    String GET_URL = "http://10.0.2.2/schedule_api/get_schedule_detail.php";
    String UPDATE_URL = "http://10.0.2.2/schedule_api/update_schedule.php";
    String DELETE_URL = "http://10.0.2.2/schedule_api/delete_schedule.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        edtTitle = findViewById(R.id.edtTitle);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        edtDescription = findViewById(R.id.edtDescription);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        edtDate.setInputType(InputType.TYPE_NULL);
        edtTime.setInputType(InputType.TYPE_NULL);

        edtDate.setOnClickListener(v -> showDatePicker());
        edtTime.setOnClickListener(v -> showTimePicker());

        scheduleId = getIntent().getIntExtra("schedule_id", -1);

        loadDetail();

        btnUpdate.setOnClickListener(v -> updateSchedule());
        btnDelete.setOnClickListener(v -> deleteSchedule());
    }

    private void loadDetail() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        edtTitle.setText(obj.getString("title"));
                        edtDate.setText(obj.getString("date"));
                        edtTime.setText(obj.getString("time"));
                        edtDescription.setText(obj.getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi tải chi tiết", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(scheduleId));
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateSchedule() {
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("success")) {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK); // Thêm để đồng bộ lại ở user
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi phản hồi JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(scheduleId));
                m.put("title", edtTitle.getText().toString());
                m.put("date", edtDate.getText().toString());
                m.put("time", edtTime.getText().toString());
                m.put("description", edtDescription.getText().toString());
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteSchedule() {
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL,
                response -> {
                    Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Thêm để đồng bộ lại ở user
                    finish();
                },
                error -> Toast.makeText(this, "Lỗi xóa", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(scheduleId));
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    edtDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    edtTime.setText(selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }
}
