package com.test.map;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.amap.api.maps.UiSettings;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.test.map.model.NaviLatLngBean;
import com.test.map.util.LogUtil;
import com.test.map.util.MapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAN on 2017/5/8.
 */

public class NavActivity extends Activity implements AMapNaviViewListener, AMapNaviListener {
    TTSController mTtsManager;
    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    NaviLatLng mEndLatlng;
    NaviLatLng mStartLatlng;
    List<NaviLatLng> mStartList = new ArrayList<NaviLatLng>();
    List<NaviLatLng> mEndList = new ArrayList<NaviLatLng>();
    List<NaviLatLng> mWayPointList;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nav);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();
        initHudMap();
        MapActivity.TEXT_GO_MODE = 0;
        EventBus.getDefault().register(this);
    }

    private void initHudMap() {
        mAMapNavi = AMapNavi.getInstance(this);
        mUiSettings = mAMapNaviView.getMap().getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(60);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTtsManager.stopSpeaking();
        mAMapNaviView.onDestroy();
        //since 1.6.0
        //不再在naviview destroy的时候自动执行AMapNavi.stopNavi();
        //请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
        mTtsManager.destroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus 接收
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NaviLatLngBean naviLatLngBean) {
        mStartLatlng = naviLatLngBean.getStartNaviLatLng();
        mEndLatlng = naviLatLngBean.getStopNaviLatLng();
        startNav();
    }

    private void startNav() {
        mStartList.add(mStartLatlng);
        mEndList.add(mEndLatlng);
        int strategy = 0;
        try {
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MapUtil.PathPlanningMode==0){
            boolean ss = mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
        }else if (MapUtil.PathPlanningMode==1){
            boolean ss = mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE);
        }else if (MapUtil.PathPlanningMode==2){
            boolean ss = mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList, PathPlanningStrategy.DRIVING_AVOID_CONGESTION);
        }

        LogUtil.e("导航策略:" + MapUtil.PathPlanningMode);
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
/**
 * 方法:
 *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
 * 参数:
 * @congestion 躲避拥堵
 * @avoidhightspeed 不走高速
 * @cost 避免收费
 * @hightspeed 高速优先
 * @multipleroute 多路径
 *
 * 说明:
 *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
 * 注意:
 *      不走高速与高速优先不能同时为true
 *      高速优先与避免收费不能同时为true
 */

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {
        mAMapNavi.setEmulatorNaviSpeed(60);
        AMapNavi.getInstance(this).startNavi(NaviType.EMULATOR);
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }




  /*  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg) {
        if (msg.equalsIgnoreCase("PhoneComming")) {
            Intent intent = new Intent();
            intent.setAction(MyVar.STATICACTION);
            sendBroadcast(intent);
        }
    }*/
}
