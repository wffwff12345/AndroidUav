package com.example.uavapplication.http;

import android.content.Context;

public class Constant {
    public final static String API_URL = "http://223.112.179.125:29031";
    public final static String WEBSOCKET_API_URL = "223.112.179.125:29031";
    public final static String isLogin = "isLogin";
    public final static String APP_VERSION = "0.5";

    public enum API {
        UAV_LIST("/admin/uavVehicleInfo/new/connect/list"),
        LOGOUT("/logout");
        public final String url;

        public final String getUrl(Context context) {
            return API_URL + url;
        }

        API(String url) {
            this.url = url;
        }
    }

    public final static String SCHEDULE_STATUS_STANDBY = "0";
    public final static String SCHEDULE_STATUS_PREPARE = "1";
    public final static String SCHEDULE_STATUS_SURGERY = "2";
    public final static String SCHEDULE_STATUS_CLEAN = "3";

}
