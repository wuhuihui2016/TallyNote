package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.AppManager;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.SystemUtils;
import com.fengyang.tallynote.utils.ToastUtils;
import com.fengyang.tallynote.view.StatusChange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 手势密码设置
 * Created by wuhuihui on 2017/12/6.
 */
public class SetGestureActivity extends BaseActivity {
    public static final int launcherAct = 0;
    public static final int settingAct = 1;
    public static int activityNum;
    public static StatusChange statusChange = new StatusChange();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture);
        intent = getIntent();
        activityNum = intent.getIntExtra("activityNum", 0);

        final TextView gestureText = (TextView) findViewById(R.id.gesture_text);
        ImageView user_icon = (ImageView) findViewById(R.id.user_icon);

        switch (activityNum) {
            case settingAct:
                statusChange.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        if ((int) event.getNewValue() == 2) {
                            ToastUtils.showToast(activity, false, "手势密码设置成功！");
                            finish();
                        } else if ((int) event.getNewValue() == 4) {
                            gestureText.setText("再次绘制以确认\n请与上次绘制保持一致");
                        }
                    }
                });
                break;
            case launcherAct:
                if (!TextUtils.isEmpty((String) ContansUtils.get("gesture", ""))) {
                    gestureText.setText("验证手势密码");
                    user_icon.setVisibility(View.VISIBLE);
                    user_icon.setImageResource(R.drawable.user);
                    statusChange.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent event) {
                            if ((int) event.getNewValue() == 2) {
                                if (intent.hasExtra("secureSet")) { //安全设置弹出选择dialog
                                    finish();
                                    startActivity(new Intent(activity, SecureSetActivity.class));
                                    intent.removeExtra("secureSet");
                                } else { //关闭当前界面，进入APP
                                    finish();
                                    if (intent.hasExtra("start")) {
                                        LogUtils.i("lifecycle", "getIntent start true");
                                        startActivity(new Intent(activity, MainActivity.class));
                                        intent.removeExtra("start");
                                    }
                                }
                            }
                        }
                    });
                }
                break;
        }
    }

    /**
     * 重回界面不绘制手势密码
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //重回界面不输入密码退出APP
            if (intent.hasExtra(SystemUtils.key)) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    ToastUtils.showToast(this, true, "再按一次退出程序");
                    mExitTime = System.currentTimeMillis();
                } else {
                    AppManager.getAppManager().AppExit(this);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
