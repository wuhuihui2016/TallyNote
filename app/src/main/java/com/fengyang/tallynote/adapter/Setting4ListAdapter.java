package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;

import java.util.List;

/**
 * Created by wuhuihui on 2017/7/5.
 */
public class Setting4ListAdapter extends BaseAdapter {

    private Context context;
    private List<String> setttings;

    public Setting4ListAdapter(Context context, List<String> setttings) {
        this.context = context;
        this.setttings = setttings;
    }


    @Override
    public int getCount() {
        return setttings.size();
    }

    @Override
    public Object getItem(int position) {
        return setttings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_item_layout_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.set_info = (TextView) convertView.findViewById(R.id.set_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.set_info.setText(setttings.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView set_info;
    }
}