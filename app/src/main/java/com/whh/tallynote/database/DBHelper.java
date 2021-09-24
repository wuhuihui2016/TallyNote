package com.whh.tallynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whh.tallynote.utils.LogUtils;


/**
 * 数据库操作类
 * Created by wuhuihui on 2020/6/10.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
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
//        db.execSQL(DBCommon.NotePadRecordColumns.SQL_CREATE_NOTEPAD_TABLE);
        db.execSQL(DBCommon.NotePadRecordColumns.SQL_CREATE_NOTEPAD_TABLE2);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.e(TAG, "oldVersion : " + oldVersion + ", newVersion : " + newVersion);
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            switch (i) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                default:
                    break;
            }
        }


    }

    /**
     * 数据库升级到版本2：
     * 记事本表增加 imgcount图片张数,img1,img2,img3 字段
     */
    private void upgradeToVersion2(SQLiteDatabase db) {
        LogUtils.e(TAG, "upgradeToVersion2....");
        db.execSQL("ALTER TABLE note_pad ADD COLUMN imgcount integer");
        db.execSQL("ALTER TABLE note_pad ADD COLUMN img1 BLOB");
        db.execSQL("ALTER TABLE note_pad ADD COLUMN img2 BLOB");
        db.execSQL("ALTER TABLE note_pad ADD COLUMN img3 BLOB");
    }
}
