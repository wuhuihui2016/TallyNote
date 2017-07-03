package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.activity.NewMonthActivity;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengyangtech
 * 数据库工具类
 * 2017年6月22日
 */

public class DBUtils extends SQLiteOpenHelper {

	private static String TAG = "DBUtils";

	public DBUtils(Context context){
		super(context, "tally_note.db", null, 1);
		LogUtils.i(TAG, "保存账本数据-->" + "tally_note.db");
	}

	@Override
	public synchronized void onCreate(SQLiteDatabase db) {

		//日消费记录：消费用途usage,消费金额money,备注remark,时间time
		db.execSQL("create table if not exists day_note(_id integer primary key," +
				"usage varchar(100),money varchar(20),remark varchar(100),time varchar(20))");

		//月消费记录：上次结余last_balance,支出金额money,工资salary,收益income,家用补贴homeuse,结余balance,实际结余actual_balance,期间duration,时间time,备注remark
		db.execSQL("create table if not exists month_note(_id integer primary key," +
				"last_balance varchar(20),pay varchar(20),salary varchar(20),income varchar(20),homeuse varchar(20),balance varchar(20),actual_balance varchar(20)," +
				"duration varchar(20),remark varchar(100),time varchar(20))");
//
		//理财记录：money投入金额（单位万）,incomeRatio预期年化（%）,days投资期限(天),durtion投资时期,dayIncome拟日收益（万/天）,finalIncome最终收益
		//        finalCash最终提现,finalCashGo提现去处,finished完成状态,time记录时间
		db.execSQL("create table if not exists income_note(_id integer primary key," +
				"money varchar(20),incomeRatio varchar(20),days varchar(20),durtion varchar(20),dayIncome varchar(20),finalIncome varchar(20)," +
				"finalCash varchar(20),finalCashGo varchar(20),finished integer,remark varchar(100),time varchar(20))");

	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			db.execSQL("drop table if exists day_note");
			db.execSQL("drop table if exists month_note");
			onCreate(db);
		}
	}

	/**
	 * 插入一条新日帐单
	 * @param dayNote
	 */
	public synchronized boolean newDNote(DayNote dayNote){
		boolean isExit;
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("insert into day_note(usage,money,remark,time) values(?,?,?,?)",
				new Object[]{dayNote.getUsage(), dayNote.getMoney(), dayNote.getRemark(), dayNote.getTime()});
		Cursor cursor = db.rawQuery("select * from day_note where money = ? and time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
		isExit = cursor.moveToFirst();
		cursor.close();
		db.close();
		return isExit;
	}

	//删除某个日记录账单（仅用于最后一次账单记录，删除后可重新记录）
	public synchronized void delDNote(DayNote dayNote){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from day_note where money = ? or time = ?", new String[]{dayNote.getMoney(), dayNote.getTime()});
		db.close();
	}

	/**
	 * 查看所有的日记录账单
	 * @return
	 */
	public synchronized List<DayNote> getDayNotes(){
		List<DayNote> dayNotes = new ArrayList<DayNote>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from day_note",null);
		while(cursor.moveToNext()){
			//String usage, String money, String remark, String time
			DayNote dayNote = new DayNote(cursor.getString(cursor.getColumnIndex("usage")),
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
	 * 清除所有日账单(仅用于每月总结时，每月的发工资总结时)
	 * @param activity
	 */
	public synchronized void clearDayNotes(final Activity activity){
		DialogUtils.showMsgDialog(activity, "提示", "清除日账单，并导出日账单Excel",
				new DialogUtils.DialogListener() {
					@Override
					public void onClick(View v) {
						super.onClick(v);
						ExcelUtils.writeDayNote(activity, MyApp.utils.getDayNotes());
						SQLiteDatabase db = getWritableDatabase();
						db.execSQL("delete from day_note", null);
						db.close();
						DialogUtils.showMsgDialog(activity, "清除完成", "请新增月账单！", new DialogUtils.DialogListener() {
							@Override
							public void onClick(View v) {
								super.onClick(v);
								activity.startActivity(new Intent(activity, NewMonthActivity.class));
							};
						});

					}
				});

	}

	/**
	 * 插入一条新月帐单
	 * @param monthNote
	 */
	public synchronized boolean newMNote(MonthNote monthNote){
		boolean isExit;
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("insert into month_note(last_balance,pay,salary,income,homeuse,balance,actual_balance,duration,remark,time) values(?,?,?,?,?,?,?,?,?,?)",
				new Object[]{monthNote.getLast_balance(), monthNote.getPay(), monthNote.getSalary(), monthNote.getIncome(),
						monthNote.getHomeuse(),monthNote.getBalance(), monthNote.getActual_balance(),
						monthNote.getDuration(), monthNote.getRemark(), monthNote.getTime()});
		Cursor cursor = db.rawQuery("select * from month_note where actual_balance = ? and duration = ?",
				new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
		isExit = cursor.moveToFirst();
		cursor.close();
		db.close();
		return isExit;
	}

	/**
	 * 查看所有的月记录账单
	 * @return
	 */
	public synchronized List<MonthNote> getMonNotes(){
		List<MonthNote> monthNotes = new ArrayList<MonthNote>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from month_note",null);
		while(cursor.moveToNext()){
			//String last_balance, String pay, String salary, String income, String homeuse,String balance,
			//String actual_balance, String duration, String remark, String time
			MonthNote monthNote = new MonthNote(cursor.getString(cursor.getColumnIndex("last_balance")),
					cursor.getString(cursor.getColumnIndex("pay")),
					cursor.getString(cursor.getColumnIndex("salary")),
					cursor.getString(cursor.getColumnIndex("income")),
					cursor.getString(cursor.getColumnIndex("homeuse")),
					cursor.getString(cursor.getColumnIndex("balance")),
					cursor.getString(cursor.getColumnIndex("actual_balance")),
					cursor.getString(cursor.getColumnIndex("duration")),
					cursor.getString(cursor.getColumnIndex("remark")),
					cursor.getString(cursor.getColumnIndex("time")));
			monthNotes.add(monthNote);
		}
		cursor.close();
		db.close();
		return monthNotes;
	}

	//删除某个月记录账单（仅用于最后一次账单记录，删除后可重新记录）
	public synchronized void delMNote(MonthNote monthNote){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from month_note where actual_balance = ? or duration = ?",
				new String[]{monthNote.getActual_balance(), monthNote.getDuration()});
		db.close();
	}

	/**
	 * 清除所有月账单(仅用于年度总结时，每年的1月发工资总结时)
	 * @param activity
	 */
	public synchronized void clearMonthNotes(Activity activity){
		ExcelUtils.writeMonthNote(activity, MyApp.utils.getMonNotes());
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from month_note", null);
		db.close();
	}


	/**
	 * 插入一条理财记录(默认未完成状态，修改状态需在列表中操作)
	 * @param income
	 */
	//理财记录：money 投入金额（单位万）,incomeRatio 预期年化（%）,days 投资期限(天),durtion 投资时期,dayIncome 拟日收益（万/天）,finalIncome 最终收益, time 记录时间
	public synchronized boolean newIncome(IncomeNote income){
		boolean isExit;
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("insert into income_note(money,incomeRatio,days,durtion,dayIncome,finalIncome,finalCash,finalCashGo,finished,remark,time) values(?,?,?,?,?,?,?,?,?,?,?)",
				new Object[]{income.getMoney(),income.getIncomeRatio(),income.getDays(),income.getDurtion(),
						income.getDayIncome(),income.getFinalIncome(),income.getFinalCash(),income.getFinalCashGo(), 0, income.getRemark(), income.getTime()});
		Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ?", new String[]{income.getMoney(), income.getFinalIncome()});
		isExit = cursor.moveToFirst();
		cursor.close();
		db.close();
		return isExit;
	}

	//完成某个理财记录
	public synchronized boolean finishIncome(IncomeNote income){
		boolean isFinished;
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("update income_note set finished = 1 and finalCash = ? and finalCashGo = ? where money = ? and finalIncome = ? ",
				new String[]{income.getFinalCash(), income.getFinalCashGo()});
		Cursor cursor = db.rawQuery("select * from income_note where money = ? and finalIncome = ? and finalCash= ?", new String[]{income.getMoney(), income.getFinalIncome(), income.getFinalCash()});
		isFinished = cursor.moveToFirst();
		cursor.close();
		db.close();
		return isFinished;
	}

	//删除某个理财记录（仅用于最后一次账单记录，删除后可重新记录）
	public synchronized void delIncome(IncomeNote income){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from income_note where money = ? and finalIncome = ?", new String[]{income.getMoney(), income.getFinalIncome()});
		db.close();
	}

	/**
	 * 查看所有的理财
	 * @return
	 */
	public synchronized List<IncomeNote> getIncomes(){
		List<IncomeNote> incomes = new ArrayList<IncomeNote>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from income_note",null);
		while(cursor.moveToNext()){
			/*String money; //投入金额（单位万）
			String incomeRatio; //预期年化（%）
			String days; //投资期限(天)
			String durtion; //投资时期
			String dayIncome; //拟日收益（万/天）
			String finalIncome; //最终收益
			String finalCash; //最终提现
			String finalCashGo; //提现去处
			int finished; //完成状态
			String remark;//理财备注
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
			incomes.add(income);
		}
		cursor.close();
		db.close();
		return incomes;
	}

	/**
	 * 清除所有理财记录(一般不用)
	 * @param activity
	 */
	public synchronized void clearIncomes(final Activity activity){
		DialogUtils.showMsgDialog(activity, "提示", "清除日账单，并导出日账单Excel",
				new DialogUtils.DialogListener() {
					@Override
					public void onClick(View v) {
						super.onClick(v);
						ExcelUtils.writeIncomeNote(activity, MyApp.utils.getIncomes());
						SQLiteDatabase db = getWritableDatabase();
						db.execSQL("delete from day_note", null);
						db.close();
						DialogUtils.showMsgDialog(activity, "清除完成", "可新增理财记录！", new DialogUtils.DialogListener() {
							@Override
							public void onClick(View v) {
								super.onClick(v);
								activity.startActivity(new Intent(activity, NewMonthActivity.class));
							};
						});

					}
				});

	}

}
