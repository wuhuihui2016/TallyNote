package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.DayNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 依据月账显示该月的日账明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4DayOfMonthActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.all)
    public TextView all;
    @BindView(R.id.consume)
    public TextView consume;
    @BindView(R.id.account_out)
    public TextView account_out;
    @BindView(R.id.account_in)
    public TextView account_in;
    @BindView(R.id.homeuse)
    public TextView homeuse;

    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private String duration;
    @BindView(R.id.spinner)
    public Spinner spinner;

    private List<DayNote> dayNotes;
    private List<DayNote> list = new ArrayList<>();
    private DayNoteAdapter dayNoteAdapter;

    @Override
    protected void initBundleData(Bundle bundle) {
        duration = getIntent().getStringExtra("duration");
        setContentView(duration, R.layout.activity_day_list);
    }

    @Override
    protected void initView() {
        final List<String> durations = MonthNote.getDurations();
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        for (int i = 0; i < durations.size(); i++) {
            if (durations.get(i).equals(duration)) spinner.setSelection(i);
        }
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        duration = durations.get(position);
                        setTitle(duration);
                        getAll();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        getAll();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all:
                getAll();
                break;
            case R.id.consume:
                getAll4UseType(DayNote.consume);
                break;
            case R.id.high_consume:
                AppManager.transfer(activity, List4DayOfHighConsumeActivity.class,"duration", duration);
                break;
            case R.id.account_out:
                getAll4UseType(DayNote.account_out);
                break;
            case R.id.account_in:
                getAll4UseType(DayNote.account_in);
                break;
            case R.id.homeuse:
                getAll4UseType(DayNote.homeuse);
                break;
        }
    }

    /**
     * 总账单记录
     */
    private void getAll() {
        all.setTextColor(Color.parseColor("#007500"));
        all.setBackgroundResource(R.drawable.shape_type_btn);
        consume.setTextColor(Color.GRAY);
        consume.setBackgroundResource(R.drawable.shape_type);
        account_out.setTextColor(Color.GRAY);
        account_out.setBackgroundResource(R.drawable.shape_type);
        account_in.setTextColor(Color.GRAY);
        account_in.setBackgroundResource(R.drawable.shape_type);
        homeuse.setTextColor(Color.GRAY);
        homeuse.setBackgroundResource(R.drawable.shape_type);
        dayNotes = MyApp.dayNoteDBHandle.getDayNotes4History(duration);
        Collections.reverse(dayNotes);
        if (dayNotes.size() > 0) {
            info.setText("账单记录：" + dayNotes.size()
                    + "，支出 + 转账 - 入账 + 家用：" + StringUtils.showPrice(DayNote.getAllSum() + ""));
            dayNoteAdapter = new DayNoteAdapter(activity, dayNotes, false);
            listView.setAdapter(dayNoteAdapter);
        } else {
            ToastUtils.showWarningLong(activity, duration + "时段内未找到日账单明细！\n可能是首次月账！");
            finish();
        }
    }

    /**
     * 依据类型显示
     */
    private void getAll4UseType(int type) {
        all.setTextColor(Color.GRAY);
        all.setBackgroundResource(R.drawable.shape_type);
        consume.setTextColor(Color.GRAY);
        consume.setBackgroundResource(R.drawable.shape_type);
        account_out.setTextColor(Color.GRAY);
        account_out.setBackgroundResource(R.drawable.shape_type);
        account_in.setTextColor(Color.GRAY);
        account_in.setBackgroundResource(R.drawable.shape_type);
        homeuse.setTextColor(Color.GRAY);
        homeuse.setBackgroundResource(R.drawable.shape_type);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == type) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        if (type == DayNote.consume) {
            consume.setTextColor(Color.parseColor("#007500"));
            consume.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("支出记录：" + list.size() + "，支出金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_out) {
            account_out.setTextColor(Color.parseColor("#007500"));
            account_out.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("转账记录：" + list.size() + "，转账金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_in) {
            account_in.setTextColor(Color.parseColor("#007500"));
            account_in.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("入账记录： " + list.size() + "，入账金额：" + StringUtils.showPrice(sum + ""));
        } else {
            homeuse.setTextColor(Color.parseColor("#007500"));
            homeuse.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("家用记录： " + list.size() + "，家用金额：" + StringUtils.showPrice(sum + ""));
        }
        dayNoteAdapter = new DayNoteAdapter(activity, list, false);
        listView.setAdapter(dayNoteAdapter);
    }


}
