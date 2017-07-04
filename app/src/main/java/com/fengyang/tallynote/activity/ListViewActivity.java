package com.fengyang.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.DayNoteAdapter;
import com.fengyang.tallynote.adapter.IncomeNoteAdapter;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class ListViewActivity extends BaseActivity {

    private int type;//0：日账单，1：月账单，其他：理财记录
    private ListView listView;
    private TextView emptyView;

    private List<DayNote> dayNotes;
    private DayNoteAdapter dayNoteAdapter;

    private List<MonthNote> monthNotes;
    private MonthNoteAdapter monthNoteAdapter;

    private List<IncomeNote> incomes;
    private IncomeNoteAdapter incomeNoteAdapter;
    private TextView sort_info;
    private PopupWindow popupWindow;
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra("type", MyApp.DAY);
        if (type == MyApp.DAY) setContentView("日账单列表", R.layout.activity_list);
        else if (type == MyApp.MONTH) setContentView("月账单列表", R.layout.activity_list);
        else setContentView("理财列表", R.layout.activity_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView () {
        listView = (ListView) findViewById(R.id.listView);

        if (type == MyApp.DAY) {
            dayNotes = MyApp.utils.getDayNotes();
            Collections.reverse(dayNotes);//倒序排列
            dayNoteAdapter = new DayNoteAdapter(context, dayNotes);
            listView.setAdapter(dayNoteAdapter);

        } else if (type == MyApp.MONTH){
            monthNotes = MyApp.utils.getMonNotes();
            Collections.reverse(monthNotes);//倒序排列
            monthNoteAdapter = new MonthNoteAdapter(context, monthNotes);
            listView.setAdapter(monthNoteAdapter);

        } else if (type == MyApp.INCOME){
            monthNotes = MyApp.utils.getMonNotes();
            incomes = MyApp.utils.getIncomes();
            Collections.reverse(incomes);//倒序排列
            incomeNoteAdapter = new IncomeNoteAdapter(context, incomes, isStart);
            listView.setAdapter(incomeNoteAdapter);

            sort_info = (TextView) findViewById(R.id.sort_info);
            sort_info.setVisibility(View.VISIBLE);
            sort_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        initPopupWindow();
                    }
                }
            });

        }

        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == MyApp.DAY) {
                    if (dayNotes.size() > 0) ExcelUtils.exportDayNote(activity);
                } else if (type == MyApp.MONTH){
                    if (monthNotes.size() > 0) ExcelUtils.exportMonthNote(activity);
                } else ExcelUtils.exportIncomeNote(activity);
            }
        });

    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getApplication()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_sort_pop, null);
        popupWindow = new PopupWindow(layout, 300, 200);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);
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

    /**
     * 以投资时间排序
     */
    private void sort4Start() {
        if (! isStart) {
            incomes = MyApp.utils.getIncomes();
            if (incomes.size() > 0) {
                sort_info.setText("按投资时间排序");
                isStart = true;
                Collections.reverse(incomes);//倒序排列
                incomeNoteAdapter = new IncomeNoteAdapter(context, incomes, isStart);
                listView.setAdapter(incomeNoteAdapter);
            }
        }
    }

    /**
     * 以到期时间排序
     */
    private void sort4End() {
        incomes = MyApp.utils.getIncomes();
        if (incomes.size() > 0) {
            isStart = false;
            sort_info.setText("按到期时间排序");
            for(int i = 0; i < incomes.size() - 1; i ++) {
                for(int j = 1; j < incomes.size() - i; j ++) {
                    IncomeNote incomeNote;
                    int days1 = DateUtils.daysBetween(incomes.get(j).getDurtion().split("-")[1]) ;
                    int days2 = DateUtils.daysBetween(incomes.get(j - 1).getDurtion().split("-")[1]) ;
                    if(days1 < days2) {
                        incomeNote = incomes.get(j - 1);
                        incomes.set((j - 1), incomes.get(j));
                        incomes.set(j , incomeNote);
                    }
                }
            }
            LogUtils.i("sort", incomes.toString());
            incomeNoteAdapter = new IncomeNoteAdapter(context, incomes, isStart);//列表显示按投资时间排序时，最后一个才可做删除操作
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        else super.onBackPressed();
    }
}
