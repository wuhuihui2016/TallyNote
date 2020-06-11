package com.whh.tallynote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.text.DecimalFormat;

import butterknife.BindView;

/**
 * 计算日收益
 * Created by wuhuihui on 2017/6/27.
 */
public class CalculateActivity extends BaseActivity {

    @BindView(R.id.cal_money)
    public EditText cal_money;

    @BindView(R.id.cal_ratio)
    public EditText cal_ratio;

    @BindView(R.id.cal_day)
    public EditText cal_day;

    @BindView(R.id.cal_finalIncome)
    public EditText cal_finalIncome;

    @BindView(R.id.cal_result)
    public TextView cal_result;

    @BindView(R.id.reset)
    public ImageButton reset;

    private String resultStr;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("计算日收益", R.layout.activity_calculate);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        setRightBtnListener("计算", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    /**
     * 计算结果
     */
    private void calculate() {
        SystemUtils.hideInput(activity);

        String moneyStr = cal_money.getText().toString();
        String rationStr = cal_ratio.getText().toString();
        String dayStr = cal_day.getText().toString();
        String finalIncomeStr = cal_finalIncome.getText().toString();

        if (moneyStr.length() == 0 || rationStr.length() == 0 || dayStr.length() == 0 || finalIncomeStr.length() == 0) {
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

        reset.setVisibility(View.VISIBLE);

        setRightBtnListener("比较", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.transfer(activity, CompareActivity.class, "cal_result", resultStr);
            }
        });

    }

    /**
     * 重置按钮点击事件
     *
     * @param v
     */
    public void reset(View v) {
        cal_money.setText("");
        cal_ratio.setText("");
        cal_day.setText("");
        cal_finalIncome.setText("");
        cal_result.setText("");
        reset.setVisibility(View.GONE);
        setRightBtnListener("计算", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }
}
