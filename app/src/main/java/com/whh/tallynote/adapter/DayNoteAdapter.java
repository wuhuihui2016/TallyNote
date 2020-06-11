package com.whh.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNoteAdapter extends BaseAdapter {

    private Activity activity;
    private List<DayNote> dayNotes;
    private boolean isEditable;//是否为可编辑列表，如果是历史账单，不可编辑

    public DayNoteAdapter(Activity activity, List<DayNote> dayNotes, boolean isEditable) {
        this.activity = activity;
        this.dayNotes = dayNotes;
        this.isEditable = isEditable;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.day_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.spot = convertView.findViewById(R.id.spot);
            viewHolder.tag = (ImageView) convertView.findViewById(R.id.tag);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.usage = (TextView) convertView.findViewById(R.id.usage);
            viewHolder.money = (TextView) convertView.findViewById(R.id.money);
            viewHolder.remask = (TextView) convertView.findViewById(R.id.remask);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final DayNote dayNote = dayNotes.get(position);
        viewHolder.time.setText(DateUtils.diffTime(dayNote.getTime()));
        viewHolder.usage.setText(DayNote.getUserTypeStr(dayNote.getUseType()));
        if (dayNote.getUseType() == DayNote.consume) { //0.支出
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_consume_spot);
            viewHolder.tag.setImageResource(R.drawable.consume);
        } else if (dayNote.getUseType() == DayNote.account_out) { //1.转账
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_out_spot);
            viewHolder.tag.setImageResource(R.drawable.account_out);
        } else if (dayNote.getUseType() == DayNote.account_in) { //2.入账
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_in_spot);
            viewHolder.tag.setImageResource(R.drawable.account_in);
        } else { //3.家用
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_home_spot);
            viewHolder.tag.setImageResource(R.drawable.account_home);
        }
        viewHolder.money.setText(StringUtils.showPrice(dayNote.getMoney()));
        viewHolder.remask.setText(dayNote.getRemark());
        return convertView;
    }

    class ViewHolder {
        View spot;
        ImageView tag;
        TextView time, usage, money, remask;
    }
}
