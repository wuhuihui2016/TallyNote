package com.whh.tallynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 数据库操作类
 * Created by wuhuihui on 2020/6/10.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tally_note.db";
    private volatile static DBHelper sInstance;

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DBHelper.class) {
                if (sInstance == null) {
                    sInstance = new DBHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DBCommon.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBCommon.DayNoteRecordColumns.SQL_CREATE_DAYNOTE_TABLE);
        db.execSQL(DBCommon.DayNoteRecordColumns.SQL_CREATE_DAYNOTE_HISTORY_TABLE);
        db.execSQL(DBCommon.MonthNoteRecordColumns.SQL_CREATE_MONTHNOTE_TABLE);
        db.execSQL(DBCommon.IncomeNoteRecordColumns.SQL_CREATE_INCOMENOTE_TABLE);
        db.execSQL(DBCommon.MemoNoteRecordColumns.SQL_CREATE_MEMO_TABLE);
        db.execSQL(DBCommon.NotePadRecordColumns.SQL_CREATE_NOTEPAD_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO 测试数据库升级，新增数据表中的列
//        if (oldVersion == 3) {//如果版本是3.0的，升级下面的内容或修改
//            LogUtils.i("whh1028", "onUpgrade=>你在没有卸载的情况下，在线更新了版本3.0");
//            String sql_upgrade = "alter table identify_record add zyycjdm text";
//            db.execSQL(sql_upgrade);
//            sql_upgrade = "alter table identify_record add zyqsqk text";
//            db.execSQL(sql_upgrade);
//            sql_upgrade = "alter table identify_record add yyqsqk text";
//            db.execSQL(sql_upgrade);
//
//        } else if (oldVersion == 4) {
//            LogUtils.i("whh1028", "onUpgrade=>你在没有卸载的情况下，在线更新了版本4.0");
//            String sql_upgrade = "alter table identify_record add sbbzpf text";
//            db.execSQL(sql_upgrade);
//        }
    }
}
