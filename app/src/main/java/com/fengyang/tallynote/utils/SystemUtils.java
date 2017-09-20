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
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fengyang.tallynote.activity.OnStartActivity;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

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
     * 针对与某个输入框的方法：禁掉系统软键盘
     * （用于自定义键盘）
     */
    public static void hideSoftInputMethod(Activity activity, EditText editText) {
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            String methodName = null;
            if (currentVersion >= 16) { // 4.2
                methodName = "setShowSoftInputOnFocus";
            } else if (currentVersion >= 14) { // 4.0
                methodName = "setSoftInputShownOnFocus";
            }
            if (methodName == null) {
                editText.setInputType(InputType.TYPE_NULL);
            } else {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            }
        } catch (Exception e) {
            LogUtils.e(TAG + "-hideSoftInputMethod", e.toString());
        }
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
     * @Title: getIsBackgroup
     * @Description: TODO 判断APP是否在前台运行
     * @return
     * @return boolean
     * @author wuhuihui
     * @date 2016年3月29日 下午4:58:34
     */
    public static String key = "isBack";
    private static Timer timer;
    private static TimerTask task;

    public static void getIsRunningForeground(final String tag, final Context context) {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                String currentPackageName = cn.getPackageName();
                if (!TextUtils.isEmpty(currentPackageName)
                        && currentPackageName.equals(context.getPackageName())) {
//                    LogUtils.i(tag, "APP is running foreground");
                    if ((Boolean) ContansUtils.get(key, false)) {
                        Intent intent = new Intent(context, OnStartActivity.class);
                        intent.putExtra(key, true);
                        context.startActivity(intent);
                        ContansUtils.put(key, false);
                    }
                } else {
//                    LogUtils.i(tag, "APP is running background");
                    ContansUtils.put(key, true);
                }
            }
        };
        timer.schedule(task, 0, 1000);//每1秒执行一次
    }

    /**
     * 停止判断APP前台运行的状态轮询
     *
     * @param tag
     */
    public static void stopIsForeTimer(final String tag) {
        if (timer != null && task != null) {
            LogUtils.i(tag, "APP is stopIsForeTimer");
            timer.cancel();
            timer = null;
            task.cancel();
            task = null;
        }
    }

}
