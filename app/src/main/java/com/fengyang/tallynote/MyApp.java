package com.fengyang.tallynote;

import android.app.Application;

import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.CrashHandler;
import com.fengyang.tallynote.utils.DBUtils;
import com.fengyang.tallynote.utils.FileUtils;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MyApp extends Application {

    private static Application instance;

    public static DBUtils utils;

    public static final int DAY = 0, MONTH = 1, INCOME = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        ContansUtils.setPres(this);//设置存储空间，获取编辑器

        utils = new DBUtils(instance); //开辟用户数据库

        FileUtils.createDir(); //创建项目文件目录
    }

    /**
     * 获取application实例
     */
    public static Application getInstance() {
        return instance;
    }

}
