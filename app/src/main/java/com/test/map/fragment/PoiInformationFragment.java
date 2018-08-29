package com.test.map.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.map.MapActivity;
import com.test.map.R;
import com.test.map.model.PathPlanDataBean;
import com.test.map.model.PoiInfoBean;
import com.test.map.util.MapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by FAN on 2017/5/8.
 */

public class PoiInformationFragment extends Fragment {
    private TextView poi_name;
    private TextView poi_address;
    private TextView poi_distance;
    private Button go_there_bt;
    private LinearLayout ly_info;
    private LinearLayout ly_choice_mode;
    private Button bt_start_nav;

    private Button bt_poi_01;
    private Button bt_poi_02;
    private Button bt_poi_03;
    private TextView tv_nav_time;
    private TextView tv_nav_distance;
    private TextView tv_nav_light;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_poi_info, container, false);
        initUi(root);
        showPoiInfoLy();
        hideChoiceLy();
        return root;
    }

    private void initUi(View root) {
        ly_info = (LinearLayout) root.findViewById(R.id.ly_info);
        ly_choice_mode = (LinearLayout) root.findViewById(R.id.ly_choice_mode);

        poi_name = (TextView) root.findViewById(R.id.poi_name);
        poi_address = (TextView) root.findViewById(R.id.poi_address);
        poi_distance = (TextView) root.findViewById(R.id.poi_distance);
        go_there_bt = (Button) root.findViewById(R.id.go_there_bt);
        go_there_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    EventBus.getDefault().post("默认");
                    hidePoiInfoLy();
                    showChoiceLy();

            }
        });
        bt_start_nav=(Button)root.findViewById(R.id.bt_start_nav);
        bt_start_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EventBus.getDefault().post("开始导航");
            showPoiInfoLy();
            hideChoiceLy();
            }
        });

        bt_poi_01 = (Button) root.findViewById(R.id.bt_poi_01);
        bt_poi_02 = (Button) root.findViewById(R.id.bt_poi_02);
        bt_poi_03 = (Button) root.findViewById(R.id.bt_poi_03);

        bt_poi_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("默认");
                MapUtil.PathPlanningMode=0;
                changeUi();
            }
        });
        bt_poi_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("距离最短");
                MapUtil.PathPlanningMode=1;
                changeUi();
            }
        });
        bt_poi_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("避免拥堵");
                MapUtil.PathPlanningMode=2;
                changeUi();
            }
        });
        tv_nav_time=(TextView)root.findViewById(R.id.tv_nav_time);
        tv_nav_distance=(TextView)root.findViewById(R.id.tv_nav_distance);
        tv_nav_light=(TextView)root.findViewById(R.id.tv_nav_light);
    }

    /**
     * 显现 poi信息
     */
    private void showPoiInfoLy() {
        ly_info.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 poi信息
     */
    private void hidePoiInfoLy() {
        ly_info.setVisibility(View.GONE);
    }

    /**
     * 显现 导航策略信息
     */
    private void showChoiceLy() {
        ly_choice_mode.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 导航策略信息
     */
    private void hideChoiceLy() {
        ly_choice_mode.setVisibility(View.GONE);
    }

    /**
     * EventBus 接收
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PoiInfoBean poiInfoBean) {
        String PoiName = poiInfoBean.getPoi_Name();
        String PoiAddress = poiInfoBean.getPoi_Address();
        String PoiDistance = poiInfoBean.getPoi_Distance();
        poi_name.setText(PoiName);
        poi_address.setText(PoiAddress);
        poi_distance.setText("距离 "+PoiDistance + " 公里");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PathPlanDataBean pathPlanDataBean) {
        String time = pathPlanDataBean.getTime();
        String distance = pathPlanDataBean.getDistance();
        tv_nav_time.setText(time);
        tv_nav_distance.setText(distance);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        if (string.equalsIgnoreCase("查看poi信息界面是否在算路界面")) {
            if (MapActivity.TEXT_GO_MODE == 1) {
                showPoiInfoLy();
                hideChoiceLy();
                MapActivity.TEXT_GO_MODE = 0;
                go_there_bt.setText("去这里");
            }
        }else if (string.contains("红绿灯")){
            String light=string;
            tv_nav_light.setText(light);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void changeUi(){
      if (MapUtil.PathPlanningMode==0){
          bt_poi_01.setTextColor(Color.parseColor("#FFFFFF"));
          bt_poi_01.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_l));
          bt_poi_02.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_02.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
          bt_poi_03.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_03.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
      }else if (MapUtil.PathPlanningMode==1){
          bt_poi_01.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_01.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
          bt_poi_02.setTextColor(Color.parseColor("#FFFFFF"));
          bt_poi_02.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_l));
          bt_poi_03.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_03.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
      }else if (MapUtil.PathPlanningMode==2){
          bt_poi_01.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_01.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
          bt_poi_02.setTextColor(Color.parseColor("#9a9b98"));
          bt_poi_02.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_w));
          bt_poi_03.setTextColor(Color.parseColor("#FFFFFF"));
          bt_poi_03.setBackground(getActivity().getResources().getDrawable(R.drawable.map_bt_bottm_bg_l));
      }
    }
}
