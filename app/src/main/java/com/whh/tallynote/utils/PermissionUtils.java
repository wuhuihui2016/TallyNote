package com.whh.tallynote.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class PermissionUtils {

    public static int REQUESTCODE = 0;
    public static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] PERMISSIONS_READ_PHONE_STATE = {Manifest.permission.READ_PHONE_STATE};

    /**
     * TODO 判断SDcard权限获取成功与否
     * 失败后本方法不调用系统弹出框
     *
     * @param activity
     * @param checkCallback
     */
    public static void checkSDcardPermission(Activity activity, OnCheckCallback checkCallback) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            checkCallback.onCheck(false);
        } else {
            checkCallback.onCheck(true);
        }
    }

    /**
     * TODO 判断手机信息权限获取成功与否
     * 失败后本方法不调用系统弹出框
     *
     * @param activity
     * @param checkCallback
     */
    public static void checkPhonestatePermission(Activity activity, OnCheckCallback checkCallback) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            checkCallback.onCheck(false);
        } else {
            checkCallback.onCheck(true);
        }
    }

    /**
     * 权限获取回调
     */
    public interface OnCheckCallback {
        void onCheck(boolean isSucess);
    }

    /**
     * 获取SDCard读写权限失败时的操作
     * 申请弹出获取权限系统框
     */
    public static void notSDCardPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUESTCODE);
    }

    /**
     * 获取手机信息权限失败时的操作
     * 申请弹出获取权限系统框
     */
    public static void notPhoneStatePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_READ_PHONE_STATE, REQUESTCODE);
    }
}
