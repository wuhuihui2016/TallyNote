package com.fengyang.tallynote.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Title: DateUtils
 * @Description: TODO 时间格式工具类
 * @author wuhuihui
 * @date 2015年11月20日 下午4:39:26
 */
public class DateUtils {

	/*
	方法测试数据
	LogUtils.i("diffTime", DateUtils.diffTime("2017-1-12 12:30:31", "2017-1-12 12:24:31"));
    LogUtils.i("diffTime2", DateUtils.diffTime("2016-1-10 12:30:31"));
	*/

	private static SimpleDateFormat time_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat date_sdf = new SimpleDateFormat("yyyyMMdd");

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
	 * @return
	 */
	public static String formatDate4fileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		return sdf.format(new Date());
	}

	/**
	 * @Title: ComTimeDiff
	 * @Description: TODO 不同日
	 * @param lastTime 后一个时间 2016-08-30 12:30:31
	 * @param formeTime 前一个时间
	 * @return
	 * @return String
	 * @author wuhuihui
	 * @date 2016年7月7日 上午11:19:42 前一个和后一个
	 */
	public static String diffTime(String lastTime, String formeTime) {
		try {

			String year1 = lastTime.split("-")[0];//2016
			String year2 = formeTime.split("-")[0];
			String yMd = lastTime.split(" ")[0];
			String Md = yMd.split("-")[1] + "-" + yMd.split("-")[2];//月日 08-30
			String hms = lastTime.split(" ")[1];//时分秒 12:30:31
			String hm = hms.split(":")[0] + ":" + hms.split(":")[1];//时分 12:30

			if (TextUtils.isEmpty(formeTime)) {
				return hm;
			} else {

//			LogUtils.i("diffTime", "lastTime" + lastTime + "-formeTime" + formeTime);
//			LogUtils.i("diffTime", "年"  + year1 + "-日" + day1 + "-月日" + Md);
//			LogUtils.i("diffTime", "时分秒"  + hms + "-时分" + hm);

				if (! year1.equals(year2)) return diffTime(lastTime); //不同年
				else {//同年,判断是否同日
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(time_sdf.parse(lastTime));
					int day1 = calendar.get(Calendar.DAY_OF_YEAR);//在一年中第几天
					calendar.setTime(time_sdf.parse(formeTime));
					int day2 = calendar.get(Calendar.DAY_OF_YEAR);

					if (day1 == day2) {//同一天
						Date laster = time_sdf.parse(lastTime);
						Date former = time_sdf.parse(formeTime);
						long m = (laster.getTime() - former.getTime()) / (60 * 1000);
//							LogUtils.i("diffTime", lastTime + "-" + formeTime + "相差" + m + "分");
						if (m <= 5) {//相差小于5分不显示
							return "";
						} else {//相差大于5分则与当前时间比较
							return diffTime(lastTime);
						}
					} else {//不同一天则与当前时间比较
						return diffTime(lastTime);
					}

				}

			}
		} catch (Exception e) {}

		return "";
	}
	/**
	 * @Title: ComTimeDiff
	 * @Description: TODO 不同日
	 * @param time
	 * @return
	 * @return String
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

			if (! year1.equals(year2)) return time.split(" ")[0]; //不同年 2016-08-30
			else {//同年,判断是否同日
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(time_sdf.parse(now));
				int day1 = calendar.get(Calendar.DAY_OF_YEAR);//在一年中第几天
				calendar.setTime(time_sdf.parse(time));
				int day2 = calendar.get(Calendar.DAY_OF_YEAR);
				int d_value = day1 - day2;//天数差值
//				LogUtils.i("diffTime", day1 + "-" + day2 + "=" + d_value);
				if(d_value <= 2) {
					if (d_value == 2) return "前天 " + hm;//前天
					else if(d_value == 1)  return "昨天 " + hm;//昨天
					else if(d_value == 0) {
						Date laster = time_sdf.parse(now);
						Date former = time_sdf.parse(time);
						long m = (laster.getTime() - former.getTime()) / (60 * 1000);
						if (m >= 5) {//相差5分以上显示时分
							return hm;
						} else {//小于5分不显示
							return "刚刚";
						}
					}
				}  else {
					return Md + " " + hm; //08-30 12:30
				}
			}

		} catch (Exception e) {}

		return "";
	}

	/**
	 * 计算某个日期距离当天的天数
	 * @param date
	 * @return
     */
	public static final int daysBetween(String date) {
		int days = 0;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date_sdf.parse(date));
			long time1 = cal.getTimeInMillis();
			cal.setTime(date_sdf.parse(formatDate()));
			long time2 = cal.getTimeInMillis();
			long between_days = (time1 - time2) / ( 1000 * 3600 * 24);
			days = Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * @Title: getTimeFormat
	 * @Description: TODO 将毫秒转为00:00:00/00:00秒格式(适用于计算睡眠时间)
	 * @param time 毫秒
	 * @return
	 * @return String
	 * @author wuhuihui
	 * @date 2016年5月12日 上午11:13:54
	 */
	public static String getTimeFormat(int time) {
		String timeStr = "";
		int s = time/1000;   //秒
		int h = s / 3600;    //求整数部分 ，小时
		int r = s % 3600;    //求余数
		int m = 0;
		if(r > 0) {
			m = r / 60;    //分
			r = r % 60;    //求分后的余数，即为秒
		}
		if(h < 10) {
			timeStr = "0" + h + ":";
			if (h == 0) {
				timeStr = "";
			}
		} else {
			timeStr = h + ":";
		}

		if(m < 10) {
			timeStr = timeStr + "0" + m + ":";
		} else {
			timeStr = timeStr + m + ":";
		}

		if(r < 10) {
			timeStr = timeStr + "0" + r;
		} else {
			timeStr = timeStr + r;
		}
		return timeStr;
	}


}
