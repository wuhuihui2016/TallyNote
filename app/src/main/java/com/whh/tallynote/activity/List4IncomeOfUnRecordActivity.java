package com.whh.tallynote.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.IncomeNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.StringUtils;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本次月结的理财明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4IncomeOfUnRecordActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private List<IncomeNote> unRecordInComes;
    private IncomeNoteAdapter incomeNoteAdapter;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("本次月结的理财明细", R.layout.activity_income_list_unrecord);
    }

    @Override
    protected void initView() {
        //获取截至到某一天已完成而未结算的理财列表
        unRecordInComes = IncomeNote.getUnRecordInComes();
        info.setText("投资账单记录有" + unRecordInComes.size() + "比,本次收益总金额：" + StringUtils.showPrice(IncomeNote.getUnRecordSum() + ""));

        Collections.reverse(unRecordInComes);
        listView.setEmptyView(emptyView);
        incomeNoteAdapter = new IncomeNoteAdapter(activity, unRecordInComes);
        listView.setAdapter(incomeNoteAdapter);
    }

    @Override
    protected void initEvent() {

    }

}
