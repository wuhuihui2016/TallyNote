package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.DayNoteAdapter;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class ListViewActivity extends BaseActivity {

    private boolean isDayList;
    private ListView listView;
    private TextView emptyView;
    private DayNoteAdapter dayNoteAdapter;
    private MonthNoteAdapter monthNoteAdapter;

    private List<DayNote> dayNotes;
    private List<MonthNote> monthNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isDayList = getIntent().getBooleanExtra("isDayList", false);
        if (isDayList) setContentView("日账单列表", R.layout.activity_list);
        else setContentView("月账单列表", R.layout.activity_list);

        listView = (ListView) findViewById(R.id.listView);

        if (isDayList) {
            dayNotes = MyApp.utils.getDayNotes();
            Collections.reverse(dayNotes);//倒序排列
            dayNoteAdapter = new DayNoteAdapter(context, dayNotes);
            listView.setAdapter(dayNoteAdapter);
        } else {
            monthNotes = MyApp.utils.getMonNotes();
            Collections.reverse(monthNotes);//倒序排列
            monthNoteAdapter = new MonthNoteAdapter(context, monthNotes);
            listView.setAdapter(monthNoteAdapter);
        }

        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDayList) {
                    if (dayNotes.size() > 0) ExcelUtils.writeDayNote(activity, dayNotes);
                } else {
                    if (monthNotes.size() > 0) ExcelUtils.writeMonthNote(activity, monthNotes);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isDayList) {
                    DayNote dayNote = dayNotes.get(position);
                    DialogUtils.showMsgDialog(activity, dayNote.getTime(),
                            dayNote.getUsage() + "：" + dayNote.getMoney() + "\n" + dayNote.getRemark());
                } else {
                    MonthNote monthNote = monthNotes.get(position);
                    DialogUtils.showMsgDialog(activity, monthNote.getDuration(),
                            "上次结余:" + monthNote.getLast_balance() + "\n" +
                                    "本次支出:" + monthNote.getPay() + "\n" +
                                    "本次工资:" + monthNote.getSalary() + "\n" +
                                    "本次收益:" + monthNote.getIncome() + "\n" +
                                    "家用补贴:" + monthNote.getHomeuse() + "\n" +
                                    "本次结余:" + monthNote.getBalance() + "\n" +
                                    "实际结余:" + monthNote.getActual_balance() + "\n" +
                                    "备    注:" + monthNote.getRemark());
                }
            }
        });

    }
}
