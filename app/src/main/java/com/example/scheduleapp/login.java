package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;

public class login extends AppCompatActivity {
    EditText emailLogin, passwordLogin;
    Button btnLogin;
    TextView linkToRegister;
    String URL = "http://10.0.2.2/schedule_api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        linkToRegister = findViewById(R.id.linkToRegister);

        btnLogin.setOnClickListener(v -> {
            String em = emailLogin.getText().toString();
            String pw = passwordLogin.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            int id = obj.getInt("id");
                            String role = obj.getString("role");
                            String name = obj.getString("name");

                            // Lưu session
                            SharedPreferences pref = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("id", id);
                            editor.putString("role", role);
                            editor.putString("name", name);
                            editor.apply();

                            Intent intent;
                            if (role.equals("admin")) {
                                intent = new Intent(this, admin.class);
                            } else {
                                intent = new Intent(this, user.class);
                                intent.putExtra("user_id", id);
                                intent.putExtra("user_name", name);
                            }
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("VOLLEY_ERROR", "Volley Error: " + error.toString());
                        Toast.makeText(this, "Lỗi kết nối: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", em);
                    map.put("password", pw);
                    return map;
                }
            };
            Volley.newRequestQueue(this).add(request);
        });

        linkToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, register.class));
        });
    }
}
