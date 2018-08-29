package com.test.map;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.test.map.model.NaviLatLngBean;
import com.test.map.model.PathPlanDataBean;
import com.test.map.model.PoiInfoBean;
import com.test.map.model.PoiItemOnclickBean;
import com.test.map.fragment.PoiInformationFragment;
import com.test.map.fragment.PoiSearchPageFragment;
import com.test.map.listener.GeocodeSearchListenerUtil;
import com.test.map.listener.MyLocationChangeListenerUtil;
import com.test.map.permission.MPermission;
import com.test.map.permission.annotation.OnMPermissionDenied;
import com.test.map.permission.annotation.OnMPermissionGranted;
import com.test.map.permission.annotation.OnMPermissionNeverAskAgain;
import com.test.map.sqlite.MapHistoryBean;
import com.test.map.sqlite.UserDataBaseOperate;
import com.test.map.sqlite.UserSQLiteOpenHelper;
import com.test.map.util.LogUtil;
import com.test.map.util.MapUtil;
import com.test.map.util.OffLineMapUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by FAN on 2017/5/8.
 */

public class MapActivity extends Activity implements AMap.OnPOIClickListener, RouteSearch.OnRouteSearchListener {
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private FragmentManager mManager;
    PoiInformationFragment poiinfoFragment;
    PoiSearchPageFragment poiSearchPageFragment;
    // 主控件
    private MapView mapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private GeocodeSearchListenerUtil geocodeSearchListenerUtil;
    public static LatLng poiLatLng;
    private UiSettings mUiSettings;
    private Button bt_back;
    private ImageView go_where;

    //发送给information 的信息
    public static LatLonPoint mLatLonPoint;
    public static String PoiName;
    public static String PoiAddress;
    public static String PoiDistance;

    public static LatLonPoint infoLatLonPoint;
    public static String infoPoiName;
    //以下为路径规划
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

