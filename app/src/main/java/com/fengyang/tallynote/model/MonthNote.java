package com.fengyang.tallynote.model;

import com.fengyang.tallynote.database.MonthNoteDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MonthNote implements Serializable {

    String last_balance; //上次结余
    String pay; //本次支出
    String salary; //本次工资
    String income; //本次收益
    String homeuse; //家用补贴
    String balance; //本次结余
    String actual_balance; //实际结余
    String duration; //月结时段
    String remark; //月结说明
    String time; //记录时间

    public MonthNote(String last_balance, String pay,
                     String salary, String income, String homeuse,
                     String balance, String actual_balance, String duration,
                     String remark, String time) {
        this.last_balance = last_balance;
        this.pay = pay;
        this.salary = salary;
        this.income = income;
        this.homeuse = homeuse;
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

    public String getHomeuse() {
        return homeuse;
    }

    public void setHomeuse(String homeuse) {
        this.homeuse = homeuse;
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
                ", homeuse='" + homeuse +
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
        if (MonthNoteDao.getMonthNotes().size() > 0)
            return MonthNoteDao.getMonthNotes().get(MonthNoteDao.getMonthNotes().size() - 1).getDuration().split("-")[1];
        else return null;
    }

    /**
     * 获取所有月账的时段
     * @return
     */
    public static List<String> getDurations() {
        List<String> durations = new ArrayList<>();
        List<MonthNote> monthNotes = MonthNoteDao.getMonthNotes();
        for (int i = 0; i < monthNotes.size(); i++) {
            durations.add(monthNotes.get(i).getDuration());
        }
        return durations;
    }

}
