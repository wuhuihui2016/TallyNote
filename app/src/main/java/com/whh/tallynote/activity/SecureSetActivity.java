package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;

import butterknife.BindView;

/**
 * 记日账
 */
public class SecureSetActivity extends BaseActivity {

    @BindView(R.id.resetPass)
    public TextView resetPass;
    @BindView(R.id.lock_switch)
    public Switch lock_switch;
    @BindView(R.id.close)
    public ImageButton close;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView(R.layout.activity_secure_set);
    }

    @Override
    protected void initView() {
        //重置密码
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                AppManager.transfer(activity, SetOrCheckPwdActivity.class, ContansUtils.RESETPWD, true);
            }
        });

        //手势密码的设置
        if (TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.GESTURE, ""))) {
            lock_switch.setChecked(false);
        } else {
            lock_switch.setChecked(true);
        }
        lock_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                finish();
                if (isChecked) {
                    AppManager.transfer(activity, SetGestureActivity.class, "activityNum", 1);
                } else {
                    ContansUtils.remove(ContansUtils.GESTURE);
                }
            }
        });

        //关闭dialog
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initEvent() {

    }

}
