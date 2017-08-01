package com.fengyang.tallynote.utils;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {

    //文件名
    private static final String day_file = "day_note_", month_file = "month_note_", income_file = "income_note_",
            tallynote_file = "tally_note_", day_history_file = "day_note_history_";

    //表单名
    private static final String day_sheetName = "日账单", month_sheetName = "月账单", income_sheetName = "理财记录", day_history_sheetName = "历史日账单";

    //表单头部标题
    private static String[] dayTitle = {"消费类型", "金额（元）", "消费明细", "消费时间"};
    private static String[] dayHistoryTitle = {"消费类型", "金额（元）", "消费明细", "消费时间", "消费时段"};
    private static String[] monthTitle = {"上次结余（元）", "本次支出（元）", "本次工资（元）", "本次收益（元）", "家用补贴（元）", "本次结余（元）", "实际结余（元）",
            "月结时段", "月结说明", "记录时间"};
    private static String[] incomeTitle = {"投入金额(万元)", "预期年化（%）", "投资期限（天）", "投资时段", "拟日收益（元/万天）", "最终收益（元）",
            "最终提现（元）", "提现去处", "完成状态", "投资说明", "记录时间"};

    //数据列表
    private static List<DayNote> dayNotes;
    private static List<DayNote> dayNotes_history;
    private static List<MonthNote> monthNotes;
    private static List<IncomeNote> incomeNotes;

    /**
     * 文件导出时清除旧文件
     *
     * @param type
     */
    private static void clearOldExcelFile(int type) {
        File excelDir = FileUtils.getExcelDir();
        final File files[] = excelDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (type == ContansUtils.DAY) {
                if (files[i].getName().contains(day_file)) files[i].delete();
            } else if (type == ContansUtils.DAY_HISTORY){
                if (files[i].getName().contains(day_history_file)) files[i].delete();
            } else if (type == ContansUtils.MONTH) {
                if (files[i].getName().contains(month_file)) files[i].delete();
            } else if (type == ContansUtils.INCOME) {
                if (files[i].getName().contains(income_file)) files[i].delete();
            } else {
                if (files[i].getName().contains(tallynote_file)) files[i].delete();
            }
        }
    }

    /**
     * 日账单表
     *
     * @param callBackExport
     */
    public static void exportDayNote(ICallBackExport callBackExport) {
        try {
            clearOldExcelFile(ContansUtils.DAY);
            File file = new File(FileUtils.excelPath + day_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet day_sheet = writebook.createSheet(day_sheetName, 0);

            //添加表头
            for (int i = 0; i < dayTitle.length; i++) {
                day_sheet.addCell(new Label(i, 0, dayTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.DAY, day_sheet);
            writebook.write();
            writebook.close();
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.i("Exception", e.toString());
        }

    }
    /**
     * 历史日账单表
     *
     * @param callBackExport
     */
    public static void exportDayNote4History(ICallBackExport callBackExport) {
        try {
            clearOldExcelFile(ContansUtils.DAY_HISTORY);
            File file = new File(FileUtils.excelPath + day_history_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet day_history_sheet = writebook.createSheet(day_history_sheetName, 0);

            //添加表头
            for (int i = 0; i < dayHistoryTitle.length; i++) {
                day_history_sheet.addCell(new Label(i, 0, dayHistoryTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.DAY_HISTORY, day_history_sheet);
            writebook.write();
            writebook.close();
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.i("Exception", e.toString());
        }

    }

    /**
     * 月账单表
     *
     * @param callBackExport
     */
    public static void exportMonthNote(ICallBackExport callBackExport) {
        try {
            clearOldExcelFile(ContansUtils.MONTH);
            File file = new File(FileUtils.excelPath + month_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);


            // 创建工作表
            WritableSheet month_sheet = writebook.createSheet(month_sheetName, 0);

            //添加表头
            for (int i = 0; i < monthTitle.length; i++) {
                month_sheet.addCell(new Label(i, 0, monthTitle[i]));//列，行
            }

            //写入数据
            writeSheet(ContansUtils.MONTH, month_sheet);
            writebook.write();
            writebook.close();

            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.i("Exception", e.toString());
        }

    }

    /**
     * 理财记录表
     *
     * @param callBackExport
     */
    public static void exportIncomeNote(ICallBackExport callBackExport) {
        try {
            clearOldExcelFile(ContansUtils.INCOME);
            File file = new File(FileUtils.excelPath + income_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet income_sheet = writebook.createSheet(income_sheetName, 0);

            //添加表头
            for (int i = 0; i < incomeTitle.length; i++) {
                income_sheet.addCell(new Label(i, 0, incomeTitle[i]));//列，行
            }

            //写入数据
            writeSheet(ContansUtils.INCOME, income_sheet);
            writebook.write();
            writebook.close();

            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.i("Exception", e.toString());
        }

    }

    /**
     * 导出所有账单到一个Excel中
     *
     * @param callBackExport
     */
    public static void exportAll(ICallBackExport callBackExport) {
        try {
            clearOldExcelFile(ContansUtils.ALL);
            File file = new File(FileUtils.excelPath + tallynote_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            //初始化日账单工作表
            WritableSheet day_sheet = writebook.createSheet(day_sheetName, 0);
            for (int i = 0; i < dayTitle.length; i++) {
                day_sheet.addCell(new Label(i, 0, dayTitle[i]));//列，行
            }

            //初始化月账单工作表
            WritableSheet month_sheet = writebook.createSheet(month_sheetName, 1);
            for (int i = 0; i < monthTitle.length; i++) {
                month_sheet.addCell(new Label(i, 0, monthTitle[i]));//列，行
            }

            //初始化理财记录工作表
            WritableSheet income_sheet = writebook.createSheet(income_sheetName, 2);
            for (int i = 0; i < incomeTitle.length; i++) {
                income_sheet.addCell(new Label(i, 0, incomeTitle[i]));//列，行
            }

            //初始化历史日账单工作表
            WritableSheet day_history_sheet = writebook.createSheet(day_history_sheetName, 3);
            for (int i = 0; i < dayHistoryTitle.length; i++) {
                day_history_sheet.addCell(new Label(i, 0, dayHistoryTitle[i]));//列，行
            }

            writeSheet(ContansUtils.DAY, day_sheet);
            writeSheet(ContansUtils.MONTH, month_sheet);
            writeSheet(ContansUtils.INCOME, income_sheet);
            writeSheet(ContansUtils.DAY_HISTORY, day_history_sheet);
            writebook.write();//只能执行一次
            writebook.close();

            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.i("Exception", e.toString());
        }

    }

    /**
     * 写入数据
     *
     * @param type
     * @param sheet
     */
    private static void writeSheet(int type, WritableSheet sheet) {
        try {
            String tag = "writeSheet";
            dayNotes = MyApp.utils.getDayNotes();
            dayNotes_history = MyApp.utils.getDayNotes4History();
            monthNotes = MyApp.utils.getMonNotes();
            incomeNotes = MyApp.utils.getIncomes();

            switch (type) {
                case ContansUtils.DAY:
                    LogUtils.i(tag, dayNotes.size() + "---" + dayNotes.toString());
                    if (dayNotes.size() > 0) {
                        for (int i = 0; i < dayNotes.size(); i++) {
                            String dayType = null;
                            if (dayNotes.get(i).getUseType() == DayNote.consume) dayType = "支出";
                            if (dayNotes.get(i).getUseType() == DayNote.account_out) dayType = "转账";
                            if (dayNotes.get(i).getUseType() == DayNote.account_in) dayType = "转入";
                            sheet.addCell(new Label(0, i + 1, dayType));
                            sheet.addCell(new Label(1, i + 1, dayNotes.get(i).getMoney()));
                            sheet.addCell(new Label(2, i + 1, dayNotes.get(i).getRemark()));
                            sheet.addCell(new Label(3, i + 1, dayNotes.get(i).getTime()));
                        }
                    }
                    break;

                case ContansUtils.MONTH:
                    LogUtils.i(tag, monthNotes.size() + "---" + monthNotes.toString());
                    if (monthNotes.size() > 0) {
                        for (int i = 0; i < monthNotes.size(); i++) {
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
                    }
                    break;

                case ContansUtils.INCOME:
                    LogUtils.i(tag, incomeNotes.size() + "---" + incomeNotes.toString());
                    if (incomeNotes.size() > 0) {
                        for (int i = 0; i < incomeNotes.size(); i++) {
                            sheet.addCell(new Label(0, i + 1, incomeNotes.get(i).getMoney()));
                            sheet.addCell(new Label(1, i + 1, incomeNotes.get(i).getIncomeRatio()));
                            sheet.addCell(new Label(2, i + 1, incomeNotes.get(i).getDays()));
                            sheet.addCell(new Label(3, i + 1, incomeNotes.get(i).getDurtion()));
                            sheet.addCell(new Label(4, i + 1, incomeNotes.get(i).getDayIncome() + ""));
                            sheet.addCell(new Label(5, i + 1, incomeNotes.get(i).getFinalIncome()));
                            sheet.addCell(new Label(6, i + 1, incomeNotes.get(i).getFinalCash()));
                            sheet.addCell(new Label(7, i + 1, incomeNotes.get(i).getFinalCashGo()));
                            if (incomeNotes.get(i).getFinished() == 0)
                                sheet.addCell(new Label(8, i + 1, "未完结"));
                            else sheet.addCell(new Label(8, i + 1, "已完结"));
                            sheet.addCell(new Label(9, i + 1, incomeNotes.get(i).getRemark()));
                            sheet.addCell(new Label(10, i + 1, incomeNotes.get(i).getTime()));
                        }
                    }
                    break;

                case ContansUtils.DAY_HISTORY:
                    LogUtils.i(tag, dayNotes_history.size() + "---" + dayNotes_history.toString());
                    if (dayNotes_history.size() > 0) {
                        for (int i = 0; i < dayNotes_history.size(); i++) {
                            String dayType = null;
                            if (dayNotes_history.get(i).getUseType() == DayNote.consume) dayType = "支出";
                            if (dayNotes_history.get(i).getUseType() == DayNote.account_out) dayType = "转账";
                            if (dayNotes_history.get(i).getUseType() == DayNote.account_in) dayType = "转入";
                            sheet.addCell(new Label(0, i + 1, dayType));
                            sheet.addCell(new Label(1, i + 1, dayNotes_history.get(i).getMoney()));
                            sheet.addCell(new Label(2, i + 1, dayNotes_history.get(i).getRemark()));
                            sheet.addCell(new Label(3, i + 1, dayNotes_history.get(i).getTime()));
                            sheet.addCell(new Label(4, i + 1, dayNotes_history.get(i).getDuration()));
                        }
                    }
                    break;

            }
        } catch (Exception e) {
        }
    }

    /**
     * 导出结果回调
     */
    public interface ICallBackExport {
        void callback(boolean sucess, String fileName);
    }

    /**
     * 导入本地Excel文件到数据库
     */
    public static void importExcel(String filePath, ICallBackImport callBackImport) {
        String tag = "importExcel";
        int day_count = 0, day_history_count = 0, month_count = 0, income_count = 0;
        try {
            Workbook book = Workbook.getWorkbook(new File(filePath));
            int num = book.getNumberOfSheets();
            LogUtils.i(tag, "表单数：" + num);
            for (int i = 0; i < num; i++) {
                Sheet sheet = book.getSheet(i);
                int rows = sheet.getRows();
                int cols = sheet.getColumns();//列
                String sheetName = sheet.getName();
                LogUtils.i(tag, "第" + (i + 1) + "个表单名：" + sheetName + ",表单行数：" + rows + "表单列数：" + cols);

                if (rows > 0) { //如果表单中有数据时解析
                    if (sheetName.equals(day_sheetName)) { //日账单解析
                        dayNotes = new ArrayList<>();
                        dayNotes.clear();
                        for (int j = 1; j < rows; j++) {//行
                            int type = 1;
                            if (sheet.getCell(0, j).getContents().contains("支出")) type = 1;
                            if (sheet.getCell(0, j).getContents().contains("转账")) type = 2;
                            if (sheet.getCell(0, j).getContents().contains("转入")) type = 3;
                            dayNotes.add(new DayNote(
                                    type,
                                    sheet.getCell(1, j).getContents(),
                                    sheet.getCell(2, j).getContents(),
                                    sheet.getCell(3, j).getContents()));
                        }
                        LogUtils.i(tag, dayNotes.size() + "---" + dayNotes.toString());
                        if (dayNotes.size() > 0) if (MyApp.utils.newDNotes(dayNotes)) {
                            day_count = dayNotes.size();
                            exportDayNote(null);
                        }

                    } else if (sheetName.equals(month_sheetName)) { //月账单解析
                        monthNotes = new ArrayList<>();
                        monthNotes.clear();
                        for (int j = 1; j < rows; j++) {//行
                            monthNotes.add(new MonthNote(
                                    sheet.getCell(0, j).getContents(),
                                    sheet.getCell(1, j).getContents(),
                                    sheet.getCell(2, j).getContents(),
                                    sheet.getCell(3, j).getContents(),
                                    sheet.getCell(4, j).getContents(),
                                    sheet.getCell(5, j).getContents(),
                                    sheet.getCell(6, j).getContents(),
                                    sheet.getCell(7, j).getContents(),
                                    sheet.getCell(8, j).getContents(),
                                    sheet.getCell(9, j).getContents()));
                        }
                        LogUtils.i(tag, monthNotes.size() + "---" + monthNotes.toString());
                        if (monthNotes.size() > 0) if (MyApp.utils.newMNotes(monthNotes)) {
                            month_count = monthNotes.size();
                            exportMonthNote(null);
                        }

                    } else if (sheetName.equals(income_sheetName)) { //理财记录解析
                        incomeNotes = new ArrayList<>();
                        incomeNotes.clear();
                        for (int j = 1; j < rows; j++) {//行
                            incomeNotes.add(new IncomeNote(
                                    sheet.getCell(0, j).getContents(),
                                    sheet.getCell(1, j).getContents(),
                                    sheet.getCell(2, j).getContents(),
                                    sheet.getCell(3, j).getContents(),
                                    sheet.getCell(4, j).getContents(),
                                    sheet.getCell(5, j).getContents(),
                                    sheet.getCell(6, j).getContents(),
                                    sheet.getCell(7, j).getContents(),
                                    (sheet.getCell(8, j).getContents().contains("已")) ? 1 : 0,
                                    sheet.getCell(9, j).getContents(),
                                    sheet.getCell(10, j).getContents()));
                        }
                        LogUtils.i(tag, incomeNotes.size() + "---" + incomeNotes.toString());
                        if (incomeNotes.size() > 0) if (MyApp.utils.newINotes(incomeNotes)) {
                            income_count = incomeNotes.size();
                            exportIncomeNote(null);
                        }

                    } else if (sheetName.equals(day_history_sheetName)) { //历史日账单解析
                        dayNotes_history = new ArrayList<>();
                        dayNotes_history.clear();
                        for (int j = 1; j < rows; j++) {//行
                            int type = 1;
                            if (sheet.getCell(0, j).getContents().contains("支出")) type = 1;
                            if (sheet.getCell(0, j).getContents().contains("转账")) type = 2;
                            if (sheet.getCell(0, j).getContents().contains("转入")) type = 3;
                            dayNotes_history.add(new DayNote(
                                    type,
                                    sheet.getCell(1, j).getContents(),
                                    sheet.getCell(2, j).getContents(),
                                    sheet.getCell(3, j).getContents(),
                                    sheet.getCell(4, j).getContents())
                            );
                        }
                        LogUtils.i(tag, dayNotes_history.size() + "---" + dayNotes_history.toString());
                        if (dayNotes_history.size() > 0) if (MyApp.utils.newDNotes4History(dayNotes_history)) {
                            day_history_count = dayNotes_history.size();
                            exportDayNote4History(null);
                        }
                    } else {
                        callBackImport.callback("导入失败！原因：非本APP导出的文件！");
                        return;
                    }

                } else LogUtils.i(tag, sheetName + "表单中没有可解析的数据！");
            }

            if (callBackImport != null) {
                if (day_count > 0 || day_history_count > 0 || month_count > 0 || income_count > 0) {
                    callBackImport.callback(day_count, month_count, income_count, day_history_count);
                } else callBackImport.callback("导入失败, 原因：表单中没有可解析的数据！");
            }
            book.close();
        } catch (Exception e) {
            System.out.println(e);
            callBackImport.callback("导入失败！");
        }
    }

    /**
     * 导出结果回调
     */
    public interface ICallBackImport {
        void callback(String errorMsg);

        void callback(int day_count, int month_count, int income_count, int day_history_count);
    }
}
