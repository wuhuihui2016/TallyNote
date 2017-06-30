package com.fengyang.tallynote.utils;

import android.app.Activity;

import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.Income;
import com.fengyang.tallynote.model.MonthNote;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {

	/**
	 * 日账单表
	 * @param activity
	 * @param dayNotes
	 */
	public static void writeDayNote(Activity activity, List<DayNote> dayNotes){
		if (dayNotes.size() > 0) {
			try {
				LogUtils.i("writeDayNote", dayNotes.size() + "--" + dayNotes.toString());

				File file = new File(FileUtils.dirPath + "/dayNote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);

				String[] title = { "消费用途", "消费支出", "消费备注", "消费时间"};
				// 创建工作表
				WritableSheet sheet = writebook.createSheet("日账单", 0);

				//添加表头
				for (int i = 0; i < title.length; i ++){
					sheet.addCell(new Label(i, 0, title[i]));//列，行
				}
				//写入数据
				for (int i = 0; i < dayNotes.size(); i ++) {
					sheet.addCell(new Label(0, i + 1, dayNotes.get(i).getUsage()));
					sheet.addCell(new Label(1, i + 1, dayNotes.get(i).getMoney()));
					sheet.addCell(new Label(2, i + 1, dayNotes.get(i).getRemark()));
					sheet.addCell(new Label(3, i + 1, dayNotes.get(i).getTime()));
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
	 * @param monthNotes
	 * @param activity
	 */
	public static void writeMonthNote(Activity activity, List<MonthNote> monthNotes){
		if (monthNotes.size() > 0) {
			try {
				LogUtils.i("writeMonthNote", monthNotes.size() + "--" + monthNotes.toString());

				File file = new File(FileUtils.dirPath + "/monthNote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);

				String[] title = {"上次结余", "本次支出", "本次工资", "本次收益", "家用补贴", "本次结余", "实际结余",
						"期间", "备注", "记录时间"};

				// 创建工作表
				WritableSheet sheet = writebook.createSheet("月账单", 0);

				//添加表头
				for (int i = 0; i < title.length; i ++){
					sheet.addCell(new Label(i, 0, title[i]));//列，行
				}

				//写入数据
				for (int i = 0; i < monthNotes.size(); i ++) {
					sheet.addCell(new Label(0, i + 1, monthNotes.get(i).getLast_balance()));
					sheet.addCell(new Label(1, i + 1, monthNotes.get(i).getPay()));
					sheet.addCell(new Label(2, i + 1, monthNotes.get(i).getSalary()));
					sheet.addCell(new Label(3, i + 1, monthNotes.get(i).getIncome()));
					sheet.addCell(new Label(4, i + 1, monthNotes.get(i).getHomeuse()));
					sheet.addCell(new Label(5, i + 1, monthNotes.get(i).getBalance()));
					sheet.addCell(new Label(6, i + 1, monthNotes.get(i).getActual_balance()));
					sheet.addCell(new Label(7, i + 1, monthNotes.get(i).getDuration()));
					sheet.addCell(new Label(8, i + 1, monthNotes.get(i).getRemark()));
					sheet.addCell(new Label(9, i + 1, monthNotes.get(i).getTime()));
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
	 * @param incomes
	 * @param activity
	 */
	public static void writeIncomeNote(Activity activity, List<Income> incomes){
		if (incomes.size() > 0) {
			try {
				LogUtils.i("writeIncomeNote", incomes.size() + "--" + incomes.toString());

				File file = new File(FileUtils.dirPath + "/incomeNote_" + DateUtils.formatDate4fileName() +".xls");
				if(! file.exists()) file.createNewFile();
				WritableWorkbook writebook = Workbook.createWorkbook(file);

				String[] title = {"投入金额", "预期年化", "投资期限", "投资时期", "拟日收益", "最终收益",
						"最终提现", "提现去处", "完成状态"};

				// 创建工作表
				WritableSheet sheet = writebook.createSheet("月账单", 0);

				//添加表头
				for (int i = 0; i < title.length; i ++){
					sheet.addCell(new Label(i, 0, title[i]));//列，行
				}

				//写入数据
				for (int i = 0; i < incomes.size(); i ++) {
					sheet.addCell(new Label(0, i + 1, incomes.get(i).getMoney()));
					sheet.addCell(new Label(1, i + 1, incomes.get(i).getIncomeRatio()));
					sheet.addCell(new Label(2, i + 1, incomes.get(i).getDays()));
					sheet.addCell(new Label(3, i + 1, incomes.get(i).getDurtion()));
					sheet.addCell(new Label(4, i + 1, incomes.get(i).getDayIncome()+ ""));
					sheet.addCell(new Label(5, i + 1, incomes.get(i).getFinalIncome()));
					sheet.addCell(new Label(6, i + 1, incomes.get(i).getFinalCash()));
					sheet.addCell(new Label(7, i + 1, incomes.get(i).getFinalCashGo()));
					if (incomes.get(i).getFinished() == 0) {
						sheet.addCell(new Label(8, i + 1, "未完结"));
					} else {
						sheet.addCell(new Label(8, i + 1, "已完结"));
					}
				}
				writebook.write();
				writebook.close();
				StringUtils.show1Toast(activity, "导出成功：" + file.getAbsolutePath());

			} catch (Exception e){
				LogUtils.i("Exception", e.toString());
			}

		}
	}
}
