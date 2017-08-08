package com.fengyang.tallynote.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.NumAdapter;
import com.fengyang.tallynote.database.MonthNoteDao;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.SystemUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 忘记密码
 * Created by wuhuihui on 2017/6/27.
 */
public class ForgetPwdActivity extends BaseActivity {

    private List<TextView> textViews = new ArrayList<>();
    private GridView numGridView;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_forgetpwd);

        initView();
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
        list = new ArrayList<>();

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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.num0:
                onClickCallback("0");
                break;
            case R.id.clear:
                list.clear();
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
        if (list.size() < 6) {
//        StringUtils.show1Toast(activity, pwd);
            list.add(pwd);
            for (int i = 0; i < list.size(); i++) {
                if (list.size() - 1 >= i) {
                    textViews.get(i).setText(list.get(i));
                } else {
                    textViews.get(i).setText("");
                }
            }

            if (list.size() == 6) {
                new DelayTask(300, new DelayTask.ICallBack() {
                    @Override
                    public void deal() {
                        String pwdKey = "";
                        for (int i = 0; i < list.size(); i++) {
                            pwdKey += list.get(i);
                        }

                        String getPwdKey = (String) ContansUtils.get("pwdKey", "");
                        if (pwdKey.equals(getPwdKey)) {
                            List<MonthNote> monthNotes = MonthNoteDao.getMonthNotes();
                            String pwd = monthNotes.get(monthNotes.size() - 1).getActual_balance().split("\\.")[0];
                            if (pwd.length() > 6) {
                                pwd = pwd.substring(0, 5);
                            } else if (pwd.length() < 6) {
                                pwd = getPwdKey;
                            }
                            DialogUtils.showMsgDialog(activity, "密码提示", pwd, new DialogUtils.DialogListener() {
                                @Override
                                public void onClick(View v) {
                                    super.onClick(v);
                                    finish();
                                }
                            });

                        } else {
                            SystemUtils.Vibrate(activity, 100);
                            ToastUtils.showToast(context, true, "验证失败！请重新输入！");
                            list.clear();
                            for (int i = 0; i < textViews.size(); i++) {
                                textViews.get(i).setText("");
                            }
                        }
                    }
                }).execute();
            }
        }
    }


}
