package com.fengyang.tallynote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * @author wuhuihui
 * @Title: ContansUtils
 * @Description: TODO 常量类
 * @date 2015年11月20日 下午4:24:40
 */
public class ContansUtils {

    private static final String TAG = "ContansUtils";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static final String PWD = "pwdKey";

    public static final int DAY = 0, MONTH = 1, INCOME = 2, DAY_HISTORY = 3, MEMO = 4, NOTEPAD = 5, ALL = 6;
    public static final String ACTION_DAY = "day";
    public static final String ACTION_MONTH = "month";
    public static final String ACTION_INCOME = "income";
    public static final String ACTION_NOTE = "notepad";
    public static final String ACTION_MEMO = "memo";

    /**
     * 设置存储空间，获取编辑器
     *
     * @param context
     * @return
     */
    public static void setPres(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key    键
     * @param object 要保存的值
     */
    public static void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key           键
     * @param defaultObject 默认值
     * @return Object 根据默认值，来确定返回值的具体类型
     */
    public static Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return preferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return preferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return preferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return preferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return preferences.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key 键
     */
    public static void remove(String key) {
        editor.remove(key).commit();
    }

    /**
     * 清除默认SharedPreferences数据
     */
    public static void clear() {
        editor.clear().commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key 键
     * @return 成功或失败
     */
    public static boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getAll() {
        return preferences.getAll();
    }

}
