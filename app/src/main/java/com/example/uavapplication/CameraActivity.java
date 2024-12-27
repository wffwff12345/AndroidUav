package com.example.uavapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uavapplication.websocket.WebSocketService;


public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private WebSocketService webSocketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        webSocketService = (WebSocketService) getIntent().getExtras().get("");
        Log.i(TAG, "onCreate: " + webSocketService);
    }
}