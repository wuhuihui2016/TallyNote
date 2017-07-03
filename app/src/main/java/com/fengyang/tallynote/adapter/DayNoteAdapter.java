package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.DetailsActivity;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNoteAdapter extends BaseAdapter {

    private Context context;
    private List<DayNote> dayNotes;

    public DayNoteAdapter(Context context, List<DayNote> dayNotes) {
        this.context = context;
        this.dayNotes = dayNotes;
    }


    @Override
    public int getCount() {
        return dayNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return dayNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.day_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.item_day_layout = (LinearLayout) convertView.findViewById(R.id.item_day_layout);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.usage = (TextView) convertView.findViewById(R.id.usage);
            viewHolder.money = (TextView) convertView.findViewById(R.id.money);
            viewHolder.remask = (TextView) convertView.findViewById(R.id.remask);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final DayNote dayNote = dayNotes.get(position);
        viewHolder.time.setText(DateUtils.diffTime(dayNote.getTime()));
        viewHolder.usage.setText(dayNote.getUsage());
        viewHolder.money.setText(StringUtils.showPrice(dayNote.getMoney()) + " 元");
        if (! TextUtils.isEmpty(dayNote.getRemark())) viewHolder.remask.setText(dayNote.getRemark());

        viewHolder.item_day_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("type", MyApp.DAY);
                if (position == 0) intent.putExtra("last", true);
                intent.putExtra("note", dayNote);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        LinearLayout item_day_layout;
        TextView time, usage, money, remask;
    }
}
