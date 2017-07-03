package com.fengyang.tallynote.model;

import java.io.Serializable;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNote implements Serializable {

    String usage; //消费用途
    String money; //消费金额
    String remark; //消费备注
    String time; //消费时间

    public DayNote(String usage, String money, String remark, String time) {
        this.usage = usage;
        this.money = money;
        this.remark = remark;
        this.time = time;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
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
        return "DayNote{" +
                "usage='" + usage  +
                ", money=" + money +
                ", remark='" + remark  +
                ", time='" + time  +
                '}';
    }
}
