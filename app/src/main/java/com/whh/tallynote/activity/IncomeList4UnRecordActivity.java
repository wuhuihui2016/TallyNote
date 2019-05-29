package com.whh.tallynote.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.IncomeNoteAdapter;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 本次月结的理财明细
 * Created by wuhuihui on 2017/6/27.
 */
public class IncomeList4UnRecordActivity extends BaseActivity {

    private TextView info;
    private ListView listView;
    private List<IncomeNote> unRecordInComes;
    private IncomeNoteAdapter incomeNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("本次月结的理财明细", R.layout.activity_income_list_unrecord);

        //获取截至到某一天已完成而未结算的理财列表
        unRecordInComes = IncomeNote.getUnRecordInComes();
        info = (TextView) findViewById(R.id.info);
        info.setText("投资账单记录有" + unRecordInComes.size() + "比,本次收益总金额：" + StringUtils.showPrice(IncomeNote.getUnRecordSum() + ""));

        Collections.reverse(unRecordInComes);
        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));
        incomeNoteAdapter = new IncomeNoteAdapter(activity, unRecordInComes);
        listView.setAdapter(incomeNoteAdapter);
    }

}
