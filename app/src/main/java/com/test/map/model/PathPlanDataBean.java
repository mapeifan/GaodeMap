package com.test.map.model;

/**
 * Created by FAN on 2017/5/11.
 */

public class PathPlanDataBean {
    private String Time;
    private String distance;

    public PathPlanDataBean(String time, String distance) {
        Time = time;
        this.distance = distance;
    }

    public String getTime() {
        return Time;
    }

    public String getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "PathPlanDataBean{" +
                "Time='" + Time + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}
