package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.SystemUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.text.DecimalFormat;

/**
 * 计算日收益
 * Created by wuhuihui on 2017/6/27.
 */
public class CalculateActivity extends BaseActivity {

    private EditText cal_money, cal_ratio, cal_day, cal_finalIncome;
    private TextView cal_result;
    private String resultStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("计算日收益", R.layout.activity_calculate);

        initView();

    }

    private void initView() {

        cal_money = (EditText) findViewById(R.id.cal_money);
        cal_ratio = (EditText) findViewById(R.id.cal_ratio);
        cal_day = (EditText) findViewById(R.id.cal_day);
        cal_finalIncome = (EditText) findViewById(R.id.cal_finalIncome);

        cal_result = (TextView) findViewById(R.id.cal_result);

        setRightBtnListener("比较", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cal_result.getText().length() > 0) {
                    Intent intent = new Intent(activity, CompareActivity.class);
                    intent.putExtra("cal_result", resultStr);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 计算结果
     */
    public void calculate(View v) {
        SystemUtils.hideInput(activity);

        String moneyStr = cal_money.getText().toString();
        String rationStr = cal_ratio.getText().toString();
        String dayStr = cal_day.getText().toString();
        String finalIncomeStr = cal_finalIncome.getText().toString();

        if (moneyStr.length()  == 0 || rationStr.length()  == 0 || dayStr.length()  == 0 || finalIncomeStr.length()  == 0) {
            ToastUtils.showToast(context, true, "请填入所有数值！");
            return;
        }

        double money = Double.parseDouble(moneyStr);
        double ratio = Double.parseDouble(rationStr);
        double day = Double.parseDouble(dayStr);
        double income = Double.parseDouble(finalIncomeStr);
        double result = income / day / (money / 10000);
        DecimalFormat df = new DecimalFormat("0.000");
        resultStr = df.format(result);
        cal_result.setText(resultStr);
    }


}
