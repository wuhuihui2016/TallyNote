package com.fengyang.tallynote.utils;

import android.app.Activity;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {

	private static String[] dayTitle = {"消费用途", "消费支出（元）", "消费备注", "消费时间"};

	private static String[] monthTitle = {"上次结余（元）", "本次支出（元）", "本次工资（元）", "本次收益（元）", "家用补贴（元）", "本次结余（元）", "实际结余（元）",
			"月结期间", "月结备注", "记录时间"};

	private static String[] incomeTitle = {"投入金额(万元)", "预期年化（%）", "投资期限（天）", "投资时期", "拟日收益（元/万天）", "最终收益（元）",
			"最终提现（元）", "提现去处", "完成状态", "投资备注", "记录时间"};

	/**
	 * 日账单表
	 * @param activity
	 */
	public static void exportDayNote(Activity activity){
		List<DayNote> dayNotes = MyApp.utils.getDayNotes();
		if (dayNotes.size() > 0) {
			try {
				LogUtils.i("exportDayNote", dayNotes.size() + "--" + dayNotes.toString());

				File file = new File(FileUtils.dirPath + "/daynote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);

				// 创建工作表
				WritableSheet day_sheet = writebook.createSheet("日账单", 0);

				//添加表头
				for (int i = 0; i < dayTitle.length; i ++){
					day_sheet.addCell(new Label(i, 0, dayTitle[i]));//列，行
				}
				//写入数据
				for (int i = 0; i < dayNotes.size(); i ++) {
					day_sheet.addCell(new Label(0, i + 1, dayNotes.get(i).getUsage()));
					day_sheet.addCell(new Label(1, i + 1, dayNotes.get(i).getMoney()));
					day_sheet.addCell(new Label(2, i + 1, dayNotes.get(i).getRemark()));
					day_sheet.addCell(new Label(3, i + 1, dayNotes.get(i).getTime()));
				}
				writebook.write();
				writebook.close();
				StringUtils.show1Toast(activity, "导出成功：" + file.getAbsolutePath());

			} catch (Exception e){
				LogUtils.i("Exception", e.toString());
			}

		}
	}

	/**
	 * 月账单表
	 * @param activity
	 */
	public static void exportMonthNote(Activity activity){
		List<MonthNote> monthNotes = MyApp.utils.getMonNotes();
		if (monthNotes.size() > 0) {
			try {
				LogUtils.i("exportMonthNote", monthNotes.size() + "--" + monthNotes.toString());

				File file = new File(FileUtils.dirPath + "/monthnote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);


				// 创建工作表
				WritableSheet month_sheet = writebook.createSheet("月账单", 0);

				//添加表头
				for (int i = 0; i < monthTitle.length; i ++){
					month_sheet.addCell(new Label(i, 0, monthTitle[i]));//列，行
				}

				//写入数据
				for (int i = 0; i < monthNotes.size(); i ++) {
					month_sheet.addCell(new Label(0, i + 1, monthNotes.get(i).getLast_balance()));
					month_sheet.addCell(new Label(1, i + 1, monthNotes.get(i).getPay()));
					month_sheet.addCell(new Label(2, i + 1, monthNotes.get(i).getSalary()));
					month_sheet.addCell(new Label(3, i + 1, monthNotes.get(i).getIncome()));
					month_sheet.addCell(new Label(4, i + 1, monthNotes.get(i).getHomeuse()));
					month_sheet.addCell(new Label(5, i + 1, monthNotes.get(i).getBalance()));
					month_sheet.addCell(new Label(6, i + 1, monthNotes.get(i).getActual_balance()));
					month_sheet.addCell(new Label(7, i + 1, monthNotes.get(i).getDuration()));
					month_sheet.addCell(new Label(8, i + 1, monthNotes.get(i).getRemark()));
					month_sheet.addCell(new Label(9, i + 1, monthNotes.get(i).getTime()));
				}
				writebook.write();
				writebook.close();
				StringUtils.show1Toast(activity, "导出成功：" + file.getAbsolutePath());

			} catch (Exception e){
				LogUtils.i("Exception", e.toString());
			}

		}
	}

	/**
	 * 理财记录表
	 * @param activity
	 */
	public static void exportIncomeNote(Activity activity){
		List<IncomeNote> incomes = MyApp.utils.getIncomes();
		if (incomes.size() > 0) {
			try {
				LogUtils.i("exportIncomeNote", incomes.size() + "--" + incomes.toString());

				File file = new File(FileUtils.dirPath + "/incomenote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);

				// 创建工作表
				WritableSheet income_sheet = writebook.createSheet("理财记录", 0);

				//添加表头
				for (int i = 0; i < incomeTitle.length; i ++){
					income_sheet.addCell(new Label(i, 0, incomeTitle[i]));//列，行
				}

				//写入数据
				for (int i = 0; i < incomes.size(); i ++) {
					income_sheet.addCell(new Label(0, i + 1, incomes.get(i).getMoney()));
					income_sheet.addCell(new Label(1, i + 1, incomes.get(i).getIncomeRatio()));
					income_sheet.addCell(new Label(2, i + 1, incomes.get(i).getDays()));
					income_sheet.addCell(new Label(3, i + 1, incomes.get(i).getDurtion()));
					income_sheet.addCell(new Label(4, i + 1, incomes.get(i).getDayIncome()+ ""));
					income_sheet.addCell(new Label(5, i + 1, incomes.get(i).getFinalIncome()));
					income_sheet.addCell(new Label(6, i + 1, incomes.get(i).getFinalCash()));
					income_sheet.addCell(new Label(7, i + 1, incomes.get(i).getFinalCashGo()));
					if (incomes.get(i).getFinished() == 0) income_sheet.addCell(new Label(8, i + 1, "未完结"));
					else income_sheet.addCell(new Label(8, i + 1, "已完结"));
					income_sheet.addCell(new Label(9, i + 1, incomes.get(i).getRemark()));
					income_sheet.addCell(new Label(10, i + 1, incomes.get(i).getTime()));
				}
				writebook.write();
				writebook.close();
				StringUtils.show1Toast(activity, "导出成功：" + file.getAbsolutePath());

			} catch (Exception e){
				LogUtils.i("Exception", e.toString());
			}
		}

	}

	/**
	 * 导出所有账单到一个Excel中
	 * @param activity
	 */
	public static void exportAll(Activity activity){
		try {
			List<DayNote> dayNotes = MyApp.utils.getDayNotes();
			List<MonthNote> monthNotes = MyApp.utils.getMonNotes();
			List<IncomeNote> incomes = MyApp.utils.getIncomes();

			LogUtils.i("exportAll", dayNotes.size() + "--" + dayNotes.toString());
			LogUtils.i("exportAll", monthNotes.size() + "--" + monthNotes.toString());
			LogUtils.i("exportAll", incomes.size() + "--" + incomes.toString());

			File file = new File(FileUtils.dirPath + "/tallynote_" + DateUtils.formatDate4fileName() +".xls");
			if(! file.exists()) file.createNewFile();
			WritableWorkbook writebook = Workbook.createWorkbook(file);

			//初始化日账单工作表
			WritableSheet day_sheet = writebook.createSheet("日账单", 0);
			for (int i = 0; i < dayTitle.length; i ++){
				day_sheet.addCell(new Label(i, 0, dayTitle[i]));//列，行
			}

			//初始化月账单工作表
			WritableSheet month_sheet = writebook.createSheet("月账单", 1);
			for (int i = 0; i < monthTitle.length; i ++){
				month_sheet.addCell(new Label(i, 0, monthTitle[i]));//列，行
			}

			//初始化理财记录工作表
			WritableSheet income_sheet = writebook.createSheet("理财记录", 2);
			for (int i = 0; i < incomeTitle.length; i ++){
				income_sheet.addCell(new Label(i, 0, incomeTitle[i]));//列，行
			}

			//写入数据
			if (dayNotes.size() > 0) {
				for (int i = 0; i < dayNotes.size(); i ++) {
					day_sheet.addCell(new Label(0, i + 1, dayNotes.get(i).getUsage()));
					day_sheet.addCell(new Label(1, i + 1, dayNotes.get(i).getMoney()));
					day_sheet.addCell(new Label(2, i + 1, dayNotes.get(i).getRemark()));
					day_sheet.addCell(new Label(3, i + 1, dayNotes.get(i).getTime()));
				}
			}

			if (monthNotes.size() > 0) {
				for (int i = 0; i < monthNotes.size(); i ++) {
					month_sheet.addCell(new Label(0, i + 1, monthNotes.get(i).getLast_balance()));
					month_sheet.addCell(new Label(1, i + 1, monthNotes.get(i).getPay()));
					month_sheet.addCell(new Label(2, i + 1, monthNotes.get(i).getSalary()));
					month_sheet.addCell(new Label(3, i + 1, monthNotes.get(i).getIncome()));
					month_sheet.addCell(new Label(4, i + 1, monthNotes.get(i).getHomeuse()));
					month_sheet.addCell(new Label(5, i + 1, monthNotes.get(i).getBalance()));
					month_sheet.addCell(new Label(6, i + 1, monthNotes.get(i).getActual_balance()));
					month_sheet.addCell(new Label(7, i + 1, monthNotes.get(i).getDuration()));
					month_sheet.addCell(new Label(8, i + 1, monthNotes.get(i).getRemark()));
					month_sheet.addCell(new Label(9, i + 1, monthNotes.get(i).getTime()));
				}
			}

			if (incomes.size() > 0) {
				for (int i = 0; i < incomes.size(); i ++) {
					income_sheet.addCell(new Label(0, i + 1, incomes.get(i).getMoney()));
					income_sheet.addCell(new Label(1, i + 1, incomes.get(i).getIncomeRatio()));
					income_sheet.addCell(new Label(2, i + 1, incomes.get(i).getDays()));
					income_sheet.addCell(new Label(3, i + 1, incomes.get(i).getDurtion()));
					income_sheet.addCell(new Label(4, i + 1, incomes.get(i).getDayIncome()+ ""));
					income_sheet.addCell(new Label(5, i + 1, incomes.get(i).getFinalIncome()));
					income_sheet.addCell(new Label(6, i + 1, incomes.get(i).getFinalCash()));
					income_sheet.addCell(new Label(7, i + 1, incomes.get(i).getFinalCashGo()));
					if (incomes.get(i).getFinished() == 0) income_sheet.addCell(new Label(8, i + 1, "未完结"));
					else income_sheet.addCell(new Label(8, i + 1, "已完结"));
					income_sheet.addCell(new Label(9, i + 1, incomes.get(i).getRemark()));
					income_sheet.addCell(new Label(10, i + 1, incomes.get(i).getTime()));
				}
			}
			writebook.write();//只能执行一次
			writebook.close();
			StringUtils.show1Toast(activity, "导出成功：" + file.getAbsolutePath());

		} catch (Exception e){
			LogUtils.i("Exception", e.toString());
		}

	}
}
