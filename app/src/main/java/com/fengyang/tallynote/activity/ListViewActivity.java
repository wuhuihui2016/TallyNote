package com.fengyang.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.DayNoteAdapter;
import com.fengyang.tallynote.adapter.IncomeNoteAdapter;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.Income;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class ListViewActivity extends BaseActivity {

    private final int DAY = 0, MONTH = 1, INCOME = 2;
    private int type;//0：日账单，1：月账单，其他：理财记录
    private ListView listView;
    private TextView emptyView;

    private List<DayNote> dayNotes;
    private DayNoteAdapter dayNoteAdapter;

    private List<MonthNote> monthNotes;
    private MonthNoteAdapter monthNoteAdapter;

    private List<Income> incomes;
    private IncomeNoteAdapter incomeNoteAdapter;
    private LinearLayout sort_layout;
    private TextView start_time, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra("type", DAY);
        if (type == DAY) setContentView("日账单列表", R.layout.activity_list);
        else if (type == MONTH) setContentView("月账单列表", R.layout.activity_list);
        else setContentView("理财列表", R.layout.activity_list);

        initView();

    }

    private void initView () {
        listView = (ListView) findViewById(R.id.listView);

        if (type == DAY) {
            dayNotes = MyApp.utils.getDayNotes();
            Collections.reverse(dayNotes);//倒序排列
            dayNoteAdapter = new DayNoteAdapter(context, dayNotes);
            listView.setAdapter(dayNoteAdapter);

        } else if (type == MONTH){
            monthNotes = MyApp.utils.getMonNotes();
            Collections.reverse(monthNotes);//倒序排列
            monthNoteAdapter = new MonthNoteAdapter(context, monthNotes);
            listView.setAdapter(monthNoteAdapter);

        } else if (type == INCOME){
            incomes = MyApp.utils.getIncomes();
            Collections.reverse(incomes);//倒序排列
            incomeNoteAdapter = new IncomeNoteAdapter(context, incomes);
            listView.setAdapter(incomeNoteAdapter);
            sort_layout = (LinearLayout) findViewById(R.id.sort_layout);
            start_time = (TextView) findViewById(R.id.start_time);
            end_time = (TextView) findViewById(R.id.end_time);

        }

        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == DAY) {
                    if (dayNotes.size() > 0) ExcelUtils.writeDayNote(activity, dayNotes);
                } else if (type == MONTH){
                    if (monthNotes.size() > 0) ExcelUtils.writeMonthNote(activity, monthNotes);
                } else ExcelUtils.writeIncomeNote(activity, incomes);
            }
        });

        if (type == INCOME) {
            setRightMoreBtnListener("排序", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sort_layout.isShown()) {
                        sort_layout.setVisibility(View.GONE);
                    } else {
                        sort_layout.setVisibility(View.VISIBLE);
                        start_time.setOnClickListener(new View.OnClickListener() {//按投资时间排序
                            @Override
                            public void onClick(View v) {
                                sort4Start();
                            }
                        });

                        end_time.setOnClickListener(new View.OnClickListener() {//按到期时间排序
                            @Override
                            public void onClick(View v) {
                                sort4End();
                            }
                        });
                    }
                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == DAY) {
                    DayNote dayNote = dayNotes.get(position);
                    String message;
                    if (dayNote.getRemark().length() > 0) message = dayNote.getUsage() + "：" + StringUtils.showPrice(dayNote.getMoney()) + " (" + dayNote.getRemark() + ")";
                    else message = dayNote.getUsage() + "：" + StringUtils.showPrice(dayNote.getMoney());
                    DialogUtils.showMsgDialog(activity, dayNote.getTime(), message);

                } else if (type == MONTH){
                    MonthNote monthNote = monthNotes.get(position);
                    DialogUtils.showMsgDialog(activity, monthNote.getDuration(),
                            "上次结余：" + StringUtils.showPrice(monthNote.getLast_balance()) + "\n" +
                                    "本次支出：" + StringUtils.showPrice(monthNote.getPay()) + "\n" +
                                    "本次工资：" + StringUtils.showPrice(monthNote.getSalary()) + "\n" +
                                    "本次收益：" + StringUtils.showPrice(monthNote.getIncome()) + "\n" +
                                    "家用补贴：" + StringUtils.showPrice(monthNote.getHomeuse()) + "\n" +
                                    "本次结余：" + StringUtils.showPrice(monthNote.getBalance()) + "\n" +
                                    "实际结余：" + StringUtils.showPrice(monthNote.getActual_balance()) + "\n" +
                                    "月结备注：" + monthNote.getRemark());
                }
            }
        });
    }

    /**
     * 以投资时间排序
     */
    private void sort4Start() {
        if (incomes.size() > 0) {
            start_time.setTextColor(Color.BLACK);
            end_time.setTextColor(Color.RED);
            sort_layout.setVisibility(View.GONE);
            incomeNoteAdapter = new IncomeNoteAdapter(context, incomes);
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    /**
     * 以到期时间排序
     */
    private void sort4End() {
        if (incomes.size() > 0) {
            start_time.setTextColor(Color.BLACK);
            end_time.setTextColor(Color.RED);
            sort_layout.setVisibility(View.GONE);
            for(int i = 0; i < incomes.size() - 1; i ++) {
                for(int j = 1; j < incomes.size() - i; j ++) {
                    Income income;
                    int days1 = DateUtils.daysBetween(incomes.get(j).getDurtion().split("-")[1]) ;
                    int days2 = DateUtils.daysBetween(incomes.get(j - 1).getDurtion().split("-")[1]) ;
                    if(days1 > days2) {
                        income = incomes.get(j - 1);
                        incomes.set((j - 1), incomes.get(j));
                        incomes.set(j , income);
                    }
                }
            }
        }
        LogUtils.i("sort", incomes.toString());
        incomeNoteAdapter = new IncomeNoteAdapter(context, incomes);
        listView.setAdapter(incomeNoteAdapter);
    }

}
