package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.net.URISyntaxException;

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

    private void initDate () {
        TextView notesNum = (TextView) findViewById(R.id.notesNum);
        notesNum.setText("日账记录：" + MyApp.utils.getDayNotes().size() +
                "\n月账记录：" + MyApp.utils.getMonNotes().size() +
                "\n理财记录：" + MyApp.utils.getIncomes().size());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.import2file:
                if (FileUtils.isSDCardAvailable()) {

                    DialogUtils.showMsgDialog(activity, "导入提示", "从文件中导入将覆盖已有数据，是否继续导入？", new DialogUtils.DialogListener(){
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
                    }, new DialogUtils.DialogListener(){
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
                ExcelUtils.exportAll(new ExcelUtils.ICallBackExport() {
                    @Override
                    public void callback(boolean sucess, String fileName) {
                        if (sucess) ToastUtils.showToast(context, true, "导出成功:" + fileName);
                        else ToastUtils.showToast(context, true, "导出失败！");
                    }
                });
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
                        ExcelUtils.importExcel(context, path, new ExcelUtils.ICallBackImport() {

                            @Override
                            public void callback(int day_count, int month_count, int income_count) {
                                ToastUtils.showSucessLong(context, "导入成功！" +
                                        "\n日账记录：" + day_count +
                                        "\n月账记录：" + month_count  +
                                        "\n理财记录：" + income_count);
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
