package com.example.uavapplication.http;

import android.content.Context;

public class Constant {
    public final static String API_URL = "http://223.112.179.125:29031/";
    public final static String isLogin = "isLogin";
    public final static String APP_VERSION = "0.5";

    public enum API {
        LOGIN("/login"),
        GET_INFO("/getInfo"),
        APP_PATROL_POSITION("/app/patrol/position"),
        APP_PATROL_ADD("/app/patrol/order"),
        APP_PATROL_COMPLETE("/app/patrol/order/complete"),
        APP_DIAMETER("/app/diameter/type"),
        UPLOAD_FILE("/common/upload"),
        PATROL_ORDER("/app/patrol/order"),
        TROUBLE_REPORT("/app/trouble"),
        TROUBLE_REPORT_LIST("/app/trouble/list"),

        PATROL_ORDER_LIST("/app/patrol/order/list"),
        MAP_LINES("/green/patrolOrder/getMapOrderInfo/"),
        LOAD_DICTS("/system/dict/data/type/"),
        TELL_STAND("/app/tellStand"),
        END_TELL_STAND("/app/tellStand/end/"),
        TELL_STAND_LIST("/app/tellStand/list"),

        PROJECT_LIST("/app/project/allList"),
        PROJECT_PROCESS("/app/project/"),
        ADD_PROJECT_LOG("/app/project/addProjectLog"),
        PROJECT_LOG_LIST("/app/project/log/list/"),
        PROJECT_TASK_LOG_LIST("/app/project/task/process/"),
        FILL_TASK_PROCESS("/app/project/task/process"),

        GET_PROJECT_LIST("/app/project/list"),
        SAFE_CHECK("/app/project/safeCheck"),
        GET_SAFE_CHECK("/app/project/safeCheck/"),
        GET_RESPONSIBILITY("/app/project/responsibility"),
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
