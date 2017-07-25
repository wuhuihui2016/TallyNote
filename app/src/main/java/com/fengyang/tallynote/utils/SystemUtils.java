package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class SystemUtils {

    private static String TAG = "SystemUtils";

    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        if(context != null){
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
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
            LogUtils.i("hideSoftInputMethod", e.toString());
        }
    }

}
