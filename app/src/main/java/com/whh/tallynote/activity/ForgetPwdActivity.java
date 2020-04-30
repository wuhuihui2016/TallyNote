package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DesEncryptUtils;
import com.whh.tallynote.utils.ToastUtils;

/**
 * 忘记密码
 * Created by wuhuihui on 2020/4/29.
 */
public class ForgetPwdActivity extends BaseActivity {

    private TextView et_nickName, et_phoneNum;
    private EditText et_secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("忘记密码", R.layout.activity_forget_pwd);
        setReturnBtnClickLitener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReturn(); //页面返回，取消重置密码操作
            }
        });

        et_nickName = (TextView) findViewById(R.id.et_nickName);
        et_nickName.setText((String) ContansUtils.get(ContansUtils.NICKNAME, ""));
        et_phoneNum = (TextView) findViewById(R.id.et_phoneNum);
        String phoneNum = (String) ContansUtils.get(ContansUtils.PHONENUM, "");
        et_phoneNum.setText(phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length() - 4));
        et_secretKey = (EditText) findViewById(R.id.et_secretKey);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.resetPwd_btn:
                String secretKey = et_secretKey.getText().toString();
                if(TextUtils.isEmpty(secretKey)) {
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

                    Intent intent = new Intent(activity, SetOrCheckPwdActivity.class);
                    intent.putExtra(ContansUtils.RESETPWD, true);
                    startActivity(intent);

                    ToastUtils.showToast(activity, false, "启动密码已清除！请重置密码~");
                    finish();
                } else ToastUtils.showToast(activity, false, "输入的密钥不正确！");
                break;
        }
    }

    /**
     * 设置页面返回时，取消重置密码，跳转验证密码页面
     */
    private void activityReturn(){
        if (ContansUtils.getCheckWayIsGesture()) {
            if (ContansUtils.getCheckWayIsGesture()) { //手势密码不为空，关闭当前界面，验证手势密码
                Intent intent = new Intent(activity, SetGestureActivity.class);
                intent.putExtra("start", true);
                intent.putExtra("activityNum", 0);
                startActivity(intent);
            }
        } else {
            startActivity(new Intent(activity, SetOrCheckPwdActivity.class));
        }
        finish();
    }

    /**
     * 取消重置密码
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
