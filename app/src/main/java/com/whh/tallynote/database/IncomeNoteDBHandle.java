package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.ContansUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于操作理财记录表
 * Created by wuhuihui on 2020/6/10.
 */
public class IncomeNoteDBHandle extends DBHandle {
    private static IncomeNoteDBHandle instance;

    public static IncomeNoteDBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new IncomeNoteDBHandle(mContext.getApplicationContext());
        return instance;
    }

    private IncomeNoteDBHandle(Context context) {
        super(context);
    }

    /**
     * 插入一条新日帐单
     *
     * @param incomeNote
     * @return
     */
    public int saveIncomeNote(IncomeNote incomeNote) {
        if (incomeNote == null) {
            return -1;
        }

        ContentValues value = new ContentValues();
        //money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_MONEY, incomeNote.getMoney());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_INCOMERATIO, incomeNote.getIncomeRatio());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYS, incomeNote.getDays());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DURTION, incomeNote.getDurtion());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYINCOME, incomeNote.getDayIncome());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALINCOME, incomeNote.getFinalIncome());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASH, incomeNote.getFinalCash());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASHGO, incomeNote.getFinalCashGo());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINISHED, incomeNote.getFinished());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_REMARK, incomeNote.getRemark());
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_TIME, incomeNote.getTime());
        return insert(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, value);
    }

    /**
     * 完成某个理财记录
     *
     * @param incomeNote
     */
    public void finishIncome(IncomeNote incomeNote) {
        ContentValues value = new ContentValues();
        //finished = 1,finalCash = ?,finalCashGo = ?
        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINISHED, 1);
        update(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, value, "_id = ? and money = ?",
                new String[]{incomeNote.getId(), incomeNote.getMoney()});

        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASH, incomeNote.getFinalCash());
        update(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, value, "_id = ? and money = ?",
                new String[]{incomeNote.getId(), incomeNote.getMoney()});

        value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASHGO, incomeNote.getFinalCashGo());
        update(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, value, "_id = ? and money = ?",
                new String[]{incomeNote.getId(), incomeNote.getMoney()});
    }

    /**
     * 查看所有的月账单
     *
     * @return
     */
    public List<IncomeNote> getIncomeNotes() {
        List<IncomeNote> list = new ArrayList<>();
        Cursor cursor = null;
        try {
//            cursor = query(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, DBCommon.IncomeNoteRecordColumns.projects,
//                    null, null, null, null, null, null);
            cursor = rawQuery("select * from income_note");
            while (cursor.moveToNext()) {
                IncomeNote incomeNote = new IncomeNote(cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_MONEY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_INCOMERATIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DURTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYINCOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALINCOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASHGO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINISHED)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_REMARK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_TIME)));
                incomeNote.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")) + "");
                list.add(incomeNote);
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
     * 批量插入理财记录（在导入账单时批量插入理财记录）
     *
     * @param incomeNotes
     */
    public boolean saveIncomeNoteList(List<IncomeNote> incomeNotes) {
        ContentValues[] cv = new ContentValues[incomeNotes.size()];
        for (int i = 0; i < incomeNotes.size(); i++) {
            ContentValues value = new ContentValues();
            IncomeNote incomeNote = incomeNotes.get(i);
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_MONEY, incomeNote.getMoney());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_INCOMERATIO, incomeNote.getIncomeRatio());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYS, incomeNote.getDays());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DURTION, incomeNote.getDurtion());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYINCOME, incomeNote.getDayIncome());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALINCOME, incomeNote.getFinalIncome());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASH, incomeNote.getFinalCash());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASHGO, incomeNote.getFinalCashGo());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINISHED, incomeNote.getFinished());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_REMARK, incomeNote.getRemark());
            value.put(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_TIME, incomeNote.getTime());
            cv[i] = value;
        }
        return multiInsert(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, cv);
    }

    /**
     * 获取最后一条数据
     *
     * @return
     */
    public IncomeNote getLastIncomeNote() {
        Cursor cursor = rawQuery("select * from " + DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE +
                " LIMIT 1 OFFSET " + (MyApp.dbHandle.getCount4Record(ContansUtils.INCOME) - 1), new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            IncomeNote incomeNote = new IncomeNote(cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_MONEY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_INCOMERATIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DURTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_DAYINCOME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALINCOME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINALCASHGO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_FINISHED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_REMARK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBCommon.IncomeNoteRecordColumns.COLUMN_INCOMENOTE_TIME)));
            incomeNote.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")) + "");
            return incomeNote;
        }
        return null;
    }


}
