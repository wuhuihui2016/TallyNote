package com.whh.tallynote.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
import com.whh.tallynote.utils.PermissionUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

import butterknife.BindView;

/**
 * 登录页面
 * Created by wuhuihui on 2020/4/29.
 */
public class LoginUserActivity extends BaseActivity {

    @BindView(R.id.et_nickName)
    public TextView et_nickName;
    @BindView(R.id.et_phoneNum)
    public TextView et_phoneNum;
    @BindView(R.id.et_secretKey)
    public EditText et_secretKey;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("登录", R.layout.activity_login_user);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initEvent() {
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

    @Override
    protected void onResume() {
        super.onResume();
        //检测权限后显示界面
        PermissionUtils.checkPhonestatePermission(activity, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isSucess) {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String deviceid = tm.getDeviceId(); //无sim卡也可获取
                    //以下信息需要插上sim卡才能获取
                    String tel = tm.getLine1Number(); //+8618513199489
                    if (!TextUtils.isEmpty(tel) && tel.length() == 14 && tel.startsWith("+86")) {
                        tel = tel.substring(3);
                        et_phoneNum.setText(tel);
//                        et_phoneNum.setText(tel.substring(0, 3) + " " + tel.substring(3, 7) + " " + tel.substring(7, 11));
                    }

                } else {
                    PermissionUtils.notPhoneStatePermission(activity);
//                    ToastUtils.showToast(context, true, "可能读取手机信息权限未打开，请检查后重试！");
                }
            }
        });

    }

    /**
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast(activity, true, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(activity);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
