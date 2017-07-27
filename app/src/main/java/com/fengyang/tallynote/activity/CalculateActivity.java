package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.ToastUtils;

import java.text.DecimalFormat;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class CalculateActivity extends InputBaseActivity {

    private TextView cal_result;
    private String resultStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("计算日收益", R.layout.activity_calculate);

        initView();

    }

    private void initView() {

        editTexts.add((EditText) findViewById(R.id.cal_money));
        editTexts.add((EditText) findViewById(R.id.cal_ratio));
        editTexts.add((EditText) findViewById(R.id.cal_day));
        editTexts.add((EditText) findViewById(R.id.cal_finalIncome));

        setCallBack(new ICallBackFinished() {
            @Override
            public void callback() {
                calculate();
            }
        });

        cal_result = (TextView) findViewById(R.id.cal_result);

        setRightBtnListener("比较", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CompareActivity.class);
                if (cal_result.getText().length() > 0) intent.putExtra("cal_result", resultStr);
                startActivity(intent);
            }
        });

    }

    /**
     * 计算结果
     */
    private void calculate() {
        getInputNum();
        for (int i = 0; i < size; i++) {
            if (contents.get(i).length() > 0) continue;
            else ToastUtils.showToast(context, true, "请填入所有数值！");
            return;
        }
        double money = Double.parseDouble(contents.get(0));
        double ratio = Double.parseDouble(contents.get(1));
        double day = Double.parseDouble(contents.get(2));
        double income = Double.parseDouble(contents.get(3));
        double result = income / day / (money / 10000);
        DecimalFormat df = new DecimalFormat("0.000");
        resultStr = df.format(result);
        cal_result.setText(resultStr);
    }


}
