package com.whh.tallynote.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.whh.tallynote.MyApplication;
import com.whh.tallynote.R;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;
import com.whh.tallynote.utils.WPSUtils;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected Context context;//获取当前对象
    protected Activity activity;//获取当前对象
    protected String TAG;//当前界面输出log时的标签字段
    protected FrameLayout content_layout;
    protected PopupWindow popupWindow;
    private ImageButton addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();

    }

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

        addNote = (ImageButton) findViewById(R.id.addNote);

        if (TAG.contains("New")) {
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
                            startActivity(new Intent(activity, NewDayActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.newMNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewMonthActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.newIncome).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewIncomeActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.newMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewMemoActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.newNotepad).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewNotePadActivity.class));
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

        ImageButton return_btn = (ImageButton) findViewById(R.id.return_btn);
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
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);
        } else {
            RelativeLayout top_layout = (RelativeLayout) findViewById(R.id.top_layout);
            top_layout.setVisibility(View.GONE);
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
        content_layout = (FrameLayout) findViewById(R.id.content_layout);
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
            TextView right_btn = (TextView) findViewById(R.id.right_btn);
            right_btn.setVisibility(View.VISIBLE);
            right_btn.setText(text);
            right_btn.setOnClickListener(listener);
            findViewById(R.id.right_imgbtn).setVisibility(View.GONE);
        }
    }

    /**
     * 设置界面右上角图片按钮的点击事件
     *
     * @param resId
     * @param listener
     */
    protected void setRightImgBtnListener(int resId, View.OnClickListener listener) {
        ImageView right_imgbtn = (ImageView) findViewById(R.id.right_imgbtn);
        right_imgbtn.setVisibility(View.VISIBLE);
        right_imgbtn.setImageResource(resId);
        right_imgbtn.setOnClickListener(listener);

        findViewById(R.id.right_btn).setVisibility(View.GONE);
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

        try {
            MyApplication.dbHelper.db.close();
        } catch (Exception e) {
            LogUtils.e(TAG + "-onDestroy", e.toString());
        }
    }
}
