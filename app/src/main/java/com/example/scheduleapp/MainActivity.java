package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("user_session", MODE_PRIVATE);
        String role = pref.getString("role", null);

        if (role != null) {
            if (role.equals("admin")) {
                startActivity(new Intent(this, admin.class));
            } else {
                Intent i = new Intent(this, user.class);
                i.putExtra("user_id", pref.getInt("id", -1));
                i.putExtra("user_name", pref.getString("name", ""));
                startActivity(i);
            }
        } else {
            startActivity(new Intent(this, login.class));
        }

        finish();
    }
}
