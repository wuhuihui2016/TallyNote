package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.whh.tallynote.R;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DesEncryptUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

/**
 * 登录页面
 * Created by wuhuihui on 2020/4/29.
 */
public class LoginUserActivity extends BaseActivity {

    private EditText et_nickName, et_phoneNum, et_secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("登录", R.layout.activity_login_user);

        et_nickName = (EditText) findViewById(R.id.et_nickName);
        et_phoneNum = (EditText) findViewById(R.id.et_phoneNum);
        et_secretKey = (EditText) findViewById(R.id.et_secretKey);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_btn:
                String nickName = et_nickName.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    ToastUtils.showToast(activity, false, "请输入你的昵称~");
                    return;
                }
                String phoneNum = et_phoneNum.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtils.showToast(activity, false, "请输入你的手机号~");
                    return;
                }
                if (!StringUtils.isPhone(phoneNum)) {
                    ToastUtils.showToast(activity, false, "请输入正确的手机号~");
                    return;
                }
                String secretKey = et_secretKey.getText().toString();
                if (TextUtils.isEmpty(secretKey)) {
                    ToastUtils.showToast(activity, false, "请输入你的密钥~");
                    return;
                }
                if (secretKey.length() < 6) {
                    ToastUtils.showToast(activity, false, "输入的密钥不能小于6位~");
                    return;
                }

                try {
                    ContansUtils.put(ContansUtils.NICKNAME, nickName);
                    ContansUtils.put(ContansUtils.PHONENUM, phoneNum);
                    ContansUtils.put(ContansUtils.SECRETKEY, DesEncryptUtils.encode(secretKey)); //加密保存密钥
                } catch (DesEncryptUtils.CodecException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    /**
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast(this, true, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
