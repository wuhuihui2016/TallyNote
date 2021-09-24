package com.whh.tallynote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.whh.tallynote.database.DBCommon;

import java.util.Map;

/**
 * @author wuhuihui
 * @Title: ContansUtils
 * @Description: TODO 常量类
 * @date 2015年11月20日 下午4:24:40
 */
public class ContansUtils {

    public static final String TAG = "ContansUtils";
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static final int DAY = 0, MONTH = 1, INCOME = 2, DAY_HISTORY = 3, MEMO = 4, NOTEPAD = 5, ALL = 6;
    public static final String ACTION_DAY = "day";
    public static final String ACTION_MONTH = "month";
    public static final String ACTION_INCOME = "income";
    public static final String ACTION_NOTE = "notepad";
    public static final String ACTION_MEMO = "memo";

    public static final String NICKNAME = "nickName"; //登录时设置的昵称，用于显示在个人主页
    public static final String PHONENUM = "phoneNum"; //登录时设置的手机号码，用于显示在个人主页
    public static final String SECRETKEY = "secretKey"; //注册时设置的密钥，作为找回密码的钥匙，需要加密存储

    public static final String CHECKWAY = "checkWay"; //验证方式
    public static final String PWDKEY = "pwdKey"; //验证密码，进入应用需要验证的密码
    public static final String GESTURE = "gesture"; //手势密码
    public static final String RESETPWD = "resetPwd"; //重置密码

    public static final String ISBACK = "isBack"; //是否在后台运行的标志
    public static boolean pauseCheckBack = false; //是否中止判断应用的前后台运行，如果中止，用完需恢复状态位
    public static final String INCOMEREMINDER = "incomeReminder"; //记录当天日期，用于理财到期通知，当天仅显示一次通知
    public static final String COUNTER = "counter"; //计数器功能
    public static final String VERSION = "version"; //记录当前版本


    //文件名(最初的文件名)
    public static final String day_file0 = "day_note_", month_file0 = "month_note_",
            income_file0 = "income_note_", day_history_file0 = "day_note_history_",
            memo_file0 = "memo_note_", notepad_file0 = "notepad_", tallynote_file0 = "tally_note_";

    //文件名(20210917 add DBVersion to index update database)
    public static final String day_file = day_file0 + DBCommon.VERSION + "_",
            month_file = month_file0 + DBCommon.VERSION + "_",
            income_file = income_file0 + DBCommon.VERSION + "_",
            day_history_file = day_history_file0 + DBCommon.VERSION + "_",
            memo_file = memo_file0 + DBCommon.VERSION + "_",
            notepad_file = notepad_file0 + DBCommon.VERSION + "_",
            tallynote_file = tallynote_file0 + DBCommon.VERSION + "_";

    //表单名
    public static final String day_sheetName = "日账单", month_sheetName = "月账单", income_sheetName = "理财记录",
            day_history_sheetName = "历史日账单", memo_sheetName = "备忘录", notepad_sheetName = "记事本";

    //表单头部标题
    public static String[] dayTitle = {"消费类型", "金额（元）", "消费明细", "消费时间"};
    public static String[] dayHistoryTitle = {"消费类型", "金额（元）", "消费明细", "消费时间", "消费时段"};
    public static String[] monthTitle = {"上次结余（元）", "本次支出（元）", "本次工资（元）", "本次收益（元）", "本次结余（元）", "实际结余（元）",
            "月结时段", "月结说明", "记录时间"};
    public static String[] incomeTitle = {"投入金额(万元)", "预期年化（%）", "投资期限（天）", "投资时段", "拟日收益（元/万天）", "最终收益（元）",
            "最终提现（元）", "提现去处", "完成状态", "投资说明", "记录时间"};
    public static String[] memoTitle = {"内容", "状态", "记录时间"};
    public static String[] notepadTitle = {"标签", "图片数量", "图片1", "图片2", "图片3", "内容", "记录时间"};

    /**
     * 是否为手势验证方式
     * @return
     */
    public static boolean getCheckWayIsGesture() {
        if (contains(CHECKWAY)) {
            if (get(CHECKWAY, "").equals(GESTURE))
                return true;
        }
        return false;
    }

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
