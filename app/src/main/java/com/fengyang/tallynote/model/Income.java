package com.fengyang.tallynote.model;

import java.io.Serializable;

/**
 * Created by wuhuihui on 2017/6/28.
 */
public class Income implements Serializable {

    String money; //投入金额（单位万）
    String incomeRatio; //预期年化（%）
    String days; //投资期限(天)
    String durtion; //投资时期
    String dayIncome; //拟日收益（万/天）
    String finalIncome; //最终收益
    String finalCash; //最终提现
    String finalCashGo; //提现去处
    int finished; //完成状态
    String remark;//理财备注
    String time; //记录时间

    public Income(String money, String incomeRatio, String days, String durtion, String dayIncome, String finalIncome, String finalCash, String finalCashGo, int finished, String remark, String time) {
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
        return "Income{" +
                "money='" + money + 
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
}
