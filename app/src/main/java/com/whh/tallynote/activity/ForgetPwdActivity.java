package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DesEncryptUtils;
import com.whh.tallynote.utils.ToastUtils;

import butterknife.BindView;

/**
 * 忘记密码
 * Created by wuhuihui on 2020/4/29.
 */
public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.et_nickName)
    public TextView et_nickName;
    @BindView(R.id.et_phoneNum)
    public TextView et_phoneNum;
    @BindView(R.id.et_secretKey)
    public EditText et_secretKey;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("忘记密码", R.layout.activity_forget_pwd);
    }

    @Override
    protected void initView() {
        setReturnBtnClickLitener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReturn(); //页面返回，取消重置密码操作
            }
        });

        et_nickName.setText((String) ContansUtils.get(ContansUtils.NICKNAME, ""));
        String phoneNum = (String) ContansUtils.get(ContansUtils.PHONENUM, "");
        et_phoneNum.setText(phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length() - 4));
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.resetPwd_btn:
                String secretKey = et_secretKey.getText().toString();
                if (TextUtils.isEmpty(secretKey)) {
                    ToastUtils.showToast(activity, false, "请输入你设置的密钥~");
                    return;
                }

                try {
                    secretKey = DesEncryptUtils.encode(secretKey);
                } catch (DesEncryptUtils.CodecException e) {
                    e.printStackTrace();
                }

                String pwdKey = (String) ContansUtils.get(ContansUtils.SECRETKEY, "");
                if (secretKey.equals(pwdKey)) { //输入的密钥加密后和设置的密钥一致则可重置密码
                    ContansUtils.remove(ContansUtils.GESTURE); //清除之前设置的手势密码
                    ContansUtils.remove(ContansUtils.PWDKEY); //清除之前设置的验证密码
                    ContansUtils.remove(ContansUtils.CHECKWAY); //清除验证方式

                    AppManager.transfer(activity, SetOrCheckPwdActivity.class, ContansUtils.RESETPWD, true);

                    ToastUtils.showToast(activity, false, "启动密码已清除！请重置密码~");
                    finish();
                } else ToastUtils.showToast(activity, false, "输入的密钥不正确！");
                break;
        }
    }

    /**
     * 设置页面返回时，取消重置密码，跳转验证密码页面
     */
    private void activityReturn() {
        if (ContansUtils.getCheckWayIsGesture()) {
            if (ContansUtils.getCheckWayIsGesture()) { //手势密码不为空，关闭当前界面，验证手势密码
                AppManager.transfer(activity, SetGestureActivity.class,"start", true, "activityNum", 0);
            }
        } else {
            AppManager.transfer(activity, SetOrCheckPwdActivity.class);
        }
        finish();
    }

    /**
     * 取消重置密码
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityReturn();
        }
        return super.onKeyDown(keyCode, event);
    }

}
