package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * 导入/导出
 * Created by wuhuihui on 2017/7/5.
 */
public class ImportExportActivity extends BaseActivity {

    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("导入/导出", R.layout.activity_port_notes);

        initDate();
    }

    private void initDate() {
        TextView notesNum1 = (TextView) findViewById(R.id.notesNum1);
        TextView notesNum2 = (TextView) findViewById(R.id.notesNum2);
        TextView notesNum3 = (TextView) findViewById(R.id.notesNum3);
        TextView notesNum4 = (TextView) findViewById(R.id.notesNum4);
        notesNum1.setText("日账记录：" + MyApp.utils.getDayNotes().size());
        notesNum2.setText("月账记录：" + MyApp.utils.getMonNotes().size());
        notesNum3.setText("理财记录：" + MyApp.utils.getIncomes().size());
        notesNum4.setText("历史日账记录：" + MyApp.utils.getDayNotes4History().size());
        notesNum1.setOnClickListener(new MyOnClickListener(ContansUtils.DAY));
        notesNum2.setOnClickListener(new MyOnClickListener(ContansUtils.MONTH));
        notesNum3.setOnClickListener(new MyOnClickListener(ContansUtils.INCOME));
        notesNum4.setOnClickListener(new MyOnClickListener(ContansUtils.DAY_HISTORY));
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int index;

        public MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (index == ContansUtils.DAY && MyApp.utils.getDayNotes().size() > 0)
                startActivity(new Intent(activity, DayListActivity.class));
            if (index == ContansUtils.MONTH && MyApp.utils.getMonNotes().size() > 0)
                startActivity(new Intent(activity, MonthListActivity.class));
            if (index == ContansUtils.INCOME && MyApp.utils.getIncomes().size() > 0)
                startActivity(new Intent(activity, IncomeListActivity.class));
            if (index == ContansUtils.DAY_HISTORY && MyApp.utils.getDayNotes4History().size() > 0) {
                List<DayNote> dayNotes4History = MyApp.utils.getDayNotes4History();
                Collections.reverse(dayNotes4History);
                Intent intent = new Intent(activity, DayListOfMonthActivity.class);
                intent.putExtra("duration", dayNotes4History.get(0).getDuration());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.import2file:
                if (FileUtils.isSDCardAvailable()) {

                    DialogUtils.showMsgDialog(activity, "导入提示", "从文件中导入将覆盖已有数据，是否继续导入？", new DialogUtils.DialogListener() {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            try {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                            } catch (android.content.ActivityNotFoundException ex) {
                                ToastUtils.showToast(context, true, "Please install a File Manager.");
                            }
                        }
                    }, new DialogUtils.DialogListener() {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                        }
                    });

                } else {
                    ToastUtils.showToast(context, true, "SDCard is not available");
                }
                break;

            case R.id.exportAll:
                ExcelUtils.exportAll(callBackExport);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String path = FileUtils.getPath(context, uri);
                        LogUtils.i(TAG, "File Path: " + path);
                        ExcelUtils.importExcel(path, new ExcelUtils.ICallBackImport() {

                            @Override
                            public void callback(int day_count, int month_count, int income_count, int day_history_count) {
                                ToastUtils.showSucessLong(context, "导入成功！" +
                                        "\n日账记录：" + day_count +
                                        "\n月账记录：" + month_count +
                                        "\n理财记录：" + income_count +
                                        "\n历史日账记录：" + day_history_count);
                                initDate();
                            }

                            @Override
                            public void callback(String errorMsg) {
                                ToastUtils.showSucessLong(context, errorMsg);
                            }
                        });
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

}
