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
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.uavapplication.constant.SpConstant;
import com.example.uavapplication.http.Constant;
import com.example.uavapplication.http.HttpUtils;
import com.example.uavapplication.http.OnHttpCallback;
import com.example.uavapplication.model.Result;
import com.example.uavapplication.model.UavEntity;
import com.example.uavapplication.model.UavVehicleInfo;
import com.example.uavapplication.utils.JsonUtils;
import com.example.uavapplication.utils.SPUtils;
import com.example.uavapplication.websocket.WebSocketService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FirstActivity extends AppCompatActivity implements OnHttpCallback {
    private static final String TAG = "FirstActivity";
    private static final int UAV_LIST = 1;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private Intent bindIntent;
    private Handler wsHandler = new Handler();
    private WebSocketService webSocketService;
    private static final long HEART_BEAT_RATE = 60 * 1000;
    private Handler handler = new Handler();
    private static List<UavEntity> uavList = new ArrayList<>(Arrays.asList(
            new UavEntity(1, "设备1-223", "223.112.179.125", "29031", 0),
            new UavEntity(2, "设备2-192", "192.168.1.45", "28080", 0),
            new UavEntity(3, "设备3-10", "10.1.2.45", "28080", 0)
    ));
    private int total = 0;
    List<UavVehicleInfo> options = new ArrayList<>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_first);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        HttpUtils.Instance().get(Constant.API.UAV_LIST.getUrl(this), this, UAV_LIST, this);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.main_toolbar);
        toolbar.getMenu().clear();
        //addDevices(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                UavVehicleInfo uav = options.stream().filter(uavVehicleInfo -> uavVehicleInfo.getVehicleId() == item.getItemId()).collect(Collectors.toList()).stream().findFirst().get();
                intent.putExtra("uav", uav);
                startActivity(intent);
                finish();
                return true;
            }
        });
        invalidateOptionsMenu(); //
    }

    private void addDevices(Toolbar toolbar) {
        toolbar.getMenu().clear();
        for (UavVehicleInfo vehicleInfo : options) {
            MenuItem menuItem = toolbar.getMenu().add(0, vehicleInfo.getVehicleId().intValue(), vehicleInfo.getVehicleId().intValue(), vehicleInfo.getVehicleName());
            if ("1".equals(vehicleInfo.getVehicleStatus())) {
                menuItem.setIcon(R.drawable.ic_status_green);
            } else {
                menuItem.setIcon(R.drawable.ic_status_red).setEnabled(false);
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

    @Override
    public void onFailure(IOException e, int id, int error) {

    }

    @Override
    public void onResponse(Result result, int id) {
        if (200 == (Integer) result.get("code")) {
            List<LinkedHashMap> data = (List<LinkedHashMap>) result.get("rows");
            if(options.size() > 0){
                options.clear();
            }
            if (data != null && data.size() > 0) {
                for (LinkedHashMap d : data) {
                    UavVehicleInfo order = JsonUtils.toBean(d, UavVehicleInfo.class);
                    options.add(order);
                }
                Log.i(TAG, "FirstActivity onResponse: " + options.toString());
                runOnUiThread(() -> {
                    addDevices(toolbar);
                });
            }
        }
    }
}