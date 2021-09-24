package com.whh.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.whh.tallynote.activity.SetGestureActivity;
import com.whh.tallynote.activity.SetOrCheckPwdActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取栈里的Activity
     * @return
     */
    public static List<Activity> getAllActivitys(){
        List<Activity> list=new ArrayList<>();
        try {
            Class<?> activityThread=Class.forName("android.app.ActivityThread");
            Method currentActivityThread=activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            //获取主线程对象
            Object activityThreadObject=currentActivityThread.invoke(null);
            Field mActivitiesField = activityThread.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Map<Object,Object> mActivities = (Map<Object,Object>) mActivitiesField.get(activityThreadObject);
            for (Map.Entry<Object,Object> entry:mActivities.entrySet()){
                Object value = entry.getValue();
                Class<?> activityClientRecordClass = value.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Object o = activityField.get(value);
                list.add((Activity) o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
     * 后台运行时的操作
     * 如果选择文件导入，打开系统的文件后不设为后台
     * 如果从APP内部发送文件或者打开文件时跳转第三方APP视为APP后台执行
     */
    public static void setBack(Activity activity) {
        ContansUtils.pauseCheckBack = false; //暂停判断前后台切换验证标志位恢复
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String className = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        LogUtils.i(TAG, "APP运行在>>>>" + className);
        //com.android.documentsui.DocumentsActivity 系统文件夹
        //com.android.internal.app.ChooserActivity 系统分享列表，可忽略
        //如果直接跳转第三方APP回来后还是需要验证密码
        if ((!className.contains("DocumentsActivity")) && (!className.contains("ChooserActivity"))) {
            LogUtils.i(TAG, "lifecycle--APP运行在>>>>后台");
            ContansUtils.put(ContansUtils.ISBACK, true);
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
        if (ContansUtils.pauseCheckBack) return; //如果中止了判断前后台的切换，则不需跳转验证密码页
        if ((Boolean) ContansUtils.get(ContansUtils.ISBACK, false)) {
            LogUtils.i(TAG, "lifecycle--APP运行在>>>>前台");
            ContansUtils.put(ContansUtils.ISBACK, false); //写入非后台运行标记

            Intent intent = new Intent();
            intent.putExtra(ContansUtils.ISBACK, true);
            if (!TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.GESTURE, ""))) {
                intent.setClass(activity, SetGestureActivity.class);
                intent.putExtra("activityNum", 0);
                activity.startActivity(intent);
            } else {
                intent.setClass(activity, SetOrCheckPwdActivity.class);
                activity.startActivity(intent);
            }
        }
    }

    /**
     * 判断app是否处于前台
     * (用于通知点击判断跳转，如果APP在后台运行，在点击通知时需要验证密码)
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络是否为WIFI
     *
     * @param context
     * @return
     */
    public static boolean getIsWIFI(Context context) {

        boolean isWIFI = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                isWIFI = true;
            } else {
                isWIFI = false;
            }
        } else {
            isWIFI = false;
        }

        return isWIFI;
    }


    /**
     * 避免按钮短时间点击多次
     */
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getWidth(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getHeight(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

}
