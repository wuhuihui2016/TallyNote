package com.whh.tallynote.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.activity.NewDayActivity;
import com.whh.tallynote.activity.NewIncomeActivity;
import com.whh.tallynote.activity.NewMemoActivity;
import com.whh.tallynote.activity.NewMonthActivity;
import com.whh.tallynote.activity.NewNotePadActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;
import com.whh.tallynote.utils.WPSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;


/**
 * Created by wuhuihui on 2017/3/24.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected Context context;//获取当前对象
    protected Activity activity;//获取当前对象
    protected String TAG;//当前界面输出log时的标签字段

    private RelativeLayout top_layout; //头布局
    private ImageButton return_btn; //返回按钮
    private TextView titleView; //标题
    private TextView right_btn; //右按钮
    private ImageView right_imgbtn; //右图片按钮
    protected FrameLayout content_layout; //中间内容布局
    private ImageButton addNote; //显示/隐藏新增项目按钮
    protected PopupWindow popupWindow; //新增项目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();
        initBundleData(savedInstanceState); //接收传递的数据
        ButterKnife.bind(this);
        initView(); //初始化界面
        initEvent(); //初始化事件操作
    }

    //接收传递的数据
    protected abstract void initBundleData(Bundle bundle);

    //初始化界面
    protected abstract void initView();

    //初始化事件操作
    protected abstract void initEvent();

    /**
     * 初始化Activity
     */
    private void initActivity() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
        setContentView(R.layout.activity_base_layout);

        context = this;
        activity = this;
        TAG = getLocalClassName(); //初始化常量

        Log.i(TAG, TAG + " is onCreated!");

        AppManager.getAppManager().addActivity(this);

        top_layout = (RelativeLayout) findViewById(R.id.top_layout);
        return_btn = (ImageButton) findViewById(R.id.return_btn);
        titleView = (TextView) findViewById(R.id.title);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_imgbtn = (ImageView) findViewById(R.id.right_imgbtn);
        content_layout = (FrameLayout) findViewById(R.id.content_layout);
        addNote = (ImageButton) findViewById(R.id.addNote);

        if (TAG.contains("New") || TAG.contains("ForgetPwdActivity")
                || TAG.contains("LoginUserActivity")) {
            addNote.setVisibility(View.GONE);

        } else {
            addNote.setVisibility(View.VISIBLE);
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopupWindow();
                }
            });
        }

        if (TAG.contains("List4") && !TAG.contains("Of")) { //仅一级列表页注册EventBus
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this); //注册事件,不可重复注册
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_add_note_pop, null);
            popupWindow = new PopupWindow(layout, 400, 800);
            ViewUtils.setPopupWindow(activity, popupWindow);
            // 相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAsDropDown(addNote, 50, 20);
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);

            layout.findViewById(R.id.newDNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewDayActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newMNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMonthActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newIncome).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewIncomeActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMemoActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newNotepad).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewNotePadActivity.class);
                        }
                    }
            );
        }
    }

    /**
     * 设置中间内容布局
     *
     * @param title
     */
    protected void setTitle(String title) {

        if (isOtherActivity()) {
            return_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TAG.contains("New")) {
                        DialogUtils.showMsgDialog(activity, "退出本次编辑",
                                "退出", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                        finish();
                                    }
                                },
                                "取消", new DialogListener() {
                                    @Override
                                    public void onClick() {

                                    }
                                });
                    } else {
                        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                        else finish();
                    }
                }
            });
        } else {
            return_btn.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            //设置当前界面的title
            titleView.setText(title);
        } else {

            top_layout.setVisibility(View.GONE);
        }
    }

    protected void setReturnBtnClickLitener(View.OnClickListener listener) {
        if (listener != null) {
            return_btn.setOnClickListener(listener);
        }
    }

    /**
     * 设置中间内容布局
     *
     * @param layoutID
     * @param title
     */
    protected void setContentView(String title, int layoutID) {

        setTitle(title);

        //加载中间布局
        content_layout.removeAllViews();
        View view = LayoutInflater.from(this).inflate(layoutID, null);
        content_layout.addView(view);

    }

    /**
     * 设置界面右上角按钮的点击事件
     *
     * @param text
     * @param listener
     */
    protected void setRightBtnListener(CharSequence text, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(text)) {
            right_btn.setVisibility(View.VISIBLE);
            right_btn.setText(text);
            right_btn.setOnClickListener(listener);
            right_imgbtn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置界面右上角图片按钮的点击事件
     *
     * @param resId
     * @param listener
     */
    protected void setRightImgBtnListener(int resId, View.OnClickListener listener) {
        right_imgbtn.setVisibility(View.VISIBLE);
        right_imgbtn.setImageResource(resId);
        right_imgbtn.setOnClickListener(listener);
        right_btn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (SystemUtils.isFastDoubleClick()) {
            return;
        }
    }

    /**
     * 导出结果回调
     */
    protected ExcelUtils.ICallBackExport callBackExport = new ExcelUtils.ICallBackExport() {
        @Override
        public void callback(boolean sucess, final String fileName) {
            if (sucess) {
                DialogUtils.showMsgDialog(activity, "导出成功\n" + fileName,
                        "查看", new DialogListener() {
                            @Override
                            public void onClick() {
                                WPSUtils.openFile(context, fileName);
                            }
                        },
                        "忽略", new DialogListener() {
                            @Override
                            public void onClick() {
                            }
                        });
            } else ToastUtils.showErrorLong(activity, "导出失败！");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "lifecycle---" + TAG + " onResume");
    }

    @Override
    public void finish() {
        super.finish();
        if (isOtherActivity()) {
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    @Override
    public void onBackPressed() {
        if (isOtherActivity()) {
            if (TAG.contains("New")) {
                DialogUtils.showMsgDialog(activity, "退出本次编辑",
                        "退出", new DialogListener() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        },
                        "取消", new DialogListener() {
                            @Override
                            public void onClick() {

                            }
                        });
            } else {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                else super.onBackPressed();
            }

        }
    }


    /**
     * 判断当前activity是不是非MainActivity
     *
     * @return
     */
    private boolean isOtherActivity() {
        return !TAG.contains("MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "lifecycle---" + TAG + " onDestroy");
        //获取栈里所有的Activity
//        List<Activity> allActivitys = SystemUtils.getAllActivitys();
//        StringBuffer sb = new StringBuffer();
//        sb.append("栈里的Activity==>");
//        for (int i = 0; i < allActivitys.size(); i++) {
//            sb.append(allActivitys.get(i).getLocalClassName().replace("com.whh.tallynote.activity.", "") + "---");
//        }
//        LogUtils.d(TAG, "lifecycle---" + sb.toString());
//
//        try {
//            MyApplication.dbHelper.db.close();
//        } catch (Exception e) {
//            LogUtils.e(TAG + "-onDestroy", e.toString());
//        }

        if (TAG.contains("List4") && !TAG.contains("Of")) {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this); //注销事件
        }
    }

}
