package com.example.uavapplication.utils;

import com.example.uavapplication.constant.COPTER_MODE;

import java.util.HashMap;
import java.util.Map;

public class ModeUtils {

    private static final Map<Integer, String> modeMap = new HashMap<>();

    static {
        modeMap.put(COPTER_MODE.COPTER_MODE_STABILIZE, "稳定模式(STABILIZE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_ACRO, "特技模式(ACRO)");
        modeMap.put(COPTER_MODE.COPTER_MODE_ALT_HOLD, "高度保持模式(ALTITUDEHOLD)");
        modeMap.put(COPTER_MODE.COPTER_MODE_AUTO, "自动模式(AUTO)");
        modeMap.put(COPTER_MODE.COPTER_MODE_GUIDED, "引导模式(GUIDE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_LOITER, "悬停模式(LOITER)");
        modeMap.put(COPTER_MODE.COPTER_MODE_RTL, "返航模式(RTL)");
        modeMap.put(COPTER_MODE.COPTER_MODE_CIRCLE, "圆周模式(CIRCLE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_LAND, "降落模式(LAND)");
        modeMap.put(COPTER_MODE.COPTER_MODE_DRIFT, "漂移模式(DRIFT)");
        modeMap.put(COPTER_MODE.COPTER_MODE_SPORT, "运动模式(SPORT)");
        modeMap.put(COPTER_MODE.COPTER_MODE_FLIP, "翻滚模式(FLIP)");
        modeMap.put(COPTER_MODE.COPTER_MODE_AUTOTUNE, "自动调谐模式(AUTOTUNE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_POSHOLD, "位置保持模式(POSHOLD)");
        modeMap.put(COPTER_MODE.COPTER_MODE_BRAKE, "刹车模式(BRAKE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_THROW, "投掷模式(THROW)");
        modeMap.put(COPTER_MODE.COPTER_MODE_AVOID_ADSB, "避让模式(AVOID_ADSB)");
        modeMap.put(COPTER_MODE.COPTER_MODE_GUIDED_NOGPS, "无GPS引导模式(GUIDED_NOGPS)");
        modeMap.put(COPTER_MODE.COPTER_MODE_SMART_RTL, "智能返航模式(SMART_RTL)");
        modeMap.put(COPTER_MODE.COPTER_MODE_FLOWHOLD, "流保持模式(FLOWHOLD)");
        modeMap.put(COPTER_MODE.COPTER_MODE_FOLLOW, "跟随模式(FOLLOW)");
        modeMap.put(COPTER_MODE.COPTER_MODE_ZIGZAG, "之字形模式(ZIGZAG)");
        modeMap.put(COPTER_MODE.COPTER_MODE_SYSTEMID, "系统识别模式(SYSTEMID)");
        modeMap.put(COPTER_MODE.COPTER_MODE_AUTOROTATE, "自旋模式(AUTOROTATE)");
        modeMap.put(COPTER_MODE.COPTER_MODE_AUTO_RTL, "自动返航模式(AUTO_RTL)");
    }

    public static String getMode(long mode) {
        return modeMap.getOrDefault((int) mode, "未知模式");
    }
}
