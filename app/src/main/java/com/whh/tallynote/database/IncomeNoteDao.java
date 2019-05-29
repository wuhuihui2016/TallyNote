package com.whh.tallynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whh.tallynote.MyApplication;
import com.whh.tallynote.model.IncomeNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyangtech on 2017/8/4.
 */

public class IncomeNoteDao {

    /**
     * 插入一条理财记录(默认未完成状态，修改状态需在列表中操作)
     *
     * @param incomeNote
     */
    //理财记录：money 投入金额（单位万）,incomeRatio 预期年化（%）,days 投资期限(天),durtion 投资时段,dayIncome 拟日收益（万/天）,finalIncome 最终收益, time 记录时间
    public static synchronized boolean newINote(IncomeNote incomeNote) {
        boolean isExit;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
        db.execSQL("insert into income_note(money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time) values(?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{incomeNote.getMoney(), incomeNote.getIncomeRatio(), incomeNote.getDays(), incomeNote.getDurtion(),
                        incomeNote.getDayIncome(), incomeNote.getFinalIncome(), incomeNote.getFinalCash(), incomeNote.getFinalCashGo(), IncomeNote.ON, incomeNote.getRemark(), incomeNote.getTime()});
        Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome()});
        isExit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isExit;
    }

    /**
     * 完成某个理财记录
     *
     * @param incomeNote
     * @return
     */
    public static synchronized boolean finishIncome(IncomeNote incomeNote) {
        boolean isFinished;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
        db.execSQL("update income_note set finished = 1,finalCash = ?,finalCashGo = ? where _id = ? and money = ? ",
                new String[]{incomeNote.getFinalCash(), incomeNote.getFinalCashGo(), incomeNote.getId(), incomeNote.getMoney()});
        Cursor cursor = db.rawQuery("select * from income_note where _id = ? and finalIncome = ? and finalCash= ?",
                new String[]{incomeNote.getId(), incomeNote.getFinalIncome(), incomeNote.getFinalCash()});
        isFinished = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isFinished;
    }

    /**
     * 查看所有的理财
     *
     * @return
     */
    public static synchronized List<IncomeNote> getIncomes() {
        List<IncomeNote> incomeNotes = new ArrayList<IncomeNote>();
        SQLiteDatabase db = MyApplication.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from income_note", null);
        while (cursor.moveToNext()) {
            /*String money; //投入金额（单位万）
            String incomeRatio; //预期年化（%）
			String days; //投资期限(天)
			String durtion; //投资时段
			String dayIncome; //拟日收益（万/天）
			String finalIncome; //最终收益
			String finalCash; //最终提现
			String finalCashGo; //提现去处
			int finished; //完成状态
			String remark;//投资说明
			String time; //记录时间*/

            IncomeNote income = new IncomeNote(cursor.getString(cursor.getColumnIndex("money")),
                    cursor.getString(cursor.getColumnIndex("incomeRatio")),
                    cursor.getString(cursor.getColumnIndex("days")),
                    cursor.getString(cursor.getColumnIndex("durtion")),
                    cursor.getString(cursor.getColumnIndex("dayIncome")),
                    cursor.getString(cursor.getColumnIndex("finalIncome")),
                    cursor.getString(cursor.getColumnIndex("finalCash")),
                    cursor.getString(cursor.getColumnIndex("finalCashGo")),
                    cursor.getInt(cursor.getColumnIndex("finished")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    cursor.getString(cursor.getColumnIndex("time"))
            );

            income.setId(cursor.getInt(cursor.getColumnIndex("_id")) + "");
            incomeNotes.add(income);
        }
        cursor.close();
        db.close();
        return incomeNotes;
    }

    /**
     * 批量插入理财记录
     *
     * @param incomeNotes
     */
    public static synchronized boolean newINotes(List<IncomeNote> incomeNotes) {
        boolean isExit = true;
        SQLiteDatabase db = MyApplication.dbHelper.getWritableDatabase();
        db.execSQL("delete from income_note"); //先清除本地数据,再一次添加新数据
        for (int i = 0; i < incomeNotes.size(); i++) {
            if (isExit) {
                IncomeNote incomeNote = incomeNotes.get(i);
                db.execSQL("insert into income_note(money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time) values(?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{incomeNote.getMoney(), incomeNote.getIncomeRatio(), incomeNote.getDays(), incomeNote.getDurtion(),
                                incomeNote.getDayIncome(), incomeNote.getFinalIncome(), incomeNote.getFinalCash(),
                                incomeNote.getFinalCashGo(), incomeNote.getFinished(), incomeNote.getRemark(), incomeNote.getTime()});
                Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ?", new String[]{incomeNote.getMoney(), incomeNote.getFinalIncome()});
                isExit = cursor.moveToFirst();
                cursor.close();
            } else return false;
        }
        db.close();
        return isExit;
    }

}
