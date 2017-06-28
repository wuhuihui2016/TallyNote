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
 * Created by wuhuihui on 2017/6/23.
 */
public class PwdAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public PwdAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_pwd_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.pwd = (TextView) convertView.findViewById(R.id.pwd);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        if (list.size() - 1 >= position) {
            String txt = list.get(position);
            viewHolder.pwd.setText(txt);
        } else {
            viewHolder.pwd.setText("");
        }
        return convertView;
    }

    class ViewHolder{
        TextView pwd;
    }
}
