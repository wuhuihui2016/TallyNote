package com.fengyang.tallynote.model;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class DayNote implements Serializable {

    public static final int consume = 1, account_out = 2, account_in = 3;

    int useType; //消费类型：1.支出，2.转账，3.转入
    String money; //消费金额
    String remark; //消费说明
    String time; //消费时间

    String duration; //消费区间，用于区别显示每月消费

    public DayNote(int useType, String money, String remark, String time) {
        this.useType = useType;
        this.money = money;
        this.remark = remark;
        this.time = time;
    }

    public DayNote(int useType, String money, String remark, String time, String duration) {
        this.useType = useType;
        this.money = money;
        this.remark = remark;
        this.time = time;
        this.duration = duration;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "DayNote{" +
                "useType=" + useType +
                ", money='" + money + 
                ", remark='" + remark + 
                ", time='" + time + 
                ", duration='" + duration + 
                '}';
    }

    /**
     * 总消费
     * @return
     */
    public static String getAllSum() {
        List<DayNote> dayNotes = MyApp.utils.getDayNotes();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.consume)
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_out)
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_in)
                sum -= Double.parseDouble(dayNotes.get(i).getMoney());
        }
        return StringUtils.showPrice(sum + "");
    }

    /**
     * 某一时段的总消费
     * @return
     */
    public static String getAllSum(String duration) {
        List<DayNote> dayNotes = MyApp.utils.getDayNotes4History(duration);
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.consume)
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_out)
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            if (dayNotes.get(i).getUseType() == DayNote.account_in)
                sum -= Double.parseDouble(dayNotes.get(i).getMoney());
        }
        return StringUtils.showPrice(sum + "");
    }

}
