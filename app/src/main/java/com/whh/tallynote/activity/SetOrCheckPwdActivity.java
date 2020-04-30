package com.whh.tallynote.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.NumAdapter;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DelayTask;
import com.whh.tallynote.utils.DesEncryptUtils;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证密码
 * Created by wuhuihui on 2017/6/27.
 */
public class SetOrCheckPwdActivity extends BaseActivity {

    private ImageButton pwdVisible;

    private List<TextView> textViews = new ArrayList<>();//密码输入显示的view
    private GridView numGridView; //输入密码的按键
    private List<String> pwds = new ArrayList<>(); //输入的密码数字集合

    private boolean isSetting = true; //设置密码还是验证密码,默认设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_pwd);

        pwdVisible = (ImageButton) findViewById(R.id.pwdVisible);
        initView();

        //判断是否设置过，是否重置密码
        if (TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.PWDKEY, ""))
                || getIntent().hasExtra(ContansUtils.RESETPWD)) {
            //为设置密码操作
            TextView edit_tips = (TextView) findViewById(R.id.edit_tips);

            if (getIntent().hasExtra(ContansUtils.RESETPWD)) {
                edit_tips.setHint("请输入新的6位密码");
            } else {
                edit_tips.setHint("请输入6位密码，为进入APP验证使用");
                if (!ContansUtils.contains(ContansUtils.NICKNAME)) //如果本地没有昵称，则去登录
                    startActivity(new Intent(activity, LoginUserActivity.class));
            }

        } else {
            //为验证密码操作
            isSetting = false;

            //忘记密码
            Button forgetPwd_btn = (Button) findViewById(R.id.forgetPwd_btn);
            forgetPwd_btn.setVisibility(View.VISIBLE);
            forgetPwd_btn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //增加下划线
            forgetPwd_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, ForgetPwdActivity.class));
                    AppManager.getAppManager().finishActivity(); //清除所有Acitivity
                }
            });

            //判断验方式：手势密码还是启动密码
            if (ContansUtils.getCheckWayIsGesture()) { //关闭当前界面，验证手势密码
                Intent intent = new Intent(activity, SetGestureActivity.class);
                intent.putExtra("start", true);
                intent.putExtra("activityNum", 0);
                startActivity(intent);
                finish();
            }
        }

    }

    /**
     * 设置密保后跳转页面
     */
    private void skipMainActivity() {
        finish();
        startActivity(new Intent(activity, MainActivity.class));
    }

    /**
     * 初始化View
     */
    private void initView() {
        //密码输入显示的TextView集合
        textViews.add((TextView) findViewById(R.id.pwd1));
        textViews.add((TextView) findViewById(R.id.pwd2));
        textViews.add((TextView) findViewById(R.id.pwd3));
        textViews.add((TextView) findViewById(R.id.pwd4));
        textViews.add((TextView) findViewById(R.id.pwd5));
        textViews.add((TextView) findViewById(R.id.pwd6));

        //输入数字View
        numGridView = (GridView) findViewById(R.id.numGridView);
        //输入后数字集合
        pwds = new ArrayList<>();

        //数字显示集合
        List<Drawable> numRes = new ArrayList<>();
        numRes.add(getResources().getDrawable(R.drawable.number1));
        numRes.add(getResources().getDrawable(R.drawable.number2));
        numRes.add(getResources().getDrawable(R.drawable.number3));
        numRes.add(getResources().getDrawable(R.drawable.number4));
        numRes.add(getResources().getDrawable(R.drawable.number5));
        numRes.add(getResources().getDrawable(R.drawable.number6));
        numRes.add(getResources().getDrawable(R.drawable.number7));
        numRes.add(getResources().getDrawable(R.drawable.number8));
        numRes.add(getResources().getDrawable(R.drawable.number9));

        //数字显示
        numGridView.setAdapter(new NumAdapter(activity, numRes));
        numGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickCallback((position + 1) + "");
            }
        });
    }

    boolean isPwdVisible = false;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pwdVisible:
                isPwdVisible = !isPwdVisible;
                if (isPwdVisible) {
                    pwdVisible.setImageResource(R.drawable.eye_open_pwd);
                } else {
                    pwdVisible.setImageResource(R.drawable.eye_close_pwd);
                }

                for (int i = 0; i < textViews.size(); i++) {
                    if (isPwdVisible) {
                        textViews.get(i).setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        textViews.get(i).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }

                break;
            case R.id.num0:
                onClickCallback("0");
                break;
            case R.id.clear:
                pwds.clear();
                for (int i = 0; i < textViews.size(); i++) {
                    textViews.get(i).setText("");
                }
                break;
        }
    }

    /**
     * 数字的点击事件回调
     *
     * @param pwd
     */
    private void onClickCallback(String pwd) {
        SystemUtils.vibrate(activity, 50);
        if (pwds.size() < 6) { //输入的数字加入密码集
            pwds.add(pwd);
            for (int i = 0; i < pwds.size(); i++) {
                if (pwds.size() - 1 >= i) {
                    textViews.get(i).setText(pwds.get(i));
                } else {
                    textViews.get(i).setText("");
                }
            }

            if (pwds.size() == 6) { //输入完毕，验证密码
                new DelayTask(300, new DelayTask.ICallBack() {
                    @Override
                    public void deal() {
                        String password = "";
                        for (int i = 0; i < pwds.size(); i++) {
                            password += pwds.get(i);
                        }

                        if (isSetting) {
                            final String finalPassword = password;
                            DialogUtils.showMsgDialog(activity, "设置当前密码？", "确定", new DialogListener() {
                                @Override
                                public void onClick() {
                                    try {
                                        ContansUtils.put(ContansUtils.CHECKWAY, ContansUtils.PWDKEY);
                                        ContansUtils.put(ContansUtils.PWDKEY, DesEncryptUtils.encode(finalPassword));
                                    } catch (DesEncryptUtils.CodecException e) {
                                        e.printStackTrace();
                                    }
                                    skipMainActivity();
                                }
                            }, "取消", new DialogListener() {
                                @Override
                                public void onClick() {
                                    pwds.clear();
                                    for (int i = 0; i < textViews.size(); i++) {
                                        textViews.get(i).setText("");
                                    }

                                }
                            });
                        } else {
                            //密码取已保存的启动密码
                            String pwdKey = (String) ContansUtils.get(ContansUtils.PWDKEY, "");
                            try {
                                password = DesEncryptUtils.encode(password);
                            } catch (DesEncryptUtils.CodecException e) {
                                e.printStackTrace();
                            }
                            if (password.equals(pwdKey)) {  //验证通过
                                if (getIntent().hasExtra("secureSet")) { //进入安全设置
                                    finish();
                                    startActivity(new Intent(activity, SecureSetActivity.class));
                                } else { //进入APP
                                    LogUtils.i("isSetting", "验证通过,进入APP");
                                    if (!getIntent().hasExtra(ContansUtils.ISBACK)) {
                                        finish();
                                        startActivity(new Intent(activity, MainActivity.class));
                                    } else {
                                        finish();
                                    }
                                    LogUtils.i("isSetting", "currentActivity:" + AppManager.getAppManager().currentActivity().getLocalClassName());
                                }
                            } else { //验证不通过：提示，震动，布局摇晃，密码清空
                                SystemUtils.keyError(activity, findViewById(R.id.pwd_layout));
                                ToastUtils.showToast(context, true, "密码验证失败！请重新输入！");
                                pwds.clear();
                                for (int i = 0; i < textViews.size(); i++) {
                                    textViews.get(i).setText("");
                                }
                            }
                        }
                    }
                }).execute();
            }
        }
    }

    /**
     * 重回界面不输入密码
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //重回界面不输入密码退出APP
            if (getIntent().hasExtra(ContansUtils.ISBACK)) {
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
