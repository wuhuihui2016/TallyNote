package com.fengyang.tallynote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class MonthListActivity extends BaseActivity {

    private TextView info;
    private ListView listView;

    private List<MonthNote> monthNotes;
    private MonthNoteAdapter monthNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("月账单明细", R.layout.activity_month_list);

        //删除后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_MONTH);
        registerReceiver(myReceiver, intentFilter);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        info = (TextView) findViewById(R.id.info);
        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindow();
                }
            }
        });

        initData();

    }

    //删除后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_MONTH)) {
                initData();
            }
        }
    };

    private void initData() {
        monthNotes = MyApp.utils.getMonNotes();
        info.setText("月账单记录有" + monthNotes.size() + "条");

        Collections.reverse(monthNotes);//倒序排列
        monthNoteAdapter = new MonthNoteAdapter(activity, monthNotes);
        listView.setAdapter(monthNoteAdapter);
    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
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
                        Intent intent = new Intent(activity, NewMonthActivity.class);
                        intent.putExtra("list", true);
                        startActivity(intent);
                    }
                }
        );
        layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ExcelUtils.exportMonthNote(callBackExport);
            }
        });
        layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
