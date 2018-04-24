package com.fengyang.tallynote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.DayNoteAdapter;
import com.fengyang.tallynote.database.DayNoteDao;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 日账单明细
 * Created by wuhuihui on 2017/6/27.
 */
public class DayListActivity extends BaseActivity {

    private ListView listView;

    private List<DayNote> dayNotes;
    private List<DayNote> list = new ArrayList<>();
    private DayNoteAdapter dayNoteAdapter;

    private TextView info, all, consume, account_out, account_in, homeuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("日账单明细", R.layout.activity_day_list);
        //删除后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_DAY);
        registerReceiver(myReceiver, intentFilter);

        info = (TextView) findViewById(R.id.info);
        all = (TextView) findViewById(R.id.all);
        consume = (TextView) findViewById(R.id.consume);
        account_out = (TextView) findViewById(R.id.account_out);
        account_in = (TextView) findViewById(R.id.account_in);
        homeuse = (TextView) findViewById(R.id.homeuse);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        getAll();
    }

    //删除后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_DAY)) {
                getAll();
            }
        }
    };

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater inflater = (LayoutInflater) getApplication()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_list_pop, null);
            popupWindow = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            ViewUtils.setPopupWindow(context, popupWindow);
            popupWindow.showAtLocation(findViewById(R.id.list_layout), Gravity.BOTTOM, 0, 0);

            layout.findViewById(R.id.newNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Intent intent = new Intent(activity, NewDayActivity.class);
                            intent.putExtra("list", true);
                            startActivity(intent);
                        }
                    }
            );
            layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    ExcelUtils.exportDayNote(callBackExport);
                }
            });
            layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
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
                startActivity(new Intent(activity, HighConsumeDayListActivity.class));
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
        all.setTextColor(Color.RED);
        consume.setTextColor(Color.GRAY);
        account_out.setTextColor(Color.GRAY);
        account_in.setTextColor(Color.GRAY);
        homeuse.setTextColor(Color.GRAY);
        dayNotes = DayNoteDao.getDayNotes();
        LogUtils.i("dayNotes", dayNotes.toString());
        Collections.reverse(dayNotes);
        info.setText("账单记录：" + dayNotes.size()
                + "，支出 + 转账 - 转入 + 家用：" + StringUtils.showPrice(DayNote.getAllSum() + ""));
        dayNoteAdapter = new DayNoteAdapter(activity, dayNotes, true);
        listView.setAdapter(dayNoteAdapter);
    }

    /**
     * 依据类型显示
     */
    private void getAll4UseType(int type) {
        all.setTextColor(Color.GRAY);
        consume.setTextColor(Color.GRAY);
        account_out.setTextColor(Color.GRAY);
        account_in.setTextColor(Color.GRAY);
        homeuse.setTextColor(Color.GRAY);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == type) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        if (type == DayNote.consume) {
            consume.setTextColor(Color.RED);
            info.setText("支出记录：" + list.size() + "，支出金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_out) {
            account_out.setTextColor(Color.RED);
            info.setText("转账记录：" + list.size() + "，转账金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_in) {
            account_in.setTextColor(Color.RED);
            info.setText("转入记录： " + list.size() + "，转入金额：" + StringUtils.showPrice(sum + ""));
        } else {
            homeuse.setTextColor(Color.RED);
            info.setText("家用记录： " + list.size() + "，家用金额：" + StringUtils.showPrice(sum + ""));
        }
        dayNoteAdapter = new DayNoteAdapter(activity, list, false);
        listView.setAdapter(dayNoteAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
