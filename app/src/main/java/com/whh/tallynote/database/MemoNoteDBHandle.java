package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.whh.tallynote.model.MemoNote;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于操作备忘录表
 * Created by wuhuihui on 2020/6/10.
 */
public class MemoNoteDBHandle extends DBHandle {
    private static MemoNoteDBHandle instance;

    public static MemoNoteDBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new MemoNoteDBHandle(mContext.getApplicationContext());
        return instance;
    }

    private MemoNoteDBHandle(Context context) {
        super(context);
    }

    /**
     * 插入一条新日帐单
     *
     * @param incomeNote
     * @return
     */
    public int saveMemoNote(MemoNote incomeNote) {
        if (incomeNote == null) {
            return -1;
        }

        ContentValues value = new ContentValues();
        value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_CONTENT, incomeNote.getContent());
        value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_STATUS, incomeNote.getStatus());
        value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_TIME, incomeNote.getTime());
        return insert(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, value);
    }

    /**
     * 修改一条备忘录
     *
     * @param memoNote
     */
    public void updateMemoNote(MemoNote memoNote) {
        ContentValues value = new ContentValues();
        value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_CONTENT, memoNote.getContent());
        update(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, value, "time = ?", new String[]{memoNote.getTime()});
    }

    /**
     * 删除一条备忘录
     *
     * @param memoNote
     */
    public void delMemoNote(MemoNote memoNote) {
        ContentValues value = new ContentValues();
        delete(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, "content = ? and time = ?",
                new String[]{memoNote.getContent(), memoNote.getTime()});
    }

    /**
     * 完成一条备忘录
     *
     * @param memoNote
     */
    public void finishMemoNote(MemoNote memoNote) {
        ContentValues value = new ContentValues();
        value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_STATUS, 1);
        update(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, value,
                "content = ? and time = ?", new String[]{memoNote.getContent(), memoNote.getTime()});
    }

    /**
     * 查看所有的月账单
     *
     * @return
     */
    public List<MemoNote> getMemoNotes() {
        List<MemoNote> memoNotes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, DBCommon.MemoNoteRecordColumns.projects,
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                MemoNote memoNote = new MemoNote(cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_CONTENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_TIME)));
                memoNotes.add(memoNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return memoNotes;
    }

    /**
     * 批量插入理财记录（在导入账单时批量插入理财记录）
     *
     * @param incomeNotes
     */
    public boolean saveMemoNoteList(List<MemoNote> incomeNotes) {
        ContentValues[] cv = new ContentValues[incomeNotes.size()];
        for (int i = 0; i < incomeNotes.size(); i++) {
            ContentValues value = new ContentValues();
            MemoNote incomeNote = incomeNotes.get(i);
            value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_CONTENT, incomeNote.getContent());
            value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_STATUS, incomeNote.getStatus());
            value.put(DBCommon.MemoNoteRecordColumns.COLUMN_MEMONOTE_TIME, incomeNote.getTime());
            cv[i] = value;
        }
        return multiInsert(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, cv);
    }

}
