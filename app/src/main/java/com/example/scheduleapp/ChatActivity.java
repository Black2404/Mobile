package com.example.scheduleapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private Button sendButton;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> messageList = new ArrayList<>();

    private static final String API_KEY = "AIzaSyAfh_Vn9K8-y8IxrRE6k9hgfvc87NFN6dI";
    // Sử dụng mô hình gemini-2.0-flash
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);

        adapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Kiểm tra mô hình khả dụng khi khởi tạo
        fetchAvailableModels();

        sendButton.setOnClickListener(view -> {
            String userMessage = inputMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messageList.add(new ChatMessage(userMessage, true));
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                inputMessage.setText("");
                sendMessageToGemini(userMessage);
            }
        });
    }

    private void fetchAvailableModels() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                Scanner in = new Scanner(inputStream);
                StringBuilder response = new StringBuilder();
                while (in.hasNext()) {
                    response.append(in.nextLine());
                }
                in.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray models = jsonResponse.getJSONArray("models");
                    for (int i = 0; i < models.length(); i++) {
                        String modelName = models.getJSONObject(i).getString("name");
                        Log.d("AvailableModel", "Model: " + modelName);
                    }
                } else {
                    Log.e("APIError", "Error fetching models: " + response);
                }
            } catch (Exception e) {
                Log.e("APIError", "Exception: " + e.getMessage());
            }
        });
    }

    private void sendMessageToGemini(String message) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject userMessage = new JSONObject();
                JSONArray parts = new JSONArray();
                parts.put(new JSONObject().put("text", message));
                userMessage.put("parts", parts);
                contents.put(userMessage);
                requestBody.put("contents", contents);

                URL url = new URL(ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                Scanner in = new Scanner(inputStream);
                StringBuilder response = new StringBuilder();
                while (in.hasNext()) {
                    response.append(in.nextLine());
                }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String reply = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    runOnUiThread(() -> {
                        messageList.add(new ChatMessage(reply.trim(), false));
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    });
                } else {
                    runOnUiThread(() -> {
                        messageList.add(new ChatMessage("Gemini lỗi " + responseCode + ":\n" + response, false));
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    messageList.add(new ChatMessage("Không thể kết nối Gemini:\n" + e.getMessage(), false));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
            }
        });
    }
}