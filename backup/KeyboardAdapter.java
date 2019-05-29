package com.whh.tallynote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whh.tallynote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhuihui on 2017/7/5.
 */
public class KeyboardAdapter extends BaseAdapter {

    private Context context;
    private List<String> numbers = new ArrayList<>();

    public KeyboardAdapter(Context context) {
        this.context = context;
    }


    @Override
    public int getCount() {
        return 16;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.keyboard_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position >= 0 && position <= 2) {
            viewHolder.num.setBackgroundColor(Color.WHITE);
            viewHolder.num.setText((position + 1) + "");
        } else if (position == 3) {
            viewHolder.num.setBackgroundColor(Color.parseColor("#f5f5f5"));
            viewHolder.num.setText("C");
        } else if (position >= 4 && position <= 6) {
            viewHolder.num.setBackgroundColor(Color.WHITE);
            viewHolder.num.setText((position) + "");
        } else if (position == 7) {
            viewHolder.num.setText("");
            viewHolder.num.setBackgroundColor(Color.parseColor("#f5f5f5"));
        } else if (position >= 8 && position <= 10) {
            viewHolder.num.setBackgroundColor(Color.WHITE);
            viewHolder.num.setText((position - 1) + "");
        } else if (position == 11) {
            viewHolder.num.setText("下一个");
            viewHolder.num.setBackgroundColor(Color.parseColor("#f5f5f5"));
        } else if (position >= 12 && position <= 14) {
            viewHolder.num.setBackgroundColor(Color.parseColor("#f5f5f5"));
            if (position == 12) viewHolder.num.setText("00");
            if (position == 13) {
                viewHolder.num.setText("0");
                viewHolder.num.setBackgroundColor(Color.WHITE);
            }
            if (position == 14) viewHolder.num.setText("CE");
        } else if (position == 15) {
            viewHolder.num.setText("完成");
            viewHolder.num.setTextColor(Color.RED);
            viewHolder.num.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }

        return convertView;
    }

    class ViewHolder {
        TextView num;
    }
}