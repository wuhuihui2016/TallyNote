package com.fengyang.tallynote;

import android.app.Application;

import com.fengyang.tallynote.database.DBHelper;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.CrashHandler;
import com.fengyang.tallynote.utils.FileUtils;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MyApp extends Application {

    public static DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        ContansUtils.setPres(this);//设置存储空间，获取编辑器

        dbHelper = new DBHelper(this); //开辟用户数据库

        FileUtils.createDir(); //创建项目文件目录
    }

}
