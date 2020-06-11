package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 用于操作日账表/历史日账表
 * Created by wuhuihui on 2020/6/10.
 */
public class DayNoteDBHandle extends DBHandle {
    private static DayNoteDBHandle instance;

    public static DayNoteDBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new DayNoteDBHandle(mContext.getApplicationContext());
        return instance;
    }

    private DayNoteDBHandle(Context context) {
        super(context);
    }

    /**
     * 插入一条新日帐单
     *
     * @param dayNote
     * @return
     */
    public int saveDayNote(DayNote dayNote) {
        if (dayNote == null) {
            return -1;
        }

        ContentValues value = new ContentValues();
        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE, dayNote.getUseType());
        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY, dayNote.getMoney());
        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK, dayNote.getRemark());
        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME, dayNote.getTime());
        return insert(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, value);
    }

    /**
     * 批量插入新日帐单
     *
     * @param dayNotes
     */
    public boolean saveDayNoteList(List<DayNote> dayNotes) {
        ContentValues[] cv = new ContentValues[dayNotes.size()];
        for (int i = 0; i < dayNotes.size(); i++) {
            ContentValues value = new ContentValues();
            DayNote dayNote = dayNotes.get(i);
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE, dayNote.getUseType());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY, dayNote.getMoney());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK, dayNote.getRemark());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME, dayNote.getTime());
            cv[i] = value;
        }
        return multiInsert(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, cv);
    }

    /**
     * 修改一条日账
     *
     * @param dayNote
     */
    public void updateDayNote(DayNote dayNote) {
        ContentValues value = new ContentValues();

        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE, dayNote.getUseType());
        update(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, value,
                DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME + " = ? ", new String[]{dayNote.getTime()});

        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY, dayNote.getMoney());
        update(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, value,
                DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME + " = ? ", new String[]{dayNote.getTime()});

        value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK, dayNote.getRemark());
        update(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, value,
                DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME + " = ? ", new String[]{dayNote.getTime()});
    }

    /**
     * 删除某条日账
     *
     * @param dayNote
     */
    public void delDayNote(DayNote dayNote) {
        delete(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE,
                DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME + " = ? ",
                new String[]{dayNote.getTime()});
    }

    /**
     * 查看所有的日记录账单
     *
     * @return
     */
    public List<DayNote> getDayNotes() {
        List<DayNote> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, DBCommon.DayNoteRecordColumns.projects,
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME)));
                list.add(dayNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 批量插入历史日账单（在导入账单时批量插入历史日账单）
     *
     * @param dayNotes
     */
    public boolean saveDayNoteList4History(List<DayNote> dayNotes) {
        ContentValues[] cv = new ContentValues[dayNotes.size()];
        for (int i = 0; i < dayNotes.size(); i++) {
            ContentValues value = new ContentValues();
            DayNote dayNote = dayNotes.get(i);
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE, dayNote.getUseType());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY, dayNote.getMoney());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK, dayNote.getRemark());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME, dayNote.getTime());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_DURATION, dayNote.getDuration());
            cv[i] = value;
        }
        return multiInsert(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY, cv);
    }

    /**
     * 将所有临时日账单移植到历史日账单（在新建月账单时使用）
     *
     * @param duration
     */
    public boolean saveDayNotes4History(String duration) {
        List<DayNote> dayNotes = getDayNotes();
        if (dayNotes.size() == 0) return false;
        ContentValues[] cv = new ContentValues[dayNotes.size()];
        for (int i = 0; i < dayNotes.size(); i++) {
            ContentValues value = new ContentValues();
            DayNote dayNote = dayNotes.get(i);
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE, dayNote.getUseType());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY, dayNote.getMoney());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK, dayNote.getRemark());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME, dayNote.getTime());
            value.put(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_DURATION, duration);
            cv[i] = value;
        }
        return multiInsert(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY, cv);
    }

    /**
     * 查看所有的历史日账记录
     *
     * @return
     */
    public List<DayNote> getDayNotes4History() {
        List<DayNote> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY, DBCommon.DayNoteRecordColumns.projects_history,
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_DURATION)));
                list.add(dayNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 依据选择的时间段查看历史日记录账单
     *
     * @param duration
     * @return
     */
    public List<DayNote> getDayNotes4History(String duration) {
        List<DayNote> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY, DBCommon.DayNoteRecordColumns.projects_history,
                    "duration = ?", new String[]{duration}, null, null, null, null);
            while (cursor.moveToNext()) {
                DayNote dayNote = new DayNote(cursor.getInt(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_USETYPE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_MONEY)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_REMARK)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_TIME)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.DayNoteRecordColumns.COLUMN_DAYNOTE_DURATION)));
                list.add(dayNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

}
