package com.example.uavapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity{
    private static final String TAG = "MapActivity";
//    private TextureMapView mMapView;
    private MapView mMapView;

    private BaiduMap mBaiduMap;
    private Toolbar toolbar;

    private Long patrolId;

    public static final int MAP_LINES = 1;

    private List<LinkedHashMap<String, Object>> mapList;

    private List<LatLng> points;

    private LoadingDialog ld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            initSDK(true);
            if(!Environment.isExternalStorageManager()){
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
        setContentView(R.layout.activity_map);

        patrolId = getIntent().getLongExtra("patrolOrder", 0l);

        initView();
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
        mapList = new ArrayList<>();
        points = new ArrayList<>();
        mMapView = findViewById(R.id.bmapView);
        mMapView.setVisibility(View.INVISIBLE);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        ld = new LoadingDialog(this);
        ld.setLoadingText("地图加载中！").show();
        initData();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
        mMapView.onResume();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void initData() {
        Log.e(TAG, "initData: ");
        showLines();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause: ");
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    public void showLines() {
        points.add(new LatLng(31.81743804181273, 119.99922600827112));
        points.add(new LatLng(31.817136413268564, 120.0004990653097));
        points.add(new LatLng(31.816554288475235, 120.00047010159739));
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(0xAAFF0000)
                .points(points).dottedLine(false).clickable(false).focus(false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
        BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
        LatLng point = new LatLng((Double) 31.81743804181273, (Double) 119.99922600827112);
        LatLng point2 = new LatLng((Double) 31.816554288475235, (Double) 120.00047010159739);
        MarkerOptions options = new MarkerOptions().icon(bitmapDescriptor).position(point);
        MarkerOptions options2 = new MarkerOptions().icon(bitmapDescriptor2).position(point2);
        mBaiduMap.addOverlay(options);
        mBaiduMap.addOverlay(options2);
        mBaiduMap.addOverlay(polylineOptions);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 16.0f));
        boolean isConnect = (point.latitude == point2.latitude && point.longitude == point2.longitude);
        Log.e(TAG, "showLines: " + isConnect);
        ld.close();
        mMapView.setVisibility(View.VISIBLE);
    }
    public void cleanMapCache() {
        if (mBaiduMap == null) {
            return;
        }
        int mapType = mBaiduMap.getMapType();
        if (mapType == BaiduMap.MAP_TYPE_NORMAL) {
            // // 清除地图缓存数据
            mBaiduMap.cleanCache(BaiduMap.MAP_TYPE_NORMAL);
        } else if (mapType == BaiduMap.MAP_TYPE_SATELLITE) {
            // 清除地图缓存数据
            mBaiduMap.cleanCache(BaiduMap.MAP_TYPE_SATELLITE);
        }
    }
}