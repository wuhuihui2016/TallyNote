package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Stack;

/**
 *  * 应用程序Activity管理类
 */

public class AppManager {

    private static Stack<Activity> activityStack;

    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */

    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     *  * 添加Activity到堆栈
     */

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     *  * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     *  * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     *  * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     *  * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                finishActivity(activity);
                break;
            }
        }

		/*for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}*/
    }

    /**
     *  * 结束所有Activity
     */
    public void finishAllActivity() {
        NotificationUtils.cancel();
        if (activityStack != null) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }

//		System.exit(0);
    }

    /**
     *  * 结束所有Activity
     */
    public void finishAllBesideSefeActivity() {
        for (int i = 0, size = activityStack.size(); i < size - 1; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     *  * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }

    }

    //判断是否获取到了通知栏的权限   判断用户是否开启在通知栏显示通知
    public boolean isNotificationEnabled(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            String packageName = context.getPackageName();
            int uid = applicationInfo.uid;
            Class appOpsClass = null;
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkMethod = appOpsClass.getMethod("checkOpNoThrow",
                        Integer.TYPE,
                        Integer.TYPE,
                        String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return (
                        (int) checkMethod.invoke(mAppOpsManager, value, uid, packageName) ==
                                AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
                //如果出现了异常就返回true,默认app获得了通知栏的权限
                return true;
            }
        } else {
            return true;
        }
    }

}
