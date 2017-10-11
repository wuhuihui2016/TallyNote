package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
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
    public static Timer timer = new Timer();
    private static TimerTask task;

    public static void getIsRunningForeground(final String tag, final Context context) {
        try {
            task = new TimerTask() {

                @Override
                public void run() {
                    if (checkOnBack(context)) {
//                        LogUtils.i(tag, "APP后台运行...");
                        ContansUtils.put(key, true); //写入后台运行的标记
                    } else {
//                        LogUtils.i(tag, "APP前台运行...");
                        if ((Boolean) ContansUtils.get(key, false)) {
                            //如果是后台转前台运行需要验证密码
                            Intent intent = new Intent(context, OnStartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(key, true);
                            context.startActivity(intent);
                            ContansUtils.put(key, false); //写入非后台运行的标记
                        }
                    }
                }
            };
            if (timer != null) timer.schedule(task, 0, 500);//每0.5秒执行一次
        } catch (Exception e) {

        }
    }

    /**
     * 判断当前是否后台运行或屏幕熄灭
     *
     * @param context
     * @return
     */
    private static boolean checkOnBack(Context context) {
        boolean flag = false;

        //判断是否为前台运行,true为后台运行，false为前台运行
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName)
                && currentPackageName.equals(context.getPackageName())) {
            //前台运行
        } else {
            //后台运行
            flag = true;
        }

        //判断手机屏幕是否亮着
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean ifOpen = powerManager.isScreenOn(); //true为打开，false为关闭

        return flag || (!ifOpen); //后台运行或屏幕灭屏返回true,需要验证密码

    }

    /**
     * 手动设置为后台运行，用于首页MainActivity被销毁的时候
     */
    public static void setBack() {
        ContansUtils.put(key, true);
    }


    /**
     * 停止判断APP前后台运行的状态轮询
     */
    public static void stopIsForeTimer() {
        if (timer != null && task != null) {
            LogUtils.i(TAG, "APP停止前后台运行的状态轮询");
            timer.cancel();
            timer = null;
            task.cancel();
            task = null;
        }
    }

}
