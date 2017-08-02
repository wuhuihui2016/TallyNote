package com.fengyang.tallynote.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.model.NotePad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengyangtech
 *         数据库工具类
 *         2017年6月22日
 */

public class DBUtils extends SQLiteOpenHelper {

    private static String TAG = "DBUtils";
    public static SQLiteDatabase db = null;

    public DBUtils(Context context) {
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

        //月消费记录：上次结余last_balance,支出金额money,工资salary,收益income,家用补贴homeuse,结余balance,实际结余actual_balance,时段duration,时间time,说明remark
        db.execSQL("create table if not exists month_note(_id integer primary key," +
                "last_balance varchar(20),pay varchar(20),salary varchar(20),income varchar(20),homeuse varchar(20),balance varchar(20),actual_balance varchar(20)," +
                "duration varchar(20),remark varchar(100),time varchar(20))");

        //理财记录：money投入金额（单位万）,incomeRatio预期年化（%）,days投资期限(天),durtion投资时段,dayIncome拟日收益（万/天）,finalIncome最终收益
        //        finalCash最终提现,finalCashGo提现去处,finished完成状态,time记录时间
        db.execSQL("create table if not exists income_note(_id integer primary key," +
                "money varchar(20),incomeRatio varchar(20),days varchar(20),durtion varchar(20),dayIncome varchar(20),finalIncome varchar(20)," +
                "finalCash varchar(20),finalCashGo varchar(20),finished integer,remark varchar(100),time varchar(20))");

        //记事本：tag标签,words内容,time记录时间
        db.execSQL("create table if not exists note_pad(_id integer primary key," +
                "tag integer,words varchar(120),time varchar(20))");

    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists day_note");
            db.execSQL("drop table if exists day_note_history");
            db.execSQL("drop table if exists month_note");
            db.execSQL("drop table if exists income_note");
            db.execSQL("drop table if exists note_pad");
            onCreate(db);
        }
    }

    /**
     * 新建数据表
     * @param sql
     */
    public synchronized void newTable(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    /**
     * 插入一条新日帐单
     *
     * @param dayNote
     */
    public synchronized boolean newDNote(DayNote dayNote) {
        boolean isExit;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into day_note(useType,money,remark,time) values(?,?,?,?)",
                new Object[]{dayNote.getUseType(), dayNote.getMoney(), dayNote.getRemark(), dayNote.getTime()});
        Cursor cursor = db.rawQuery("select * from day_note where money = ? and time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 删除某个日记录账单（仅用于最后一次账单记录，删除后可重新记录）
     *
     * @param dayNote
     */
    public synchronized void delDNote(DayNote dayNote) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from day_note where money = ? or time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
        db.close();
    }

    /**
     * 查看所有的日记录账单
     *
     * @return
     */
    public synchronized List<DayNote> getDayNotes() {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from day_note", null);
        while (cursor.moveToNext()) {
            //int useType, String money, String remark, String time
            DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex("useType")),
                    cursor.getString(cursor.getColumnIndex("money")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("time")));
            dayNotes.add(dayNote);
        }
        cursor.close();
        db.close();
        return dayNotes;
    }

    /**
     * 插入一条新月帐单
     *
     * @param monthNote
     */
    public synchronized boolean newMNote(MonthNote monthNote) {
        boolean isExit;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into month_note(last_balance,pay,salary,income,homeuse,balance,actual_balance,duration,remark,time) values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{monthNote.getLast_balance(), monthNote.getPay(), monthNote.getSalary(), monthNote.getIncome(),
                        monthNote.getHomeuse(), monthNote.getBalance(), monthNote.getActual_balance(),
                        monthNote.getDuration(), monthNote.getRemark(), monthNote.getTime()});
        Cursor cursor = db.rawQuery("select * from month_note where actual_balance = ? and duration = ?",
                new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 查看所有的月记录账单
     *
     * @return
     */
    public synchronized List<MonthNote> getMonNotes() {
        List<MonthNote> monthNotes = new ArrayList<MonthNote>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from month_note", null);
        while (cursor.moveToNext()) {
            //String last_balance, String pay, String salary, String income, String homeuse,String balance,
            //String actual_balance, String duration, String remark, String time
            MonthNote monthNote = new MonthNote(cursor.getString(cursor.getColumnIndex("last_balance")),
                    cursor.getString(cursor.getColumnIndex("pay")),
                    cursor.getString(cursor.getColumnIndex("salary")),
                    cursor.getString(cursor.getColumnIndex("income")),
                    cursor.getString(cursor.getColumnIndex("homeuse")),
                    cursor.getString(cursor.getColumnIndex("balance")),
                    cursor.getString(cursor.getColumnIndex("actual_balance")),
                    cursor.getString(cursor.getColumnIndex("duration")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("time")));
            monthNotes.add(monthNote);
        }
        cursor.close();
        db.close();
        return monthNotes;
    }

    /**
     * 删除某个月记录账单（仅用于最后一次账单记录，删除后可重新记录）
     *
     * @param monthNote
     */
    public synchronized void delMNote(MonthNote monthNote) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from month_note where actual_balance = ? or duration = ?",
                new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
        db.close();
    }

    /**
     * 插入一条理财记录(默认未完成状态，修改状态需在列表中操作)
     *
     * @param incomeNote
     */
    //理财记录：money 投入金额（单位万）,incomeRatio 预期年化（%）,days 投资期限(天),durtion 投资时段,dayIncome 拟日收益（万/天）,finalIncome 最终收益, time 记录时间
    public synchronized boolean newINote(IncomeNote incomeNote) {
        boolean isExit;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into income_note(money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time) values(?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{incomeNote.getMoney(), incomeNote.getIncomeRatio(), incomeNote.getDays(), incomeNote.getDurtion(),
                        incomeNote.getDayIncome(), incomeNote.getFinalIncome(), incomeNote.getFinalCash(), incomeNote.getFinalCashGo(), 0, incomeNote.getRemark(), incomeNote.getTime()});
        Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 完成某个理财记录
     *
     * @param incomeNote
     * @return
     */
    public synchronized boolean finishIncome(IncomeNote incomeNote) {
        boolean isFinished;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update income_note set finished = 1,finalCash = ?,finalCashGo = ? where money = ? and finalIncome = ? ",
                new String[]{incomeNote.getFinalCash(), incomeNote.getFinalCashGo(), incomeNote.getMoney(), incomeNote.getFinalIncome()});
        Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ? and finalCash= ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome(), incomeNote.getFinalCash()});
        isFinished = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isFinished;
    }

    /**
     * 删除某个理财记录（仅用于最后一次账单记录，删除后可重新记录）
     *
     * @param incomeNote
     */
    public synchronized void delIncome(IncomeNote incomeNote) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from income_note where money = ? and finalIncome = ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome()});
        db.close();
    }

    /**
     * 查看所有的理财
     *
     * @return
     */
    public synchronized List<IncomeNote> getIncomes() {
        List<IncomeNote> incomeNotes = new ArrayList<IncomeNote>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from income_note", null);
        while (cursor.moveToNext()) {
            /*String money; //投入金额（单位万）
            String incomeRatio; //预期年化（%）
			String days; //投资期限(天)
			String durtion; //投资时段
			String dayIncome; //拟日收益（万/天）
			String finalIncome; //最终收益
			String finalCash; //最终提现
			String finalCashGo; //提现去处
			int finished; //完成状态
			String remark;//投资说明
			String time; //记录时间*/

            IncomeNote income = new IncomeNote(cursor.getString(cursor.getColumnIndex("money")),
                    cursor.getString(cursor.getColumnIndex("incomeRatio")),
                    cursor.getString(cursor.getColumnIndex("days")),
                    cursor.getString(cursor.getColumnIndex("durtion")),
                    cursor.getString(cursor.getColumnIndex("dayIncome")),
                    cursor.getString(cursor.getColumnIndex("finalIncome")),
                    cursor.getString(cursor.getColumnIndex("finalCash")),
                    cursor.getString(cursor.getColumnIndex("finalCashGo")),
                    cursor.getInt(cursor.getColumnIndex("finished")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            );

            income.setId(cursor.getInt(cursor.getColumnIndex("_id")) + "");
            incomeNotes.add(income);
        }
        cursor.close();
        db.close();
        return incomeNotes;
    }

    /**
     * 批量插入新日帐单
     *
     * @param dayNotes
     */
    public synchronized boolean newDNotes(List<DayNote> dayNotes) {
        boolean isExit = true;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from day_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < dayNotes.size(); i++) {
            if (isExit) {
                DayNote dayNote = dayNotes.get(i);
                db.execSQL("insert into day_note(useType,money,remark,time) values(?,?,?,?)",
                        new Object[]{dayNote.getUseType(), dayNote.getMoney(), dayNote.getRemark(), dayNote.getTime()});
                Cursor cursor = db.rawQuery("select * from day_note where money = ? and time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

    /**
     * 批量插入历史日账单（在导入账单时批量插入历史日账单）
     *
     */
    public synchronized boolean newDNotes4History(List<DayNote> dayNotes) {
        boolean isExit = true;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from day_note_history"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < dayNotes.size(); i++) {
            if (isExit) {
                DayNote dayNote = dayNotes.get(i);
                db.execSQL("insert into day_note_history(useType,money,remark,time,duration) values(?,?,?,?,?)",
                        new Object[]{dayNote.getUseType(), dayNote.getMoney(), dayNote.getRemark(), dayNote.getTime(), dayNote.getDuration()});
                Cursor cursor = db.rawQuery("select * from day_note_history where money = ? and time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

    /**
     * 将所有临时日账单移植到历史日账单（在新建月账单时使用）
     *
     * @param duration
     */
    public synchronized boolean newDNotes4History(String duration) {
        boolean isExit = true;
        List<DayNote> dayNotes = getDayNotes();
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < dayNotes.size(); i++) {
            if (isExit) {
                DayNote dayNote = dayNotes.get(i);
                db.execSQL("insert into day_note_history(useType,money,remark,time,duration) values(?,?,?,?,?)",
                        new Object[]{dayNote.getUseType(), dayNote.getMoney(), dayNote.getRemark(), dayNote.getTime(), duration});
                Cursor cursor = db.rawQuery("select * from day_note_history where money = ? and time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        if (isExit) { //移植完成后，清除临时日账单数据
            db.execSQL("delete from day_note");
        }
//        db.close();
        return isExit;
    }

    /**
     * 查看所有的历史日记录账单
     *
     * @return
     */
    public synchronized List<DayNote> getDayNotes4History() {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from day_note_history", null);
        while (cursor.moveToNext()) {
            //int useType, String money, String remark, String time
            DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex("useType")),
                    cursor.getString(cursor.getColumnIndex("money")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getString(cursor.getColumnIndex("duration")));
            dayNotes.add(dayNote);
        }
        cursor.close();
        db.close();
        return dayNotes;
    }

    /**
     * 依据选择的时间段查看历史日记录账单
     * @param duration
     * @return
     */
    public synchronized List<DayNote> getDayNotes4History(String duration) {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from day_note_history", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("duration")).equals(duration)) {
                //int useType, String money, String remark, String time, String duration
                DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex("useType")),
                        cursor.getString(cursor.getColumnIndex("money")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("duration")));
                dayNotes.add(dayNote);
            }
        }
        cursor.close();
        db.close();
        return dayNotes;
    }

    /**
     * 批量插入新月帐单
     *
     * @param monthNotes
     */
    public synchronized boolean newMNotes(List<MonthNote> monthNotes) {
        boolean isExit = true;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from month_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < monthNotes.size(); i++) {
            if (isExit) {
                MonthNote monthNote = monthNotes.get(i);
                db.execSQL("insert into month_note(last_balance,pay,salary,income,homeuse,balance,actual_balance,duration,remark,time) values(?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{monthNote.getLast_balance(), monthNote.getPay(), monthNote.getSalary(), monthNote.getIncome(),
                                monthNote.getHomeuse(), monthNote.getBalance(), monthNote.getActual_balance(),
                                monthNote.getDuration(), monthNote.getRemark(), monthNote.getTime()});
                Cursor cursor = db.rawQuery("select * from month_note where actual_balance = ? and duration = ?",
                        new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

    /**
     * 批量插入理财记录
     *
     * @param incomeNotes
     */
    public synchronized boolean newINotes(List<IncomeNote> incomeNotes) {
        boolean isExit = true;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from income_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < incomeNotes.size(); i++) {
            if (isExit) {
                IncomeNote incomeNote = incomeNotes.get(i);
                db.execSQL("insert into income_note(money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time) values(?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{incomeNote.getMoney(), incomeNote.getIncomeRatio(), incomeNote.getDays(), incomeNote.getDurtion(),
                                incomeNote.getDayIncome(), incomeNote.getFinalIncome(), incomeNote.getFinalCash(),
                                incomeNote.getFinalCashGo(), incomeNote.getFinished(), incomeNote.getRemark(), incomeNote.getTime()});
                Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }


    /**
     * 插入一条记事本
     *
     * @param notePad
     */
    public synchronized boolean newNotePad(NotePad notePad) {
        boolean isExit;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into note_pad(tag,words,time) values(?,?,?)",
                new Object[]{notePad.getTag(), notePad.getWords(), notePad.getTime()});
        Cursor cursor = db.rawQuery("select * from note_pad where words = ? and time = ?", new String[]{notePad.getWords(), notePad.getTime()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 删除记事本
     *
     * @param notePad
     */
    public synchronized void delNotePad(NotePad notePad) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from note_pad where words = ? and time = ?", new String[]{notePad.getWords(), notePad.getTime()});
        db.close();
    }

    /**
     * 查看所有的记事本
     *
     * @return
     */
    public synchronized List<NotePad> getNotePads() {
        List<NotePad> notePads = new ArrayList<NotePad>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from note_pad", null);
        while (cursor.moveToNext()) {
            //int note_tag, String words, String time
            NotePad notePad = new NotePad(cursor.getInt(cursor.getColumnIndex("tag")),
                    cursor.getString(cursor.getColumnIndex("words")),
                    cursor.getString(cursor.getColumnIndex("time")));
            notePads.add(notePad);
        }
        cursor.close();
        db.close();
        return notePads;
    }


    /**
     * 批量插入记事本
     *
     * @param notePads
     */
    public synchronized boolean newNotePads(List<NotePad> notePads) {
        boolean isExit = true;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from note_pad"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < notePads.size(); i++) {
            if (isExit) {
                NotePad notePad = notePads.get(i);
                db.execSQL("insert into note_pad(tag,words,time) values(?,?,?)",
                        new Object[]{notePad.getTag(), notePad.getWords(), notePad.getTime()});
                Cursor cursor = db.rawQuery("select * from note_pad where words = ? and time = ?", new String[]{notePad.getWords(), notePad.getTime()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

}
