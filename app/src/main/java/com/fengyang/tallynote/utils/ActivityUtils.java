package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtils {
	
	private static String TAG = "ActivityUtils";
	private static List<Activity> activityList;

	/**
	 *  * 添加Activity
	 */
	public static void addActivity(Activity activity) {
		if (activityList == null) {
			activityList = new ArrayList<Activity>();
		}

		Log.i(TAG, "addActivity---" + activity.getLocalClassName());
		activityList.add(activity);
		
		//去重
		for ( int i = 0 ; i < activityList.size() - 1 ; i ++ ) { 
			for ( int j  = activityList.size()  - 1 ; j > i; j -- ) { 
				if (activityList.get(j).getLocalClassName().equals(activityList.get(i).getLocalClassName())) { 
					activityList.remove(j); 
				} 
			} 
		} 
		Log.i(TAG, activityList.toString());
	
	}

	/**
	 *  * 获取当前Activity
	 */
	public static Activity currentActivity() {
		Activity activity = activityList.get(activityList.size() - 1);
		Log.i(TAG, "currentActivity---" + activity.getLocalClassName());
		return activity;
	}

	/**
	 *  * 结束当前Activity
	 */
	public static void finishActivity(){
		Activity activity = activityList.get(activityList.size() - 1);
		if (activity != null) {
			activityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 *  * 结束指定的Activity
	 */
	public static void finishActivity(Activity activity) {
		if (activity != null) {
			activityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 *  * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls) {
		for (Activity activity : activityList) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 *  * 结束所有Activity
	 */
	public static void finishAllActivity() {
		if (activityList != null) {
			for (int i = 0, size = activityList.size(); i < size; i++) {
				if (null != activityList.get(i)) {
					activityList.get(i).finish();
				}
			}
			activityList.clear();
		}
	}

	/**
	 *  * 结束所有Activity
	 */
	public static void finishAllBesideSefeActivity() {
		for (int i = 0, size = activityList.size(); i < size-1; i++) {
			if (null != activityList.get(i)) {
				activityList.get(i).finish();
			}
		}
		activityList.clear();
	}

	/**
	 *  * 退出应用程序
	 */
	
	@SuppressWarnings("deprecation")
	public static void AppExit(Context context) {
		Log.i(TAG, "退出APP");
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}

	}

}
