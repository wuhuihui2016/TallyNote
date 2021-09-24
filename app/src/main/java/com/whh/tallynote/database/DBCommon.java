package com.whh.tallynote.database;

/**
 * day_note(日账记录)
 * day_note_history(历史日账记录)
 * month_note(月账记录)
 * income_note(理财记录)
 * memo_note(备忘录)
 * note_pad(记事本)
 * Created by wuhuihui on 2020/6/10.
 */
public class DBCommon {

    private interface Base_Columns {
    }

//    public static int VERSION = 1;
    public static int VERSION = 2; //记事本表增加 imgcount图片张数,img1,img2,img3 字段

    /**
     * 日消费记录
     */
    public interface DayNoteRecordColumns extends Base_Columns {

        String TABLE_NAME_DAYNOTE = "day_note"; //日账表
        String TABLE_NAME_DAYNOTE_HISTORY = "day_note_history"; //历史日账表

        String COLUMN_DAYNOTE_USETYPE = "useType"; //消费类型
        String COLUMN_DAYNOTE_MONEY = "money"; //金额
        String COLUMN_DAYNOTE_REMARK = "remark"; //消费明细
        String COLUMN_DAYNOTE_TIME = "time";//记录时间
        String COLUMN_DAYNOTE_DURATION = "duration"; //时段(历史日账)

        String[] projects = {COLUMN_DAYNOTE_USETYPE, COLUMN_DAYNOTE_MONEY, COLUMN_DAYNOTE_REMARK, COLUMN_DAYNOTE_TIME};
        String[] projects_history = {COLUMN_DAYNOTE_USETYPE, COLUMN_DAYNOTE_MONEY, COLUMN_DAYNOTE_REMARK,
                COLUMN_DAYNOTE_TIME, COLUMN_DAYNOTE_DURATION};

        //日消费记录：消费类型useType,金额money,消费明细remark,时间time
        String SQL_CREATE_DAYNOTE_TABLE = "create table if not exists day_note(_id integer primary key," +
                "useType integer,money varchar(20),remark varchar(100),time varchar(20))";

        //日消费历史记录：消费类型useType,金额money,消费明细remark,时间time,duration消费时段
        String SQL_CREATE_DAYNOTE_HISTORY_TABLE = "create table if not exists day_note_history(_id integer primary key," +
                "useType integer,money varchar(20),remark varchar(100),time varchar(20),duration varchar(20))";

    }

    /**
     * 月消费记录
     */
    public interface MonthNoteRecordColumns extends Base_Columns {

        String TABLE_NAME_MONTHNOTE = "month_note";

        String COLUMN_MONTHNOTE_LASTBALANCE = "last_balance"; //上次结余
        String COLUMN_MONTHNOTE_PAY = "pay"; //支出金额
        String COLUMN_MONTHNOTE_SALARY = "salary"; //工资
        String COLUMN_MONTHNOTE_INCOME = "income"; //收益
        String COLUMN_MONTHNOTE_BALANCE = "balance"; //结余
        String COLUMN_MONTHNOTE_ACTUALBALANCE = "actual_balance"; //实际结余
        String COLUMN_MONTHNOTE_DURATION = "duration"; //时段
        String COLUMN_MONTHNOTE_TIME = "time"; //记录时间
        String COLUMN_MONTHNOTE_REMARK = "remark"; //说明

        String[] projects = {COLUMN_MONTHNOTE_LASTBALANCE, COLUMN_MONTHNOTE_PAY, COLUMN_MONTHNOTE_SALARY,
                COLUMN_MONTHNOTE_INCOME, COLUMN_MONTHNOTE_BALANCE, COLUMN_MONTHNOTE_ACTUALBALANCE,
                COLUMN_MONTHNOTE_DURATION, COLUMN_MONTHNOTE_TIME, COLUMN_MONTHNOTE_REMARK};

        //月消费记录：上次结余last_balance,支出金额money,工资salary,收益income,结余balance,实际结余actual_balance,时段duration,时间time,说明remark
        String SQL_CREATE_MONTHNOTE_TABLE = "create table if not exists month_note(_id integer primary key," +
                "last_balance varchar(20),pay varchar(20),salary varchar(20),income varchar(20),balance varchar(20)," +
                "actual_balance varchar(20),duration varchar(20),remark varchar(100),time varchar(20))";

    }

    /**
     * 理财记录
     */
    public interface IncomeNoteRecordColumns extends Base_Columns {

        String TABLE_NAME_INCOMENOTE = "income_note";

