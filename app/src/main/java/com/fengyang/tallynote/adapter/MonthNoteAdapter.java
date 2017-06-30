package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.text.TextUtils;
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
            viewHolder.month_id = (TextView) convertView.findViewById(R.id.month_id);
            viewHolder.month_money = (TextView) convertView.findViewById(R.id.month_money);
            viewHolder.month_actual_balance = (TextView) convertView.findViewById(R.id.month_actual_balance);
            viewHolder.month_info = (TextView) convertView.findViewById(R.id.month_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        MonthNote monthNote = monthNotes.get(position);
        viewHolder.month_id.setText(monthNote.getDuration().split("-")[0].substring(4, 6));

        viewHolder.month_money.setText(monthNote.getDuration() +
                "\n本次支出:  " + StringUtils.showPrice(monthNote.getPay()) +
                "\n本次工资:  " + StringUtils.showPrice(monthNote.getSalary()));

        String info = "上次结余： " + StringUtils.showPrice(monthNote.getLast_balance()) +
                "\n本次结余:  " + StringUtils.showPrice(monthNote.getBalance());
        if (! TextUtils.isEmpty(monthNote.getIncome())) info = info + "\n本次收益:  " + StringUtils.showPrice(monthNote.getIncome());
        if (! TextUtils.isEmpty(monthNote.getHomeuse())) info = info + "\n家用补贴:  " + StringUtils.showPrice(monthNote.getHomeuse());
        if (! TextUtils.isEmpty(monthNote.getRemark())) info = info + "\n月结备注:  " + monthNote.getRemark();
        viewHolder.month_info.setText(info);

        viewHolder.month_actual_balance.setText("实际结余:  " + StringUtils.showPrice(monthNote.getActual_balance()));
        return convertView;
    }

    class ViewHolder{
        TextView month_id, month_money, month_info, month_actual_balance;
    }
}
