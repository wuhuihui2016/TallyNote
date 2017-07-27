package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by wuhuihui on 2017/7/27.
 */
public class ViewUtils {


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    private static void setBackgroundAlpha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }

    /**
     * 设置popupWindow参数
     * @param context
     * @param popupWindow
     */
    public static void setPopupWindow(final Context context, PopupWindow popupWindow) {
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);
        setBackgroundAlpha(context, 0.8f);
        
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(context, 1.0f);
            }
        });
    }
}
