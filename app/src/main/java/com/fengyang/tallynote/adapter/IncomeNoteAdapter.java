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
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.FinishIncomeActivity;
import com.fengyang.tallynote.model.Income;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class IncomeNoteAdapter extends BaseAdapter{

    private Context context;
    private List<Income> incomes;

    public IncomeNoteAdapter(Context context, List<Income> incomes) {
        this.context = context;
        this.incomes = incomes;
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
            viewHolder.income_id = (TextView) convertView.findViewById(R.id.income_id);
            viewHolder.income_money = (TextView) convertView.findViewById(R.id.income_money);
            viewHolder.income_finished = (TextView) convertView.findViewById(R.id.income_finished);
            viewHolder.income_info = (TextView) convertView.findViewById(R.id.income_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final Income income = incomes.get(position);
        viewHolder.income_id.setText(income.getDurtion().split("-")[0].substring(4, 6));
        viewHolder.income_money.setText("投入金额：" + StringUtils.showPrice(income.getMoney())  +
                "\n预期年化：" + income.getIncomeRatio() );

        String info = "投资期限:" + income.getDays()  +
                "\n投资时期:" + income.getDurtion()  +
                "\n拟日收益:" + StringUtils.showPrice(income.getDayIncome())  +
                "\n最终收益:" + StringUtils.showPrice(income.getFinalIncome());
        
        if (! TextUtils.isEmpty(income.getRemark()))  info = info + "\n投资备注：" + income.getRemark();
        
        if (income.getFinished() == 0) {//未完成
            viewHolder.income_info.setText(info);
            viewHolder.income_finished.setTextColor(Color.RED);
            viewHolder.income_finished.setText("计息中," +
                    "还剩" + DateUtils.daysBetween(income.getDurtion().split("-")[1]) + "天");
            viewHolder.income_finished.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FinishIncomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("income", incomes.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
        } else {
            
            viewHolder.income_info.setText(info + "\n最终提现：" + StringUtils.showPrice(income.getFinalCash()) + 
                    "\n提现去处：" + income.getFinalCashGo());

            viewHolder.income_finished.setTextColor(Color.GRAY);
            viewHolder.income_finished.setText("已完成");
        }

        return convertView;
    }

    class ViewHolder{
        TextView income_id, income_money, income_finished, income_info;
    }
}
