package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.MonthNoteAdapter;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class MonthListActivity extends BaseActivity {

    private ListView listView;

    private List<MonthNote> monthNotes;
    private MonthNoteAdapter monthNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("月账单列表", R.layout.activity_month_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView () {
        listView = (ListView) findViewById(R.id.listView);

        monthNotes = MyApp.utils.getMonNotes();
        Collections.reverse(monthNotes);//倒序排列
        monthNoteAdapter = new MonthNoteAdapter(activity, monthNotes);
        listView.setAdapter(monthNoteAdapter);

        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightBtnListener("导出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExcelUtils.exportMonthNote(callBackExport);
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

}
