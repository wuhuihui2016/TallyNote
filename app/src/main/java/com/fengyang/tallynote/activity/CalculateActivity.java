package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.CalculateAdapter;
import com.fengyang.tallynote.utils.KeyboardUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.text.DecimalFormat;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class CalculateActivity extends BaseActivity {

    private EditText cal_money, cal_ratio, cal_day, cal_finalIncome;
    private TextView cal_result;
    private GridView numGridView;
    private KeyboardUtils keyboardUtil;

    private int area = 1;
    private final int MONEY = 1, RATIO = 2, DAY = 3, INCOME = 4;

    private String moneyStr, ratioStr, dayStr, incomeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("计算日收益", R.layout.activity_calculate);

        initView();

    }

    private void initView () {

        cal_money = (EditText) findViewById(R.id.cal_money); cal_money.setOnTouchListener(new OnTouchListener(MONEY));
        cal_ratio = (EditText) findViewById(R.id.cal_ratio); cal_ratio.setOnTouchListener(new OnTouchListener(RATIO));
        cal_day = (EditText) findViewById(R.id.cal_day); cal_day.setOnTouchListener(new OnTouchListener(DAY));
        cal_finalIncome = (EditText) findViewById(R.id.cal_finalIncome); cal_finalIncome.setOnTouchListener(new OnTouchListener(INCOME));

        cal_result = (TextView) findViewById(R.id.cal_result);

        numGridView = (GridView) findViewById(R.id.numGridView);

        numGridView.setAdapter(new CalculateAdapter(context));
        numGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position <= 2) {
                    inputNum((position + 1) + "");
                } else if (position == 3) {
                    delNum();
                } else if (position >= 4 && position <= 6) {
                    inputNum((position) + "");
                } else if (position == 7) {
                    inputNum(".");
                } else if (position >= 8 && position <= 10) {
                    inputNum((position - 1) + "");
                } else if (position == 11) {
                    nextInput();
                } else if (position >= 12 && position <= 14) {
                    if (position == 12) inputNum("00");
                    if (position == 13) inputNum("0");
                    if (position == 14) clear();
                } else if (position == 15) {
                    calculate();
                }
            }
        });

        setRightBtnListener("比较", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CompareActivity.class);
                if (cal_result.getText().length() > 0) intent.putExtra("cal_result", cal_result.getText().toString());
                startActivity(intent);
            }
        });

    }
    /**
     * 输入时区域的设置
     * @param area
     */
    private void setArea(int area) {

        cal_money.setBackground(null); cal_money.clearFocus();
        cal_ratio.setBackground(null); cal_ratio.clearFocus();
        cal_day.setBackground(null); cal_day.clearFocus();
        cal_finalIncome.setBackground(null); cal_finalIncome.clearFocus();
        openNumKeyboard(true);

        this.area = area;
        switch (area) {
            case MONEY: setEditFocus(cal_money); break;
            case RATIO: setEditFocus(cal_ratio); break;
            case DAY: setEditFocus(cal_day); break;
            case INCOME: setEditFocus(cal_finalIncome); break;
        }
    }

    private void setEditFocus(EditText edit) {
        edit.setBackgroundResource(R.drawable.shape_input_bkg);
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        edit.setCursorVisible(true);
    }

    /**
     * 跳转下一个输入区
     */
    private void nextInput() {
        switch (area) {
            case MONEY: setArea(RATIO); break;
            case RATIO: setArea(DAY); break;
            case DAY: setArea(INCOME); break;
            case INCOME: setArea(MONEY); break;
        }
    }

    /**
     * 输入数字并显示数字在相应位置
     * @param num
     */
    private void inputNum(String num) {
        switch (area) {
            case MONEY: if (! isStartWith0(num)) cal_money.append(num); break;
            case RATIO: if (! isStartWith0(num)) cal_ratio.append(num); break;
            case DAY: if (! isStartWith0(num)) cal_day.append(num); break;
            case INCOME: if (! isStartWith0(num)) cal_finalIncome.append(num); break;
        }
    }

    /**
     * 判断当前是否可输入0，00
     * @param num
     * @return
     */
    private boolean isStartWith0(String num) {
        if (num.equals("0") || num.equals("00") || num.equals(".")) {
            getInputNum();
            if (area == MONEY && moneyStr.length() == 0) return true;
            if (area == RATIO && ratioStr.length() == 0) return true;
            if (area == DAY && dayStr.length() == 0) return true;
            if (area == INCOME && incomeStr.length() == 0) return true;
            if (num.equals(".")) {
                if (area == MONEY && moneyStr.contains(".")) return true;
                if (area == RATIO && ratioStr.contains(".")) return true;
                if (area == DAY && dayStr.contains(".")) return true;
                if (area == INCOME && incomeStr.contains(".")) return true;
            }
        }
        return false;
    }

    /**
     * 清除当前位置输入的所有数字
     * @return
     */
    private void clear() {
        switch (area) {
            case MONEY: cal_money.setText(""); break;
            case RATIO: cal_ratio.setText(""); break;
            case DAY: cal_day.setText(""); break;
            case INCOME: cal_finalIncome.setText(""); break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hide_nunkeyboard) openNumKeyboard(false);
    }

    private class OnTouchListener implements View.OnTouchListener {

        private int area;

        public OnTouchListener(int area) {
            this.area = area;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//            setArea(area);

            if(keyboardUtil == null){
                EditText editText = (EditText) v;
                keyboardUtil = new KeyboardUtils(activity, editText);
                keyboardUtil.hideSoftInputMethod();
                keyboardUtil.showKeyboard();
            }
            return false;
        }
    }

    /**
     * 逐个删除当前位置输入的数字
     * @return
     */
    private void delNum() {
        getInputNum();
        switch (area) {
            case MONEY:
                if (moneyStr.length() > 1) cal_money.setText(moneyStr.substring(0, moneyStr.length() - 1));
                else if (moneyStr.length() == 1) clear();
                break;

            case RATIO:
                if (ratioStr.length() > 1) cal_ratio.setText(ratioStr.substring(0, ratioStr.length() - 1));
                else if (ratioStr.length() == 1) clear();
                break;

            case DAY:
                if (dayStr.length() > 1) cal_day.setText(dayStr.substring(0, dayStr.length() - 1));
                else if (dayStr.length() == 1) clear();
                break;

            case INCOME:
                if (incomeStr.length() > 1) cal_finalIncome.setText(incomeStr.substring(0, incomeStr.length() - 1));
                else if (incomeStr.length() == 1) clear();
                break;
        }
    }

    /**
     * 分别获取当前输入区的数字
     */
    private void getInputNum () {
        moneyStr = cal_money.getText().toString();
        ratioStr = cal_ratio.getText().toString();
        dayStr = cal_day.getText().toString();
        incomeStr = cal_finalIncome.getText().toString();
    }

    /**
     * 计算结果
     */
    private void calculate() {
        getInputNum();
        if (moneyStr.length() > 0 && ratioStr.length() > 0 && dayStr.length() > 0 && incomeStr.length() > 0) {
            double money = Double.parseDouble(moneyStr);
            double ratio = Double.parseDouble(ratioStr);
            double day = Double.parseDouble(dayStr);
            double income = Double.parseDouble(incomeStr);
            double result = income / day / (money / 10000);
            DecimalFormat df = new DecimalFormat("0.000");
            cal_result.setText(df.format(result));
            openNumKeyboard(false);
        } else {
            ToastUtils.showToast(context, true, "请填入所有数值！");}
    }

    /**
     * 控制键盘的显示隐藏
     * @param open
     */
    private void openNumKeyboard (boolean open) {
        RelativeLayout nunkeyboard_layout = (RelativeLayout) findViewById(R.id.nunkeyboard_layout);

        Animation animation;
        LayoutAnimationController controller;
        if (open) {
            animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_show);
            nunkeyboard_layout.setVisibility(View.VISIBLE);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_hidden);
            nunkeyboard_layout.setVisibility(View.GONE);
        }
        controller = new LayoutAnimationController(animation);
        nunkeyboard_layout.setLayoutAnimation(controller);// 设置动画
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        openNumKeyboard(false);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(keyboardUtil.isShow()){
                keyboardUtil.hideKeyboard();
            }else{
                finish();
            }
        }
        return false;
    }
}
