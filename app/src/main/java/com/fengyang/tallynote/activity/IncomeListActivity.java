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
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.NotificationUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

/**
 * 理财明细
 * Created by wuhuihui on 2017/6/27.
 */
public class IncomeListActivity extends BaseActivity {

    private TextView info;
    private ListView listView;

    private int index = 0;
    private List<IncomeNote> incomeNotes4index;
    private IncomeNoteAdapter incomeNoteAdapter;
    private TextView sort_info, income_earning, income_finished;
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("理财明细", R.layout.activity_income_list);

        initView();

        initData(0);

    }

    private void initView() {
        //新增或完成后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_INCOME);
        registerReceiver(myReceiver, intentFilter);

        income_earning = (TextView) findViewById(R.id.income_earning);
        income_earning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(0);
            }
        });
        income_finished = (TextView) findViewById(R.id.income_finished);
        income_finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_info.setText("按投资时间排序");
                initData(1);
            }
        });

        info = (TextView) findViewById(R.id.info);
        info.setText("投资账单记录有" + IncomeNoteDao.getIncomes().size() + "条" +
                "\n计息中" + IncomeNote.getEarningInComes().size() + "条" +
                "\n计息中的总金额：" + StringUtils.showPrice(IncomeNote.getEarningMoney() + ""));
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

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        NotificationUtils.cancel();//关闭通知
    }

    //新增或完成后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_INCOME)) {
                info.setText("投资账单记录有" + IncomeNoteDao.getIncomes().size() + "条" +
                        "\n计息中" + IncomeNote.getEarningInComes().size() + "条" +
                        "\n计息中的总金额：" + StringUtils.showPrice(IncomeNote.getEarningMoney() + ""));
                initData(0);
            }
        }
    };

    /**
     * 依据计息中还是已完成来显示理财记录
     *
     * @param index
     */
    private boolean unSelected = true;

    private void initData(int index) {

        income_earning.setBackgroundResource(R.color.transparent);
        income_finished.setBackgroundResource(R.color.transparent);
        this.index = index;
        if (index == 0) {
            income_earning.setBackgroundResource(R.drawable.shape_left_btn_bkg);
            incomeNotes4index = IncomeNote.getEarningInComes();
        } else {
            income_finished.setBackgroundResource(R.drawable.shape_right_btn_bkg);
            incomeNotes4index = IncomeNote.getFinishedInComes();
        }
        Collections.reverse(incomeNotes4index);
        incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
        listView.setAdapter(incomeNoteAdapter);

        if (unSelected) {
            if (getIntent().hasExtra("position")) {
                new DelayTask(1000, new DelayTask.ICallBack() {
                    @Override
                    public void deal() {
                        int position = getIntent().getIntExtra("position", 0);
                        IncomeNote curIncomeNote = IncomeNoteDao.getIncomes().get(position);
                        for (int i = 0; i < incomeNotes4index.size(); i++) {
                            if (curIncomeNote.getId().equals(incomeNotes4index.get(i).getId())) {
                                listView.setSelection(i);
                                break;
                            }
                        }
                    }
                }).execute();
            }
        }
        unSelected = false;
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
        if (incomeNotes4index.size() > 0) {
            sort_info.setText("按投资时间排序");
            if (index == 0) isStart = true;
            else isStart = false;
            Collections.reverse(incomeNotes4index);//倒序排列
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    /**
     * 以到期时间排序
     */
    private void sort4End() {
        if (incomeNotes4index.size() > 0) {
            isStart = false;
            sort_info.setText("按到期时间排序");
            for (int i = 0; i < incomeNotes4index.size() - 1; i++) {
                for (int j = 1; j < incomeNotes4index.size() - i; j++) {
                    IncomeNote incomeNote;
                    int days1 = DateUtils.daysBetween(incomeNotes4index.get(j).getDurtion().split("-")[1]);
                    int days2 = DateUtils.daysBetween(incomeNotes4index.get(j - 1).getDurtion().split("-")[1]);
                    if (days1 < days2) {
                        incomeNote = incomeNotes4index.get(j - 1);
                        incomeNotes4index.set((j - 1), incomeNotes4index.get(j));
                        incomeNotes4index.set(j, incomeNote);
                    }
                }
            }
            LogUtils.i("sort", incomeNotes4index.toString());
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
