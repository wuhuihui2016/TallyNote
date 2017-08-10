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
import com.fengyang.tallynote.adapter.IncomeNoteAdapter;
import com.fengyang.tallynote.database.IncomeNoteDao;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.NotificationUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

/**理财明细
 * Created by wuhuihui on 2017/6/27.
 */
public class IncomeListActivity extends BaseActivity {

    private TextView info;
    private ListView listView;

    private List<IncomeNote> incomeNotes;
    private IncomeNoteAdapter incomeNoteAdapter;
    private TextView sort_info;
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("理财明细", R.layout.activity_income_list);
        //删除后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_INCOME);
        registerReceiver(myReceiver, intentFilter);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        info = (TextView) findViewById(R.id.info);
        sort_info = (TextView) findViewById(R.id.sort_info);
        sort_info.setVisibility(View.VISIBLE);
        sort_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initpopupWindow();
                }
            }
        });

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        initData();

        if (getIntent().hasExtra("position")) {
            listView.setSelection(getIntent().getIntExtra("position", 0));
        }

        NotificationUtils.cancel();//关闭通知

    }

    //删除后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_INCOME)) {
                initData();
            }
        }
    };

    private void initData() {
        incomeNotes = IncomeNoteDao.getIncomes();
        info.setText("投资账单记录有" + incomeNotes.size() + "条,计息中" + IncomeNote.getEarningInComes().size() + "条");

        Collections.reverse(incomeNotes);//倒序排列
        incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes, isStart);
        listView.setAdapter(incomeNoteAdapter);
    }

    /**
     * 初始化popupWindow
     */
    private void initpopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater inflater = (LayoutInflater) getApplication()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_sort_pop, null);
            popupWindow = new PopupWindow(layout, 300, 200);
            ViewUtils.setPopupWindow(context, popupWindow);
            popupWindow.showAsDropDown(findViewById(R.id.sort_info), 80, -30);

            final TextView start_time = (TextView) layout.findViewById(R.id.start_time);
            final TextView end_time = (TextView) layout.findViewById(R.id.end_time);

            if (isStart) {
                start_time.setTextColor(Color.RED);
                end_time.setTextColor(Color.BLACK);
            } else {
                start_time.setTextColor(Color.BLACK);
                end_time.setTextColor(Color.RED);
            }

            start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_time.setTextColor(Color.RED);
                    end_time.setTextColor(Color.BLACK);
                    popupWindow.dismiss();
                    sort4Start();
                }
            });

            end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_time.setTextColor(Color.BLACK);
                    end_time.setTextColor(Color.RED);
                    popupWindow.dismiss();
                    sort4End();
                }
            });

        }
    }

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
                        Intent intent = new Intent(activity, NewIncomeActivity.class);
                        intent.putExtra("list", true);
                        startActivity(intent);
                    }
                }
        );
        layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ExcelUtils.exportIncomeNote(callBackExport);
            }
        });
        layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 以投资时间排序
     */
    private void sort4Start() {
        if (!isStart) {
            incomeNotes = IncomeNoteDao.getIncomes();
            if (incomeNotes.size() > 0) {
                sort_info.setText("按投资时间排序：");
                isStart = true;
                Collections.reverse(incomeNotes);//倒序排列
                incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes, isStart);
                listView.setAdapter(incomeNoteAdapter);
            }
        }
    }

    /**
     * 以到期时间排序
     */
    private void sort4End() {
        incomeNotes = IncomeNote.getEarningInComes();
        if (incomeNotes.size() > 0) {
            isStart = false;
            sort_info.setText("按到期时间排序：");
            for (int i = 0; i < incomeNotes.size() - 1; i++) {
                for (int j = 1; j < incomeNotes.size() - i; j++) {
                    IncomeNote incomeNote;
                    int days1 = DateUtils.daysBetween(incomeNotes.get(j).getDurtion().split("-")[1]);
                    int days2 = DateUtils.daysBetween(incomeNotes.get(j - 1).getDurtion().split("-")[1]);
                    if (days1 < days2) {
                        incomeNote = incomeNotes.get(j - 1);
                        incomeNotes.set((j - 1), incomeNotes.get(j));
                        incomeNotes.set(j, incomeNote);
                    }
                }
            }
            LogUtils.i("sort", incomeNotes.toString());
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes, isStart);//列表显示按投资时间排序时，最后一个才可做删除操作
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
