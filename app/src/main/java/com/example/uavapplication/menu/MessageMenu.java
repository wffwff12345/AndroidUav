package com.example.uavapplication.menu;

public enum MessageMenu {
    VEHICLEINFO("vehicleInfo"),
    CAMERAMOVE("cameraMove"), // 云平台转向
    CAMERAFOLLOW("cameraFollow"), // 云平台跟踪
    CAMERACONTROL("cameraControl"); // 云平台控制
    private final String value;

    MessageMenu(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }

}
