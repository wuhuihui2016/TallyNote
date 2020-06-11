package com.whh.tallynote;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.whh.tallynote.database.DBHandle;
import com.whh.tallynote.database.DayNoteDBHandle;
import com.whh.tallynote.database.IncomeNoteDBHandle;
import com.whh.tallynote.database.MemoNoteDBHandle;
import com.whh.tallynote.database.MonthNoteDBHandle;
import com.whh.tallynote.database.NotePadDBHandle;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.CrashHandler;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MyApp extends Application {

    private static String TAG = "MyApplication";

    public static DBHandle dbHandle;
    public static DayNoteDBHandle dayNoteDBHandle;
    public static MonthNoteDBHandle monthNoteDBHandle;
    public static IncomeNoteDBHandle incomeNoteDBHandle;
    public static MemoNoteDBHandle memoNoteDBHandle;
    public static NotePadDBHandle notePadDBHandle;

    public int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this); //检测内存泄露
        ContansUtils.setPres(this);//设置存储空间，获取编辑器
        FileUtils.createDir(); //创建项目文件目录(excel文档及crash日志)
        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        //创建数据库表
        dbHandle = DBHandle.getInstance(this);
        dayNoteDBHandle = DayNoteDBHandle.getInstance(this); //日账
        monthNoteDBHandle = MonthNoteDBHandle.getInstance(this); //月账
        incomeNoteDBHandle = IncomeNoteDBHandle.getInstance(this); //理财
        memoNoteDBHandle = MemoNoteDBHandle.getInstance(this); //备忘录
        notePadDBHandle = NotePadDBHandle.getInstance(this); //记事本

        dbHandle.getCount4Record(ContansUtils.ALL); //获取所有的表分别的记录数


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
