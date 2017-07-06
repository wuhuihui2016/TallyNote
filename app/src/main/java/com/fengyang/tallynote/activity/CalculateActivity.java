package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.CalculateAdapter;
import com.fengyang.tallynote.utils.StringUtils;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class CalculateActivity extends BaseActivity {

    private TextView cal_money, cal_ratio, cal_day, cal_finalIncome, cal_result, cal_del, calculate;
    private GridView numGridView;

    private int location = 1;
    private final int MONEY = 1, RATIO = 2, DAY = 3, INCOME = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("计算日收益", R.layout.activity_calculate);

        initView();

    }

    private void initView () {

        cal_money = (TextView) findViewById(R.id.cal_money);
        cal_ratio = (TextView) findViewById(R.id.cal_ratio);
        cal_day = (TextView) findViewById(R.id.cal_day);
        cal_finalIncome = (TextView) findViewById(R.id.cal_finalIncome);
        cal_result = (TextView) findViewById(R.id.cal_result);

        calculate = (TextView) findViewById(R.id.calculate);

        numGridView = (GridView) findViewById(R.id.numGridView);

        cal_del = (TextView) findViewById(R.id.cal_del);

        numGridView.setAdapter(new CalculateAdapter(context));
        numGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position <= 8) {
                    StringUtils.show1Toast(context, (position + 1) + "");
                    inputNum((position + 1) + "");
                } else if(position == 9) {
                    StringUtils.show1Toast(context, "00");
                    inputNum("00");
                } else if(position == 10) {
                    StringUtils.show1Toast(context, "0");
                    inputNum("0");
                } else if(position == 11) {
                    StringUtils.show1Toast(context, "clear");
                }
            }
        });

    }

    private void inputNum(String num) {
        switch (location) {
            case 1: if (num.equals("0"))cal_money.append(num);break;
            case 2: break;
            case 3: break;
            case 4: break;
        }
    }

    private boolean isStartWith0(String num) {
        switch (location) {
            case 1: if (num.equals("0"))cal_money.append(num);break;
            case 2: break;
            case 3: break;
            case 4: break;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.cal_money:
                location = MONEY;
                break;
            case R.id.cal_ratio:
                location = RATIO;
                break;
            case R.id.cal_day:
                location = DAY;
                break;
            case R.id.cal_finalIncome:
                location = INCOME;
                break;

            case R.id.cal_del://键盘删除
                break;
            case R.id.calculate://计算
                break;
        }
    }
}
