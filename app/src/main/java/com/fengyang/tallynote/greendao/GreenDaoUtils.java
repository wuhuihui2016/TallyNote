package com.fengyang.tallynote.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2019/5/13.
 */

public class GreenDaoUtils {

    public static DaoSession daoSession;

    /**
     * 创建数据库
     */
    public static void initDao(Context context) {

        //创建数据库admin_user.db
        DaoMaster.DevOpenHelper daoHelper = new DaoMaster.DevOpenHelper(context, "admin_user.db", null);

        //获取可写数据库
        SQLiteDatabase db = daoHelper.getWritableDatabase();

        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);


        //获取Dao对象管理者
        daoSession = daoMaster.newSession();



//        //使用自定义的DevOpenHelper获取数据库(用于升级数据，兼容不同数据结构的数据库版本)
//        MyDevOpenHelper myDevOpenHelper = new MyDevOpenHelper(context, "admin_user.db");
//        //获取可写数据库
//        SQLiteDatabase db = myDevOpenHelper.getWritableDatabase();
//
//        //获取数据库对象
//        DaoMaster daoMaster = new DaoMaster(db);
//
//        //加密获取daoMaster
//        DaoMaster daoMaster = new DaoMaster(daoHelper.getEncryptedWritableDb("whh123"));
//
//        //获取Dao对象管理者
//        daoSession = daoMaster.newSession();

    }

    /**
     * 获取daoSession，用于管理所有的Dao对象，实现增删改查
     * @return
     */
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 清空所有数据表的缓存数据
     */
    public static void clearDaoSession() {
        daoSession.clear();
    }

    /**
     * 清除User数据表的缓存数据
     */
    public static void clearUserSession() {
        UserDao userDao = daoSession.getUserDao();
        userDao.detachAll();

    }
}
