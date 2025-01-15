package com.example.uavapplication.model;


import java.util.HashMap;

public class Result<T> extends HashMap<String, Object> {
    public static final int SUCCESS = 200;

    private int code;
    private String status;
    private String msg;
    private String error;
    private T data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setBody(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getError() {
        return error;
    }

    public T getData() {
        return data;
    }
}
