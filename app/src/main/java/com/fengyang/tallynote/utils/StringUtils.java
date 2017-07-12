package com.fengyang.tallynote.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class StringUtils {

    private static String TAG = "StringUtils";

    /**
     * 验证输入的身份证号是否合法
     */
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }


    //价格显示标准
    public static String formatePrice(String price){
        if (! TextUtils.isEmpty(price)) {
            double d = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        } else {
            return "";
        }
    }

    //价格显示标准
    public static String showPrice(String price) {
        if (! TextUtils.isEmpty(formatePrice(price))) {
            return "¥ " + formatePrice(price) + " 元";
        } else {
            return "无";
        }
    }



}
