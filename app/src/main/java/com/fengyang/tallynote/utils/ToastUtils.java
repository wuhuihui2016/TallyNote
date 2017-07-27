package com.fengyang.tallynote.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.tallynote.R;

/**
 * 用于在屏幕中间显示toast，并带有图片
 */

public class ToastUtils {

    /**
     * 页面消息提示
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, boolean isShort, String message) {
        if (isShort) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private static Toast mToast;
    private static final int SUCESSSHOW = 1;//成功
    private static final int ERRORSHOW = 2;//失败
    private static final int WARNINGSHOW = 3;//警告

    private static void toastShow(Context context, CharSequence text, boolean isShort, int tag) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = new Toast(context);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        if (isShort) mToast.setDuration(Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_LONG);

        View view = View.inflate(context, R.layout.layout_toast, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView textView = (TextView) view.findViewById(R.id.text);
        if (tag == SUCESSSHOW) imageView.setImageResource(R.drawable.toast_sucess);
        else if (tag == ERRORSHOW) imageView.setImageResource(R.drawable.toast_fail);
        else imageView.setImageResource(R.drawable.toast_warning);

        textView.setText(text);
        if (text.length() > 13) {
            textView.setTextSize(13);
        } else {
            textView.setTextSize(15);
        }
        mToast.setView(view);
        mToast.show();
    }

    //成功的显示
    public static void showSucessLong(Context context, CharSequence text) {
        toastShow(context, text, true, SUCESSSHOW);
    }

    //成功的短显示
    public static void showSucessShort(Context context, CharSequence text) {
        toastShow(context, text, false, SUCESSSHOW);
    }

    //失败的长显示
    public static void showErrorLong(Context context, CharSequence text) {
        toastShow(context, text, true, ERRORSHOW);
    }

    //失败的短显示
    public static void showErrorShort(Context context, CharSequence text) {
        toastShow(context, text, false, ERRORSHOW);
    }

    //警告的长显示
    public static void showWarningLong(Context context, CharSequence text) {
        toastShow(context, text, true, WARNINGSHOW);
    }

    //警告的短显示
    public static void showWarningShort(Context context, CharSequence text) {
        toastShow(context, text, false, WARNINGSHOW);
    }
}

