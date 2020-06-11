package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.utils.ContansUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于操作月账表
 * Created by wuhuihui on 2020/6/10.
 */
public class MonthNoteDBHandle extends DBHandle {
    private static MonthNoteDBHandle instance;

    public static MonthNoteDBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new MonthNoteDBHandle(mContext.getApplicationContext());
        return instance;
    }

    private MonthNoteDBHandle(Context context) {
        super(context);
    }

    /**
     * 插入一条新日帐单
     *
     * @param monthNote
     * @return
     */
    public int saveMonthNote(MonthNote monthNote) {
        if (monthNote == null) {
            return -1;
        }

        ContentValues value = new ContentValues();
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_LASTBALANCE, monthNote.getLast_balance());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_PAY, monthNote.getPay());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_SALARY, monthNote.getSalary());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_INCOME, monthNote.getIncome());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_BALANCE, monthNote.getBalance());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_ACTUALBALANCE, monthNote.getActual_balance());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_DURATION, monthNote.getDuration());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_TIME, monthNote.getTime());
        value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_REMARK, monthNote.getRemark());
        return insert(DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE, value);
    }

    /**
     * 查看所有的月账单
     *
     * @return
     */
    public List<MonthNote> getMonthNotes() {
        List<MonthNote> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE, DBCommon.MonthNoteRecordColumns.projects,
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                MonthNote monthNote = new MonthNote(cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_LASTBALANCE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_PAY)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_SALARY)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_INCOME)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_BALANCE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_ACTUALBALANCE)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_DURATION)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_REMARK)),
                        cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_TIME)));
                list.add(monthNote);
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
     * 批量插入新月帐单（在导入账单时批量插入月账单））
     *
     * @param monthNotes
     */
    public boolean saveMonthNoteList(List<MonthNote> monthNotes) {
        ContentValues[] cv = new ContentValues[monthNotes.size()];
        for (int i = 0; i < monthNotes.size(); i++) {
            ContentValues value = new ContentValues();
            MonthNote monthNote = monthNotes.get(i);
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_LASTBALANCE, monthNote.getLast_balance());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_PAY, monthNote.getPay());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_SALARY, monthNote.getSalary());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_INCOME, monthNote.getIncome());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_BALANCE, monthNote.getBalance());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_ACTUALBALANCE, monthNote.getActual_balance());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_DURATION, monthNote.getDuration());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_TIME, monthNote.getTime());
            value.put(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_REMARK, monthNote.getRemark());
            cv[i] = value;
        }
        return multiInsert(DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE, cv);
    }

    /**
     * 获取最后一条数据
     *
     * @return
     */
    public MonthNote getLastMonthNote() {
        Cursor cursor = rawQuery("select * from " + DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE +
                " LIMIT 1 OFFSET " + (MyApp.dbHandle.getCount4Record(ContansUtils.MONTH) - 1), new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            return new MonthNote(cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_LASTBALANCE)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_PAY)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_SALARY)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_INCOME)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_BALANCE)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_ACTUALBALANCE)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_DURATION)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_REMARK)),
                    cursor.getString(cursor.getColumnIndex(DBCommon.MonthNoteRecordColumns.COLUMN_MONTHNOTE_TIME)));
        }
        return null;
    }

}
