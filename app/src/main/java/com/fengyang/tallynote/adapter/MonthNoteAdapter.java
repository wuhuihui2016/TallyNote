package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MonthNoteAdapter extends BaseAdapter{

    private Activity activity;
    private List<MonthNote> monthNotes;

    public MonthNoteAdapter(Activity activity, List<MonthNote> monthNotes) {
        this.activity = activity;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.month_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.month_time = (TextView) convertView.findViewById(R.id.month_time);
            viewHolder.month_pay = (TextView) convertView.findViewById(R.id.month_pay);
            viewHolder.month_salary = (TextView) convertView.findViewById(R.id.month_salary);
            viewHolder.month_last_balance = (TextView) convertView.findViewById(R.id.month_last_balance);
            viewHolder.month_balance = (TextView) convertView.findViewById(R.id.month_balance);
            viewHolder.month_income = (TextView) convertView.findViewById(R.id.month_income);
            viewHolder.month_homeuse = (TextView) convertView.findViewById(R.id.month_homeuse);
            viewHolder.month_remark = (TextView) convertView.findViewById(R.id.month_remark);
            viewHolder.month_actual_balance = (TextView) convertView.findViewById(R.id.month_actual_balance);
            viewHolder.month_del = (ImageView) convertView.findViewById(R.id.month_del);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final MonthNote monthNote = monthNotes.get(position);
        viewHolder.month_time.setText(monthNote.getDuration().split("-")[0].substring(4, 6));
        viewHolder.month_pay.setText(StringUtils.showPrice(monthNote.getPay()));
        viewHolder.month_salary.setText(StringUtils.showPrice(monthNote.getSalary()));
        viewHolder.month_last_balance.setText(StringUtils.showPrice(monthNote.getLast_balance()));
        viewHolder.month_balance.setText(StringUtils.showPrice(monthNote.getBalance()));
        viewHolder.month_income.setText(StringUtils.showPrice(monthNote.getIncome()));
        viewHolder.month_homeuse.setText(StringUtils.showPrice(monthNote.getHomeuse()));
        viewHolder.month_remark.setText(monthNote.getRemark());
        viewHolder.month_actual_balance.setText("实际结余：" + StringUtils.showPrice(monthNote.getActual_balance()));

        if (position == 0) {
            viewHolder.month_del.setVisibility(View.VISIBLE);
            viewHolder.month_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除此条记录", new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            MyApp.utils.delMNote(monthNote);

                            notifyDataSetChanged();
                        }
                    }, new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                        }
                    });
                }
            });
        } else viewHolder.month_del.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder{
        ImageView month_del;
        TextView month_time, month_pay, month_salary, month_last_balance;
        TextView month_balance, month_income, month_homeuse, month_remark, month_actual_balance;
    }
}
