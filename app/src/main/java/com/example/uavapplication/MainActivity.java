package com.example.uavapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.alibaba.fastjson2.JSONObject;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.uavapplication.constant.MavLinkProtocol;
import com.example.uavapplication.constant.SpConstant;
import com.example.uavapplication.dialog.ConfirmDialog;
import com.example.uavapplication.dialog.DialogEvent;
import com.example.uavapplication.dialog.InputTextDialog;
import com.example.uavapplication.dialog.ListDialog;
import com.example.uavapplication.menu.MessageMenu;
import com.example.uavapplication.model.Command;
import com.example.uavapplication.model.UavEntity;
import com.example.uavapplication.model.UavVehicle;
import com.example.uavapplication.utils.CoordinateConversionUtils;
import com.example.uavapplication.utils.JsonUtils;
import com.example.uavapplication.utils.LatLngDeserializer;
import com.example.uavapplication.utils.ModeUtils;
import com.example.uavapplication.utils.SPUtils;
import com.example.uavapplication.utils.ToastUtils;
import com.example.uavapplication.view.BatteryView;
import com.example.uavapplication.view.CounterView;
import com.example.uavapplication.websocket.WebSocketEvent;
import com.example.uavapplication.websocket.WebSocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.navigation.NavigationView;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private RelativeLayout mainRelativeLayout;

    private RelativeLayout fullCameraView;

    private LinearLayout leftView;

    private Handler handler = new Handler();

    private TextView txt_altitude;

    private TextView txt_mode;

    private BatteryView horizontalBattery;

    private TextView txt_battery;

    private Button btn_lock;

    private Button btn_mode;

    private Button btn_takeOff;

    private Button btn_land;

    private Button btn_launch;

    private Button btn_startTask;

    private Button btn_zoom;

    private Button btn_light;

    private Button btn_left_act;

    private Button camera_back;

    private Button btn_camera_up;

    private Button btn_camera_down;

    private Button btn_camera_left;

    private Button btn_camera_right;

    private Button btn_cancel_camera_move;

    private Button btn_way_point;

    private Button camera_ip;

    private Intent bindIntent;

    private Handler wsHandler = new Handler();

    private WebSocketService webSocketService;

    private static final long HEART_BEAT_RATE = 60 * 1000;

    private int battery = 80;

    private static int lock = 0;
    // 0:上锁 1:解锁
    private static int light = 0; // 0:红外线 1:可见光

    private static final int MODE = 0, TAKEOFF = 1, DOUBLETAKEOFF = 2, LAND = 3, LAUNCH = 4, EXTTASK = 5, LOGOUT = 6, CAMERAIP = 7;

    private UavEntity uav;

    private List<LinkedHashMap<String, Object>> mapList;

    private List<LatLng> polyLinePoints;

    private PolylineOptions polylineOptions;

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LoadingDialog ld;

    private DrawerLayout drawLayout;

    private NavigationView navigationView;

    private PlayerView videoView;

    private PlayerView videoView2;

    private ExoPlayer player;

    private ExoPlayer player2;

    private boolean isFullScreen = false;

    private String url = "http://223.112.179.125:23081/hls/stream2.m3u8";

    private Uri videoUri;

    private MediaItem mediaItem;

    private boolean leftActFlag = true;

    private CounterView counterView;

    private UavVehicle uavVehicle;

    private Marker currentMarker;

    private LatLng latLng;

    private Bitmap originalBitmap;

    private Bitmap resizedBitmap;

    private BitmapDescriptor resizedDescriptor;

    private LatLng startPoint;

    private MarkerOptions options;

    private boolean zoomFlag = false;

    private int pitchValue; //俯仰角

    private int yawValue; // 偏航角

    private ActivityResultLauncher activityResultLauncher;

    private List<LatLng> taskPoints = new ArrayList<>();

    private boolean taskFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            initSDK(true);
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //String[]中可以添加多个权限，中间用逗号隔开，后面的数字对应着onRequestPermissionsResult()中的requestCode，用于判断申请了什么权限，可以自行更改。
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                initSDK(true);
            }
        }
        setContentView(R.layout.activity_main);
        uav = (UavEntity) getIntent().getExtras().get("uav");
        if (uav != null && uav.getIp() != null) {
            SPUtils.set(SpConstant.UAV, JsonUtils.toJson(uav), this);
            startWebSocketService();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawLayout.openDrawer(GravityCompat.START);
        //drawLayout.closeDrawer(GravityCompat.START);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(19.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.setMyLocationEnabled(true);
        mapCompass(true);
        initShowLines();
        mMapView.onResume();
        player = new ExoPlayer.Builder(this).build();
        videoView.setPlayer(player);
        String cameraIp = SPUtils.get("cameraIp", this);
        if (cameraIp != null) {
            videoUri = Uri.parse(cameraIp);
            mediaItem = MediaItem.fromUri(videoUri);
        }
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        if (isFullScreen) {
           /* Log.i(TAG, "onResume: ");
            mainRelativeLayout.setVisibility(View.GONE);
                *//*btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_right5, null));
                drawLayout.setVisibility(View.GONE);
                drawLayout.closeDrawer(GravityCompat.START);
                leftActFlag = true;*//*
            fullCameraView.setVisibility(View.VISIBLE);
            counterView.setMinValue(BigDecimal.ZERO);
            counterView.setMaxValue(new BigDecimal(100));
            counterView.setIncrement(BigDecimal.ONE);
            counterView.setDefaultValue(BigDecimal.valueOf(50));*/
        }
        Log.i(TAG, "onResume: " + "isFullScreen: " + isFullScreen);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                //如果没有获取到对应权限，就返回没有权限的提示，并结束应用
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
        }
        initSDK(true);
    }

    private void initView() {
        EventBus.getDefault().register(this);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addDeserializer(LatLng.class, new LatLngDeserializer());
                objectMapper.registerModule(module);

                try {
                    taskPoints = objectMapper.readValue(data.getStringExtra("polyLinePoints"), new TypeReference<List<LatLng>>() {
                    });
                    if (taskPoints != null && taskPoints.size() > 0) {
                        addTask(taskPoints);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Log.i(TAG, "initView: error" + e.getMessage());
                }
            }
        });

        mainRelativeLayout = findViewById(R.id.main_relative);

        txt_altitude = findViewById(R.id.txt_altitude);

        txt_mode = findViewById(R.id.txt_mode);

        horizontalBattery = findViewById(R.id.horizontalBattery);
        horizontalBattery.setPower(100);

        txt_battery = findViewById(R.id.txt_battery);

        fullCameraView = findViewById(R.id.full_camera_view);

        leftView = findViewById(R.id.left_action_view);
        leftView.setOnClickListener(this);

        btn_left_act = findViewById(R.id.left_action);
        btn_left_act.setOnClickListener(this);

        // 无人机操作
        btn_lock = findViewById(R.id.lockOrUnlock);
        btn_lock.setOnClickListener(this);

        btn_mode = findViewById(R.id.setMode);
        btn_mode.setOnClickListener(this);

        btn_takeOff = findViewById(R.id.takeOff);
        btn_takeOff.setOnClickListener(this);

        btn_land = findViewById(R.id.land);
        btn_land.setOnClickListener(this);

        btn_launch = findViewById(R.id.launch);
        btn_launch.setOnClickListener(this);

        btn_startTask = findViewById(R.id.startMission);
        btn_startTask.setOnClickListener(this);

        btn_way_point = findViewById(R.id.btn_way_point);
        btn_way_point.setOnClickListener(this);

        // 云台摄像机操作
        camera_back = findViewById(R.id.camera_back);
        camera_back.setOnClickListener(this);

        btn_camera_up = findViewById(R.id.camera_up);
        btn_camera_up.setOnClickListener(this);

        btn_camera_down = findViewById(R.id.camera_down);
        btn_camera_down.setOnClickListener(this);

        btn_camera_left = findViewById(R.id.camera_left);
        btn_camera_left.setOnClickListener(this);

        btn_camera_right = findViewById(R.id.camera_right);
        btn_camera_right.setOnClickListener(this);

        btn_cancel_camera_move = findViewById(R.id.btn_cancel_camera_move);
        btn_cancel_camera_move.setOnClickListener(this);

        btn_zoom = findViewById(R.id.zoom);
        btn_zoom.setOnClickListener(this);

        btn_light = findViewById(R.id.light);
        btn_light.setOnClickListener(this);

        camera_ip = findViewById(R.id.camera_ip);
        camera_ip.setOnClickListener(this);

        counterView = findViewById(R.id.zoom_counter_view);
        counterView.setMinValue(BigDecimal.ZERO);
        counterView.setMaxValue(new BigDecimal(100));
        counterView.setIncrement(BigDecimal.ONE);
        counterView.setDefaultValue(BigDecimal.valueOf(50));
        counterView.setOnValueChangedListener(new CounterView.OnValueChangedListener() {
            @Override
            public void onValueChanged(BigDecimal newValue) {
                Command command = new Command();
                command.setCommand(MavLinkProtocol.CAMERA_ZOOM);
                command.setParam1(2);
                command.setParam2(newValue.floatValue());
                sendMsg(MavLinkProtocol.COMMAND + "#" + JsonUtils.toJson(command));
            }
        });

        videoView = findViewById(R.id.videoPlayerView);
        videoView.setUseController(false);

        videoView2 = findViewById(R.id.videoPlayerView2);
        videoView2.setUseController(false);

        SPUtils.set("cameraIp", url, this);
        videoUri = Uri.parse(url);
        mediaItem = MediaItem.fromUri(videoUri);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullScreen();
            }
        });


        drawLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navigationView.setItemIconTintList(null); // 确保没有其他代码覆盖

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        mapList = new ArrayList<>();
        polyLinePoints = new ArrayList<>();
        mMapView = findViewById(R.id.bmapView);
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.showZoomControls(false);

        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i(TAG, "onMapLongClick: " + latLng.toString());
            }
        });
        //卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        ld = new LoadingDialog(this);
        ld.setLoadingText("地图加载中！").show();
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
           /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)videoView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            videoView.setLayoutParams(params);*/
            changeView(true);
            /*videoView.setX(0);
            videoView.setY(0);
            videoView.setTranslationX(0);
            videoView.setTranslationY(0);*/
            //videoView.setAlpha(0.8f);
        } else {
            changeView(false);
           /* videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            videoView.setAlpha(1.0f);*/
            if (player != null) {
                player.release();
                player = null;
            }
            if (player2 != null) {
                player2.release();
                player2 = null;
            }
            player2 = new ExoPlayer.Builder(this).build();
            videoView2.setPlayer(player2);
            String cameraIp = SPUtils.get("cameraIp", this);
            if (cameraIp != null) {
                videoUri = Uri.parse(cameraIp);
                mediaItem = MediaItem.fromUri(videoUri);
            }
            player2.setMediaItem(mediaItem);
            player2.prepare();
            player2.play();
        }
        isFullScreen = !isFullScreen;
        SPUtils.setBoolean(SpConstant.IS_FULL_SCREEN, isFullScreen, this);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_task1:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task2:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task3:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task4:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task5:
                ToastUtils.toast(this, item.getTitle().toString());
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("startPoint", startPoint);
                //startActivity(intent);
                activityResultLauncher.launch(intent);
                break;
            case R.id.nav_task6:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task7:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task8:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task9:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task10:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task11:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task12:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task13:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task14:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task15:
                ToastUtils.toast(this, item.getTitle().toString());
                break;
            case R.id.nav_task16:
                ConfirmDialog logoutDialog = new ConfirmDialog(MainActivity.this, LOGOUT, "退出", "确定退出登录？", false);
                logoutDialog.show();
                break;
            default:
                break;
        }
        runOnUiThread(() -> {
            btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_right5, null));
            drawLayout.closeDrawer(GravityCompat.START);
            drawLayout.setVisibility(View.GONE);
            leftActFlag = true;
        });
        return true;
    }

    private void initSDK(boolean status) {
        SDKInitializer.setAgreePrivacy(getApplicationContext(), status);
        try {
            SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(getApplicationContext());
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param event 接收消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WebSocketEvent event) {
        if (event != null) {
            String msg = event.getMessage();
            Log.i(TAG, "onMessageEvent: " + msg);
            String[] msgs = msg.split("#");
            if (msgs.length > 1) {
                if (msgs[0].equals(MessageMenu.VEHICLEINFO.value())) {
                    uavVehicle = JsonUtils.toBean(msgs[1], UavVehicle.class);
                    if (uavVehicle != null) {
                        if (polyLinePoints.size() >= 2) {
                            startPoint = new LatLng(polyLinePoints.get(0).latitude, polyLinePoints.get(0).longitude);
                            updateShowLines(uavVehicle);
                        } else {
                            if (uavVehicle.getVehicleLong() != null && uavVehicle.getVehicleLat() != null) {
                                JSONObject position = CoordinateConversionUtils.wgs84ToBd09(uavVehicle.getVehicleLong(), uavVehicle.getVehicleLat());
                                String strLat = position.getString("lat");
                                String strLng = position.getString("lng");
                                latLng = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
                                if (polyLinePoints.size() > 2) {
                                    zoomFlag = !zoomFlag;
                                }
                                runOnUiThread(() -> {
                                    txt_altitude.setText("飞行高度: " + uavVehicle.getVehicleAlt() + "m");
                                    horizontalBattery.setPower(uavVehicle.getVehicleSoc());
                                    txt_battery.setText(uavVehicle.getVehicleSoc() + "%");
                                    txt_mode.setText(ModeUtils.getMode(uavVehicle.getCustomMode()));
                                });
                            }
                            polyLinePoints.add(latLng);
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        int id = v.getId();
        switch (id) {
            case R.id.left_action_view:
            case R.id.left_action:
                runOnUiThread(() -> {
                    if (leftActFlag) {
                        btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_left, null));
                        drawLayout.setVisibility(View.VISIBLE);
                        drawLayout.openDrawer(GravityCompat.START);
                        mapCompass(false);
                    } else {
                        btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_right5, null));
                        drawLayout.setVisibility(View.GONE);
                        drawLayout.closeDrawer(GravityCompat.START);
                        mapCompass(true);
                    }
                });
                leftActFlag = !leftActFlag;
                break;
            case R.id.lockOrUnlock:
                if (lock == 0) {
                    lock = 1;
                    btn_lock.setText("解锁");
                } else {
                    lock = 0;
                    btn_lock.setText("上锁");
                }
                sendMsg("LOCKORUNLOCK#" + lock);
                break;
            case R.id.setMode:
                ListDialog listDialog = new ListDialog(MainActivity.this, MODE, "请选择模式", Arrays.asList("0:稳定模式(STABILIZE)", "3:自动模式(AUTO)", "4:引导模式(GUIDE)", "6:返航模式(RTL)", "9:降落模式(LAND)"));
                listDialog.show();
                break;
            case R.id.takeOff:
                ConfirmDialog takeOffDialog = new ConfirmDialog(MainActivity.this, TAKEOFF, "一键起飞", "确定起飞？", false);
                takeOffDialog.show();
                break;
            case R.id.land:
                ConfirmDialog landDialog = new ConfirmDialog(MainActivity.this, LAND, "一键降落", "确定降落？", false);
                landDialog.show();
                break;
            case R.id.launch:
                ConfirmDialog launchDialog = new ConfirmDialog(MainActivity.this, LAUNCH, "一键返航", "确定返航？", false);
                launchDialog.show();
                break;
            case R.id.startMission:
                if(taskPoints.size() > 0 ){
                    ConfirmDialog startMissionDialog = new ConfirmDialog(MainActivity.this, 11, "执行任务", "当前任务正在上报中....", true);
                    startMissionDialog.show();
                } else {
                    if(taskFlag){
                        ConfirmDialog startMissionDialog2 = new ConfirmDialog(MainActivity.this, EXTTASK, "执行任务", "确定执行任务？", false);
                        startMissionDialog2.show();
                    }
                }
                break;
            case R.id.btn_way_point:
                if (polyLinePoints != null && !polyLinePoints.isEmpty() && polyLinePoints.size() > 2) {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
                } else if (startPoint != null) {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(startPoint, 19.0f));
                }
                if (!leftActFlag) {
                    mapCompass(false);
                } else {
                    mapCompass(true);
                }
                break;
            case R.id.camera_back:
                changeView(true);
                if (player != null) {
                    player.release();
                    player = null;
                }
                videoUri = Uri.parse(SPUtils.get("cameraIp", this));
                mediaItem = MediaItem.fromUri(videoUri);
                player = new ExoPlayer.Builder(this).build();
                videoView.setPlayer(player);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();

                isFullScreen = !isFullScreen;
                break;
            case R.id.camera_up:
                camreaContral(5, 0);
                break;
            case R.id.camera_down:
                camreaContral(-5, 0);
                break;
            case R.id.camera_left:
                camreaContral(0, -5);
                break;
            case R.id.camera_right:
                camreaContral(0, 5);
                break;
            case R.id.btn_cancel_camera_move:
                camreaContral(0, 0);
                break;
            case R.id.light:
                if (light == 0) {
                    light = 1;
                    btn_light.setText("可见光");
                } else {
                    light = 0;
                    btn_light.setText("红外线");
                }
                break;
            case R.id.camera_ip:
                InputTextDialog cameraIpDialog = new InputTextDialog(MainActivity.this, CAMERAIP, "请输入IP地址");
                cameraIpDialog.show();
                break;
            default:
                break;
        }
    }

    public void sendMsg(String msg) {
        if (webSocketService != null &&
                webSocketService.client != null && webSocketService.client.isOpen()) {
            webSocketService.sendMsg(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void operationResult(DialogEvent event) {
        Log.i(TAG, "operationResult: " + event.getWhich());
        String resultValue = event.getResultValue();
        if (event.getWhich() == DialogAction.POSITIVE) {
            if (event.getCalledByViewId() == MODE) {
                sendMsg("MODE#" + resultValue.split(":")[0]);
                runOnUiThread(() -> {
                    SPUtils.set(SpConstant.UAVMODE, resultValue.split(":")[0], this);
                    txt_mode.setText(resultValue.split(":")[1]);
                });
            } else if (event.getCalledByViewId() == TAKEOFF) {
                InputTextDialog takeOffDialog = new InputTextDialog(MainActivity.this, DOUBLETAKEOFF
                        , "请输入起飞高度");
                takeOffDialog.setKeyboardType(8192);
                takeOffDialog.show();
            } else if (event.getCalledByViewId() == DOUBLETAKEOFF) {
                sendMsg("TAKEOFF#" + resultValue);
            } else if (event.getCalledByViewId() == LAND) {
                sendMsg("LAND#LAND");
            } else if (event.getCalledByViewId() == LAUNCH) {
                sendMsg("LAUNCH#LAUNCH");
            } else if (event.getCalledByViewId() == EXTTASK) {
                if (taskPoints != null && !taskPoints.isEmpty() && taskFlag == true) {
                    sendMsg("STARTMISSION#STARTMISSION");
                    taskFlag = false;
                }
            } else if (event.getCalledByViewId() == LOGOUT) {
                onDestroy();
                Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();
            } else if (event.getCalledByViewId() == CAMERAIP) {
                SPUtils.set("cameraIp", resultValue, this);
                if (player2 != null) {
                    player2.release();
                    player2 = null;
                }
                player2 = new ExoPlayer.Builder(this).build();
                videoView2.setPlayer(player2);
                String cameraIp = SPUtils.get("cameraIp", this);
                if (cameraIp != null) {
                    videoUri = Uri.parse(cameraIp);
                    mediaItem = MediaItem.fromUri(videoUri);
                }
                player2.setMediaItem(mediaItem);
                player2.prepare();
                player2.play();
            }
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // 解除注册
        EventBus.getDefault().unregister(this);
        // 对应程序强制退出后，数据没有及时显示以及摄像头无法再次开启问题  结束
        // ★清空消息队列, 移除对象外部类的引用
        if (bindIntent != null) {
            stopService(bindIntent);
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
        mMapView.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        if (player2 != null) {
            player2.release();
            player2 = null;
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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 服务与活动断开
            webSocketService = null;
        }
    };

    public void initShowLines() {
      /*  polyLinePoints.add(new LatLng(31.81743804181273, 119.99922600827112));
        polyLinePoints.add(new LatLng(31.817136413268564, 120.0004990653097));
        polylineOptions = new PolylineOptions().width(5).color(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .points(polyLinePoints).dottedLine(false).clickable(false).focus(false);
*/
        polylineOptions = new PolylineOptions().width(5).color(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)).dottedLine(false).clickable(false).focus(false);
        originalBitmap = BitmapDescriptorFactory.fromResource(R.drawable.drone_sm).getBitmap();
        // 调整Bitmap的大小
        int newWidth = 120; // 新的宽度
        int newHeight = 120; // 新的高度
        resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        // 创建新的BitmapDescriptor
        resizedDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

        startPoint = new LatLng((Double) 31.81743804181273, (Double) 119.99922600827112);
        MarkerOptions options = new MarkerOptions().icon(resizedDescriptor).position(startPoint).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(options);
        //mBaiduMap.addOverlay(polylineOptions);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(startPoint, 19.0f));
        ld.close();
        mMapView.setVisibility(View.VISIBLE);
    }

    public void changeView(boolean flag) {
        if (flag) {
            /*btn_lock.setVisibility(View.VISIBLE);
            btn_mode.setVisibility(View.VISIBLE);
            btn_takeOff.setVisibility(View.VISIBLE);
            btn_land.setVisibility(View.VISIBLE);
            btn_launch.setVisibility(View.VISIBLE);
            btn_startTask.setVisibility(View.VISIBLE);
            btn_camera_ctl.setVisibility(View.GONE);
            btn_zoom.setVisibility(View.GONE);
            btn_light.setVisibility(View.GONE);*/
            runOnUiThread(() -> {
                mainRelativeLayout.setVisibility(View.VISIBLE);
                if (!leftActFlag) {
                    btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_right5, null));
                    drawLayout.setVisibility(View.GONE);
                    drawLayout.closeDrawer(GravityCompat.START);
                    leftActFlag = true;
                }
                fullCameraView.setVisibility(View.GONE);
                mapCompass(true);
            });
        } else {
            /*btn_camera_ctl.setVisibility(View.VISIBLE);
            btn_zoom.setVisibility(View.VISIBLE);
            btn_light.setVisibility(View.VISIBLE);
            btn_lock.setVisibility(View.GONE);
            btn_mode.setVisibility(View.GONE);
            btn_takeOff.setVisibility(View.GONE);
            btn_land.setVisibility(View.GONE);
            btn_launch.setVisibility(View.GONE);
            btn_startTask.setVisibility(View.GONE);*/
            runOnUiThread(() -> {
                mainRelativeLayout.setVisibility(View.GONE);
                btn_left_act.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_left, null));
                drawLayout.setVisibility(View.GONE);
                drawLayout.closeDrawer(GravityCompat.START);
                leftActFlag = false;
                fullCameraView.setVisibility(View.VISIBLE);
                counterView.setMinValue(BigDecimal.ZERO);
                counterView.setMaxValue(new BigDecimal(100));
                counterView.setIncrement(BigDecimal.ONE);
                counterView.setDefaultValue(BigDecimal.valueOf(50));
            });
        }
    }

    public void updateShowLines(UavVehicle uavVehicle) {
        if (uavVehicle.getVehicleLong() != null && uavVehicle.getVehicleLat() != null) {
            JSONObject position = CoordinateConversionUtils.wgs84ToBd09(uavVehicle.getVehicleLong(), uavVehicle.getVehicleLat());
            String strLat = position.getString("lat");
            String strLng = position.getString("lng");
            latLng = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
        }
        if (currentMarker != null) {
            currentMarker.remove();
        }
        polyLinePoints.add(latLng);
        polylineOptions.points(polyLinePoints);
        options = new MarkerOptions().icon(resizedDescriptor).position(latLng).anchor(0.5f, 0.5f);
        mBaiduMap.clear();
        currentMarker = (Marker) mBaiduMap.addOverlay(options);
        mBaiduMap.addOverlay(polylineOptions);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
        if (zoomFlag) {
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
            zoomFlag = !zoomFlag;
        }
    }

    public void wayPoint() {
    }

    private void addTask(List<LatLng> list) {
        for (LatLng lng : list) {
            JSONObject jsonObject = CoordinateConversionUtils.bd09ToWgs84(lng.longitude, lng.latitude);
            sendMsg("ADDMISSION" + "#" + jsonObject.getString("lat") + "," + jsonObject.getString("lng"));
        }
        taskPoints.clear();
        taskFlag = true;
    }

    public void mapCompass(boolean flag) {
        mBaiduMap.setCompassEnable(true);
        Bitmap compassIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_compass_fill);
        mBaiduMap.setCompassIcon(compassIcon);
        // 获取屏幕宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 获取指南针图标的宽度
        int compassWidth = compassIcon.getWidth();
        if (flag) {
            // 计算指南针位置
            int x = screenWidth - compassWidth - 50; // 50 是额外的边距
            int y = screenHeight / 2;
            Log.i(TAG, "flag mapCompass: screenWidth: " + screenWidth + "  compassWidth: " + compassWidth + " x: " + x + " y: " + y);
            mBaiduMap.setCompassPosition(new Point(x, y - 240));
        } else {
            int x = screenWidth - compassWidth - 50; // 50 是额外的边距
            int y = screenHeight / 2;
            Log.i(TAG, "!flag mapCompass: screenWidth: " + screenWidth + "  compassWidth: " + compassWidth + " x: " + x + " y: " + y);
            mBaiduMap.setCompassPosition(new Point(x - 250, y - 240));
        }
    }

    public void camreaContral(float pitch, float yaw) {
        Command command = new Command();
        command.setCommand(1000);
        command.setParam1(Float.parseFloat("NaN"));
        command.setParam2(Float.parseFloat("NaN"));
        command.setParam3(pitch);
        command.setParam4(yaw);
        command.setParam5(16);
        sendMsg(MavLinkProtocol.COMMAND + "#" + JsonUtils.toJson(command));
    }
}