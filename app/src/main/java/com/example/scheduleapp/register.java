package com.example.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import java.util.*;

public class register extends AppCompatActivity {
    EditText nameInput, emailInput, passwordInput;
    Button btnRegister;
    TextView linkToLogin;
    String URL = "http://10.0.2.2/schedule_api/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.btnRegister);
        linkToLogin = findViewById(R.id.linkToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String pw = passwordInput.getText().toString().trim();

            // Kiểm tra không để trống
            if (name.isEmpty() || email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra độ dài mật khẩu
            if (pw.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu đăng ký
            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        switch (response.trim()) {
                            case "success":
                                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, login.class));
                                break;
                            case "invalid_email":
                                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                                break;
                            case "empty_fields":
                                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                break;
                            case "short_password":
                                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                                break;
                            case "error":
                            default:
                                Toast.makeText(this, "Đăng ký thất bại. Thử lại sau!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối. Kiểm tra internet!", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", pw);
                    return map;
                }
            };
            Volley.newRequestQueue(this).add(request);
        });



        linkToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, login.class));
        });
    }
}