    public static int TEXT_GO_MODE = 0;
    private UserSQLiteOpenHelper userSQLiteOpenHelper;
    private UserDataBaseOperate userDataBaseOperate;
    public static   List<MapHistoryBean> mapHistoryBeenList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);
        MapsInitializer.sdcardDir= OffLineMapUtils.getSdCacheDir(this);
        mManager = getFragmentManager();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initUi();
        geocodeSearchListenerUtil = new GeocodeSearchListenerUtil(this);
        initFragment();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        userSQLiteOpenHelper = UserSQLiteOpenHelper.getInstance(this);
        userDataBaseOperate = new UserDataBaseOperate(userSQLiteOpenHelper.getWritableDatabase());
        mapHistoryBeenList = userDataBaseOperate.findAll();
    }

    private void initUi() {
        bt_back = (Button) findViewById(R.id.bt_back);
        go_where = (ImageView) findViewById(R.id.go_where);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
        }
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(MyLocationChangeListenerUtil.getInstance(this));
        aMap.setOnPOIClickListener(this);
        mUiSettings.setRotateGesturesEnabled(false);
        //初始化路径搜索
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this); //路径规划事件监听
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 如果要设置定位的默认状态，可以在此处进行设置
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    private void initFragment() {
        poiinfoFragment = new PoiInformationFragment();
        poiSearchPageFragment = new PoiSearchPageFragment();
    }

    public void Enter_SearchPage(View view) {
        addSearchPageFragment();
      //  addBackMap();
        hideGoWhere();
    }

    /**
     * poi 搜索返回到地图界面
     *
     * @param view
     */
    public void Back_Map(View view) {
        if (poiSearchPageFragment.isAdded()) {
            removeSearchPageFragment();
        }
        if (poiinfoFragment.isAdded()) {
            removePoiInfoFragment();
        }
        hideBackMap();
        addGoWhere();
        clearMap();
        startLocation();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startLocation();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPOIClick(Poi poi) {
        if (poiSearchPageFragment.isAdded())
            return;
        aMap.clear();
        poiLatLng = poi.getCoordinate();  //得到poi的LatLng 值
        infoPoiName = poi.getName();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        markOptiopns.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
        aMap.addMarker(markOptiopns);
        LogUtil.e("" + poi.toString());
        mLatLonPoint = MapUtil.convertToLatLonPoint(poi.getCoordinate());
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                MapUtil.convertToLatLng(mLatLonPoint), 15));
        getAddress(mLatLonPoint);
        addPoiInfoFragment();
        addBackMap();
        hideGoWhere();
        EventBus.getDefault().post("查看poi信息界面是否在算路界面");
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        GeocodeSearchListenerUtil.geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 显示poi信息Fragment
     */
    private void addPoiInfoFragment() {
        if (poiinfoFragment.isAdded())
            return;
        FragmentTransaction ft = mManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.poi_info, poiinfoFragment);
        ft.commit();
    }

    /**
     * 移除poi信息Fragment
     */
    private void removePoiInfoFragment() {
        FragmentTransaction ft = mManager.beginTransaction();
        ft.remove(poiinfoFragment);
        ft.commit();
    }

    /**
     * 显示搜索页面的Fragment
     */
    private void addSearchPageFragment() {
        if (poiSearchPageFragment.isAdded())
            return;
        FragmentTransaction ft = mManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.search_page, poiSearchPageFragment);
        ft.commit();
    }

    /**
     * 移除搜索页面的Fragment
     */
    private void removeSearchPageFragment() {
        FragmentTransaction ft = mManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.remove(poiSearchPageFragment);
        ft.commit();
    }

    /**
     * 导航Fragment已打开
     */
    private void StartNav() {
        Double a = MapUtil.latLonPoint.getLatitude();
        Double b = MapUtil.latLonPoint.getLongitude();
        Double c = poiLatLng.latitude;
        Double d = poiLatLng.longitude;
        NaviLatLng satrtNaviLatLng = new NaviLatLng(a, b);
        NaviLatLng stopNaviLatLng = new NaviLatLng(c, d);
        NaviLatLngBean naviLatLngBean = new NaviLatLngBean(satrtNaviLatLng, stopNaviLatLng);
        EventBus.getDefault().post(naviLatLngBean);
    }

    /**
     * 显现 目的地
     */
    private void addGoWhere() {
        go_where.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 目的地
     */
    private void hideGoWhere() {
        go_where.setVisibility(View.GONE);
    }

    /**
     * 显现 返回地图
     */
    private void addBackMap() {
        bt_back.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 返回地图
     */
    private void hideBackMap() {
        bt_back.setVisibility(View.GONE);
    }

    /**
     * Amap Clear
     */
    private void clearMap() {
        aMap.clear();
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        setUpMap();
    }


    /**
     * EventBus 接收
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PoiItemOnclickBean poiItemOnclickBean) {
        addPoiInfoFragment();
        removeSearchPageFragment();
        hideBackMap();
        mLatLonPoint = poiItemOnclickBean.getLatLonPoint();
        poiLatLng = MapUtil.convertToLatLng(mLatLonPoint);
        LatLng latLng = MapUtil.convertToLatLng(mLatLonPoint);
        //虽然 数据传输过来  但是  目前也只是用到了经纬度，在PoiInformationFragment 中用到其他 数据
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(latLng);
        markOptiopns.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
        aMap.addMarker(markOptiopns);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                MapUtil.convertToLatLng(mLatLonPoint), 15));
        PoiName = poiItemOnclickBean.getPoi_Name();
        PoiAddress = poiItemOnclickBean.getPoi_Address();
        PoiDistance = poiItemOnclickBean.getPoi_Distance();
        mHandler.sendEmptyMessageDelayed(SEND_HANDLE, 1000);
    }
    /**
     * EventBus 接收
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MapHistoryBean mapHistoryBean) {
        userDataBaseOperate.insertToHistory(mapHistoryBean);
        mapHistoryBeenList = userDataBaseOperate.findAll();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg) {
        if (msg.equalsIgnoreCase("back_map")) {
            Back_Map(null);
        } else if (msg.equalsIgnoreCase("默认")) {
            searchRouteResult(RouteSearch.DRIVING_SINGLE_DEFAULT);
            TEXT_GO_MODE = 1;
            MapUtil.PathPlanningMode = 0;
        } else if (msg.equalsIgnoreCase("距离最短")) {
            searchRouteResult(RouteSearch.DRIVING_SINGLE_SHORTEST);
            MapUtil.PathPlanningMode = 1;
        } else if (msg.equalsIgnoreCase("避免拥堵")) {
            MapUtil.PathPlanningMode = 2;
            searchRouteResult(RouteSearch.DRIVING_SINGLE_AVOID_CONGESTION);
        } else if (msg.equalsIgnoreCase("开始导航")) {
            removePoiInfoFragment();
            Back_Map(null);
            Intent intent = new Intent();
            intent.setClass(this, NavActivity.class);
            startActivity(intent);
            mHandler.sendEmptyMessageDelayed(SEND_NAV_DATA, 1000);
        }else if (msg.equalsIgnoreCase("ClearMapHistory")){
             userDataBaseOperate.deleteAll();
            mapHistoryBeenList = userDataBaseOperate.findAll();
            EventBus.getDefault().post("HistoryNull");
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int mode) {

        if (MapUtil.latLonPoint == null) {
            Toast.makeText(getApplicationContext(), "定位中，稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLatLonPoint == null) {
            Toast.makeText(getApplicationContext(), "终点未设置", Toast.LENGTH_SHORT).show();
            return;
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                MapUtil.latLonPoint, mLatLonPoint);
        // 驾车路径规划
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }

    /**
     * 路径规划
     *
     * @param busRouteResult
     * @param i
     */
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        clearMap();
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DriveRouteColorfulOverLay drivingRouteOverlay = new DriveRouteColorfulOverLay(
                            aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    if (MapUtil.PathPlanningMode == 0) {
                        MapUtil.DRIVING_SINGLE_DEFAULT_TIME = MapUtil.getFriendlyTime(dur) ;
                        MapUtil.DRIVING_SINGLE_DEFAULT_DISTANCE = MapUtil.getFriendlyLength(dis);
                        PathPlanDataBean pathPlanDataBean = new PathPlanDataBean(MapUtil.DRIVING_SINGLE_DEFAULT_TIME, MapUtil.DRIVING_SINGLE_DEFAULT_DISTANCE);
                        EventBus.getDefault().post(pathPlanDataBean);
                    } else if (MapUtil.PathPlanningMode == 1) {
                        MapUtil.DRIVING_SINGLE_SHORTEST_TIME = MapUtil.getFriendlyTime(dur);
                        MapUtil.DRIVING_SINGLE_SHORTEST_DISTANCE = MapUtil.getFriendlyLength(dis);
                        PathPlanDataBean pathPlanDataBean = new PathPlanDataBean(MapUtil.DRIVING_SINGLE_SHORTEST_TIME, MapUtil.DRIVING_SINGLE_SHORTEST_DISTANCE);
                        EventBus.getDefault().post(pathPlanDataBean);
                    } else if (MapUtil.PathPlanningMode == 2) {
                        MapUtil.DRIVING_SINGLE_AVOID_CONGESTION_TIME = MapUtil.getFriendlyTime(dur);
                        MapUtil.DRIVING_SINGLE_AVOID_CONGESTION_DISTANCE = MapUtil.getFriendlyLength(dis);
                        PathPlanDataBean pathPlanDataBean = new PathPlanDataBean(MapUtil.DRIVING_SINGLE_AVOID_CONGESTION_TIME, MapUtil.DRIVING_SINGLE_AVOID_CONGESTION_DISTANCE);
                        EventBus.getDefault().post(pathPlanDataBean);
                    }

                } else if (result != null && result.getPaths() == null) {
                    Toast.makeText(getApplication(), "无返回结果", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplication(), "无返回结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            //  rCodeGaoDe(errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    public static final int SEND_HANDLE = 1;
    public static final int SEND_NAV_DATA = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case SEND_HANDLE:
                    PoiInfoBean poiInfoBean = new PoiInfoBean(PoiName, PoiAddress, PoiDistance, mLatLonPoint);
                    EventBus.getDefault().post(poiInfoBean);
                    break;
                case SEND_NAV_DATA:
                    StartNav();
                    break;
            }
        }
    };


    //=========================================================================================================================

    /**
     * 隐藏输入框 判断
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 隐藏输入框
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (poiinfoFragment.isAdded()) {
                removePoiInfoFragment();
                Back_Map(null);
            } else if (poiSearchPageFragment.isAdded()) {
                removeSearchPageFragment();
                Back_Map(null);
            } else {
                finish();
            }
        }
        return false;
    }


    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
    };
    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(value = BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        // Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
        finish();
    }
}

