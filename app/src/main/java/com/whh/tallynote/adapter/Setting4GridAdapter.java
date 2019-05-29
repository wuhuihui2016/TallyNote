package com.whh.tallynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whh.tallynote.R;

import java.util.List;

/**
 * Created by wuhuihui on 2017/7/5.
 */
public class Setting4GridAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> drawableRes;
    private List<String> setttings;

    public Setting4GridAdapter(Context context, List<Integer> drawableRes, List<String> setttings) {
        this.context = context;
        this.drawableRes = drawableRes;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_item_layout_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.set_info = (TextView) convertView.findViewById(R.id.set_info);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.image.setImageResource(drawableRes.get(position));
        viewHolder.set_info.setText(setttings.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView set_info;
        ImageView image;
    }
}