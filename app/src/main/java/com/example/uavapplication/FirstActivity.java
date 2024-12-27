package com.example.uavapplication;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.uavapplication.constant.SpConstant;
import com.example.uavapplication.model.UavEntity;
import com.example.uavapplication.utils.JsonUtils;
import com.example.uavapplication.utils.SPUtils;
import com.example.uavapplication.websocket.WebSocketService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity";
    private Toolbar toolbar;
    private AlertDialog dialog;
    private Intent bindIntent;
    private Handler wsHandler = new Handler();
    private WebSocketService webSocketService;
    private static final long HEART_BEAT_RATE = 60 * 1000;
    private Handler handler = new Handler();
    private static List<UavEntity> uavList = new ArrayList<>(Arrays.asList(
            new UavEntity(1, "设备1", "223.112.179.125", "29031", 0),
            new UavEntity(2, "设备2", "10.1.2.45", "28080", 1)
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_first);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.main_toolbar);
        toolbar.getMenu().clear();
        addDevices(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                UavEntity uav = uavList.stream().filter(uavEntity -> uavEntity.getId() == item.getItemId()).collect(Collectors.toList()).get(0);
                intent.putExtra("uav", uav);
                startActivity(intent);
                finish();
                return true;
            }
        });
        invalidateOptionsMenu(); //
    }

    private void addDevices(Toolbar toolbar) {
        for (int i = 0; i < uavList.size(); i++) {
            MenuItem menuItem = toolbar.getMenu().add(0, uavList.get(i).getId(), i, uavList.get(i).getName());
            if (uavList.get(i).getStatus() == 1) {
                menuItem.setIcon(R.drawable.ic_status_red).setEnabled(false);
            } else {
                menuItem.setIcon(R.drawable.ic_status_green);
            }
        }
    }

    private void startWebSocketService() {
        bindIntent = new Intent(this, WebSocketService.class);
        startService(bindIntent);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);

        wsHandler.removeCallbacks(webSocketRunnable);
        // 开启心跳检测
        wsHandler.postDelayed(webSocketRunnable, HEART_BEAT_RATE);
    }

    /**
     * 每隔一段时间发送一次空消息, 保持长连接
     */
    private Runnable webSocketRunnable = new Runnable() {
        @Override
        public void run() {
            if (webSocketService != null &&
                    webSocketService.client != null && webSocketService.client.isOpen()) {
                webSocketService.sendMsg("app heartbeat......");
                SPUtils.set(SpConstant.SOCKET, JsonUtils.toJson(webSocketService), FirstActivity.this);
            }
            // 每隔一定的时间, 对长连接进行一次心跳检测
            wsHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };
    /**
     * 服务连接状态监听
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // 服务与活动成功绑定
            webSocketService = ((WebSocketService.JWebSocketClientBinder) iBinder).getService();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG, "onServiceConnected: webSocketService 333" + webSocketService);
            try {
                Log.i(TAG, "onServiceConnected: 222 null" + webSocketService == null ? "null" : "not null");
                Log.i(TAG, "onServiceConnected: 222 " + JsonUtils.toJson(webSocketService));
                SPUtils.set(SpConstant.SOCKET, JsonUtils.toJson(webSocketService), FirstActivity.this);
            } catch (Exception e) {
                Log.i(TAG, "onServiceConnected: 22 " + e.getMessage());
            }
            Log.i(TAG, "onServiceConnected: 11" + SPUtils.get(SpConstant.SOCKET, FirstActivity.this));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 服务与活动断开
            webSocketService = null;
        }
    };

    @Override
    protected void onDestroy() {
        if (bindIntent != null) {
            stopService(bindIntent);
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}