package com.test.map.listener;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.test.map.MapActivity;
import com.test.map.model.PoiInfoBean;
import com.test.map.util.LogUtil;
import com.test.map.util.MapUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by FAN on 2017/5/8.
 */

public class GeocodeSearchListenerUtil implements GeocodeSearch.OnGeocodeSearchListener {
    public static GeocodeSearch geocoderSearch;
    private Context context;
    public GeocodeSearchListenerUtil(Context context){
        this.context=context;
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
             String  addressName = result.getRegeocodeAddress().getFormatAddress();
                String distance=   MapUtil.distanceLatLng(MapUtil.latLon, MapActivity.poiLatLng);
                LogUtil.e(""+addressName+"距离："+distance);
                PoiInfoBean poiInfoBean=new PoiInfoBean(MapActivity.infoPoiName, addressName,distance,MapActivity.mLatLonPoint);
                EventBus.getDefault().post(poiInfoBean);
            } else {
                LogUtil.e("onRegeocodeSearched 无返回结果");
            }
        } else {
            LogUtil.e("onRegeocodeSearched 错误码:"+rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
