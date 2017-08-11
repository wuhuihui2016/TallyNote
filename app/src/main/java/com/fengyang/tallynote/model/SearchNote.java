package com.fengyang.tallynote.model;

/**
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

    @Override
    public String toString() {
        return "SearchNote{" +
                "type=" + type +
                ", object=" + object +
                '}';
    }
}
