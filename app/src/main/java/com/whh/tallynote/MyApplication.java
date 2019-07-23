package com.whh.tallynote;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.whh.tallynote.database.DBHelper;
import com.whh.tallynote.greendao.DaoSession;
import com.whh.tallynote.greendao.GreenDaoUtils;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.CrashHandler;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MyApplication extends Application {

    private static String TAG = "MyApplication";

    public static DBHelper dbHelper;

    public static DaoSession daoSession;

    public int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        ContansUtils.setPres(this);//设置存储空间，获取编辑器

        dbHelper = new DBHelper(this); //开辟用户数据库

        GreenDaoUtils.initDao(this); //创建GreenDao数据库
        daoSession = GreenDaoUtils.getDaoSession();

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

        readFile();

    }

    private void readFile() {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("JSON_QtdFMboQ.json");
            int size = inputStream.available();
            int len = -1;
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            String content = new String(bytes);

            System.out.println(content.length() + ", [" + content + "]");
            Log.i("whhhh", content.length() + "");
            Log.i("whhhh", "start=" + content.substring(0, 20));
            Log.i("whhhh", "end=" + content.substring(content.length() - 21, content.length()));
            Log.i("whhhh", content);

            JSONObject object = new JSONObject(content);
            Log.i("whhhh_Json", "version=" + object.optString("version"));
            JSONArray array = object.optJSONArray("RECORDS");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                Log.i("whhhh_Json", i + "-->xm=" + obj.optString("xm"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
