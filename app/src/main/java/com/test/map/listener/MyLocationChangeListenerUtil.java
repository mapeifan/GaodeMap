package com.test.map.listener;

import android.content.Context;
import android.location.Location;

import com.amap.api.maps.AMap;
import com.test.map.util.LogUtil;
import com.test.map.util.MapUtil;

/**
 * Created by FAN on 2017/5/8.
 */

public class MyLocationChangeListenerUtil implements AMap.OnMyLocationChangeListener {
    public static MyLocationChangeListenerUtil myLocationChangeListenerUtil;
    private Context context;

    public MyLocationChangeListenerUtil(Context context) {
        this.context = context;
    }

    public static MyLocationChangeListenerUtil getInstance(Context context) {
        if (myLocationChangeListenerUtil == null) {
            myLocationChangeListenerUtil = new MyLocationChangeListenerUtil(context);
        }
        return myLocationChangeListenerUtil;
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {
            MapUtil.latLon = MapUtil.getLatLng(location);
            if (MapUtil.latLon != null) {
                MapUtil.latLonPoint = MapUtil.convertToLatLonPoint(MapUtil.latLon);
            }
            //  MapActivity.poiLatLng= MapUtil.latLon;
            LogUtil.e("amap  onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
        } else {
            LogUtil.e("amap 定位失败");
        }
    }

    /**
     * 以下内容为  onMyLocationChange 里填写，暂时不用
     *       Bundle bundle = location.getExtras();
     if(bundle != null) {
     int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
     String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
     // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
     int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
     Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
     } else {
     Log.e("amap", "定位信息， bundle is null ");
     }
     */
}
