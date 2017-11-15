package com.fengyang.tallynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fengyang.tallynote.utils.LogUtils;

/**
 * @author fengyangtech
 *         数据库工具类
 *         2017年6月22日
 */

public class DBHelper extends SQLiteOpenHelper {

    private static String TAG = "DBHelper";
    public static SQLiteDatabase db = null;

    public DBHelper(Context context) {
        super(context, "tally_note.db", null, 1);
        db = getWritableDatabase();
        LogUtils.i(TAG, "保存账本数据-->tally_note.db");
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {

        //日消费记录：消费类型useType,金额money,消费明细remark,时间time
        db.execSQL("create table if not exists day_note(_id integer primary key," +
                "useType integer,money varchar(20),remark varchar(100),time varchar(20))");

        //日消费历史日记录：消费类型useType,金额money,消费明细remark,时间time,duration消费时段
        db.execSQL("create table if not exists day_note_history(_id integer primary key," +
                "useType integer,money varchar(20),remark varchar(100),time varchar(20),duration varchar(20))");

        //月消费记录：上次结余last_balance,支出金额money,工资salary,收益income,结余balance,实际结余actual_balance,时段duration,时间time,说明remark
        db.execSQL("create table if not exists month_note(_id integer primary key," +
                "last_balance varchar(20),pay varchar(20),salary varchar(20),income varchar(20),balance varchar(20),actual_balance varchar(20)," +
                "duration varchar(20),remark varchar(100),time varchar(20))");

        //理财记录：money投入金额（单位万）,incomeRatio预期年化（%）,days投资期限(天),durtion投资时段,dayIncome拟日收益（万/天）,finalIncome最终收益
        //        finalCash最终提现,finalCashGo提现去处,finished完成状态,time记录时间
        db.execSQL("create table if not exists income_note(_id integer primary key," +
                "money varchar(20),incomeRatio varchar(20),days varchar(20),durtion varchar(20),dayIncome varchar(20),finalIncome varchar(20)," +
                "finalCash varchar(20),finalCashGo varchar(20),finished integer,remark varchar(100),time varchar(20))");

        //备忘录：content内容,status状态,time记录时间
        db.execSQL("create table if not exists memo_note(_id integer primary key," +
                "content varchar(200),status integer,time varchar(20))");

        //记事本：tag标签,words内容,time记录时间
        db.execSQL("create table if not exists note_pad(_id integer primary key," +
                "tag integer,words varchar(200),time varchar(20))");


    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists day_note");
            db.execSQL("drop table if exists day_note_history");
            db.execSQL("drop table if exists month_note");
            db.execSQL("drop table if exists income_note");
            db.execSQL("drop table if exists memo_note");
            db.execSQL("drop table if exists note_pad");
            onCreate(db);
        }
    }

    /**
     * 新建数据表
     *
     * @param sql
     */
    public synchronized void newTable(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

}
