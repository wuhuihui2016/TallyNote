package com.fengyang.tallynote.model;

import com.fengyang.tallynote.database.MemoNoteDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static List<MemoNote> getUnFinish(){
        List<MemoNote> unFinishMemoNotes = new ArrayList<>();
        List<MemoNote> memoNotes = MemoNoteDao.getMemoNotes();
        for (int i = 0; i < memoNotes.size(); i++) {
            if (memoNotes.get(i).getStatus() == ON) {
                unFinishMemoNotes.add(memoNotes.get(i));
            }
        }
        Collections.reverse(unFinishMemoNotes);
        return unFinishMemoNotes;
    }

    public static List<MemoNote> getFinished(){
        List<MemoNote> unFinishMemoNotes = new ArrayList<>();
        List<MemoNote> memoNotes = MemoNoteDao.getMemoNotes();
        for (int i = 0; i < memoNotes.size(); i++) {
            if (memoNotes.get(i).getStatus() == OFF) {
                unFinishMemoNotes.add(memoNotes.get(i));
            }
        }
        Collections.reverse(unFinishMemoNotes);
        return unFinishMemoNotes;
    }
}
