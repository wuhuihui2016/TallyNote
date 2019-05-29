package com.whh.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whh.tallynote.MyApplication;
import com.whh.tallynote.model.DayNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyangtech on 2017/8/4.
 */

public class DayNoteDao {

    /**
     * 插入一条新日帐单
     *
     * @param dayNote
     */
    public static synchronized boolean newDNote(DayNote dayNote) {
        boolean isExit;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
    public static synchronized void delDNote(DayNote dayNote) {
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
        db.execSQL("delete from day_note where money = ? or time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
        db.close();
    }

    /**
     * 查看所有的日记录账单
     *
     * @return
     */
    public static synchronized List<DayNote> getDayNotes() {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = MyApplication.dbHelper.getReadableDatabase();
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
     * 批量插入新日帐单
     *
     * @param dayNotes
     */
    public static synchronized boolean newDNotes(List<DayNote> dayNotes) {
        boolean isExit = true;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
     */
    public static synchronized boolean newDNotes4History(List<DayNote> dayNotes) {
        boolean isExit = true;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
    public static synchronized boolean newDNotes4History(String duration) {
        boolean isExit = true;
        List<DayNote> dayNotes = getDayNotes();
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
    public static synchronized List<DayNote> getDayNotes4History() {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = MyApplication.dbHelper.getReadableDatabase();
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
     *
     * @param duration
     * @return
     */
    public static synchronized List<DayNote> getDayNotes4History(String duration) {
        List<DayNote> dayNotes = new ArrayList<DayNote>();
        SQLiteDatabase db = MyApplication.dbHelper.getReadableDatabase();
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

}
