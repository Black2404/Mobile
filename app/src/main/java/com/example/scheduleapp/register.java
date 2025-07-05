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
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String pw = passwordInput.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        if (response.equals("success")) {
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, login.class));
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
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