package com.fengyang.tallynote.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记事本
 * Created by wuhuihui on 2017/8/2.
 */
public class NotePad implements Serializable {

    private static final long serialVersionUID = 1L;

    private int tag; //标签
    private String words; //内容
    private String time; //时间

    public NotePad(int tag, String words, String time) {
        this.tag = tag;
        this.words = words;
        this.time = time;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NotePad{" +
                "tag=" + getTagList().get(tag) +
                ", words='" + words +
                ", time='" + time +
                '}';
    }


    /**
     * 标签集合 只能增，不能减，且顺序不可变
     * @return
     */
    public static List<String> getTagList() {
        List<String> tagList = new ArrayList<String>();
        tagList.add("生活琐事");
        tagList.add("突发奇想");
        tagList.add("励志感言");
        tagList.add("心情树洞");
        tagList.add("收藏分享");
        tagList.add("读书笔记");
        return tagList;
    }

    /**
     * 获取标签对应的int值
     * @param tagStr
     * @return
     */
    public static int getTag(String tagStr) {
        List<String> tagList = getTagList();
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).equals(tagStr)) {
                return i;
            }
        }
        return 0;
    }
}
