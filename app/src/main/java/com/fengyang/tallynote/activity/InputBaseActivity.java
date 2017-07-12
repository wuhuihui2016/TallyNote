package com.fengyang.tallynote.activity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.KeyboardAdapter;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class InputBaseActivity extends BaseActivity {

    protected int area = 0, size = 0;
    protected RelativeLayout keyboard_layout;
    protected GridView keyboard;
    protected ICallBackFinished callBackFinished;
    protected List<EditText> editTexts = new ArrayList<>();
    protected List<String> contents = new ArrayList<>();

    /**
     * 重写方法，增加键盘布局
     * @param title
     * @param layoutID
     */
    @Override
    protected void setContentView(String title, int layoutID) {
        super.setContentView(title, layoutID);
        //加载键盘布局
        View view = LayoutInflater.from(this).inflate(R.layout.keyboard_layout, null);
        content_layout.addView(view);

        initView();
    }

    /**
     * 键盘布局初始化
     */
    private void initView () {

        keyboard = (GridView) findViewById(R.id.keyboard);

        keyboard.setAdapter(new KeyboardAdapter(context));
        keyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position <= 2) { // 1，2，3
                    inputNum((position + 1) + "");
                } else if (position == 3) { //删除键
                    delNum();
                } else if (position >= 4 && position <= 6) { //4，5，6
                    inputNum((position) + "");
                } else if (position == 7) { //小数点
                    inputNum(".");
                } else if (position >= 8 && position <= 10) { //7，8，9
                    inputNum((position - 1) + "");
                } else if (position == 11) { //下一个
                    nextInput();
                } else if (position >= 12 && position <= 14) {
                    if (position == 12) inputNum("00"); // 00
                    if (position == 13) inputNum("0");  //0
                    if (position == 14) clear(); // 清除
                } else if (position == 15) { //完成
                    //自定义
                    isShowKeyboard(false);
                    if (callBackFinished != null) {
                        callBackFinished.callback();
                    }
                }
            }
        });

    }

    /**
     * 设置界面中的数字输入的输入框触摸事件及输入完成的回调接口
     * @param callBackFinished
     */
    protected void setCallBack(ICallBackFinished callBackFinished) {
        for (int i = 0; i < editTexts.size(); i ++) {
            editTexts.get(i).setOnTouchListener(new OnTouchListener());
        }

        this.callBackFinished = callBackFinished;
    }

    /**
     * 设置输入框的触摸事件
     */
    private class OnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isShowKeyboard(true);
            EditText editText = (EditText) v;
            SystemUtils.hideSoftInputMethod(activity, editText);
            return false;
        }
    }

    /**
     * 输入完成回调
     */
    public interface ICallBackFinished {
        void callback();
    }

    /**
     * 跳转下一个输入区
     */
    private void nextInput() {
        getArea();
        if(area == size - 1) setEditFocus(editTexts.get(0));
        else setEditFocus(editTexts.get(area + 1));
    }

    /**
     * 获取当前光标所在位置
     */
    private void getArea() {
        size = editTexts.size();
        if (size > 1) {
            for (int i = 0; i < size; i ++) {
                if (editTexts.get(i).hasFocus()) {
                    area = i;
                }
            }
        }
    }

    /**
     * 设置某一输入框的焦点
     * @param edit
     */
    private void setEditFocus(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        edit.setCursorVisible(true);
    }

    /**
     * 输入数字并显示数字在相应位置
     * @param num
     */
    private void inputNum(String num) {
        if (! isStartWith0(num)) editTexts.get(area).append(num);
    }

    /**
     * 判断当前是否可输入0，00,.
     * @param num
     * @return
     */
    private boolean isStartWith0(String num) {
        getArea();
        if (num.equals("0") || num.equals("00") || num.equals(".")) {
            getInputNum();
            if (contents.get(area).length() == 0 || contents.get(area).contains(".")) return true;
        }
        return false;
    }

    /**
     * 清除当前位置输入的所有数字
     * @return
     */
    private void clear() {
        editTexts.get(area).setText("");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hidekeyboard) isShowKeyboard(false);
    }

    /**
     * 逐个删除当前位置输入的数字
     * @return
     */
    private void delNum() {
        getInputNum();
        String content = contents.get(area);
        if (content.length() > 1) editTexts.get(area).setText(content.substring(0, content.length() - 1));
        else if (content.length() == 1) clear();
        if (content.length() > 0) editTexts.get(area).setSelection(content.length() - 1);
    }

    /**
     * 分别获取当前输入区的数字
     */
    protected void getInputNum () {
        contents.clear();
        for (int i = 0; i < editTexts.size(); i ++) {
            contents.add(editTexts.get(i).getText().toString());
        }
    }

    /**
     * 控制键盘的显示隐藏
     * @param open
     */
    private void isShowKeyboard(boolean open) {
        keyboard_layout = (RelativeLayout) findViewById(R.id.keyboard_layout);

        Animation animation;
        LayoutAnimationController controller;
        if (open) {
            LogUtils.i(TAG, "isShowKeyboard");
            animation = AnimationUtils.loadAnimation(this, R.anim.keyboard_up);
            keyboard_layout.setVisibility(View.VISIBLE);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.keyboard_down);
            keyboard_layout.setVisibility(View.GONE);
        }
        controller = new LayoutAnimationController(animation);
        keyboard_layout.setLayoutAnimation(controller);// 设置动画
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isShowKeyboard(false);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (keyboard_layout.isShown()) isShowKeyboard(false);
        else super.onBackPressed();
    }
}
