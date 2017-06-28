package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fengyang.tallynote.R;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class NumAdapter extends BaseAdapter {

    private Context context;
    private List<Drawable> drawables;

    public NumAdapter(Context context, List<Drawable> drawables) {
        this.context = context;
        this.drawables = drawables;
    }


    @Override
    public int getCount() {
        return drawables.size();
    }

    @Override
    public Object getItem(int position) {
        return drawables.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        Drawable drawable = drawables.get(position);
        viewHolder.image.setImageDrawable(drawable);
        return convertView;
    }

    class ViewHolder{
        ImageView image;
    }
}
