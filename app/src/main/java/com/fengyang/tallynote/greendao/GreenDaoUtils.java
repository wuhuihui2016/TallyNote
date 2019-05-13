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
        //创建数据库user.db
        DaoMaster.DevOpenHelper daoHelper = new DaoMaster.DevOpenHelper(context, "user.db", null);

        //获取可写数据库
        SQLiteDatabase db = daoHelper.getWritableDatabase();

        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);

        //获取Dao对象管理者
        daoSession = daoMaster.newSession();

    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
