package com.example.uavapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
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
import com.example.uavapplication.dialog.ConfirmDialog;
import com.example.uavapplication.dialog.DialogEvent;
import com.example.uavapplication.utils.JsonUtils;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TaskActivity";

    private Button btn_task_backck;

    private Button btn_add_task;

    private Button btn_task_way_point;

    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private LoadingDialog ld;

    private LatLng startPoint;

    private PolylineOptions polylineOptions;

    private Bitmap originalBitmap;

    private Bitmap resizedBitmap;

    private BitmapDescriptor resizedDescriptor;

    private int ADDTASK = 101;

    private List<LatLng> polyLinePoints;

    private Marker currentMarker;

    private MarkerOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        LatLng latLng = (LatLng) getIntent().getExtras().get("startPoint");
        if (latLng != null) {
            startPoint = latLng;
        } else {
            startPoint = new LatLng(31.81743804181273, 119.99922600827112);
        }
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        btn_task_backck = findViewById(R.id.btn_task_back);
        btn_task_backck.setOnClickListener(this);

        btn_add_task = findViewById(R.id.btn_add_task);
        btn_add_task.setOnClickListener(this);

        btn_task_way_point = findViewById(R.id.btn_task_way_point);
        btn_task_way_point.setOnClickListener(this);

        mMapView = findViewById(R.id.bmapTaskView);
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.showZoomControls(false);
        polyLinePoints = new ArrayList<>();
        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i(TAG, "onMapLongClick: " + latLng.toString());
                updateShowLines(latLng);
            }
        });
        //卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        ld = new LoadingDialog(this);
        ld.setLoadingText("地图加载中！").show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(19.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mBaiduMap.setMyLocationEnabled(true);
        mapCompass(true);
        initShowLines();
        mMapView.onResume();
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_task_back:
                finish();
                break;
            case R.id.btn_add_task:
                ConfirmDialog addTaskDialog = new ConfirmDialog(this, ADDTASK, "添加任务", "确定添加任务？", true);
                addTaskDialog.show();
                //finish();
                break;
            case R.id.btn_task_way_point:
                if (polyLinePoints != null && !polyLinePoints.isEmpty() && polyLinePoints.size() > 2) {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
                } else if (startPoint != null) {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(startPoint, 19.0f));
                }
                mapCompass(true);
                break;
            default:
                break;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        mMapView.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void operationResult(DialogEvent event) {
        Log.i(TAG, "operationResult: " + event.getWhich());
        String resultValue = event.getResultValue();
        if (event.getWhich() == DialogAction.POSITIVE) {
            if (event.getCalledByViewId() == ADDTASK) {
                Intent intent = new Intent();
                if (polyLinePoints.size() > 2) {
                    intent.putExtra("polyLinePoints", JsonUtils.toJson(polyLinePoints));
                    setResult(TaskActivity.RESULT_OK, intent);
                }
                finish();
            }
        }
    }

    public void updateShowLines(LatLng latLng) {
        polyLinePoints.add(latLng);
        /*if (currentMarker != null) {
            currentMarker.remove();
        }*/
        options = new MarkerOptions().icon(resizedDescriptor).position(latLng).anchor(0.5f, 0.5f);
        //mBaiduMap.clear();
        //currentMarker = (Marker) mBaiduMap.addOverlay(options);
        if (polyLinePoints.size() == 1) {
            mBaiduMap.clear();
        } else if (polyLinePoints.size() > 2) {
            polylineOptions.points(polyLinePoints);
            mBaiduMap.addOverlay(polylineOptions);
        }
        mBaiduMap.addOverlay(options);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
        /*if (zoomFlag) {
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(polyLinePoints.get(0), 19.0f));
            zoomFlag = !zoomFlag;
        }*/
    }
}