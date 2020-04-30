package com.whh.tallynote.utils;

import android.text.TextUtils;

import com.whh.tallynote.database.DayNoteDao;
import com.whh.tallynote.database.IncomeNoteDao;
import com.whh.tallynote.database.MemoNoteDao;
import com.whh.tallynote.database.MonthNoteDao;
import com.whh.tallynote.database.NotePadDao;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.model.MemoNote;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.model.NotePad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {

    private static final String TAG = "ExcelUtils";

    //数据列表
    private static List<DayNote> dayNotes;
    private static List<DayNote> dayNotes_history;
    private static List<MonthNote> monthNotes;
    private static List<IncomeNote> incomeNotes;
    private static List<MemoNote> memoNotes;
    private static List<NotePad> notePads;


    /**
     * 导出日账单表
     *
     * @param callBackExport
     */
    public static void exportDayNote(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.DAY);
            File file = new File(FileUtils.excelPath + ContansUtils.day_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet day_sheet = writebook.createSheet(ContansUtils.day_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.dayTitle.length; i++) {
                day_sheet.addCell(new Label(i, 0, ContansUtils.dayTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.DAY, day_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportDayNote", e.toString());
        }

    }

    /**
     * 导出历史日账单表
     *
     * @param callBackExport
     */
    public static void exportDayNote4History(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.DAY_HISTORY);
            File file = new File(FileUtils.excelPath + ContansUtils.day_history_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet day_history_sheet = writebook.createSheet(ContansUtils.day_history_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.dayHistoryTitle.length; i++) {
                day_history_sheet.addCell(new Label(i, 0, ContansUtils.dayHistoryTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.DAY_HISTORY, day_history_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportDayNote4History", e.toString());
        }

    }

    /**
     * 导出月账单表
     *
     * @param callBackExport
     */
    public static void exportMonthNote(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.MONTH);
            File file = new File(FileUtils.excelPath + ContansUtils.month_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);


            // 创建工作表
            WritableSheet month_sheet = writebook.createSheet(ContansUtils.month_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.monthTitle.length; i++) {
                month_sheet.addCell(new Label(i, 0, ContansUtils.monthTitle[i]));//列，行
            }

            //写入数据
            writeSheet(ContansUtils.MONTH, month_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportMonthNote", e.toString());
        }

    }

    /**
     * 导出理财记录表
     *
     * @param callBackExport
     */
    public static void exportIncomeNote(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.INCOME);
            File file = new File(FileUtils.excelPath + ContansUtils.income_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet income_sheet = writebook.createSheet(ContansUtils.income_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.incomeTitle.length; i++) {
                income_sheet.addCell(new Label(i, 0, ContansUtils.incomeTitle[i]));//列，行
            }

            //写入数据
            writeSheet(ContansUtils.INCOME, income_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportIncomeNote", e.toString());
        }

    }


    /**
     * 导出备忘录表
     *
     * @param callBackExport
     */
    public static void exportMemoNote(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.MEMO);
            File file = new File(FileUtils.excelPath + ContansUtils.memo_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet memo_sheet = writebook.createSheet(ContansUtils.memo_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.memoTitle.length; i++) {
                memo_sheet.addCell(new Label(i, 0, ContansUtils.memoTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.MEMO, memo_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportMemoNote", e.toString());
        }

    }

    /**
     * 导出记事本表
     *
     * @param callBackExport
     */
    public static void exportNotePad(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.NOTEPAD);
            File file = new File(FileUtils.excelPath + ContansUtils.notepad_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            // 创建工作表
            WritableSheet notepad_sheet = writebook.createSheet(ContansUtils.notepad_sheetName, 0);

            //添加表头
            for (int i = 0; i < ContansUtils.notepadTitle.length; i++) {
                notepad_sheet.addCell(new Label(i, 0, ContansUtils.notepadTitle[i]));//列，行
            }
            //写入数据
            writeSheet(ContansUtils.NOTEPAD, notepad_sheet);
            writebook.write();
            writebook.close();
            exportAll(null);
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportNotePad", e.toString());
        }

    }

    /**
     * 导出所有账单到一个Excel中
     *
     * @param callBackExport
     */
    public static void exportAll(ICallBackExport callBackExport) {
        try {
            FileUtils.clearOldExcelFile(ContansUtils.ALL);
            File file = new File(FileUtils.excelPath + ContansUtils.tallynote_file + DateUtils.formatDate4fileName() + ".xls");
            if (!file.exists()) file.createNewFile();
            WritableWorkbook writebook = Workbook.createWorkbook(file);

            //初始化日账单工作表
            WritableSheet day_sheet = writebook.createSheet(ContansUtils.day_sheetName, 0);
            for (int i = 0; i < ContansUtils.dayTitle.length; i++) {
                day_sheet.addCell(new Label(i, 0, ContansUtils.dayTitle[i]));//列，行
            }

            //初始化月账单工作表
            WritableSheet month_sheet = writebook.createSheet(ContansUtils.month_sheetName, 1);
            for (int i = 0; i < ContansUtils.monthTitle.length; i++) {
                month_sheet.addCell(new Label(i, 0, ContansUtils.monthTitle[i]));//列，行
            }

            //初始化理财记录工作表
            WritableSheet income_sheet = writebook.createSheet(ContansUtils.income_sheetName, 2);
            for (int i = 0; i < ContansUtils.incomeTitle.length; i++) {
                income_sheet.addCell(new Label(i, 0, ContansUtils.incomeTitle[i]));//列，行
            }

            //初始化历史日账单工作表
            WritableSheet day_history_sheet = writebook.createSheet(ContansUtils.day_history_sheetName, 3);
            for (int i = 0; i < ContansUtils.dayHistoryTitle.length; i++) {
                day_history_sheet.addCell(new Label(i, 0, ContansUtils.dayHistoryTitle[i]));//列，行
            }

            //初始化备忘录工作表
            WritableSheet memo_sheet = writebook.createSheet(ContansUtils.memo_sheetName, 4);
            for (int i = 0; i < ContansUtils.memoTitle.length; i++) {
                memo_sheet.addCell(new Label(i, 0, ContansUtils.memoTitle[i]));//列，行
            }

            //初始化记事本工作表
            WritableSheet notepad_sheet = writebook.createSheet(ContansUtils.notepad_sheetName, 5);
            for (int i = 0; i < ContansUtils.notepadTitle.length; i++) {
                notepad_sheet.addCell(new Label(i, 0, ContansUtils.notepadTitle[i]));//列，行
            }

            writeSheet(ContansUtils.DAY, day_sheet);
            writeSheet(ContansUtils.MONTH, month_sheet);
            writeSheet(ContansUtils.INCOME, income_sheet);
            writeSheet(ContansUtils.DAY_HISTORY, day_history_sheet);
            writeSheet(ContansUtils.MEMO, memo_sheet);
            writeSheet(ContansUtils.NOTEPAD, notepad_sheet);
            writebook.write();//只能执行一次
            writebook.close();

            //导出所有
            if (callBackExport != null) callBackExport.callback(true, file.getAbsolutePath());

        } catch (Exception e) {
            if (callBackExport != null) callBackExport.callback(false, null);
            LogUtils.e(TAG + "-exportAll", e.toString());
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
            dayNotes = DayNoteDao.getDayNotes();
            dayNotes_history = DayNoteDao.getDayNotes4History();
            monthNotes = MonthNoteDao.getMonthNotes();
            incomeNotes = IncomeNoteDao.getIncomes();
            memoNotes = MemoNoteDao.getMemoNotes();
            notePads = NotePadDao.getNotePads();

            switch (type) {
                case ContansUtils.DAY:
                    LogUtils.i(tag, dayNotes.size() + "---" + dayNotes.toString());
                    if (dayNotes.size() > 0) {
                        for (int i = 0; i < dayNotes.size(); i++) {
                            sheet.addCell(new Label(0, i + 1, DayNote.getUserType(dayNotes.get(i).getUseType())));
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
                            sheet.addCell(new Label(4, i + 1, monthNotes.get(i).getBalance()));
                            sheet.addCell(new Label(5, i + 1, monthNotes.get(i).getActual_balance()));
                            sheet.addCell(new Label(6, i + 1, monthNotes.get(i).getDuration()));
                            sheet.addCell(new Label(7, i + 1, monthNotes.get(i).getRemark()));
                            sheet.addCell(new Label(8, i + 1, monthNotes.get(i).getTime()));
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
                            if (incomeNotes.get(i).getFinished() == IncomeNote.ON)
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
                            sheet.addCell(new Label(0, i + 1, DayNote.getUserType(dayNotes_history.get(i).getUseType())));
                            sheet.addCell(new Label(1, i + 1, dayNotes_history.get(i).getMoney()));
                            sheet.addCell(new Label(2, i + 1, dayNotes_history.get(i).getRemark()));
                            sheet.addCell(new Label(3, i + 1, dayNotes_history.get(i).getTime()));
                            sheet.addCell(new Label(4, i + 1, dayNotes_history.get(i).getDuration()));
                        }
                    }
                    break;

                case ContansUtils.MEMO:
                    LogUtils.i(tag, memoNotes.size() + "---" + memoNotes.toString());
                    if (memoNotes.size() > 0) {
                        for (int i = 0; i < memoNotes.size(); i++) {
                            String status = memoNotes.get(i).getStatus() == 0 ? "进行中" : "已完成";
                            sheet.addCell(new Label(0, i + 1, memoNotes.get(i).getContent()));
                            sheet.addCell(new Label(1, i + 1, status));
                            sheet.addCell(new Label(2, i + 1, memoNotes.get(i).getTime()));
                        }
                    }
                    break;
                case ContansUtils.NOTEPAD:
                    LogUtils.i(tag, notePads.size() + "---" + notePads.toString());
                    if (notePads.size() > 0) {
                        for (int i = 0; i < notePads.size(); i++) {
                            String tagStr = NotePad.getTagList().get(notePads.get(i).getTag());
                            sheet.addCell(new Label(0, i + 1, tagStr));
                            sheet.addCell(new Label(1, i + 1, notePads.get(i).getWords()));
                            sheet.addCell(new Label(2, i + 1, notePads.get(i).getTime()));
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            LogUtils.e(TAG + "-writeSheet", e.toString());
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
     * 如果表单中没有数据不覆盖已有数据
     */
    public static void importExcel(final String filePath, final ICallBackImport callBackImport) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                FileUtils.backupTallyNoteFile(); //导入前先备份已有账本数据

                String tag = "importExcel";
                int day_count = 0, day_history_count = 0, month_count = 0, income_count = 0, memo_count = 0, notepad_count = 0;
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
                            if (sheetName.equals(ContansUtils.day_sheetName)) { //日账单解析
                                dayNotes = new ArrayList<>();
                                dayNotes.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    int type = 1;
                                    if (sheet.getCell(0, j).getContents().contains("支出")) type = 1;
                                    if (sheet.getCell(0, j).getContents().contains("转账")) type = 2;
                                    if (sheet.getCell(0, j).getContents().contains("转入")) type = 3;
                                    if (sheet.getCell(0, j).getContents().contains("家用")) type = 4;
                                    if (!TextUtils.isEmpty(sheet.getCell(1, j).getContents())) {
                                        dayNotes.add(new DayNote(
                                                type,
                                                sheet.getCell(1, j).getContents(),
                                                sheet.getCell(2, j).getContents(),
                                                sheet.getCell(3, j).getContents()));
                                    }
                                }
                                LogUtils.i(tag, dayNotes.size() + "---" + dayNotes.toString());
                                if (dayNotes.size() > 0) if (DayNoteDao.newDNotes(dayNotes)) {
                                    day_count = dayNotes.size();
                                    exportDayNote(null);
                                }

                            } else if (sheetName.equals(ContansUtils.month_sheetName)) { //月账单解析
                                monthNotes = new ArrayList<>();
                                monthNotes.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    if (!TextUtils.isEmpty(sheet.getCell(0, j).getContents())) {
                                        monthNotes.add(new MonthNote(
                                                sheet.getCell(0, j).getContents(),
                                                sheet.getCell(1, j).getContents(),
                                                sheet.getCell(2, j).getContents(),
                                                sheet.getCell(3, j).getContents(),
                                                sheet.getCell(4, j).getContents(),
                                                sheet.getCell(5, j).getContents(),
                                                sheet.getCell(6, j).getContents(),
                                                sheet.getCell(7, j).getContents(),
                                                sheet.getCell(8, j).getContents()));
                                    }
                                }
                                LogUtils.i(tag, monthNotes.size() + "---" + monthNotes.toString());
                                if (monthNotes.size() > 0) if (MonthNoteDao.newMNotes(monthNotes)) {
                                    month_count = monthNotes.size();
                                    exportMonthNote(null);
                                }

                            } else if (sheetName.equals(ContansUtils.income_sheetName)) { //理财记录解析
                                incomeNotes = new ArrayList<>();
                                incomeNotes.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    if (!TextUtils.isEmpty(sheet.getCell(0, j).getContents())) {
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
                                }
                                LogUtils.i(tag, incomeNotes.size() + "---" + incomeNotes.toString());
                                if (incomeNotes.size() > 0) if (IncomeNoteDao.newINotes(incomeNotes)) {
                                    income_count = incomeNotes.size();
                                    exportIncomeNote(null);
                                }

                            } else if (sheetName.equals(ContansUtils.day_history_sheetName)) { //历史日账单解析
                                dayNotes_history = new ArrayList<>();
                                dayNotes_history.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    if (!TextUtils.isEmpty(sheet.getCell(0, j).getContents())) {
                                        dayNotes_history.add(new DayNote(
                                                DayNote.getUserType(sheet.getCell(0, j).getContents()),
                                                sheet.getCell(1, j).getContents(),
                                                sheet.getCell(2, j).getContents(),
                                                sheet.getCell(3, j).getContents(),
                                                sheet.getCell(4, j).getContents())
                                        );
                                    }
                                }
                                LogUtils.i(tag, dayNotes_history.size() + "---" + dayNotes_history.toString());
                                if (dayNotes_history.size() > 0)
                                    if (DayNoteDao.newDNotes4History(dayNotes_history)) {
                                        day_history_count = dayNotes_history.size();
                                        exportDayNote4History(null);
                                    }
                            } else if (sheetName.equals(ContansUtils.memo_sheetName)) { //备忘录解析
                                memoNotes = new ArrayList<>();
                                memoNotes.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    if (!TextUtils.isEmpty(sheet.getCell(0, j).getContents())) {
                                        int status = sheet.getCell(1, j).getContents().equals("进行中") ? 0 : 1;
                                        memoNotes.add(new MemoNote(sheet.getCell(0, j).getContents(),
                                                status, sheet.getCell(2, j).getContents()));
                                    }
                                }
                                LogUtils.i(tag, memoNotes.size() + "---" + memoNotes.toString());
                                if (memoNotes.size() > 0)
                                    if (MemoNoteDao.newMemoNotes(memoNotes)) {
                                        memo_count = memoNotes.size();
                                        exportMemoNote(null);
                                    }
                            } else if (sheetName.equals(ContansUtils.notepad_sheetName)) { //记事本解析
                                notePads = new ArrayList<>();
                                notePads.clear();
                                for (int j = 1; j < rows; j++) {//行
                                    if (!TextUtils.isEmpty(sheet.getCell(0, j).getContents())) {
                                        String tagStr = sheet.getCell(0, j).getContents();
                                        notePads.add(new NotePad(NotePad.getTag(tagStr),
                                                sheet.getCell(1, j).getContents(), sheet.getCell(2, j).getContents())
                                        );
                                    }
                                }
                                LogUtils.i(tag, notePads.size() + "---" + notePads.toString());
                                if (notePads.size() > 0)
                                    if (NotePadDao.newNotePads(notePads)) {
                                        notepad_count = notePads.size();
                                        exportNotePad(null);
                                    }
                            } else {
                                callBackImport.callback("导入失败！原因：非本APP导出的文件！");
                                return;
                            }

                        } else LogUtils.i(tag, sheetName + "表单中没有可解析的数据！");
                    }

                    if (callBackImport != null) {
                        if (day_count > 0 || day_history_count > 0 || month_count > 0 || income_count > 0 || notepad_count > 0) {
                            callBackImport.callback(day_count, month_count, income_count, day_history_count, memo_count, notepad_count);
                            exportAll(null);
                        } else {
                            callBackImport.callback("导入失败, 原因：表单中没有可解析的数据！");
                        }
                    }
                    book.close();
                } catch (Exception e) {
                    LogUtils.e(TAG + "-importExcel", e.toString());
                    callBackImport.callback("导入失败！");
                }
            }
        }).start();
    }

    /**
     * 导出结果回调
     */
    public interface ICallBackImport {

        void callback(String errorMsg);

        void callback(int day_count, int month_count, int income_count, int day_history_count, int memo_count, int notepad_count);
    }
}
