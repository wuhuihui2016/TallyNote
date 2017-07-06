package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhuihui on 2017/7/5.
 */
public class CalculateAdapter extends BaseAdapter {

    private Context context;
    private List<String> numbers = new ArrayList<>();

    public CalculateAdapter(Context context) {
        this.context = context;
    }


    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calculate_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.num = (TextView) convertView.findViewById(R.id.set_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position >= 0 && position <= 8) viewHolder.num.setText((position + 1) + "");
        else if (position == 9) viewHolder.num.setText("00");
        else if (position == 10) viewHolder.num.setText("0");
        else if (position == 11) viewHolder.num.setText("CE");

        return convertView;
    }

    class ViewHolder {
        TextView num;
    }
}