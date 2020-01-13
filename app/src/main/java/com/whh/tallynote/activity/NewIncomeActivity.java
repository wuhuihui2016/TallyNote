package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.database.IncomeNoteDao;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

/**
 * 记理财
 */
public class NewIncomeActivity extends BaseActivity {

    private EditText moneyEt, incomeRatioEt, daysEt, finalIncomeEt, remarkEt;
    private TextView durationEt, dayIncomeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("记理财", R.layout.activity_income);

        initView();
    }

    private void initView() {

        //新建理财显示新的编码
        TextView incomeNum = (TextView) findViewById(R.id.incomeNum);
        incomeNum.setText(IncomeNote.getNewIncomeID());

        moneyEt = (EditText) findViewById(R.id.moneyEt);
        incomeRatioEt = (EditText) findViewById(R.id.incomeRatioEt);
        daysEt = (EditText) findViewById(R.id.daysEt);
        finalIncomeEt = (EditText) findViewById(R.id.finalIncomeEt);
        dayIncomeEt = (TextView) findViewById(R.id.dayIncomeEt);
        durationEt = (TextView) findViewById(R.id.durtionEt);
        remarkEt = (EditText) findViewById(R.id.remarkEt);

        setRightBtnListener("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = StringUtils.formatePrice(moneyEt.getText().toString());
                String incomeRatio = incomeRatioEt.getText().toString();
                String days = daysEt.getText().toString();
                String dayIncome = StringUtils.formatePrice(dayIncomeEt.getText().toString());
                String finalIncome = StringUtils.formatePrice(finalIncomeEt.getText().toString());
                String duration = durationEt.getText().toString();
                String remark = remarkEt.getText().toString();

                if (!TextUtils.isEmpty(money) && !TextUtils.isEmpty(incomeRatio) &&
                        !TextUtils.isEmpty(days) && !TextUtils.isEmpty(dayIncome) &&
                        !TextUtils.isEmpty(finalIncome) && !TextUtils.isEmpty(duration)) {
                    final IncomeNote incomeNote = new IncomeNote(money, incomeRatio, days, duration, dayIncome, finalIncome,
                            null, null, 0, remark, DateUtils.formatDateTime());
                    LogUtils.i("commit", incomeNote.toString());
                    DialogUtils.showMsgDialog(activity, "提交理财\n"
                                    +"投入金额：" + StringUtils.showPrice(incomeNote.getMoney()) +
                                    "\n预期年化：" + incomeNote.getIncomeRatio() +
                                    " %\n投资期限：" + incomeNote.getDays() +
                                    " 天\n投资时段：" + incomeNote.getDurtion() +
                                    " \n拟日收益：" + incomeNote.getDayIncome() +
                                    " 元/万/天\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) +
                                    "\n投资说明：" + incomeNote.getRemark(),
                            "提交", new DialogListener() {
                                @Override
                                public void onClick() {
                                    if (IncomeNoteDao.newINote(incomeNote)) {
                                        ToastUtils.showSucessLong(activity, "理财提交成功！");
                                        ExcelUtils.exportIncomeNote(null);
                                        if (getIntent().hasExtra("list")) {
                                            EventBus.getDefault().post(ContansUtils.ACTION_INCOME);
                                        } else {
                                            startActivity(new Intent(activity, IncomeListActivity.class));
                                        }
                                        finish();
                                    } else ToastUtils.showErrorLong(activity, "理财提交失败！");
                                }
                            }, "取消", new DialogListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                } else {
                    ToastUtils.showToast(context, true, "请完善必填信息！");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        final String daysStr = daysEt.getText().toString();

        if (v.getId() == R.id.durtionEt) {
            if (!TextUtils.isEmpty(daysStr))  ViewUtils.showDatePickerDialog(activity, durationEt, Integer.parseInt(daysStr));
            else {
                ToastUtils.showToast(context, false, "请先输入投资期限！");
                daysEt.requestFocus();
            }

        } else if (v.getId() == R.id.dayIncomeEt) {//自动结算日收益
            final String moneyStr = StringUtils.formatePrice(moneyEt.getText().toString());
            final String finalIncomeStr = StringUtils.formatePrice(finalIncomeEt.getText().toString());

            if (!TextUtils.isEmpty(moneyStr) &&
                    !TextUtils.isEmpty(finalIncomeStr) &&
                    !TextUtils.isEmpty(daysStr)) {
                DialogUtils.showMsgDialog(activity,
                        "计算日收益：\n" + finalIncomeStr + " / " + daysStr + " / " + moneyStr,
                        "计算", new DialogListener() {
                            @Override
                            public void onClick() {
                                double result = Double.parseDouble(finalIncomeStr) / Double.parseDouble(daysStr)
                                        / (Double.parseDouble(moneyStr) / 10000);
                                DecimalFormat df = new DecimalFormat("0.000");
                                dayIncomeEt.setText(df.format(result));
                            }
                        }, "取消", new DialogListener() {
                            @Override
                            public void onClick() {
                            }
                        });
            } else ToastUtils.showToast(context, true, "投资金额，投资期限，最终收益等信息不能为空！");

        }
    }


}
