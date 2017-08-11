package com.fengyang.tallynote.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.IncomeListActivity;
import com.fengyang.tallynote.database.IncomeNoteDao;
import com.fengyang.tallynote.model.IncomeNote;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wuhuihui
 * @Title: NotificationUtils
 * 通知工具类
 */
public class NotificationUtils {

    public static final String TAG = "NotificationUtils";

    public static NotificationManager notifyManager;
    public static int requestCode = 0;

    /**
     * 理财到期提醒
     *
     * @param context
     */
    public static void notifyIncome(Context context) {
        if (isNeedInComeReminder()) {
            IncomeNote lastIncomeNote = IncomeNote.getLastIncomeNote();
            if (lastIncomeNote != null) {
                String title = "理财到期提醒,收益：" + StringUtils.showPrice(lastIncomeNote.getFinalIncome());
                int day = DateUtils.daysBetween(lastIncomeNote.getDurtion().split("-")[1]);
                String message;
                if (day < 0) {
                    message = "已经结束,请完成!";
                } else if (day == 0) {
                    message = "今日到期！可完成!";
                } else {
                    message = lastIncomeNote.getDurtion().split("-")[1].substring(4, 8) + "到期,还剩 " + day + " 天";
                }

                //显示通知
                notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                //设置点击事件
                Intent intent = new Intent(context, IncomeListActivity.class);
                intent.putExtra("position", IncomeNoteDao.getIncomes().size() - Integer.parseInt(lastIncomeNote.getId()));
                intent.addCategory(Intent.CATEGORY_LAUNCHER);//跳转到当前运行的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                builder.setContentTitle(title).setContentText(message).setAutoCancel(true).
                        setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher);

                Notification notification = builder.build();
                notification.icon = R.drawable.ic_launcher;
                notification.defaults = Notification.DEFAULT_SOUND;//声音

                setMIUICount(notification);
                notifyManager.notify(requestCode, notification);//发送通知
            }

        }
    }

    /**
     * 取消通知
     */
    public static void cancel() {
        if (notifyManager != null) {
            notifyManager.cancel(requestCode);
        }
    }

    /**
     * 设置小米图标的实未读消息数
     *
     * @param notification
     */
    private static void setMIUICount(Notification notification) {
        try {
            @SuppressWarnings("rawtypes")
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, 1);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
        } catch (Exception e) {
            LogUtils.e(TAG + "-setMIUICount", e.toString());
        }

    }

    /**
     * 判断是否需要提醒（理财到期提醒），确保一天提醒一次
     */
    private static boolean isNeedInComeReminder() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        //获取上次提醒时间
        String lastTime = (String) ContansUtils.get("incomeReminder", "");

        //写入当前日期
        String nowTime = sdf.format(new Date());
        ContansUtils.put("incomeReminder", nowTime);

        if (!TextUtils.isEmpty(lastTime)) {
            if (nowTime.equals(lastTime)) return false;
            else return true;//不同日期，去提示
        } else {
            return true;//从未提醒过，去提示
        }

    }

}
