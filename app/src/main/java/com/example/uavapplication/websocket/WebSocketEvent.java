package com.example.uavapplication.websocket;

public class WebSocketEvent {

    /**
     * 字符串消息
     */
    private String message;

    public WebSocketEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
