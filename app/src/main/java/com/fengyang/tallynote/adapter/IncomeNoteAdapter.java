package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.DetailsActivity;
import com.fengyang.tallynote.activity.FinishIncomeActivity;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class IncomeNoteAdapter extends BaseAdapter{

    private Context context;
    private List<IncomeNote> incomes;
    private boolean isLast;//列表显示按投资时间排序时，最后一个才可做删除操作

    public IncomeNoteAdapter(Context context, List<IncomeNote> incomes, boolean isLast) {
        this.context = context;
        this.incomes = incomes;
        this.isLast = isLast;
    }

    @Override
    public int getCount() {
        return incomes.size();
    }

    @Override
    public Object getItem(int position) {
        return incomes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.income_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.item_income_layout = (RelativeLayout) convertView.findViewById(R.id.item_income_layout);
            viewHolder.income_id = (TextView) convertView.findViewById(R.id.income_id);
            viewHolder.income_money = (TextView) convertView.findViewById(R.id.income_money);
            viewHolder.income_finished = (TextView) convertView.findViewById(R.id.income_finished);
            viewHolder.income_info = (TextView) convertView.findViewById(R.id.income_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final IncomeNote incomeNote = incomes.get(position);
        viewHolder.income_id.setText(incomeNote.getDurtion().split("-")[0].substring(4, 6));
        viewHolder.income_money.setText("投入金额：" + StringUtils.showPrice(incomeNote.getMoney()) +
                " 万元\n预期年化：" + StringUtils.formatePrice(incomeNote.getIncomeRatio()) + " %" );

        String info = "投资期限：" + incomeNote.getDays()  +
                " 天\n投资时期：" + incomeNote.getDurtion()  +
                "\n拟日收益：" + StringUtils.showPrice(incomeNote.getDayIncome())  +
                " 元/万/天\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) + " 元";
        
        if (! TextUtils.isEmpty(incomeNote.getRemark()))  info += "\n投资备注：" + incomeNote.getRemark();
        
        if (incomeNote.getFinished() == 0) {//未完成
            viewHolder.income_info.setText(info);
            viewHolder.income_finished.setTextColor(Color.RED);

            int day = DateUtils.daysBetween(incomeNote.getDurtion().split("-")[1]);
            if (day < 0) {
                viewHolder.income_finished.setText("已经结束,请完成 >");
            } else if (day == 0) {
                viewHolder.income_finished.setText("今日到期！可完成 >");
            } else {
                viewHolder.income_finished.setText("计息中,还剩 " + DateUtils.daysBetween(incomeNote.getDurtion().split("-")[1]) + " 天");
            }

            if (day <= 0) {
                viewHolder.income_finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FinishIncomeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("incomeNote", incomeNote);
                        intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                });
            } else {
                viewHolder.income_finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringUtils.show1Toast(context,
                                "计息中,还剩 " + DateUtils.daysBetween(incomeNote.getDurtion().split("-")[1]) + " 天");
                    }
                });
            }

        } else {
            viewHolder.income_info.setText(info + "\n最终提现：" + StringUtils.showPrice(incomeNote.getFinalCash()) +
                    " 元\n提现去处：" + incomeNote.getFinalCashGo());
            viewHolder.income_finished.setTextColor(Color.GRAY);
            viewHolder.income_finished.setText("已完成！");
        }

        viewHolder.item_income_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("type", MyApp.INCOME);
                if (position == 0 && isLast) intent.putExtra("last", true);//列表显示按投资时间排序时，最后一个才可做删除操作
                intent.putExtra("note", incomeNote);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        RelativeLayout item_income_layout;
        TextView income_id, income_money, income_finished, income_info;
    }
}
