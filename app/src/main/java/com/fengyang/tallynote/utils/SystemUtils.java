package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.fengyang.tallynote.activity.OnStartActivity;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class SystemUtils {

    private static String TAG = "SystemUtils";

    //隐藏键盘
    public static void hideInput(Activity activity) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            LogUtils.e(TAG + "-getVersion", e.toString());
            e.printStackTrace();
        }
        return info.versionName;
    }

    /*
      密码输入错误手机震动,界面晃动
     */
    public static void Vibrate(Activity activity, long milliseconds, View view) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);

        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }

    /**
     * 后台标志key
     */
    public static String key = "isBack";

    /**
     * 后台运行时的操作
     * 后台运行的操作，首页MainActivity被销毁的时候需要再次执行
     * 如果选择文件导入，打开系统的文件后不设为后台
     * 如果从APP内部发送文件或者打开文件时跳转第三方APP视为APP后台执行
     */
    public static void setBack(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String className = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        LogUtils.i(TAG, "APP运行在>>>>" + className);
        //com.android.documentsui.DocumentsActivity 系统文件夹
        //com.android.internal.app.ChooserActivity 系统分享列表，可忽略
        //如果直接跳转第三方APP回来后还是需要验证密码
        if ((!className.contains("DocumentsActivity")) && (!className.contains("ChooserActivity"))) {
            LogUtils.i(TAG, "APP运行在>>>>后台");
            ContansUtils.put(key, true);
        }
    }

    /**
     * 前台运行时的操作
     * 如果从后台切换回来则需要验证密码，并设为前台运行
     * @param context
     */
    public static void setFore(Context context) {
        if ((Boolean) ContansUtils.get(key, false)) {
            LogUtils.i(TAG, "APP运行在>>>>前台");
            Intent intent = new Intent(context, OnStartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(key, true);
            context.startActivity(intent);
            ContansUtils.put(key, false); //写入非后台运行的标记
        }
    }

}
