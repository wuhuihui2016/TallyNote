package com.fengyang.tallynote.model;

import java.io.Serializable;

/**
 * Created by fengyangtech on 2017/8/4.
 * 备忘录
 */
public class MemoNote implements Serializable{
    String content;
    int status;
    String time;

    public static int ON = 0, OFF = 1;

    public MemoNote(String content, String time) {
        this.content = content;
        this.time = time;
    }

    public MemoNote(String content, int status, String time) {
        this.content = content;
        this.status = status;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MemoNote{" +
                "content='" + content  +
                ", status=" + status +
                ", time='" + time  +
                '}';
    }
}
