package com.example.uavapplication.http;
import android.content.Context;
import androidx.annotation.NonNull;
import com.example.uavapplication.model.Result;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Http 工具类
 */
public class HttpUtils {
    public static final int ERROR_LOGIN = -1;
    public static final int ERROR_NORMAL = 100;
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTAINERID = "containerId";
    public static final String CONTAINERTYPE = "containerType";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static final MediaType FILE
            = MediaType.get("File/*");
    private static OkHttpClient client;
    private static HttpUtils httpUtils;
    private String auth = null;

    private static OkHttpClient HttpClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static HttpUtils Instance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    public void get(String url, OnHttpCallback callback, int id, Context context) {
        Request request = new Request.Builder().url(url).get().build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e, id, ERROR_NORMAL);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                if (401 == (Integer) result.get("code")) {
                    callback.onFailure(new IOException("验证失败"), id, ERROR_LOGIN);
                } else {
                    callback.onResponse(result, id);
                }
            }
        });
    }
}