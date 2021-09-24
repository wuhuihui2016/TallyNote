package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.whh.tallynote.model.NotePad;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于操作记事本表
 * Created by wuhuihui on 2020/6/10.
 */
public class NotePadDBHandle extends DBHandle {
    private static NotePadDBHandle instance;

    public static NotePadDBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new NotePadDBHandle(mContext.getApplicationContext());
        return instance;
    }

    private NotePadDBHandle(Context context) {
        super(context);
    }

    /**
     * 插入一条记事本
     *
     * @param notePad
     * @return
     */
    public int saveNotePad(NotePad notePad) {
        if (notePad == null) {
            return -1;
        }

        ContentValues value = new ContentValues();
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TAG, notePad.getTag());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMGCOUNT, notePad.getImgCount());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG1, notePad.getImg1());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG2, notePad.getImg2());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG3, notePad.getImg3());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_WORDS, notePad.getWords());
        value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TIME, notePad.getTime());
        return insert(DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD, value);
    }

    /**
     * 删除一条记事本
     *
     * @param notePad
     */
    public void delNotePad(NotePad notePad) {
        delete(DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD, "words = ? and time = ?",
                new String[]{notePad.getWords(), notePad.getTime()});
    }


    /**
     * 查看所有的月账单
     *
     * @return
     */
    public List<NotePad> getNotePads() {
        List<NotePad> notePads = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD, DBCommon.NotePadRecordColumns.projects,
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                NotePad notePad = new NotePad(cursor.getInt(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TAG)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMGCOUNT)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG1)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG2)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_WORDS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TIME)));
                notePads.add(notePad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return notePads;
    }

    /**
     * 批量插入理财记录（在导入账单时批量插入理财记录）
     *
     * @param incomeNotes
     */
    public boolean saveNotePadList(List<NotePad> incomeNotes) {
        ContentValues[] cv = new ContentValues[incomeNotes.size()];
        for (int i = 0; i < incomeNotes.size(); i++) {
            ContentValues value = new ContentValues();
            NotePad incomeNote = incomeNotes.get(i);
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TAG, incomeNote.getTag());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMGCOUNT, incomeNote.getImgCount());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG1, incomeNote.getImg1());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG2, incomeNote.getImg2());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_IMG3, incomeNote.getImg3());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_WORDS, incomeNote.getWords());
            value.put(DBCommon.NotePadRecordColumns.COLUMN_NOTEPAD_TIME, incomeNote.getTime());
            cv[i] = value;
        }
        return multiInsert(DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD, cv);
    }

}
