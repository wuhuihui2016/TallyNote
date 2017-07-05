package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.net.URISyntaxException;

/**
 * Created by wuhuihui on 2017/7/5.
 * 导入/导出
 */
public class PortNotesActivity extends BaseActivity {

    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("导入/导出", R.layout.activity_port_notes);

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
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        StringUtils.show1Toast(activity, "Please install a File Manager.");
                    }
                } else {
                    StringUtils.show1Toast(context, "SDCard is not available");
                }
                break;

            case R.id.exportAll:
                ExcelUtils.exportAll(new ExcelUtils.ICallBackExport() {
                    @Override
                    public void callback(boolean sucess, String fileName) {
                        if (sucess) StringUtils.show1Toast(activity, "导出成功:" + fileName);
                        else StringUtils.show1Toast(activity, "导出失败！");
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
                        ExcelUtils.importExcel(context, path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

}
