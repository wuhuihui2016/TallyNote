package com.fengyang.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fengyang.tallynote.MyApplication;
import com.fengyang.tallynote.model.NotePad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyangtech on 2017/8/4.
 */

public class NotePadDao {

    /**
     * 插入一条记事本
     *
     * @param notePad
     */
    public static synchronized boolean newNotePad(NotePad notePad) {
        boolean isExit;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
    public static synchronized void delNotePad(NotePad notePad) {
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
        db.execSQL("delete from note_pad where words = ? and time = ?", new String[]{notePad.getWords(), notePad.getTime()});
        db.close();
    }

    /**
     * 查看所有的记事本
     *
     * @return
     */
    public static synchronized List<NotePad> getNotePads() {
        List<NotePad> notePads = new ArrayList<NotePad>();
        SQLiteDatabase db = MyApplication.dbHelper.getReadableDatabase();
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
    public static synchronized boolean newNotePads(List<NotePad> notePads) {
        boolean isExit = true;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
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
