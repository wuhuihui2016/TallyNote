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
import com.fengyang.tallynote.adapter.IncomeNoteAdapter;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class IncomeListActivity extends BaseActivity {

    private ListView listView;

    private List<IncomeNote> incomes;
    private IncomeNoteAdapter incomeNoteAdapter;
    private TextView sort_info;
    private PopupWindow popupWindow;
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("理财列表", R.layout.activity_income_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView () {
        listView = (ListView) findViewById(R.id.listView);

        incomes = MyApp.utils.getIncomes();
        Collections.reverse(incomes);//倒序排列
        incomeNoteAdapter = new IncomeNoteAdapter(activity, incomes, isStart);
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


        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExcelUtils.exportIncomeNote(callBackExport);
            }
        });

    }

    /**
     * 导出结果回调
     */
    private ExcelUtils.ICallBackExport callBackExport = new ExcelUtils.ICallBackExport() {
        @Override
        public void callback(boolean sucess, String fileName) {
            if (sucess) ToastUtils.showSucessLong(context, "导出成功:" + fileName);
            else ToastUtils.showErrorLong(context, "导出失败！");
        }
    };

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
                incomeNoteAdapter = new IncomeNoteAdapter(activity, incomes, isStart);
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
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomes, isStart);//列表显示按投资时间排序时，最后一个才可做删除操作
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        else super.onBackPressed();
    }
}
