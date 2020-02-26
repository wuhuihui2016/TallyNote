package com.whh.tallynote;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.whh.tallynote.database.DBHelper;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.CrashHandler;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MyApplication extends Application {

    private static String TAG = "MyApplication";

    public static DBHelper dbHelper;

    public int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        ContansUtils.setPres(this);//设置存储空间，获取编辑器

        dbHelper = new DBHelper(this); //开辟用户数据库

        FileUtils.createDir(); //创建项目文件目录

        //初始化
        LeakCanary.install(this);

        /**
         * 注册监听APP前后台运行判断
         */
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    SystemUtils.setBack(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                String name = SystemUtils.getRunningActivityName(activity);
                LogUtils.i(TAG + "-getRunningActivityName", name);
                if (count == 0) {
                    SystemUtils.setFore(activity);
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }
        });

    }


}
