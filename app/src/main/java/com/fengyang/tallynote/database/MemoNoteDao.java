package com.fengyang.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.model.MemoNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyangtech on 2017/8/4.
 */

public class MemoNoteDao {

    /**
     * 插入一条备忘录
     *
     * @param memoNote
     */
    public static synchronized boolean newMemoNote(MemoNote memoNote) {
        boolean isExit;
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("insert into memo_note(content,status,time) values(?,?,?)",
                new Object[]{memoNote.getContent(), MemoNote.ON, memoNote.getTime()});
        Cursor cursor = db.rawQuery("select * from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 删除一条备忘录
     *
     * @param memoNote
     */
    public static synchronized void delMemoNote(MemoNote memoNote) {
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("delete from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        db.close();
    }

    /**
     * 查看所有的备忘录
     *
     * @return
     */
    public static synchronized List<MemoNote> getMemoNotes() {
        List<MemoNote> memoNotes = new ArrayList<MemoNote>();
        SQLiteDatabase db = MyApp.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from memo_note", null);
        while (cursor.moveToNext()) {
            MemoNote memoNote = new MemoNote(cursor.getString(cursor.getColumnIndex("words")),
                    cursor.getInt(cursor.getColumnIndex("status")),
                    cursor.getString(cursor.getColumnIndex("time")));
            memoNotes.add(memoNote);
        }
        cursor.close();
        db.close();
        return memoNotes;
    }

    /**
     * 批量插入备忘录
     *
     * @param memoNotes
     */
    public static synchronized boolean newMemoNotes(List<MemoNote> memoNotes) {
        boolean isExit = true;
        SQLiteDatabase db = MyApp.dbHelper.getWritableDatabase();
        db.execSQL("delete from memo_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < memoNotes.size(); i++) {
            if (isExit) {
                MemoNote memoNote = memoNotes.get(i);
                db.execSQL("insert into memo_note(content,status,time) values(?,?,?)",
                        new Object[]{memoNote.getContent(), memoNote.getStatus(), memoNote.getTime()});
                Cursor cursor = db.rawQuery("select * from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

}
