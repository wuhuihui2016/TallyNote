package com.whh.tallynote.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class StringUtils {

    private static String TAG = "StringUtils";

    //价格显示标准
    public static String formatePrice(String price) {
        if (!TextUtils.isEmpty(price)) {
            double d = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        } else {
            return "";
        }
    }

    //价格显示标准
    public static String showPrice(String price) {
        if (!TextUtils.isEmpty(formatePrice(price))) {
            return "¥ " + formatePrice(price) + " 元";
        } else {
            return "无";
        }
    }

    /**
     * 验证手机号（简单）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isPhone(final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches("^[1]\\d{10}$", input);
    }


}
