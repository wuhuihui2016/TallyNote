package com.whh.tallynote.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.IncomeNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.IncomeNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 往期理财记录日收益比较 比较显示收益大的理财列表
 * Created by wuhuihui on 2017/07/07.
 */
public class CompareActivity extends BaseActivity {

    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;
    @BindView(R.id.info)
    public TextView info;

    private List<IncomeNote> incomeNotes;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("往期理财记录日收益比较", R.layout.activity_compare);
    }
    @Override
    protected void initView() {
        listView.setEmptyView(emptyView);
        incomeNotes = MyApp.incomeNoteDBHandle.getIncomeNotes();

        if (getIntent().hasExtra("cal_result")) {
            String cal_result = getIntent().getStringExtra("cal_result");
            Double result = Double.parseDouble(cal_result);
            List<IncomeNote> list = new ArrayList<>();
            for (int i = 0; i < incomeNotes.size(); i++) {
                Double dayIncome = Double.parseDouble(incomeNotes.get(i).getDayIncome());
                if (dayIncome > result) {
                    list.add(incomeNotes.get(i));
                }
            }

            info.setText("日收益大于 " + cal_result + " 的有 " + list.size() + "条记录!");
            Collections.reverse(list);//倒序排列
            listView.setAdapter(new IncomeNoteAdapter(activity, list, true));

        } else {
            Collections.reverse(incomeNotes);//倒序排列
            listView.setAdapter(new IncomeNoteAdapter(activity, incomeNotes, true));
        }


    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }


}
