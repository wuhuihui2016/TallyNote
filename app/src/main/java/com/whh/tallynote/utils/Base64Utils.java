package com.whh.tallynote.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Base64 工具类
 */
public class Base64Utils {

    /**
     * Base64编码
     *
     * @param input
     * @return
     */
    public static byte[] encode(byte[] input) {
        if (input == null) return null;
        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * Base64编码后转String
     *
     * @param input
     * @return
     */
    public static String encodeToString(String input) {
        if (input == null) return null;
        return Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input
     * @return
     */
    public static byte[] decode(byte[] input) {
        if (input == null) return null;
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input
     * @return
     */
    public static byte[] decode(String input) {
        if (input == null) return null;
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码后转String
     * @param input
     * @return
     */
    public static String decodeToString(String input) {
        if (input == null) return null;
        return new String(Base64.decode(input, Base64.NO_WRAP));
    }

    public static Bitmap decode2Bitmap(byte[] bytes) {
        bytes = Base64Utils.decode(bytes); //解码
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static void test() {
        String words = "会飞的小盆友~~";
        LogUtils.e("whh0922", words);
        String encodeToString = Base64Utils.encodeToString(words);
        LogUtils.e("whh0922", "加密：" + words + " ==> " + encodeToString);
        LogUtils.e("whh0922", "解密：" + words + " ==> " + Base64Utils.decodeToString(encodeToString));
    }
}
