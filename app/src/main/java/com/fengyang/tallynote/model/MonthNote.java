package com.fengyang.tallynote.model;

import java.io.Serializable;

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
    String duration; //月结期间
    String remark; //月结备注
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
                "last_balance='" + last_balance  +
                ", pay='" + pay  +
                ", salary='" + salary  +
                ", income='" + income  +
                ", homeuse='" + homeuse  +
                ", balance='" + balance  +
                ", actual_balance='" + actual_balance  +
                ", duration='" + duration  +
                ", remark='" + remark  +
                ", time='" + time  +
                '}';
    }
}
