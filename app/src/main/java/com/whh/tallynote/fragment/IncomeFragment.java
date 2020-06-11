package com.whh.tallynote.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.activity.List4IncomeActivity;
import com.whh.tallynote.base.BaseFragment;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;

import java.util.List;

/**
 * 理财
 */
public class IncomeFragment extends BaseFragment {

    private static final String TAG = "IncomeFragment";

    private boolean isSeen = false;
    private ImageButton seenCheck;
    private TextView currIncomeSum;
    private String sumStr; //投资总额

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_income, container, false);
        activity = getActivity();
        seenCheck = (ImageButton) content.findViewById(R.id.seenCheck);
        return content;
    }

    @Override
    public void onResume() {
        super.onResume();
        showIncomeNote();
    }

    /**
     * 显示最近一次收益的理财记录
     */
    IncomeNote lastIncomeNote = null;

    private void showIncomeNote() {
        isSeen = true;
        seenCheck.setImageResource(R.drawable.eye_close_pwd);

        List<IncomeNote> earningInComes = IncomeNote.getEarningInComes(); //未完成的
        currIncomeSum = (TextView) content.findViewById(R.id.currIncomeSum);
        LinearLayout income_layout = (LinearLayout) content.findViewById(R.id.income_layout);
        if (earningInComes.size() > 0) {
            //显示当前未完成的理财投资的总金额
            sumStr = "当前投资总金额：" + StringUtils.showPrice(IncomeNote.getEarningMoney() + "");
            if (IncomeNote.getUnRecordSum() > 0) {
                sumStr += "\n待计入月账单的收益金额：" + StringUtils.showPrice("" + IncomeNote.getUnRecordSum());
            }
            currIncomeSum.setText("....");

            income_layout.setVisibility(View.VISIBLE);
            //显示最近一次收益的理财记录
            lastIncomeNote = IncomeNote.getLastIncomeNote();
            if (lastIncomeNote != null) {
                LogUtils.i("lastIncomeNote", lastIncomeNote.toString());
                TextView income_time = (TextView) content.findViewById(R.id.income_time);
                TextView income_money = (TextView) content.findViewById(R.id.income_money);
                TextView income_ratio = (TextView) content.findViewById(R.id.income_ratio);
                TextView income_staus = (TextView) content.findViewById(R.id.income_staus);

                String id = lastIncomeNote.getId();
                String time = lastIncomeNote.getDurtion().split("-")[1].substring(4, 8);
                SpannableStringBuilder style = new SpannableStringBuilder(id + "\n" + time);
                style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, id.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.RED), id.length(), (id + "\n" + time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                income_time.setText(style);
                income_money.setText(StringUtils.showPrice(lastIncomeNote.getMoney()));
                income_ratio.setText(lastIncomeNote.getIncomeRatio() + " %");
                if (lastIncomeNote.getFinished() == IncomeNote.ON) {//未完成
                    int day = DateUtils.daysBetween(lastIncomeNote.getDurtion().split("-")[1]);
                    if (day < 0) {
                        income_staus.setText("已经结束,请完成 >");
                    } else if (day == 0) {
                        income_staus.setText("今日到期！可完成 >");
                    } else {
                        income_staus.setText("计息中,还剩 " + day + " 天");
                    }

                }

                income_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppManager.transfer(activity, List4IncomeActivity.class,"income", true);
                    }
                });
            }
        } else {
            sumStr = "暂无投资";
            currIncomeSum.setText("....");
            income_layout.setVisibility(View.GONE);
        }

        seenCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //密文明文显示
                if (isSeen) {
                    isSeen = false;
                    seenCheck.setImageResource(R.drawable.eye_open_pwd);
                    currIncomeSum.setText(sumStr);
                } else {
                    isSeen = true;
                    seenCheck.setImageResource(R.drawable.eye_close_pwd);
                    currIncomeSum.setText("....");

                }
            }
        });
        content.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIncomeNote();
            }
        });
    }

}
