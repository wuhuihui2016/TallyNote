package com.whh.tallynote.model;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 月账model
 * Created by wuhuihui on 2017/6/23.
 */
public class MonthNote implements Serializable {

    private static final long serialVersionUID = 1L;

    String last_balance; //上次结余
    String pay; //本次支出
    String salary; //本次工资
    String income; //本次收益
    String balance; //本次结余
    String actual_balance; //实际结余
    String duration; //月结时段
    String remark; //月结说明
    String time; //记录时间

    public MonthNote(String last_balance, String pay,
                     String salary, String income, String balance,
                     String actual_balance, String duration,
                     String remark, String time) {
        this.last_balance = last_balance;
        this.pay = pay;
        this.salary = salary;
        this.income = income;
        this.balance = balance;
        this.actual_balance = actual_balance;
        this.duration = duration;
        this.remark = remark;
        this.time = time;
    }

    public String getLast_balance() {
        return last_balance;
    }

    public void setLast_balance(String last_balance) {
        this.last_balance = last_balance;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getActual_balance() {
        return actual_balance;
    }

    public void setActual_balance(String actual_balance) {
        this.actual_balance = actual_balance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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
        return "MonthNote{" +
                "last_balance='" + last_balance +
                ", pay='" + pay +
                ", salary='" + salary +
                ", income='" + income +
                ", balance='" + balance +
                ", actual_balance='" + actual_balance +
                ", duration='" + duration +
                ", remark='" + remark +
                ", time='" + time +
                '}';
    }

    /*
     * 获取最后一次月结算的截止时间
     */
    public static String getEndDate() {
        if (MyApp.dbHandle.getCount4Record(ContansUtils.MONTH) > 0)
            return MyApp.monthNoteDBHandle.getLastMonthNote().getDuration().split("-")[1];
        else return "";
    }

    /**
     * 获取最后一次月结算的截止时间的后一天
     *
     * @return
     */
    public static String getAfterEndDate() {
        String getDate = "";
        if (getEndDate().length() > 0) {
            try {
                Calendar cal = DateUtils.getAfterDate(getEndDate(), 1);
                getDate = DateUtils.date_sdf.format(cal.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return getDate + "-";
        } else return getDate;
    }

    /**
     * 获取所有月账的时段(不取第一个，没有历史日账记录)
     *
     * @return
     */
    public static List<String> getDurations() {
        List<String> durations = new ArrayList<>();
        List<MonthNote> monthNotes = MyApp.monthNoteDBHandle.getMonthNotes();
        for (int i = 0; i < monthNotes.size(); i++) {
            if (i != 0) durations.add(monthNotes.get(i).getDuration());
        }
        return durations;
    }

    /**
     * 获取所需月账的时段
     *
     * @return
     */
    public static List<String> formateDurations(List<MonthNote> monthNotes) {
        List<String> durations = new ArrayList<>();
        for (int i = 0; i < monthNotes.size(); i++) {
            durations.add(monthNotes.get(i).getDuration().split("-")[0].substring(4, 6));
        }
        return durations;
    }

    /**
     * 获取所有月账的支出
     *
     * @return
     */
    public static List<Double> getPays() {
        List<Double> pays = new ArrayList<>();
        List<MonthNote> monthNotes = MyApp.monthNoteDBHandle.getMonthNotes();
        for (int i = 0; i < monthNotes.size(); i++) {
            pays.add(Double.parseDouble(monthNotes.get(i).getPay()));
        }
        return pays;
    }

    /**
     * 获取所有月账的工资
     *
     * @return
     */
    public static List<Double> getSalarys() {
        List<Double> salarys = new ArrayList<>();
        List<MonthNote> monthNotes = MyApp.monthNoteDBHandle.getMonthNotes();
        for (int i = 0; i < monthNotes.size(); i++) {
            salarys.add(Double.parseDouble(monthNotes.get(i).getSalary()));
        }
        return salarys;
    }

    /**
     * 获取所有月账的收益
     *
     * @return
     */
    public static List<Double> getIncomes() {
        List<Double> incomes = new ArrayList<>();
        List<MonthNote> monthNotes = MyApp.monthNoteDBHandle.getMonthNotes();
        for (int i = 0; i < monthNotes.size(); i++) {
            incomes.add(Double.parseDouble(monthNotes.get(i).getIncome()));
        }
        return incomes;
    }

}
