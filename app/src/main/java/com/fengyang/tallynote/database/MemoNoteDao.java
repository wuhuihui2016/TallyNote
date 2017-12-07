package com.fengyang.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fengyang.tallynote.model.MemoNote;

import java.util.ArrayList;
import java.util.List;

import static com.fengyang.tallynote.MyApplication.dbHelper;

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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into memo_note(content,status,time) values(?,?,?)",
                new Object[]{memoNote.getContent(), MemoNote.ON, memoNote.getTime()});
        Cursor cursor = db.rawQuery("select * from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 修改一条备忘录
     *
     * @param memoNote
     */
    public static synchronized boolean alterMemoNote(MemoNote memoNote) {
        boolean isFinished;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update memo_note set content = ? where time = ?",
                new String[]{memoNote.getContent(), memoNote.getTime()});
        Cursor cursor = db.rawQuery("select * from memo_note where time = ?", new String[]{memoNote.getTime()});
        isFinished = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isFinished;

    }

    /**
     * 删除一条备忘录
     *
     * @param memoNote
     */
    public static synchronized void delMemoNote(MemoNote memoNote) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        db.close();
    }

    /**
     * 完成一条备忘录
     *
     * @param memoNote
     */
    public static synchronized boolean finishMemoNote(MemoNote memoNote) {
        boolean isFinished;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update memo_note set status = 1 where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        Cursor cursor = db.rawQuery("select * from memo_note where content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
        isFinished = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isFinished;
    }

    /**
     * 查看所有的备忘录
     *
     * @return
     */
    public static synchronized List<MemoNote> getMemoNotes() {
        List<MemoNote> memoNotes = new ArrayList<MemoNote>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from memo_note", null);
        while (cursor.moveToNext()) {
            MemoNote memoNote = new MemoNote(cursor.getString(cursor.getColumnIndex("content")),
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
