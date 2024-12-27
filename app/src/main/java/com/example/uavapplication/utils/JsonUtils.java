package com.example.uavapplication.utils;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Json操作类：base on jackson <br>
 * Created by anders_lu on 2020/6/6
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonUtils() {
    }

    /**
     * 把JavaBean对象转换为Json字符串
     *
     * @param bean JavaBean
     * @return Json字符串
     */
    public static String toJson(Object bean) {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(bean);
        } catch (IOException ex) {
            Log.i("onServiceConnected: toJson", "toJson: " + ex.getMessage());
            ex.printStackTrace();
        }

        return jsonString;
    }

    /**
     * 根据Json字符串，转换为JavaBean对象
     *
     * @param jsonString Json字符串
     * @param clazz      指定Type.class
     * @param <T>        指定Type
     * @return JavaBean对象
     */
    public static <T> T toBean(final String jsonString, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, clazz);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return t;
    }

    /**
     * 根据指定的文件内容，转换指定的JavaBean对象
     *
     * @param file  new File("src/test/resources/json_car.json")
     * @param clazz 转换指定Type.clss
     * @param <T>   指定Type
     * @return JavaBean对象
     */
    public static <T> T toBean(File file, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(file, clazz);
        } catch (IOException ex) {
        }
        return t;
    }

    /**
     * 根据URL指定的文件内容，转换指定的JavaBean对象
     *
     * @param url   new URL("file:src/test/resources/json_car.json")
     * @param clazz 转换指定Type.clss
     * @param <T>   指定Type
     * @return JavaBean对象
     */
    public static <T> T toBean(URL url, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(url, clazz);
        } catch (IOException ex) {
        }
        return t;
    }

    public static <T> T toBean(Object object, Class<T> clazz) {
        T t = objectMapper.convertValue(object, clazz);
        return t;
    }
}
