package com.fengyang.tallynote.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.DayNoteDao;
import com.fengyang.tallynote.database.IncomeNoteDao;
import com.fengyang.tallynote.database.MemoNoteDao;
import com.fengyang.tallynote.database.MonthNoteDao;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DialogListener;
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

    private static final int APP_FILE_SELECT_CODE = 0, FILE_SELECT_CODE = 1;

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
        TextView notesNum5 = (TextView) findViewById(R.id.notesNum5);
        TextView notesNum6 = (TextView) findViewById(R.id.notesNum6);
        notesNum1.setText("日账记录：" + DayNoteDao.getDayNotes().size());
        notesNum2.setText("月账记录：" + MonthNoteDao.getMonthNotes().size());
        notesNum3.setText("理财记录：" + IncomeNoteDao.getIncomes().size());
        notesNum4.setText("历史日账记录：" + DayNoteDao.getDayNotes4History().size());
        notesNum5.setText("备忘录记录：" + MemoNoteDao.getMemoNotes().size());
        notesNum6.setText("记事本记录：" + NotePadDao.getNotePads().size());
        notesNum1.setOnClickListener(new MyOnClickListener(ContansUtils.DAY));
        notesNum2.setOnClickListener(new MyOnClickListener(ContansUtils.MONTH));
        notesNum3.setOnClickListener(new MyOnClickListener(ContansUtils.INCOME));
        notesNum4.setOnClickListener(new MyOnClickListener(ContansUtils.DAY_HISTORY));
        notesNum5.setOnClickListener(new MyOnClickListener(ContansUtils.MEMO));
        notesNum6.setOnClickListener(new MyOnClickListener(ContansUtils.NOTEPAD));
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int index;

        public MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (index == ContansUtils.DAY && DayNoteDao.getDayNotes().size() > 0)
                startActivity(new Intent(activity, DayListActivity.class));
            if (index == ContansUtils.MONTH && MonthNoteDao.getMonthNotes().size() > 0)
                startActivity(new Intent(activity, MonthListActivity.class));
            if (index == ContansUtils.INCOME && IncomeNoteDao.getIncomes().size() > 0)
                startActivity(new Intent(activity, IncomeListActivity.class));
            if (index == ContansUtils.DAY_HISTORY && DayNoteDao.getDayNotes4History().size() > 0) {
                List<DayNote> dayNotes4History = DayNoteDao.getDayNotes4History();
                Collections.reverse(dayNotes4History);
                Intent intent = new Intent(activity, DayListOfMonthActivity.class);
                intent.putExtra("duration", dayNotes4History.get(0).getDuration());
                startActivity(intent);
            }
            if (index == ContansUtils.MEMO && MemoNoteDao.getMemoNotes().size() > 0)
                startActivity(new Intent(activity, MemoNoteListActivity.class));
            if (index == ContansUtils.NOTEPAD && NotePadDao.getNotePads().size() > 0)
                startActivity(new Intent(activity, NotePadListActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.import2file4APP:
                Intent intent = new Intent(activity, FileExplorerActivity.class);
                intent.putExtra("import", true);
                startActivityForResult(intent, APP_FILE_SELECT_CODE);
                break;
            case R.id.import2file4Other:
                if (FileUtils.isSDCardAvailable()) {

                    DialogUtils.showMsgDialog(activity, "从文件中导入将覆盖已有数据，是否继续导入？",
                            "导入", new DialogListener() {
                        @Override
                        public void onClick() {
                            try {
                                //跳转系统文件目录
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(intent, "导入文件"), FILE_SELECT_CODE);
                            } catch (android.content.ActivityNotFoundException ex) {
                                ToastUtils.showToast(context, true, "Please install a File Manager.");
                            }
                        }
                    },"取消", new DialogListener() {
                        @Override
                        public void onClick() {
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

    //导入文件进度
    ProgressDialog dialog = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            String path = "";
            switch (requestCode) {
                case APP_FILE_SELECT_CODE: //从APP目录导入
                    path = data.getStringExtra("path");
                    break;
                case FILE_SELECT_CODE: //从其他目录导入
                    if (resultCode == RESULT_OK) {
                        try {
                            path = FileUtils.getPath(context, data.getData());
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            if (!TextUtils.isEmpty(path)) {
                LogUtils.i(TAG, "importExcel File Path: " + path);
                if ((path.endsWith(".xlsx") || path.endsWith(".xls"))) {
                    dialog = new ProgressDialog(activity);
                    dialog.setMessage("正在导入...");
                    dialog.show();
                    ExcelUtils.importExcel(path, new ExcelUtils.ICallBackImport() {

                        @Override
                        public void callback(int day_count, int month_count, int income_count, int day_history_count, int memo_count, int notepad_count) {
                            dialog.dismiss();
                            ToastUtils.showSucessLong(activity, "导入成功！" +
                                    "\n日账记录：" + day_count +
                                    "\n月账记录：" + month_count +
                                    "\n理财记录：" + income_count +
                                    "\n历史日账记录：" + day_history_count +
                                    "\n备忘录记录：" + memo_count +
                                    "\n记事本记录：" + notepad_count);
                            initDate();
                        }

                        @Override
                        public void callback(String errorMsg) {
                            dialog.dismiss();
                            ToastUtils.showErrorLong(activity, errorMsg);
                        }
                    });
                } else {
                    ToastUtils.showErrorLong(activity, "导入失败！\n仅支持xlsx或xls文件，请检查后重试！");
                }
            }

        } catch (Exception e) {
            LogUtils.i(TAG + "onActivityResult", e.toString());
        }
    }

}
