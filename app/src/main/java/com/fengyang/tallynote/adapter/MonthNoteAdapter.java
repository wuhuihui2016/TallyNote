package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MonthNoteAdapter extends BaseAdapter{

    private Context context;
    private List<MonthNote> monthNotes;

    public MonthNoteAdapter(Context context, List<MonthNote> monthNotes) {
        this.context = context;
        this.monthNotes = monthNotes;
    }

    @Override
    public int getCount() {
        return monthNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return monthNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.month_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.month = (TextView) convertView.findViewById(R.id.month);
            viewHolder.month_info = (TextView) convertView.findViewById(R.id.month_info);
            viewHolder.month_actual_balance = (TextView) convertView.findViewById(R.id.month_actual_balance);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        MonthNote monthNote = monthNotes.get(position);
        viewHolder.month.setText(monthNote.getDuration().split("-")[0].substring(4, 6));
        viewHolder.month_info.setText(monthNote.getDuration() + "\n" +
                "上次结余:" + StringUtils.formate2Double(monthNote.getLast_balance()) + "\n" +
                        "本次支出: " + StringUtils.formate2Double(monthNote.getPay()) + "\n" +
                        "本次工资: " + StringUtils.formate2Double(monthNote.getSalary()) + "\n" +
                        "本次收益: " + StringUtils.formate2Double(monthNote.getIncome()) + "\n" +
                        "家用补贴: " + StringUtils.formate2Double(monthNote.getHomeuse()) + "\n" +
                        "本次结余: " + StringUtils.formate2Double(monthNote.getBalance()) + "\n" +
                        "备    注: " + monthNote.getRemark());
        viewHolder.month_actual_balance.setText(StringUtils.formate2Double(monthNote.getActual_balance()));
        return convertView;
    }

    class ViewHolder{
        TextView month, month_info, month_actual_balance;
    }
}
