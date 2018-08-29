package com.test.map.model;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by FAN on 2017/5/10.
 */

public class PoiInfoBean {


    /**
     * 地名
     */
    private String Poi_Name;
    /**
     * 详细地址
     */
    private String Poi_Address;

    /**
     * 与现在所处位置的距离
     */
    private String Poi_Distance;
    /**
     * 经纬度
     */
    private LatLonPoint latLonPoint;

    public PoiInfoBean(String poi_Name, String poi_Address, String poi_Distance, LatLonPoint latLonPoint) {
        Poi_Name = poi_Name;
        Poi_Address = poi_Address;
        Poi_Distance = poi_Distance;
        this.latLonPoint = latLonPoint;
    }
    public String getPoi_Name() {
        return Poi_Name;
    }

    public String getPoi_Address() {
        return Poi_Address;
    }

    public String getPoi_Distance() {
        return Poi_Distance;
    }

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }
    @Override
    public String toString() {
        return "PoiItemOnclickBean{" +
                "Poi_Name='" + Poi_Name + '\'' +
                ", Poi_Address='" + Poi_Address + '\'' +
                ", Poi_Distance='" + Poi_Distance + '\'' +
                ", latLonPoint=" + latLonPoint +
                '}';
    }
}
