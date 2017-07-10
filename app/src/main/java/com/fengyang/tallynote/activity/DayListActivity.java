package com.fengyang.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.DayNoteAdapter;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class DayListActivity extends BaseActivity {

    private ListView listView;
    private TextView emptyView;

    private List<DayNote> dayNotes;
    private List<DayNote> list = new ArrayList<>();
    private DayNoteAdapter dayNoteAdapter;

    private TextView info, all, consume, account_out, account_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("日账单列表", R.layout.activity_day_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView () {
        info = (TextView) findViewById(R.id.info);
        all = (TextView) findViewById(R.id.all);
        consume = (TextView) findViewById(R.id.consume);
        account_out = (TextView) findViewById(R.id.account_out);
        account_in = (TextView) findViewById(R.id.account_in);

        listView = (ListView) findViewById(R.id.listView);

        dayNotes = MyApp.utils.getDayNotes();
        Collections.reverse(dayNotes);
        getAll();

        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExcelUtils.exportDayNote(callBackExport);
            }
        });

    }

    /**
     * 导出结果回调
     */
    private ExcelUtils.ICallBackExport callBackExport = new ExcelUtils.ICallBackExport() {
        @Override
        public void callback(boolean sucess, String fileName) {
            if (sucess) ToastUtils.showSucessLong(context, "导出成功:" + fileName);
            else ToastUtils.showErrorLong(context, "导出失败！");
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all: getAll(); break;
            case R.id.consume: getConsume(); break;
            case R.id.account_out: getAccountOut(); break;
            case R.id.account_in: getAccountIn(); break;
        }
    }

    /**
     * 总账单记录
     */
    private void getAll () {
        all.setTextColor(Color.RED);
        consume.setTextColor(Color.GRAY);
        account_out.setTextColor(Color.GRAY);
        account_in.setTextColor(Color.GRAY);
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.consume) sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_out) sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_in) sum -= Double.parseDouble(dayNotes.get(i).getMoney());
        }
        info.setText("当前总账单记录有 " + dayNotes.size()
                + " 条，+ 支出 + 转账 - 转入：" + StringUtils.showPrice(sum + ""));
        dayNoteAdapter = new DayNoteAdapter(context, dayNotes);
        listView.setAdapter(dayNoteAdapter);
    }

    /**
     * 支出账单记录
     */
    private void getConsume () {
        all.setTextColor(Color.GRAY);
        consume.setTextColor(Color.RED);
        account_out.setTextColor(Color.GRAY);
        account_in.setTextColor(Color.GRAY);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.consume) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        info.setText("当前支出账单记录有 " + list.size() + " 条，支出金额：" + StringUtils.showPrice(sum + "") + "元");
        dayNoteAdapter = new DayNoteAdapter(context, list);
        listView.setAdapter(dayNoteAdapter);
    }

    /**
     * 转账记录
     */
    private void getAccountOut() {
        all.setTextColor(Color.GRAY);
        consume.setTextColor(Color.GRAY);
        account_out.setTextColor(Color.RED);
        account_in.setTextColor(Color.GRAY);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.account_out) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        info.setText("当前转账记录有 " + list.size() + " 条，转账金额：" + StringUtils.showPrice(sum + "") + "元");
        dayNoteAdapter = new DayNoteAdapter(context, list);
        listView.setAdapter(dayNoteAdapter);
    }

    /**
     * 转入记录
     */
    private void getAccountIn() {
        all.setTextColor(Color.GRAY);
        consume.setTextColor(Color.GRAY);
        account_out.setTextColor(Color.GRAY);
        account_in.setTextColor(Color.RED);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.account_in) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        info.setText("当前转入记录有 " + list.size() + " 条，转入金额：" + StringUtils.showPrice(sum + "") + "元");
        dayNoteAdapter = new DayNoteAdapter(context, list);
        listView.setAdapter(dayNoteAdapter);
    }
}
