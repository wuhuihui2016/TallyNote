package com.fengyang.tallynote.model;

import com.fengyang.tallynote.utils.ContansUtils;

/**
 * 搜索(1.日账检索,2.历史日账检索,3.备忘录检索,4.记事本检索)
 * Created by wuhuihui on 2017/8/11.
 */
public class SearchNote {
    int type;
    Object object;

    public SearchNote(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    /**
     * 获取当前检索到的日账/备忘录/记事本的时间
     * 用于列表中显示按时间排序，最新的在列表的上面
     *
     * @return
     */
    public String getTime() {
        String time = null;
        if (getType() == ContansUtils.DAY) {
            return ((DayNote) object).getTime();
        } else if (getType() == ContansUtils.MEMO) {
            return ((MemoNote) object).getTime();
        } else if (getType() == ContansUtils.NOTEPAD) {
            return ((NotePad) object).getTime();
        }
        return time;
    }

    @Override
    public String toString() {
        return "SearchNote{" +
                "type=" + type +
                ", object=" + object +
                ", time=" + getTime() +
                '}';
    }
}
