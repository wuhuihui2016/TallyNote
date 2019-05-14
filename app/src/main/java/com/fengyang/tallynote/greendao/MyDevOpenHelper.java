package com.fengyang.tallynote.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fengyang.tallynote.utils.LogUtils;

/**
 * Created by whh on 2019/5/14.
 */

public class MyDevOpenHelper extends DaoMaster.DevOpenHelper{

    public MyDevOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        LogUtils.i("version", "oldVersion=" + oldVersion + ",newVersion=" + newVersion);
        if (oldVersion < newVersion) {
            LogUtils.i("version", "current version  is oldVersion, need to update!");
            //方法一
            MigrationHelper.migrate(sqLiteDatabase, AdminDao.class, UserDao.class);

            //或者方法二
//            MigrationHelper.migrate(new StandardDatabase(sqLiteDatabase), AdminDao.class, UserDao.class);
        }
    }
}

