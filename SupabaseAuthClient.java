package com.example.wavemessenger;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SupabaseAuthClient {
    private static final String TAG = "SupabaseAuthClient";
    private final OkHttpClient client = new OkHttpClient();

    private final String SUPABASE_URL = "https://rlhgvpqgxvdoefdphcys.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJsaGd2cHFneHZkb2VmZHBoY3lzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc3OTA4NDQsImV4cCI6MjA3MzM2Njg0NH0.Toxl1KyO-Y3xF4RlRSyAtgW_jZBD3uyhaUG7V80RerM";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Callback interface for asynchronous network operations
    public interface AuthCallback {
        void onSuccess(String responseBody);
        void onFailure(String error);
    }

    public void signUpWithEmail(String email, String password, AuthCallback callback) {
        new Thread(() -> {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure("Error creating JSON body.");
                return;
            }

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/auth/v1/signup")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onFailure(responseBody);
                }
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }

    public void signInWithEmail(String email, String password, AuthCallback callback) {
        new Thread(() -> {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure("Error creating JSON body.");
                return;
            }

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onFailure(responseBody);
                }
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}