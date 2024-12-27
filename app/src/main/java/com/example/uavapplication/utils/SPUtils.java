package com.example.uavapplication.utils;

import android.content.Context;

public class SPUtils {
    public static final String SHAREDPREFERENCES_KEY = "com.example.uavapplication";

    public static final String AUTHORIZATION = "Authorization";
    public static final String DEVICE_MAC = "Device_Mac";
    public static final String FACE_UPDATE_TIME = "Face_Update_Time";
    public static final String SERVER_HOST = "Server_Host";
    public static final String VERSION_CODE = "version_code";
    public static final String VERSION_ID = "version_id";
    public static final String FILE_PATH = "file_path";
    public static final String APP_NAME = "app_name";

    public static final String SP_DB_STATE = "SP_DB_STATE";

    public static void set(String key, String value, Context context) {
        context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }

    public static String get(String key, Context context) {
        return context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    public static void setBoolean(String key, boolean value, Context context) {
        context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static boolean getBoolean(String key, Context context) {
        return context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(key, false);
    }

    public static void setLong(String key, long value, Context context) {
        context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putLong(key, value)
                .apply();
    }

    public static long getLong(String key, Context context) {
        return context.getSharedPreferences(SHAREDPREFERENCES_KEY, Context.MODE_PRIVATE)
                .getLong(key, 0);
    }
}
