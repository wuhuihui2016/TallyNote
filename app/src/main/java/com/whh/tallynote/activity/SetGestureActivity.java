package com.whh.tallynote.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.view.StatusChange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 手势密码设置
 * Created by wuhuihui on 2017/12/6.
 */
public class SetGestureActivity extends BaseActivity {

    @BindView(R.id.gesture_text)
    public TextView gestureText;
    @BindView(R.id.user_icon)
    public ImageView user_icon;
    @BindView(R.id.forgetPwd_btn)
    public Button forgetPwd_btn;

    public static final int launcherAct = 0;
    public static final int settingAct = 1;
    public static int activityNum;
    public static StatusChange statusChange = new StatusChange();

    private Intent intent;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView(R.layout.activity_set_gesture);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        intent = getIntent();
        activityNum = intent.getIntExtra("activityNum", 0);

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
                //忘记密码
                forgetPwd_btn.setVisibility(View.VISIBLE);
                forgetPwd_btn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //增加下划线
                forgetPwd_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppManager.transfer(activity, ForgetPwdActivity.class);
                        AppManager.getAppManager().finishActivity(); //清除所有Acitivity
                    }
                });

                if (!TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.GESTURE, ""))) {
                    gestureText.setText("验证手势密码");
                    user_icon.setVisibility(View.VISIBLE);
                    user_icon.setImageResource(R.drawable.user);
                    statusChange.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent event) {
                            if ((int) event.getNewValue() == 2) {
                                if (intent.hasExtra("secureSet")) { //安全设置弹出选择dialog
                                    finish();
                                    AppManager.transfer(activity, SecureSetActivity.class);
                                    intent.removeExtra("secureSet");
                                } else { //关闭当前界面，进入APP
                                    finish();
                                    if (intent.hasExtra("start")) {
                                        LogUtils.i("lifecycle", "getIntent start true");
                                        AppManager.transfer(activity, MainActivity.class);
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
            if (intent.hasExtra(ContansUtils.ISBACK)) {
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
