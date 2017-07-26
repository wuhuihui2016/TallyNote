package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.IncomeNoteAdapter;
import com.fengyang.tallynote.model.IncomeNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 往期理财记录日收益比较
 * Created by wuhuihui on 2017/07/07.
 */
public class CompareActivity extends BaseActivity {

    private ListView listView;
    private TextView emptyView;

    private List<IncomeNote> incomeNotes;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("往期理财记录日收益比较", R.layout.activity_compare);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView () {
        info = (TextView) findViewById(R.id.info);
        listView = (ListView) findViewById(R.id.listView);
        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        incomeNotes = MyApp.utils.getIncomes();

        if (getIntent().hasExtra("cal_result")) {
            String cal_result = getIntent().getStringExtra("cal_result");
            Double result = Double.parseDouble(cal_result);
            List<IncomeNote> list = new ArrayList<>();
            for (int i = 0; i < incomeNotes.size(); i ++) {
                Double dayIncome = Double.parseDouble(incomeNotes.get(i).getDayIncome());
                if (dayIncome > result) {
                    list.add(incomeNotes.get(i));
                }
            }

            info.setText("与日收益 " + cal_result + "比较，共有 " + incomeNotes.size() + "条记录，日收益大于 "
                    + cal_result + "的有 " + list.size() + "条记录!");
            Collections.reverse(list);//倒序排列
            listView.setAdapter(new IncomeNoteAdapter(activity, list, false));

        } else {
            Collections.reverse(incomeNotes);//倒序排列
            listView.setAdapter(new IncomeNoteAdapter(activity, incomeNotes, false));
        }


    }


}
