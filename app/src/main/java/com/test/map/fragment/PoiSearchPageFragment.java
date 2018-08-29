package com.test.map.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.test.map.MapActivity;
import com.test.map.R;
import com.test.map.adapter.MapHistoryListAdapter;
import com.test.map.adapter.PoiSearchListAdapter;
import com.test.map.model.PoiItemOnclickBean;
import com.test.map.sqlite.MapHistoryBean;
import com.test.map.util.LogUtil;
import com.test.map.util.MapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FAN on 2017/5/9.
 */

public class PoiSearchPageFragment extends Fragment {
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView back_map;
    private ListView list_input;
    private RelativeLayout ly_history_page;
    private ListView list_history;
    private TextView map_clear_history;
    private TextView map_null_history;
    private TextWatch textWatch;
    private InputLisenerTest inputLisenerTest;
    List<Map<String, Object>> listItem; //输入keyword，数据返回的list数据源

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_poi_search, container, false);
        initUi(root);
        return root;
    }


    private void initUi(View root) {
        autoCompleteTextView = (AutoCompleteTextView) root.findViewById(R.id.keyWord);
        back_map = (ImageView) root.findViewById(R.id.back_map);
        list_input = (ListView) root.findViewById(R.id.list_input);
        textWatch = new TextWatch();
        inputLisenerTest = new InputLisenerTest();
        autoCompleteTextView.addTextChangedListener(textWatch);
        back_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("back_map");
            }
        });
        ly_history_page=(RelativeLayout) root.findViewById(R.id.ly_history_page);
        list_history=(ListView)root.findViewById(R.id.list_history);
        map_clear_history=(TextView)root.findViewById(R.id.map_clear_history);
        map_null_history=(TextView)root.findViewById(R.id.map_null_history);
        if (null==MapActivity.mapHistoryBeenList||MapActivity.mapHistoryBeenList.size()<1){
            showHistoryText();
            hideHistoryPage();
        }
        if (null==MapActivity.mapHistoryBeenList||MapActivity.mapHistoryBeenList.size()<1)
            return;
        list_history.setAdapter(new MapHistoryListAdapter(getActivity(),MapActivity.mapHistoryBeenList));
        list_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name= MapActivity.mapHistoryBeenList.get(position).getName();
                String address= MapActivity.mapHistoryBeenList.get(position).getAddress();
                String distance= MapActivity.mapHistoryBeenList.get(position).getDistance();
                String dd=  MapActivity.mapHistoryBeenList.get(position).getLat();
                String ss= MapActivity.mapHistoryBeenList.get(position).getLon();
                LatLonPoint mlatLonPointItem=new LatLonPoint(Double.valueOf(dd),Double.valueOf(ss));

                PoiItemOnclickBean poiItemOnclickBean = new PoiItemOnclickBean(name, address, distance, mlatLonPointItem);
                EventBus.getDefault().post(poiItemOnclickBean);
            }
        });

        map_clear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("ClearMapHistory");
            }
        });

    }
    /**
     *  显现 input listview
     */
    private void showInputListView(){
        list_input.setVisibility(View.VISIBLE);
    }
    /**
     *  隐藏input listview
     */
    private void hideInputListView(){
        list_input.setVisibility(View.GONE);
    }
    /**
     *  显现 历史记录
     */
    private void showHistoryPage(){
        ly_history_page.setVisibility(View.VISIBLE);
    }
    /**
     *  隐藏历史记录
     */
    private void hideHistoryPage(){
        ly_history_page.setVisibility(View.GONE);
    }
    /**
     *  显现 无历史记录 TEXT
     */
    private void showHistoryText(){
        map_null_history.setVisibility(View.VISIBLE);
    }
    /**
     *  隐藏无历史记录 TEXT
     */
    private void hideHistoryText(){
        map_null_history.setVisibility(View.GONE);
    }

    class TextWatch implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int stop, int count) {
            String newText = s.toString().trim();
            if(s.length()<1){
                showHistoryPage();
                hideHistoryText();
                hideInputListView();
            }else {
                showInputListView();
                hideHistoryText();
                hideHistoryPage();
            }
            if (!MapUtil.IsEmptyOrNullString(newText)) {
                InputtipsQuery inputquery = new InputtipsQuery(newText, MapUtil.LOCATION_CITY);
                Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                inputTips.setInputtipsListener(inputLisenerTest);  //设置=======得到数据监听=======
                inputTips.requestInputtipsAsyn();
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
    class InputLisenerTest implements Inputtips.InputtipsListener {

        @Override
        public void onGetInputtips(List<Tip> tipList, int rCode) {
            if (rCode == 1000) {// 正确返回
                boolean ee = tipList.size() > 0;
                LogUtil.e("=======数据监听正确返回" + tipList.toString() + ee);
                listItem = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < tipList.size(); i++) {
                    if (tipList.get(i).getPoint() != null) {
                        LatLng startLatLng = MapUtil.latLon;  //转化LatLng单位
                        LatLonPoint latLonPoint = tipList.get(i).getPoint();   //得到要去的地方的坐标经纬度
                        LatLng stopLatLng = MapUtil.convertToLatLng(latLonPoint); //转化LatLng单位
                        String kmDistance = MapUtil.distanceLatLng(startLatLng, stopLatLng);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("mapName", tipList.get(i).getName());
                        map.put("mapAddress", tipList.get(i).getDistrict());
                        map.put("mapPosition", tipList.get(i).getPoint());
                        map.put("mapDistance", kmDistance);
                        listItem.add(map);
                        LogUtil.e("名称：" + tipList.get(i).getName() + "地点：" + tipList.get(i).getDistrict() + "经纬度：" + tipList.get(i).getPoint() + "距离：" + kmDistance + "km");
                    }
                }
                list_input.setAdapter(new PoiSearchListAdapter(getActivity(), listItem));
                list_input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String poiName = (String) listItem.get(i).get("mapName");  //地名
                        String poiAddress = (String) listItem.get(i).get("mapAddress"); //地址
                        String poiDistance = (String) listItem.get(i).get("mapDistance"); //距离
                        LatLonPoint poiLatLonPoint = (LatLonPoint) (listItem.get(i).get("mapPosition"));  //经纬度

                        PoiItemOnclickBean poiItemOnclickBean = new PoiItemOnclickBean(poiName, poiAddress, poiDistance, poiLatLonPoint);
                        EventBus.getDefault().post(poiItemOnclickBean);

                        String mapHistoryLat=poiLatLonPoint.getLatitude()+"";
                        String mapHistoryLon=poiLatLonPoint.getLongitude()+"";

                        MapHistoryBean mapHistoryBean=new MapHistoryBean();
                        mapHistoryBean.setName(poiName);
                        mapHistoryBean.setAddress(poiAddress);
                        mapHistoryBean.setDistance(poiDistance);
                        mapHistoryBean.setLat(mapHistoryLat);
                        mapHistoryBean.setLon(mapHistoryLon);
                        EventBus.getDefault().post(mapHistoryBean);

                        autoCompleteTextView.setText("");
                    }
                });
            } else {
                rCodeGaoDe(rCode);
            }
        }
    }

    private void rCodeGaoDe(int rCode) {
        switch (rCode) {
            case 1802:
            case 1804:
            case 1806:
                Toast.makeText(getActivity(), "连接超时,请检查网络状况是否良好", Toast.LENGTH_SHORT).show();
                break;
            case 3000:
                Toast.makeText(getActivity(), "不在中国范围内", Toast.LENGTH_SHORT).show();
                break;
            case 3001:
                Toast.makeText(getActivity(), "附近搜索不道路", Toast.LENGTH_SHORT).show();
                break;
            case 3002:
                Toast.makeText(getActivity(), "路线计算失败", Toast.LENGTH_SHORT).show();
                break;
            case 3003:
                Toast.makeText(getActivity(), "起点、终点距离过长导致算路失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus 接收
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg) {
       if (msg.equalsIgnoreCase("HistoryNull")){
           if (null==MapActivity.mapHistoryBeenList||MapActivity.mapHistoryBeenList.size()<1){
               showHistoryText();
               hideHistoryPage();
           }
       }
    }


}
