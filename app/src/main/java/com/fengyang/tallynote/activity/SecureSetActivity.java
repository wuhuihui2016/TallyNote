package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.ContansUtils;

/**
 * 记日账
 */
public class SecureSetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_set);

        initView();
    }

    private void initView() {
        //重置密码
        TextView resetPass = (TextView) findViewById(R.id.resetPass);
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(activity, SetOrCheckPwdActivity.class);
                intent.putExtra("reSetPwd", true);
                activity.startActivity(intent);
            }
        });

        //手势密码的设置
        Switch lock_switch = (Switch) findViewById(R.id.lock_switch);
        if (TextUtils.isEmpty((String) ContansUtils.get("gesture", ""))) {
            lock_switch.setChecked(false);
        } else {
            lock_switch.setChecked(true);
        }
        lock_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                finish();
                if (isChecked) {
                    Intent intent = new Intent(activity, SetGestureActivity.class);
                    intent.putExtra("activityNum", 1);
                    startActivity(intent);
                } else {
                    ContansUtils.remove("gesture");
                }
            }
        });

        //关闭dialog
        ImageButton close = (ImageButton) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
