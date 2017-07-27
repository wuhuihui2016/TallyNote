package com.fengyang.tallynote.utils;

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
     * 权限获取回调
     */
    public interface OnCheckCallback {
        void onCheck(boolean isSucess);
    }

    /**
     * 权限获取失败时的操作
     */
    public static void notPermission(Activity activity, String[] permissions) {
        //申请弹出获取权限系统框
        ActivityCompat.requestPermissions(activity, permissions, REQUESTCODE);
    }
}
