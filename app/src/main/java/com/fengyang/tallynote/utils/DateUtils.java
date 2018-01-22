package com.fengyang.tallynote.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wuhuihui
 * @Title: DateUtils
 * @Description: TODO 时间格式工具类
 * @date 2015年11月20日 下午4:39:26
 */
public class DateUtils {

    private static final String TAG = "DateUtils";

	/*
    方法测试数据
	LogUtils.i("diffTime", DateUtils.diffTime("2017-1-12 12:30:31", "2017-1-12 12:24:31"));
    LogUtils.i("diffTime2", DateUtils.diffTime("2016-1-10 12:30:31"));
	*/

    public static Calendar calendar = Calendar.getInstance();

    private static SimpleDateFormat time_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat date_sdf = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat show_date_sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat show_date_sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    /**
     * 日期格式
     */
    public static String formatDate() {
        return date_sdf.format(new Date());
    }

    /**
     * 时间格式
     */
    public static String formatDateTime() {
        return time_sdf.format(new Date());
    }

    /**
     * 用时间定义文件名
     *
     * @return
     */
    public static String formatDate4fileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        return sdf.format(new Date());
    }

    /*获取当前日期*/
    public static String getDate() {
        String date = show_date_sdf1.format(new Date());
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1: date += " 星期日"; break;
            case 2: date += " 星期一"; break;
            case 3: date += " 星期二"; break;
            case 4: date += " 星期三"; break;
            case 5: date += " 星期四"; break;
            case 6: date += " 星期五"; break;
            case 7: date += " 星期六"; break;
            default: date += ""; break;
        }

        //获取农历
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        LogUtils.i("日期", year + "-" + month + "-" + day);

        return date + "\n" + LunarCalendarUtils.getLunarYearMonthDay(year, month, day);
    }


    /**
     * @param time
     * @return String
     * @Title: ComTimeDiff
     * @Description: TODO 不同日
     * @author wuhuihui
     * @date 2016年7月7日 上午11:19:42 前一个和后一个
     */
    public static String diffTime(String time) {
        try {
//			LogUtils.i("time", time);
            String now = DateUtils.formatDateTime();//获取当前时间
            String year1 = now.split("-")[0];//2016
            String year2 = time.split("-")[0];
            String yMd = time.split(" ")[0];
            String Md = yMd.split("-")[1] + "-" + yMd.split("-")[2];//月日 08-30
            String hms = time.split(" ")[1];//时分秒 12:30:31
            String hm = hms.split(":")[0] + ":" + hms.split(":")[1];//时分 12:30

//			LogUtils.i("diffTime", "lastTime" + lastTime + "-formeTime" + formeTime);
//			LogUtils.i("diffTime", "年"  + year1 + "-日" + day1 + "-月日" + Md);
//			LogUtils.i("diffTime", "时分秒"  + hms + "-时分" + hm);

            if (!year1.equals(year2)) return Md + "\n" + year2; //不同年 08-30\n2016-
            else {//同年,判断是否同日
                calendar.setTime(time_sdf.parse(now));
                int day1 = calendar.get(Calendar.DAY_OF_YEAR);//在一年中第几天
                calendar.setTime(time_sdf.parse(time));
                int day2 = calendar.get(Calendar.DAY_OF_YEAR);
                int d_value = day1 - day2;//天数差值
//				LogUtils.i("diffTime", day1 + "-" + day2 + "=" + d_value);
                if (d_value <= 2) {
                    if (d_value == 2) return "前天";//前天
                    else if (d_value == 1) return "昨天";//昨天
                    else if (d_value == 0) {
                        Date laster = time_sdf.parse(now);
                        Date former = time_sdf.parse(time);
                        long m = (laster.getTime() - former.getTime()) / (60 * 1000);
                        if (m >= 5) {//相差5分以上显示时分
                            return hm;
                        } else {//小于5分不显示
                            return "刚刚";
                        }
                    }
                } else {
                    return Md; //08-30
                }
            }

        } catch (Exception e) {
            LogUtils.e(TAG + "-diffTime", e.toString());
        }

        return "";
    }

    /**
     * 详情页面显示时间
     *
     * @param time
     * @return
     */
    public static String showTime4Detail(String time) {
        Date date = null;
        try {
            date = time_sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return show_date_sdf2.format(date);
    }

    /**
     * 计算某个日期距离当天的天数
     *
     * @param date
     * @return
     */
    public static final int daysBetween(String date) {
        int days = 0;
        try {
            calendar.setTime(date_sdf.parse(date));
            long time1 = calendar.getTimeInMillis();
            calendar.setTime(date_sdf.parse(formatDate()));
            long time2 = calendar.getTimeInMillis();
            long between_days = (time1 - time2) / (1000 * 3600 * 24);
            days = Integer.parseInt(String.valueOf(between_days));
        } catch (Exception e) {
            LogUtils.e(TAG + "-daysBetween", e.toString());
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 计算两日时段隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDaysBetween(String startDate, String endDate) {
        int days = 0;
        try {
            calendar.setTime(date_sdf.parse(startDate));
            long time1 = calendar.getTimeInMillis();
            calendar.setTime(date_sdf.parse(endDate));
            long time2 = calendar.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            days = Integer.parseInt(String.valueOf(between_days));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 计算某个日期几天后的日期
     *
     * @param startDate
     * @param days
     */
    public static Calendar getAfterDate(String startDate, int days) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_sdf.parse(startDate));
        calendar.add(Calendar.DATE, days);
        return calendar;
    }

    /**
     * 判断某一日期是否在dateStr日期之后
     *
     * @param dateStr
     * @param currDateStr
     * @return
     */
    public static boolean checkAfterDate(String dateStr, String currDateStr) {
        boolean flag = false;
        try {
            Date date = date_sdf.parse(dateStr);
            Date currDate = date_sdf.parse(currDateStr);
            if (currDate.after(date)) flag = true;
            else flag = false;
        } catch (Exception e) {
            LogUtils.e(TAG + "-checkAfterDate", e.toString());
            e.printStackTrace();
        }

        return flag;
    }

}
