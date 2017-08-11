package com.fengyang.tallynote.model;

import com.fengyang.tallynote.database.DayNoteDao;

import java.io.Serializable;
import java.util.ArrayList;
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

    String duration; //消费时段，用于区别显示每月消费

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
     * 消费类型集合
     * @return
     */
    public static List<String> getUserTypes() {
        List<String> types = new ArrayList<>();
        types.add("支出：");
        types.add("转账：");
        types.add("转入：");
        return types;
    }

    /**
     * 返回消费类型
     * @param useType
     * @return
     */
    public static String getUserTypeStr(int useType) {
        return getUserTypes().get(useType - 1);
    }

    /**
     * 总消费
     *
     * @return
     */
    public static Double getAllSum() {
        List<DayNote> dayNotes = DayNoteDao.getDayNotes();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.account_in)
                sum -= Double.parseDouble(dayNotes.get(i).getMoney());
            else sum += Double.parseDouble(dayNotes.get(i).getMoney());
        }
        return sum;
    }

    /**
     * 某一时段的总消费
     *
     * @return
     */
    public static Double getAllSum(String duration) {
        List<DayNote> dayNotes = DayNoteDao.getDayNotes4History(duration);
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == DayNote.account_in)
                sum -= Double.parseDouble(dayNotes.get(i).getMoney());
            else sum += Double.parseDouble(dayNotes.get(i).getMoney());
        }
        return sum;
    }

}
