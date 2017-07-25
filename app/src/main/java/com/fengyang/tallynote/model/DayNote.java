package com.fengyang.tallynote.model;

import java.io.Serializable;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNote implements Serializable {

    public static final int consume = 1, account_out = 2, account_in = 3;

    int useType; //消费类型：1.支出，2.转账，3.转入
    String money; //消费金额
    String remark; //消费说明
    String time; //消费时间

    public DayNote(int useType, String money, String remark, String time) {
        this.useType = useType;
        this.money = money;
        this.remark = remark;
        this.time = time;
    }

    public int getUseType() {
        return useType;
    }

    public void setUseType(int useType) {
        this.useType = useType;
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
                "useType=" + useType +
                ", money='" + money + 
                ", remark='" + remark + 
                ", time='" + time + 
                '}';
    }
}
