package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;

/**
 * Created by wuhuihui on 2017/4/25.
 */
public class DialogUtils {

    private static final String TAG = "DialogUtils";

    public static Dialog dialog;
    private static TextView titleView, msgView, comfireView, cancelView;
    private static LinearLayout title_layout, cancel_layout;

    /**
     * 页面消息提示（dialog）
     *
     * @param activity
     * @param message
     */
    public static void showMsgDialog(Activity activity, String message) {
        try {
            initDialog(activity);
            title_layout.setVisibility(View.GONE);
            msgView.setText(message);
            cancel_layout.setVisibility(View.GONE);

            comfireView.setText("知道了");
            comfireView.setOnClickListener(new DialogListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                }
            });

            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog", e.toString());
        }
    }

    /**
     * 页面消息提示（dialog）
     *
     * @param activity
     * @param title
     * @param message
     */
    public static void showMsgDialog(Activity activity, String title, String message) {
        try {
            initDialog(activity);
            if (TextUtils.isEmpty(title)) {
                titleView.setText("温馨提示");
            } else {
                titleView.setText(title);
            }
            msgView.setText(message);

            comfireView.setText("知道了");
            comfireView.setOnClickListener(new DialogListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                }
            });
            cancel_layout.setVisibility(View.GONE);

            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog", e.toString());
        }
    }

    /**
     * 页面消息提示（dialog）
     *
     * @param activity
     * @param title
     * @param message
     * @param comfireListener
     */
    public static void showMsgDialog(Activity activity, String title,
                                     String message, DialogListener comfireListener) {
        try {
            initDialog(activity);
            if (TextUtils.isEmpty(title)) {
                titleView.setText("温馨提示");
            } else {
                titleView.setText(title);
            }
            msgView.setText(message);
            comfireView.setOnClickListener(comfireListener);
            cancel_layout.setVisibility(View.GONE);
            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog", e.toString());
        }
    }

    /**
     * 页面消息提示（dialog）
     *
     * @param activity
     * @param title
     * @param message
     * @param comfireListener
     * @param cancelListener
     */
    public static void showMsgDialog(Activity activity, String title, String message,
                                     DialogListener comfireListener, DialogListener cancelListener) {
        try {
            initDialog(activity);
            if (TextUtils.isEmpty(title)) {
                titleView.setText("温馨提示");
            } else {
                titleView.setText(title);
            }
            msgView.setText(message);
            comfireView.setOnClickListener(comfireListener);

            if (cancelListener == null) cancel_layout.setVisibility(View.GONE);
            else cancelView.setOnClickListener(cancelListener);
            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog", e.toString());
        }
    }

    /**
     * @param activity
     * @param title
     * @param message
     * @param comfireListener
     * @param cancelListener
     */
    public static void showMsgDialog(Activity activity, String title, String message,
                                     String comfireTxt, DialogListener comfireListener, String cancelTxt, DialogListener cancelListener) {
        try {
            initDialog(activity);
            if (TextUtils.isEmpty(title)) {
                titleView.setText("温馨提示");
            } else {
                titleView.setText(title);
            }
            msgView.setText(message);

            comfireView.setText(comfireTxt);
            comfireView.setOnClickListener(comfireListener);

            if (cancelListener == null) cancel_layout.setVisibility(View.GONE);
            else {
                cancelView.setText(cancelTxt);
                cancelView.setOnClickListener(cancelListener);
            }
            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog", e.toString());
        }
    }

    /**
     * 三个操作按钮的dialog
     *
     * @param activity
     * @param title
     * @param message
     * @param btn1Txt
     * @param btn1Listener
     * @param btn2Txt
     * @param btn2Listener
     */
    public static void showMsgDialog3(Activity activity, String title, String message,
                                      String btn1Txt, DialogListener btn1Listener, String btn2Txt, DialogListener btn2Listener) {
        try {
            dialog = new Dialog(activity, R.style.mDialogStyle);//dilog style must add,else base default style
            dialog.setContentView(R.layout.dialog_msg2);//设置布局
            dialog.setCanceledOnTouchOutside(false); //点击外部关闭dialog

            WindowManager manager = activity.getWindowManager();
            Window window = dialog.getWindow();
            Display display = manager.getDefaultDisplay();
            int width = display.getWidth();
            window.setLayout(width / 5 * 4, WindowManager.LayoutParams.WRAP_CONTENT);//设置宽度为屏幕的4/5，高度自适应
//        //弹出dialog后activity背景变暗设置
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.dimAmount = 0f;
//        window.setAttributes(lp);

            titleView = (TextView) dialog.findViewById(R.id.title);
            msgView = (TextView) dialog.findViewById(R.id.msg);
            TextView btn1 = (TextView) dialog.findViewById(R.id.btn1);
            TextView btn2 = (TextView) dialog.findViewById(R.id.btn2);

            if (TextUtils.isEmpty(title)) {
                titleView.setText("温馨提示");
            } else {
                titleView.setText(title);
            }
            msgView.setText(message);

            btn1.setText(btn1Txt);
            btn1.setOnClickListener(btn1Listener);
            btn2.setText(btn2Txt);
            btn2.setOnClickListener(btn2Listener);

            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            LogUtils.e(TAG + "-showMsgDialog3", e.toString());
        }
    }

    /**
     * 初始化dialog
     *
     * @param activity
     * @return
     */
    private static void initDialog(Activity activity) {
        dialog = new Dialog(activity, R.style.mDialogStyle);//dilog style must add,else base default style
        dialog.setContentView(R.layout.dialog_msg);//设置布局
        dialog.setCanceledOnTouchOutside(false); //点击外部关闭dialog

        WindowManager manager = activity.getWindowManager();
        Window window = dialog.getWindow();
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        window.setLayout(width / 5 * 4, WindowManager.LayoutParams.WRAP_CONTENT);//设置宽度为屏幕的三个操作按钮的dialog，高度自适应
//        //弹出dialog后activity背景变暗设置
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.dimAmount = 0f;
//        window.setAttributes(lp);

        titleView = (TextView) dialog.findViewById(R.id.title);
        msgView = (TextView) dialog.findViewById(R.id.msg);
        comfireView = (TextView) dialog.findViewById(R.id.comfire);
        cancelView = (TextView) dialog.findViewById(R.id.cancel);
        title_layout = (LinearLayout) dialog.findViewById(R.id.title_layout);
        cancel_layout = (LinearLayout) dialog.findViewById(R.id.cancel_layout);
    }

    /**
     * 自定义DialogListener，用于点击时关闭dialog
     */
    public static class DialogListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }
}
