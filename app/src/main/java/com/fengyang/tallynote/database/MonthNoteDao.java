package com.fengyang.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.model.MonthNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyangtech on 2017/8/4.
 */

public class MonthNoteDao {

    /**
     * 插入一条新月帐单
     *
     * @param monthNote
     */
    public static synchronized boolean newMNote(MonthNote monthNote) {
        boolean isExit;
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("insert into month_note(last_balance,pay,salary,income,balance,actual_balance,duration,remark,time) values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{monthNote.getLast_balance(), monthNote.getPay(), monthNote.getSalary(), monthNote.getIncome(),
                        monthNote.getBalance(), monthNote.getActual_balance(),
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
    public static synchronized List<MonthNote> getMonthNotes() {
        List<MonthNote> monthNotes = new ArrayList<MonthNote>();
        SQLiteDatabase db = MyApp.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from month_note", null);
        while (cursor.moveToNext()) {
            //String last_balance, String pay, String salary, String income, String balance,
            //String actual_balance, String duration, String remark, String time
            MonthNote monthNote = new MonthNote(cursor.getString(cursor.getColumnIndex("last_balance")),
                    cursor.getString(cursor.getColumnIndex("pay")),
                    cursor.getString(cursor.getColumnIndex("salary")),
                    cursor.getString(cursor.getColumnIndex("income")),
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
    public static synchronized void delMNote(MonthNote monthNote) {
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("delete from month_note where actual_balance = ? or duration = ?",
                new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
        db.close();
    }

    /**
     * 批量插入新月帐单
     *
     * @param monthNotes
     */
    public static synchronized boolean newMNotes(List<MonthNote> monthNotes) {
        boolean isExit = true;
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("delete from month_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < monthNotes.size(); i++) {
            if (isExit) {
                MonthNote monthNote = monthNotes.get(i);
                db.execSQL("insert into month_note(last_balance,pay,salary,income,balance,actual_balance,duration,remark,time) values(?,?,?,?,?,?,?,?,?)",
                        new Object[]{monthNote.getLast_balance(), monthNote.getPay(), monthNote.getSalary(), monthNote.getIncome(),
                                monthNote.getBalance(), monthNote.getActual_balance(),
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

}
