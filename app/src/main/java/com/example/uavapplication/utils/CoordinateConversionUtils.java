package com.example.uavapplication.utils;


import com.alibaba.fastjson2.JSONObject;

public class CoordinateConversionUtils {
    private static final double EARTH_RADIUS = 6378137; // 地球半径，单位：米
    private static final double X_PI = Math.PI * 3000.0 / 180.0;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;

    /**
     * 将84坐标系（WGS84）转换为02坐标系（GCJ02）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject wgs84ToGcj02(double lng, double lat) {
        JSONObject result = new JSONObject();
        if (outOfChina(lng, lat)) {
            result.put("lng", lng);
            result.put("lat", lat);
            return result;
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = lat + dLat;
        double mgLng = lng + dLng;
        result.put("lng", mgLng);
        result.put("lat", mgLat);
        return result;
    }

    /**
     * 将02坐标系（GCJ02）转换为84坐标系（WGS84）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject gcj02ToWgs84(double lng, double lat) {
        JSONObject result = new JSONObject();
        if (outOfChina(lng, lat)) {
            result.put("lng", lng);
            result.put("lat", lat);
            return result;
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = lat + dLat;
        double mgLng = lng + dLng;
        result.put("lng", lng * 2 - mgLng);
        result.put("lat", lat * 2 - mgLat);
        return result;
    }

    /**
     * 将02坐标系（GCJ02）转换为百度坐标系（BD09）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject gcj02ToBd09(double lng, double lat) {
        JSONObject result = new JSONObject();
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        result.put("lng", bdLng);
        result.put("lat", bdLat);
        return result;
    }

    /**
     * 将百度坐标系（BD09）转换为02坐标系（GCJ02）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject bd09ToGcj02(double lng, double lat) {
        JSONObject result = new JSONObject();
        double x = lng - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gcjLng = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        result.put("lng", gcjLng);
        result.put("lat", gcjLat);
        return result;
    }

    /**
     * 将02坐标系（GCJ02）转换为腾讯坐标系（QQ）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject gcj02ToQQ(double lng, double lat) {
        JSONObject result = new JSONObject();
        double x = lng;
        double y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double qqLng = z * Math.cos(theta) + 0.00330;
        double qqLat = z * Math.sin(theta) + 0.00150;
        result.put("lng", qqLng);
        result.put("lat", qqLat);
        return result;
    }

    /**
     * 将腾讯坐标系（QQ）转换为02坐标系（GCJ02）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject qqToGcj02(double lng, double lat) {
        JSONObject result = new JSONObject();
        double x = lng - 0.00330;
        double y = lat - 0.00150;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gcjLng = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        result.put("lng", gcjLng);
        result.put("lat", gcjLat);
        return result;
    }


    /**
     * 将W84坐标系（Wgs84）转换为百度坐标系（BD09）
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经纬度
     */
    public static JSONObject wgs84ToBd09(double lng, double lat) {
        JSONObject result = new JSONObject();
        if (outOfChina(lng, lat)) {
            result.put("lng", lng);
            result.put("lat", lat);
            return result;
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = lat + dLat;
        double mgLng = lng + dLng;

        double z = Math.sqrt(mgLng * mgLng + mgLat * mgLat) + 0.00002 * Math.sin(mgLat * X_PI);
        double theta = Math.atan2(mgLat, mgLng) + 0.000003 * Math.cos(mgLng * X_PI);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        result.put("lng", bdLng);
        result.put("lat", bdLat);
        return result;
    }

    // 将百度坐标系（BD09）转换为W84坐标系（Wgs84）
    public static JSONObject bd09ToWgs84(double bdLat, double bdLon) {
        JSONObject gcj = bd09ToGcj02(bdLat, bdLon);
        return gcj02ToWgs84(gcj.getDouble("lng"), gcj.getDouble("lat"));
    }

    /**
     * 判断是否超出中国范围
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 是否超出中国范围
     */
    private static boolean outOfChina(double lng, double lat) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    /**
     * 转换经度
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 转换后的经度
     */
    private static double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * Math.PI) + 40.0 * Math.sin(lng / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * Math.PI) + 300.0 * Math.sin(lng / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 转换纬度
     *
     * @param lat 纬度
     * @return 转换后的纬度
     */
    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320 * Math.sin(lat / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    /// <summary>
    /// 降度分秒格式经纬度转换为小数经纬度
    /// </summary>
    /// <param name="_Value">度分秒经纬度</param>
    /// <returns>小数经纬度</returns>
    public static double GPSTransforming(String _Value) {
        double Ret = 0.0;
        String[] TempStr = _Value.split("\\.");
        String x = TempStr[0].substring(0, TempStr[0].length() - 2);
        String y = TempStr[0].substring(TempStr[0].length() - 2, TempStr[0].length());
        String z = TempStr[1].substring(0, 5);
        Double calc = Double.parseDouble(y) + Double.parseDouble(z) / 100000;
        Ret = Double.parseDouble(x) + calc / 60;
        return Ret;
    }

}
