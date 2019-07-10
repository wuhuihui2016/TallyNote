package com.whh.tallynote.activity;

import android.os.Bundle;

import com.whh.tallynote.R;

/**
 * 显示excle表格数据
 * Created by wuhuihui on 2019/07/08.
 */
public class ShowData4TableActivity extends BaseActivity {

    private final String TAG = "ShowData4TableActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("表格数据", R.layout.activity_data_table);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {


    }

}
