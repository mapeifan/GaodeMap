package com.test.map.util;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;

/**
 * Created by FAN on 2017/5/8.
 */

public class MapUtil {
    public static final String Kilometer = "\u516c\u91cc";// "公里";
    public static final String Meter = "\u7c73";// "米";
    public static LatLonPoint latLonPoint = null; //经纬度
    public static LatLng latLon = null; //经纬度
    public static String LOCATION_PROVINCE = null; //所在省份
    public static String LOCATION_CITY = "深圳市"; //所在城市

    public static int PathPlanningMode = 0;  //路径规划模式 （默认、距离最短、时间最短、）

    public static String DRIVING_SINGLE_DEFAULT_TIME = " ";  // 默认的 规划路径 时间
    public static String DRIVING_SINGLE_DEFAULT_DISTANCE = " ";  // 默认的 规划路径 距离
    public static String DRIVING_SINGLE_SHORTEST_TIME = " ";  // 距离最短 规划路径 时间
    public static String DRIVING_SINGLE_SHORTEST_DISTANCE = " ";  // 距离最短 规划路径 距离
    public static String DRIVING_SINGLE_AVOID_CONGESTION_TIME = " ";  // 避免拥堵 规划路径 时间
    public static String DRIVING_SINGLE_AVOID_CONGESTION_DISTANCE = " ";  // 避免拥堵 规划路径 距离

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 根据定位结果返回  LatLonPoint
     *
     * @param
     * @return
     */
    public synchronized static LatLonPoint getLatLonPoint(AMapLocation location) {
        if (null == location) {
            return null;
        }
        latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        return latLonPoint;
    }

    /**
     * 根据定位结果返回  LatLng
     *
     * @param
     * @return
     */
    public synchronized static LatLng getLatLng(Location location) {
        if (null == location) {
            return null;
        }
        latLon = new LatLng(location.getLatitude(), location.getLongitude());
        return latLon;
    }

    /**
     * 根据定位结果返回  String City
     *
     * @param
     * @return
     */
    public synchronized static String getProvince(AMapLocation location) {
        if (null == location) {
            return null;
        }
        LOCATION_PROVINCE = location.getProvince();
        return LOCATION_PROVINCE;
    }

    /**
     * 根据定位结果返回  String Province
     *
     * @param
     * @return
     */
    public synchronized static String getCity(AMapLocation location) {
        if (null == location) {
            return null;
        }
        LOCATION_CITY = location.getCity();
        return LOCATION_CITY;
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 计算两点间距离 LatLng
     */
    public static String distanceLatLng(LatLng startLatLng, LatLng endLatLng) {
        double distanceMeter = AMapUtils.calculateLineDistance(startLatLng, endLatLng);  //得到两点间距离/米
        DecimalFormat df = new DecimalFormat("######0.00"); //去掉double 后面0.00  后面的
        String distanceKm = df.format(distanceMeter / 1000);  //化单位为千米
        return distanceKm;
    }

    /**
     * 这里用到的是 规划路径算路时间
     *
     * @param second
     * @return
     */
    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }


    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + Meter;
    }
}
