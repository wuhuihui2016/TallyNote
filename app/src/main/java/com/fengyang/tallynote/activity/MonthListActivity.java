package com.fengyang.tallynote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.database.DayNoteDao;
import com.fengyang.tallynote.database.MonthNoteDao;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.DialogListener;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.ViewUtils;
import com.fengyang.tallynote.utils.WPSUtils;

import java.util.Collections;
import java.util.List;

/**
 * 月账单明细
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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_MONTH);
        registerReceiver(myReceiver, intentFilter);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        info = (TextView) findViewById(R.id.info);
        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        initData();

        if (getIntent().hasExtra("flag")) {
            showDialog();
        }

    }

    private void initData() {
        monthNotes = MonthNoteDao.getMonthNotes();
        info.setText("月账单记录有" + monthNotes.size() + "笔");

        if (monthNotes.size() > 1) {
            findViewById(R.id.chartAnalyse).setVisibility(View.VISIBLE);
            findViewById(R.id.chartAnalyse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, MonthNotesAnalyseActivity.class));
                }
            });
        }

        Collections.reverse(monthNotes);//倒序排列
        monthNoteAdapter = new MonthNoteAdapter(activity, monthNotes);
        listView.setAdapter(monthNoteAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DayNoteDao.getDayNotes4History().size() > 0) {
                    Intent intent = new Intent(activity, DayListOfMonthActivity.class);
                    intent.putExtra("duration", monthNotes.get(position).getDuration());
                    startActivity(intent);
                }
            }
        });
    }

    //提交月账监听
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_MONTH)) {
                initData();
                showDialog();
            }
        }
    };


    /**
     * 新建月账完成，提醒上传数据
     */
    private void showDialog() {
        new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                DialogUtils.showMsgDialog(activity, "新月账单已生成,是否现在上传数据？",
                        "上传", new DialogListener() {
                            @Override
                            public void onClick() {
                                FileUtils.uploadFile(activity);
                            }
                        },"查看", new DialogListener() {
                            @Override
                            public void onClick() {
                                WPSUtils.openFile(context, FileUtils.getTallyNoteFile().getPath());
                            }
                        });
            }
        }).execute();
    }

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
            layout.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    FileUtils.uploadFile(activity);
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
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
