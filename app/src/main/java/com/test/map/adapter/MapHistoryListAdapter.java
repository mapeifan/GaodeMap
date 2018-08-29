package com.test.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.map.R;
import com.test.map.sqlite.MapHistoryBean;
import com.test.map.util.LogUtil;

import java.util.List;


/**
 * Created by FAN on 2017/5/11.
 */
public class MapHistoryListAdapter extends BaseAdapter {
    private List<MapHistoryBean> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public MapHistoryListAdapter(Context context, List<MapHistoryBean> been) {
        this.context = context;
        this.data = been;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.map_history_list_item, null);
            viewHolder.mapHistory_Name = (TextView) convertView.findViewById(R.id.map_history_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        if (null==data.get(position).getName()){
            LogUtil.e("kong  null");
        }else {
            viewHolder.mapHistory_Name.setText(data.get(position).getName());
        }


        return convertView;
    }

    public final class ViewHolder {
        public TextView mapHistory_Name;
    }
}
