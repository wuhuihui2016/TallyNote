package com.whh.tallynote.model;

import com.whh.tallynote.database.IncomeNoteDao;
import com.whh.tallynote.database.MonthNoteDao;
import com.whh.tallynote.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 理财model
 * Created by wuhuihui on 2017/6/28.
 */
public class IncomeNote implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String money; //投入金额（单位万）
    String incomeRatio; //预期年化（%）
    String days; //投资期限(天)
    String durtion; //投资时段
    String dayIncome; //拟日收益（万/天）
    String finalIncome; //最终收益
    String finalCash; //最终提现
    String finalCashGo; //提现去处
    int finished; //完成状态
    String remark;//投资说明
    String time; //记录时间

    public static int ON = 0, OFF = 1;

    public IncomeNote(String money, String incomeRatio, String days, String durtion, String dayIncome, String finalIncome, String finalCash, String finalCashGo, int finished, String remark, String time) {
        this.money = money;
        this.incomeRatio = incomeRatio;
        this.days = days;
        this.durtion = durtion;
        this.dayIncome = dayIncome;
        this.finalIncome = finalIncome;
        this.finalCash = finalCash;
        this.finalCashGo = finalCashGo;
        this.finished = finished;
        this.remark = remark;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIncomeRatio() {
        return incomeRatio;
    }

    public void setIncomeRatio(String incomeRatio) {
        this.incomeRatio = incomeRatio;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDurtion() {
        return durtion;
    }

    public void setDurtion(String durtion) {
        this.durtion = durtion;
    }

    public String getDayIncome() {
        return dayIncome;
    }

    public void setDayIncome(String dayIncome) {
        this.dayIncome = dayIncome;
    }

    public String getFinalIncome() {
        return finalIncome;
    }

    public void setFinalIncome(String finalIncome) {
        this.finalIncome = finalIncome;
    }

    public String getFinalCash() {
        return finalCash;
    }

    public void setFinalCash(String finalCash) {
        this.finalCash = finalCash;
    }

    public String getFinalCashGo() {
        return finalCashGo;
    }

    public void setFinalCashGo(String finalCashGo) {
        this.finalCashGo = finalCashGo;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "IncomeNote{" +
                "id='" + id +
                ", money='" + money +
                ", incomeRatio='" + incomeRatio +
                ", days='" + days +
                ", durtion='" + durtion +
                ", dayIncome='" + dayIncome +
                ", finalIncome='" + finalIncome +
                ", finalCash='" + finalCash +
                ", finalCashGo='" + finalCashGo +
                ", finished=" + finished +
                ", remark='" + remark +
                ", time='" + time +
                '}';
    }

    /**
     * 获取计息中的理财列表
     *
     * @return
     */
    public static List<IncomeNote> getEarningInComes() {
        List<IncomeNote> earningInComes = new ArrayList<>();
        List<IncomeNote> incomeNotes = IncomeNoteDao.getIncomes();
        for (int i = 0; i < incomeNotes.size(); i++) {
            if (incomeNotes.get(i).getFinished() == IncomeNote.ON) {
                earningInComes.add(incomeNotes.get(i));
            }
        }
        return earningInComes;
    }

    /**
     * 获取计息中的理财资产(不包含收益，仅计算投入金额)
     *
     * @return
     */
    public static Double getEarningMoney() {
        Double sum = 0.00;
        List<IncomeNote> earningInComes = getEarningInComes();
        if (earningInComes.size() > 0) {
            for (int i = 0; i < earningInComes.size(); i++) {
                sum += Double.parseDouble(earningInComes.get(i).getMoney());
            }
        }
        return sum;

    }


    //显示最近一次收益的理财记录
    public static IncomeNote getLastIncomeNote() {
        if (IncomeNoteDao.getIncomes().size() > 0) {
            IncomeNote incomeNote = getEarningInComes().get(0);
            for (int i = 0; i < getEarningInComes().size(); i++) {
                String date = getEarningInComes().get(i).getDurtion().split("-")[1];
                String lasterDate = incomeNote.getDurtion().split("-")[1];
                if (DateUtils.daysBetween(date) < DateUtils.daysBetween(lasterDate)) {
                    incomeNote = getEarningInComes().get(i);
                }
            }
            return incomeNote;
        } else return null;
    }

    /**
     * 获取已完成的理财列表
     *
     * @return
     */
    public static List<IncomeNote> getFinishedInComes() {
        List<IncomeNote> finishedIncomes = new ArrayList<>();
        List<IncomeNote> incomeNotes = IncomeNoteDao.getIncomes();
        for (int i = 0; i < incomeNotes.size(); i++) {
            if (incomeNotes.get(i).getFinished() == 1) {
                finishedIncomes.add(incomeNotes.get(i));
            }
        }
        return finishedIncomes;
    }

    /**
     * 获取截至到某一天已完成而未结算的理财列表
     *
     * @return
     */
    public static List<IncomeNote> getUnRecordInComes() {
        List<IncomeNote> unRecordInComes = new ArrayList<>();
        if (IncomeNote.getFinishedInComes().size() > 0 && MonthNoteDao.getMonthNotes().size() > 0) {
            String dateStr = MonthNote.getEndDate();
            List<IncomeNote> finishedInComes = IncomeNote.getFinishedInComes();
            for (int i = 0; i < finishedInComes.size(); i++) {
                String currDateStr = finishedInComes.get(i).getDurtion().split("-")[1];
                if (DateUtils.checkAfterDate(dateStr, currDateStr)) {
                    unRecordInComes.add(finishedInComes.get(i));
                }
            }
        }
        return unRecordInComes;
    }

    /**
     * 获取截至到某一天已完成而未结算的收益总额
     *
     * @return
     */
    public static Double getUnRecordSum() {
        Double sum = 0.00;
        List<IncomeNote> unRecordInComes = getUnRecordInComes();
        if (unRecordInComes.size() > 0) {
            for (int i = 0; i < unRecordInComes.size(); i++) {
                sum += Double.parseDouble(unRecordInComes.get(i).getFinalIncome());
            }
        }
        return sum;
    }

    /*
     * 新建理财获取新的编码
     */
    public static String getNewIncomeID() {
        String newIncomeID;
        if (IncomeNoteDao.getIncomes().size() > 0) {
            List<IncomeNote> incomeNotes = IncomeNoteDao.getIncomes();
            String id = incomeNotes.get(incomeNotes.size() - 1).getId();
            newIncomeID = "新的理财ID：" + (Integer.parseInt(id) + 1);
        } else newIncomeID = "新的理财ID：1";

        return newIncomeID;
    }


}
