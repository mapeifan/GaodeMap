package com.test.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.map.R;

import java.util.List;
import java.util.Map;


/**
 * Created by FAN on 2017/5/11.
 */
public class PoiSearchListAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public PoiSearchListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
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
            convertView = layoutInflater.inflate(R.layout.map_list_item, null);
            viewHolder.map_Name = (TextView) convertView.findViewById(R.id.item_map_name);
            viewHolder.map_Address = (TextView) convertView.findViewById(R.id.item_map_address);
            viewHolder.map_distance = (TextView) convertView.findViewById(R.id.item_map_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        viewHolder.map_Name.setText((String) data.get(position).get("mapName"));
        viewHolder.map_Address.setText((String) data.get(position).get("mapAddress"));
        viewHolder.map_distance.setText((String) data.get(position).get("mapDistance"));

        return convertView;
    }

    public final class ViewHolder {
        public TextView map_Name;
        public TextView map_Address;
        public TextView map_distance;
    }
}
