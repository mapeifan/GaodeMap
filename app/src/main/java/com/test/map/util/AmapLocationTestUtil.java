package com.test.map.util;


import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


/**
 * Created by FAN on 2017/5/8.
 */


public class AmapLocationTestUtil implements AMapLocationListener {
    public static AmapLocationTestUtil mAmapLocationTestUtil;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mlocationOption = null;
    Context context;

    public AmapLocationTestUtil(Context context) {
        this.context = context;
        mLocationClient = new AMapLocationClient(context);
        mlocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        mlocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mlocationOption.setGpsFirst(true);
        // 设置定位监听
        mLocationClient.setLocationListener(this);
        LogUtil.e("AmapLocationTestUtil init");
    }

    public static AmapLocationTestUtil getInstance(Context context) {
        if (mAmapLocationTestUtil == null) {
            mAmapLocationTestUtil = new AmapLocationTestUtil(context);
        }
        return mAmapLocationTestUtil;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            MapUtil.latLonPoint = MapUtil.getLatLonPoint(aMapLocation);
            MapUtil.LOCATION_PROVINCE = MapUtil.getProvince(aMapLocation);
            MapUtil.LOCATION_CITY = MapUtil.getCity(aMapLocation);
            if (null != MapUtil.latLonPoint) {
                MapUtil.latLon = MapUtil.convertToLatLng(MapUtil.latLonPoint);
            }
            LogUtil.e("loction__" + MapUtil.latLonPoint + "\n" + MapUtil.LOCATION_CITY + "\n" + MapUtil.latLon);
        }
    }

    public void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }
}

