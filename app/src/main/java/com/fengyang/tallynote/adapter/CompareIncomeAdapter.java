package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.DetailsActivity;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class CompareIncomeAdapter extends BaseAdapter{

    private Context context;
    private List<IncomeNote> incomes;

    public CompareIncomeAdapter(Context context, List<IncomeNote> incomes) {
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

        viewHolder.income_info.setText(info);
        viewHolder.income_finished.setVisibility(View.GONE);

        viewHolder.item_income_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("type", MyApp.INCOME);
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
