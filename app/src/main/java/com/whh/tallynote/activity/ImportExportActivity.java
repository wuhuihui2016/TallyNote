package com.whh.tallynote.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 导入/导出
 * Created by wuhuihui on 2017/7/5.
 */
public class ImportExportActivity extends BaseActivity {

    @BindView(R.id.notesNum1)
    public TextView notesNum1;
    @BindView(R.id.notesNum2)
    public TextView notesNum2;
    @BindView(R.id.notesNum3)
    public TextView notesNum3;
    @BindView(R.id.notesNum4)
    public TextView notesNum4;
    @BindView(R.id.notesNum5)
    public TextView notesNum5;
    @BindView(R.id.notesNum6)
    public TextView notesNum6;

    private static final int APP_FILE_SELECT_CODE = 0, FILE_SELECT_CODE = 1;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("导入/导出", R.layout.activity_port_notes);
    }

    @Override
    protected void initView() {
        notesNum1.setText("日账记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.DAY));
        notesNum2.setText("月账记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.MONTH));
        notesNum3.setText("理财记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.INCOME));
        notesNum4.setText("历史日账记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.DAY_HISTORY));
        notesNum5.setText("备忘录记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.MEMO));
        notesNum6.setText("记事本记录：" + MyApp.dbHandle.getCount4Record(ContansUtils.NOTEPAD));
    }

    @Override
    protected void initEvent() {
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
            if (index == ContansUtils.DAY && MyApp.dbHandle.getCount4Record(ContansUtils.DAY) > 0)
                AppManager.transfer(activity, List4DayActivity.class);
            if (index == ContansUtils.MONTH && MyApp.dbHandle.getCount4Record(ContansUtils.MONTH) > 0)
                AppManager.transfer(activity, List4MonthActivity.class);
            if (index == ContansUtils.INCOME && MyApp.dbHandle.getCount4Record(ContansUtils.INCOME) > 0)
                AppManager.transfer(activity, List4IncomeActivity.class);
            if (index == ContansUtils.DAY_HISTORY && MyApp.dbHandle.getCount4Record(ContansUtils.DAY_HISTORY) > 0) {
                List<DayNote> dayNotes4History = MyApp.dayNoteDBHandle.getDayNotes4History();
                Collections.reverse(dayNotes4History);
                AppManager.transfer(activity, List4DayOfMonthActivity.class, "duration", dayNotes4History.get(0).getDuration());
            }
            if (index == ContansUtils.MEMO && MyApp.dbHandle.getCount4Record(ContansUtils.MEMO) > 0)
                AppManager.transfer(activity, List4MemoNoteActivity.class);
            if (index == ContansUtils.NOTEPAD && MyApp.dbHandle.getCount4Record(ContansUtils.NOTEPAD) > 0)
                AppManager.transfer(activity, List4NotePadActivity.class);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.import2file4APP:
                AppManager.transfer(activity, FileExplorerActivity.class, "import", true, APP_FILE_SELECT_CODE);
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
                            }, "取消", new DialogListener() {
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
                        public void callback(final int day_count, final int month_count, final int income_count,
                                             final int day_history_count, final int memo_count, final int notepad_count) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    ToastUtils.showSucessLong(activity, "导入成功！" +
                                            "\n日账记录：" + day_count +
                                            "\n月账记录：" + month_count +
                                            "\n理财记录：" + income_count +
                                            "\n历史日账记录：" + day_history_count +
                                            "\n备忘录记录：" + memo_count +
                                            "\n记事本记录：" + notepad_count);
                                    initView();
                                }
                            });
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
