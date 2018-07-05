package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.CompareActivity;
import com.fengyang.tallynote.activity.FinishIncomeActivity;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.util.List;

import static com.fengyang.tallynote.R.id.detail_layout;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class IncomeNoteAdapter extends BaseAdapter {

    private Activity activity;
    private List<IncomeNote> incomeNotes;
//    private boolean isLast;//列表显示按投资时间排序时，最后一个才可做删除操作
    private boolean isCompare = false;//列表为比较收益列表

    public IncomeNoteAdapter(Activity activity, List<IncomeNote> incomeNotes) {
        this.activity = activity;
        this.incomeNotes = incomeNotes;
    }

//    public IncomeNoteAdapter(Activity activity, List<IncomeNote> incomeNotes, boolean isLast) {
//        this.activity = activity;
//        this.incomeNotes = incomeNotes;
//        this.isLast = isLast;
//    }

    public IncomeNoteAdapter(Activity activity, List<IncomeNote> incomeNotes, boolean isCompare) {
        this.activity = activity;
        this.incomeNotes = incomeNotes;
        this.isCompare = isCompare;
    }

    @Override
    public int getCount() {
        return incomeNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return incomeNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.income_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.detail_layout = (LinearLayout) convertView.findViewById(detail_layout);
            viewHolder.income_del = (ImageView) convertView.findViewById(R.id.income_delete);
            viewHolder.income_time = (TextView) convertView.findViewById(R.id.income_time);
            viewHolder.income_money = (TextView) convertView.findViewById(R.id.income_money);
            viewHolder.income_ratio = (TextView) convertView.findViewById(R.id.income_ratio);
            viewHolder.income_days = (TextView) convertView.findViewById(R.id.income_days);
            viewHolder.income_durtion = (TextView) convertView.findViewById(R.id.income_durtion);
            viewHolder.income_dayIncome = (TextView) convertView.findViewById(R.id.income_dayIncome);
            viewHolder.income_finalIncome = (TextView) convertView.findViewById(R.id.income_finalIncome);
            viewHolder.income_remark = (TextView) convertView.findViewById(R.id.income_remark);
            viewHolder.income_finished = (TextView) convertView.findViewById(R.id.income_finished);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final IncomeNote incomeNote = incomeNotes.get(position);

        String id = incomeNote.getId();
        String time = incomeNote.getDurtion().split("-")[1].substring(4, 8);
        SpannableStringBuilder style = new SpannableStringBuilder(id + "\n" + time);
        style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, id.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED), id.length(), (id + "\n" + time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.income_time.setText(style);
        viewHolder.income_money.setText(StringUtils.showPrice(incomeNote.getMoney()));
        viewHolder.income_ratio.setText(incomeNote.getIncomeRatio() + " %");
        viewHolder.income_days.setText(incomeNote.getDays() + " 天");
        viewHolder.income_durtion.setText(incomeNote.getDurtion());
        viewHolder.income_dayIncome.setText(incomeNote.getDayIncome() + " 元/万/天");
        viewHolder.income_finalIncome.setText(StringUtils.showPrice(incomeNote.getFinalIncome()));
        if (incomeNote.getRemark().length() > 0)
            viewHolder.income_remark.setText(incomeNote.getRemark());
        else viewHolder.income_remark.setText("无");

        final int day = DateUtils.daysBetween(incomeNote.getDurtion().split("-")[1]);
        if (incomeNote.getFinished() == IncomeNote.ON) {//未完成
            viewHolder.income_finished.setTextColor(Color.RED);

            if (day < 0) {
                viewHolder.income_finished.setText("已经结束,请完成 >");
            } else if (day == 0) {
                viewHolder.income_finished.setText("今日到期！可完成 >");
            } else {
                if (day > Integer.parseInt(incomeNote.getDays())) {
                    viewHolder.income_finished.setText((day - Integer.parseInt(incomeNote.getDays()) + " 天后开始收益"));
                } else {
                    viewHolder.income_finished.setText("计息中,还剩 " + day + " 天");
                }
            }

            if (day <= 0) {
                viewHolder.income_finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, FinishIncomeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("incomeNote", incomeNote);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);

                    }
                });
            } else {
                viewHolder.income_finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (day > Integer.parseInt(incomeNote.getDays())) {
                            ToastUtils.showToast(activity, true, (day - Integer.parseInt(incomeNote.getDays()) + " 天后开始收益"));
                        } else {
                            ToastUtils.showToast(activity, true, "计息中,还剩 " + day + " 天");
                        }
                    }
                });
            }

        } else {
            viewHolder.income_finished.setTextColor(Color.GRAY);
            viewHolder.income_finished.setText("已完成：" + StringUtils.showPrice(incomeNote.getFinalCash()) +
                    "已提现，" + incomeNote.getFinalCashGo());
        }

        if (!isCompare) {
            viewHolder.detail_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, CompareActivity.class);
                    intent.putExtra("cal_result", incomeNote.getDayIncome());
                    activity.startActivity(intent);
                }
            });
        }

//        if (position == 0 && isLast) {
//            viewHolder.income_del.setVisibility(View.VISIBLE);
//            viewHolder.income_del.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除此条记录", new DialogUtils.DialogListener() {
//                        @Override
//                        public void onClick(View v) {
//                            super.onClick(v);
//                            IncomeNoteDao.delIncome(incomeNotes.get(0));
//                            incomeNotes = IncomeNoteDao.getIncomes();
//                            notifyDataSetChanged();
//
//                            activity.sendBroadcast(new Intent(ContansUtils.ACTION_INCOME));
//                        }
//                    }, new DialogUtils.DialogListener() {
//                        @Override
//                        public void onClick(View v) {
//                            super.onClick(v);
//                        }
//                    });
//                }
//            });
//        } else viewHolder.income_del.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder {
        LinearLayout detail_layout;
        ImageView income_del;
        TextView income_time, income_money, income_ratio, income_days, income_durtion;
        TextView income_dayIncome, income_finalIncome, income_remark, income_finished;
    }
}
