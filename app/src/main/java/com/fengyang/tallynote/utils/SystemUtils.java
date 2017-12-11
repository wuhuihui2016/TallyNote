package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.fengyang.tallynote.activity.SetGestureActivity;
import com.fengyang.tallynote.activity.SetOrCheckPwdActivity;

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

    /**
     * 获取当前运行的activity名字
     *
     * @param activity
     * @return
     */
    public static String getRunningActivityName(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    /*
      密码输入手机震动
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /*
      密码输入错误手机震动,界面晃动
     */
    public static void keyError(Context context, View view) {
        vibrate(context, 200);

        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(200);
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
            LogUtils.i(TAG, "lifecycle--APP运行在>>>>后台");
            ContansUtils.put(key, true);
        }
    }

    /**
     * 前台运行时的操作
     * TODO 为避免App启动是打开多个验证界面，首页MainActivity被销毁的时候需要手动修改前后台标志，标志改为前台
     * 如果从后台切换回来则需要验证密码，并设为前台运行
     *
     * @param activity
     */
    public static void setFore(Activity activity) {
        if ((Boolean) ContansUtils.get(key, false)) {
            LogUtils.i(TAG, "lifecycle--APP运行在>>>>前台");
            ContansUtils.put(key, false); //写入非后台运行标记

            if (!TextUtils.isEmpty((String) ContansUtils.get("gesture", ""))) {
                Intent intent = new Intent(activity, SetGestureActivity.class);
                intent.putExtra(key, true);
                intent.putExtra("activityNum", 0);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(activity, SetOrCheckPwdActivity.class);
                intent.putExtra(key, true);
                activity.startActivity(intent);
            }
        }
    }

}
