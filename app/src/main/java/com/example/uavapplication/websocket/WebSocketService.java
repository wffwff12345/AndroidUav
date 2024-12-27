package com.example.uavapplication.websocket;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.uavapplication.constant.SpConstant;
import com.example.uavapplication.model.UavEntity;
import com.example.uavapplication.utils.JsonUtils;
import com.example.uavapplication.utils.SPUtils;
import com.example.uavapplication.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Serializable;
import java.net.URI;

public class WebSocketService extends Service implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static String TAG = "WebSocketService";
    /**
     * 保活
     */
    private final static int GRAY_SERVICE_ID = 1001;
    /**
     * 连接断开或者连接错误立即重连
     */
    private static final long CLOSE_RECON_TIME = 100;
    /**
     * 收发数据
     */
    public JWebSocketClient client;
    /**
     * activity和service之间通讯
     */
    private JWebSocketClientBinder binder = new JWebSocketClientBinder();
    private String authorization = "";
    private UavEntity uav;

    /**
     * activity和service之间通讯
     */
    public class JWebSocketClientBinder extends Binder implements Serializable{
        private static final long serialVersionUID = 1L;
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    /**
     * 灰色保活
     */
    public static class GrayInnerService extends Service implements Serializable {
        private static final long serialVersionUID = 1L;

        @SuppressLint("ForegroundServiceType")
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        initSocketClient();
        // 开启心跳检测, 进行长连接
        handler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        // 设置service为前台服务，提高优先级
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Android4.3以下 ，隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Android4.3 - Android8.0, 隐藏Notification上的图标
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        return START_STICKY;
    }

    /**
     * 初始化Socket
     */
    private void initSocketClient() {
        uav = JsonUtils.toBean(SPUtils.get(SpConstant.UAV, this), UavEntity.class);
        String websocketUrl = "ws://" + uav.getIp() + ":" + uav.getPort() + "/websocket/app/" + uav.getId();
        URI uri = URI.create(websocketUrl);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                // message就是接收到的消息
                Log.e(TAG, message);
                EventBus.getDefault().post(new WebSocketEvent(message));
            }

            /**
             * webSocket连接开启
             *
             * @param handShakeData
             */
            @Override
            public void onOpen(ServerHandshake handShakeData) {
                ToastUtils.toast(WebSocketService.this, "WebSocket连接成功");
                // 创建 Intent 并设置 FLAG_ACTIVITY_NEW_TASK
                Log.i(TAG, "WebSocket:" + handShakeData.getHttpStatusMessage());
            }

            /**
             * 连接断开
             *
             * @param code
             * @param reason
             * @param remote
             */
            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "onClose() 连接断开_reason：" + reason);

                handler.removeCallbacks(heartBeatRunnable);
                // 开启心跳检测
                handler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);
            }

            /**
             * 连接出错
             *
             * @param ex
             */
            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError() 连接出错：" + ex.getMessage());

                handler.removeCallbacks(heartBeatRunnable);
                // 开启心跳检测
                handler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);
            }
        };
        Log.e(TAG, "authorization_2：" + authorization);
        connect();
        Log.i(TAG, "initSocketClient: WebSocket connect");
    }

    /**
     * 连接WebSocket
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送消息
     */
    public void sendMsg(String msg) {
        if (null != client) {
            Log.i(TAG, "发送的消息：" + msg);
            try {
                client.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "Service onUnbind");
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    /**
     * 断开连接
     */
    public void closeConnect() {
        handler.removeCallbacks(heartBeatRunnable);
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    private static final long HEART_BEAT_RATE = 10 * 1000;
    private Handler handler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    Log.e(TAG, "心跳包检测WebSocket连接状态：已关闭");
                    reconnectWs();
                } else if (client.isOpen()) {
                    Log.d(TAG, "心跳包检测WebSocket连接状态：已连接");
                } else {
                    Log.e(TAG, "心跳包检测WebSocket连接状态：已断开");
                }
            } else {
                // 如果client已为空，重新初始化连接
                initSocketClient();
                Log.e(TAG, "心跳包检测WebSocket连接状态：client已为空，重新初始化连接");
            }
            // 每隔一定的时间，对长连接进行一次心跳检测
            handler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        handler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
