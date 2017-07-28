package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by wuhuihui on 2017/7/27.
 */
public class ViewUtils {

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    private static void setBackgroundAlpha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }

    /**
     * 设置popupWindow参数
     *
     * @param context
     * @param popupWindow
     */
    public static void setPopupWindow(final Context context, PopupWindow popupWindow) {
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);
        setBackgroundAlpha(context, 0.8f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(context, 1.0f);
            }
        });
    }


    /**
     * 时间选择器
     */
    public static DatePickerDialog datePickerDialog;

    public static void showDatePickerDialog(Activity activity, TextView textView) {
        try {
            datePickerDialog = new DatePickerDialog(activity, new MyDateListener(activity, textView),
                    DateUtils.calendar.get(Calendar.YEAR), DateUtils.calendar.get(Calendar.MONTH), DateUtils.calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("选择日期");

            String curDuration = textView.getText().toString();
            if (curDuration.length() > 0 && curDuration.endsWith("-")) {
                Calendar cal = DateUtils.getAfterDate(curDuration.substring(0, curDuration.length() - 1), 20);
                datePickerDialog = new DatePickerDialog(activity, new MyDateListener(activity, textView),
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setMessage("已选起始日期："
                        + curDuration.substring(0, curDuration.length() - 1) + "\n请选择终止日期");
            } else datePickerDialog.setMessage("请选择起始日期");

            datePickerDialog.show();
        } catch (Exception e) {
        }
    }

    //日期选择监听
    private static class MyDateListener implements DatePickerDialog.OnDateSetListener {

        private Activity activity;
        private TextView textView;

        public MyDateListener(Activity activity, TextView textView) {
            this.activity = activity;
            this.textView = textView;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String month;
            if (monthOfYear < 10) month = "0" + monthOfYear;
            else month = "" + monthOfYear;
            String day;
            if (dayOfMonth < 10) day = "0" + dayOfMonth;
            else day = "" + dayOfMonth;

            String curDuration = textView.getText().toString();
            if (curDuration.length() > 0) {
                if (curDuration.endsWith("-")) {
                    String startTime = curDuration.substring(0, curDuration.length() - 1);
                    String endTime = year + month + day;
                    if (DateUtils.getDaysBetween(startTime, endTime) >= 20) {//起止日期必须间隔20天及以上
                        textView.setText(curDuration + endTime);
                    } else {
                        ToastUtils.showWarningLong(activity, "起止日期必须间隔20天及以上，请重新选择起始日期");
                        textView.setText("");
                        showDatePickerDialog(activity, textView);
                    }
                } else {
                    textView.setText(year + month + day + "-");
                    showDatePickerDialog(activity, textView);
                }
            } else {
                textView.setText(year + month + day + "-");
                showDatePickerDialog(activity, textView);
            }
        }
    }

}
