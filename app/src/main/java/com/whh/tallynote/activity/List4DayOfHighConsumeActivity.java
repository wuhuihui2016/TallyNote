package com.whh.tallynote.activity;

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
import com.whh.tallynote.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 日账单明细(高额消费)
 * Created by wuhuihui on 2017/8/11.
 */
public class List4DayOfHighConsumeActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;
    @BindView(R.id.spinner)
    public Spinner spinner;

    private boolean isDuration;
    private String duration;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("高额消费日账单明细", R.layout.activity_day_list_high);
    }

    @Override
    protected void initView() {
        getAll4HighConsume();
    }

    @Override
    protected void initEvent() {

    }

    /**
     * 筛选高额消费（>=50）
     */
    private void getAll4HighConsume() {

        listView.setEmptyView(emptyView);

        if (getIntent().hasExtra("duration")) isDuration = true;

        List<DayNote> dayNotes;

        if (isDuration) {
            //数据
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
                            getAll4HighConsume();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    }
            );

            duration = getIntent().getStringExtra("duration");
            dayNotes = MyApp.dayNoteDBHandle.getDayNotes4History(duration);

        } else {
            dayNotes = MyApp.dayNoteDBHandle.getDayNotes();
        }

        List<DayNote> list = new ArrayList<>();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            Double money = Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.consume && money >= 50) {
                sum += money;
                list.add(dayNotes.get(i));
            }
        }

        if (isDuration) {
            info.setText("高额消费账单记录(" + duration + ")：" + list.size() + "\n消费金额：" + StringUtils.showPrice(sum + ""));
        } else {
            info.setText("高额消费账单记录：" + list.size() + "\n消费金额：" + StringUtils.showPrice(sum + ""));
        }

        Collections.reverse(list);
        listView.setAdapter(new DayNoteAdapter(activity, list, false));
    }

}
