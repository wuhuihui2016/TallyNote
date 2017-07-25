package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNoteAdapter extends BaseAdapter {

    private Activity activity;
    private List<DayNote> dayNotes;
    private boolean isLast;//列表显示按所有排序，最后一个才可做删除操作

    public DayNoteAdapter(Activity activity, List<DayNote> dayNotes, boolean isLast) {
        this.activity = activity;
        this.dayNotes = dayNotes;
        this.isLast = isLast;
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.day_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.spot = convertView.findViewById(R.id.spot);
            viewHolder.tag = (ImageView) convertView.findViewById(R.id.tag);
            viewHolder.day_del = (ImageView) convertView.findViewById(R.id.day_del);
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
        if (dayNote.getUseType() == DayNote.consume) {
            viewHolder.usage.setText("支出：");
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_consume_spot);
            viewHolder.tag.setImageResource(R.drawable.consume);
        } else if (dayNote.getUseType() == DayNote.account_out) {
            viewHolder.usage.setText("转账：");
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_out_spot);
            viewHolder.tag.setImageResource(R.drawable.account_out);
        } else if (dayNote.getUseType() == DayNote.account_in) {
            viewHolder.usage.setText("转入：");
            viewHolder.spot.setBackgroundResource(R.drawable.shape_day_in_spot);
            viewHolder.tag.setImageResource(R.drawable.account_in);
        }
        viewHolder.money.setText(StringUtils.showPrice(dayNote.getMoney()));
        if (! TextUtils.isEmpty(dayNote.getRemark())) viewHolder.remask.setText(dayNote.getRemark());
        else viewHolder.remask.setText("");

        if (position == 0 && isLast) {
            viewHolder.day_del.setVisibility(View.VISIBLE);
            viewHolder.day_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除此条记录", new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            MyApp.utils.delDNote(dayNote);
                            dayNotes = MyApp.utils.getDayNotes();
                            notifyDataSetChanged();

                            Intent intent = new Intent(ContansUtils.ACTION_DAY);
                            activity.sendBroadcast(intent);

                        }
                    }, new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                        }
                    });
                }
            });
        } else viewHolder.day_del.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder{
        View spot;
        ImageView tag, day_del;
        TextView time, usage, money, remask;
    }
}
