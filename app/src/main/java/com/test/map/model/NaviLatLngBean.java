package com.test.map.model;

import com.amap.api.navi.model.NaviLatLng;

/**
 * Created by FAN on 2017/5/11.
 */

public class NaviLatLngBean {
    /**
     * 导航起始坐标
     */
    private NaviLatLng startNaviLatLng;
    /**
     * 导航终点坐标
     */
    private NaviLatLng stopNaviLatLng;

    public NaviLatLngBean(NaviLatLng startNaviLatLng, NaviLatLng stopNaviLatLng) {
        this.startNaviLatLng = startNaviLatLng;
        this.stopNaviLatLng = stopNaviLatLng;
    }
    public NaviLatLng getStartNaviLatLng() {
        return startNaviLatLng;
    }

    public NaviLatLng getStopNaviLatLng() {
        return stopNaviLatLng;
    }

    @Override
    public String toString() {
        return "NaviLatLngBean{" +
                "startNaviLatLng=" + startNaviLatLng +
                ", stopNaviLatLng=" + stopNaviLatLng +
                '}';
    }
}