        String COLUMN_INCOMENOTE_MONEY = "money"; //投入金额（单位万）
        String COLUMN_INCOMENOTE_INCOMERATIO = "incomeRatio"; //预期年化（%）
        String COLUMN_INCOMENOTE_DAYS = "days"; //投资期限(天)
        String COLUMN_INCOMENOTE_DURTION = "durtion"; //投资时段
        String COLUMN_INCOMENOTE_DAYINCOME = "dayIncome"; //拟日收益（万/天）
        String COLUMN_INCOMENOTE_FINALINCOME = "finalIncome"; //最终收益
        String COLUMN_INCOMENOTE_FINALCASH = "finalCash"; //最终提现
        String COLUMN_INCOMENOTE_FINALCASHGO = "finalCashGo"; //提现去处
        String COLUMN_INCOMENOTE_FINISHED = "finished"; //完成状态
        String COLUMN_INCOMENOTE_TIME = "time"; //记录时间
        String COLUMN_INCOMENOTE_REMARK = "remark"; //说明

        String[] projects = {COLUMN_INCOMENOTE_MONEY, COLUMN_INCOMENOTE_INCOMERATIO, COLUMN_INCOMENOTE_DAYS,
                COLUMN_INCOMENOTE_DURTION, COLUMN_INCOMENOTE_DAYINCOME, COLUMN_INCOMENOTE_FINALINCOME,
                COLUMN_INCOMENOTE_FINALCASH, COLUMN_INCOMENOTE_FINALCASHGO, COLUMN_INCOMENOTE_FINISHED,
                COLUMN_INCOMENOTE_TIME, COLUMN_INCOMENOTE_REMARK};

        //理财记录：money投入金额（单位万）,incomeRatio预期年化（%）,days投资期限(天),durtion投资时段,dayIncome拟日收益（万/天）,finalIncome最终收益
        //finalCash最终提现,finalCashGo提现去处,finished完成状态,time记录时间
        String SQL_CREATE_INCOMENOTE_TABLE = "create table if not exists income_note(_id integer primary key," +
                "money varchar(20),incomeRatio varchar(20),days varchar(20),durtion varchar(20),dayIncome varchar(20),finalIncome varchar(20)," +
                "finalCash varchar(20),finalCashGo varchar(20),finished integer,remark varchar(100),time varchar(20))";

    }

    /**
     * 备忘录
     */
    public interface MemoNoteRecordColumns extends Base_Columns {

        String TABLE_NAME_MEMONOTE = "memo_note";

        String COLUMN_MEMONOTE_CONTENT = "content"; //内容
        String COLUMN_MEMONOTE_STATUS = "status"; //状态
        String COLUMN_MEMONOTE_TIME = "time"; //记录时间

        String[] projects = {COLUMN_MEMONOTE_CONTENT, COLUMN_MEMONOTE_STATUS, COLUMN_MEMONOTE_TIME};

        //备忘录：content内容,status状态,time记录时间
        String SQL_CREATE_MEMO_TABLE = "create table if not exists memo_note(_id integer primary key," +
                "content varchar(200),status integer,time varchar(20))";

    }

    /**
     * 记事本
     */
    public interface NotePadRecordColumns extends Base_Columns {

        String TABLE_NAME_NOTEPAD = "note_pad";

        String COLUMN_NOTEPAD_TAG = "tag"; //标签
        String COLUMN_NOTEPAD_IMGCOUNT = "imgcount"; //图片张数 20210914 add
        String COLUMN_NOTEPAD_IMG1 = "img1"; //图片1 20210914 add
        String COLUMN_NOTEPAD_IMG2 = "img2"; //图片2 20210914 add
        String COLUMN_NOTEPAD_IMG3 = "img3"; //图片3 20210914 add
        String COLUMN_NOTEPAD_WORDS = "words"; //内容
        String COLUMN_NOTEPAD_TIME = "time"; //记录时间

        String[] projects = {COLUMN_NOTEPAD_TAG, COLUMN_NOTEPAD_IMGCOUNT, COLUMN_NOTEPAD_IMG1, COLUMN_NOTEPAD_IMG2,
                COLUMN_NOTEPAD_IMG3, COLUMN_NOTEPAD_WORDS, COLUMN_NOTEPAD_TIME};

        //记事本：tag标签,words内容,time记录时间
        String SQL_CREATE_NOTEPAD_TABLE = "create table if not exists note_pad(_id integer primary key," +
                "tag integer,words varchar(200),time varchar(20))";

        //记事本：tag标签,图片数量,img1、img2、img3图片,words内容,time记录时间
        String SQL_CREATE_NOTEPAD_TABLE2 = "create table if not exists note_pad(_id integer primary key," +
                "tag integer,imgcount integer,img1 BLOB,img2 BLOB,img3 BLOB,words varchar(200),time varchar(20))";

    }

}
