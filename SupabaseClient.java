package com.example.wavemessenger;

import android.net.Uri;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;

public class SupabaseClient {
    private static final String TAG = "SupabaseClient";
    private final OkHttpClient client = new OkHttpClient();

    private final String SUPABASE_URL = "https://rlhgvpqgxvdoefdphcys.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJsaGd2cHFneHZkb2VmZHBoY3lzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc3OTA4NDQsImV4cCI6MjA3MzM2Njg0NH0.Toxl1KyO-Y3xF4RlRSyAtgW_jZBD3uyhaUG7V80RerM";

    // Data insert karne ka updated method (already correct from before)
    public void insertData(String category, double latitude, double longitude, String locationName, String mediaUrl, String accessToken) {
        JSONObject json = new JSONObject();
        try {
            json.put("category", category);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("location_name", locationName);
            json.put("media_url", mediaUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/reports")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Data inserted successfully: " + response.body().string());
                } else {
                    Log.e(TAG, "Failed to insert data: " + response.code() + " " + response.body().string());
                }
            } catch (IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
            }
        }).start();
    }

    // Media upload karne ka updated method (now accepts an accessToken parameter)
    public String uploadFile(File file, String bucketName, String fileName, String accessToken) throws IOException {
        String url = SUPABASE_URL + "/storage/v1/object/" + bucketName + "/" + fileName;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + accessToken) // Pass the access token here
                .addHeader("Content-Type", "multipart/form-data")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to upload file: " + response.code() + " " + response.body().string());
            }

            Log.d(TAG, "File uploaded successfully: " + response.body().string());
            return SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        }
    }
}