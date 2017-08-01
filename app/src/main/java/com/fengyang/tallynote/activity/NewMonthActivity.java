package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ToastUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.List;

public class NewMonthActivity extends BaseActivity {

    private EditText last_balanceEt, payEt, salaryEt, incomeEt, homeuseEt, actual_balanceEt, remarkEt;
    private TextView durationEt, balanceEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("记月账", R.layout.activity_month);

        initView();
    }

    private void initView() {
        last_balanceEt = (EditText) findViewById(R.id.last_balanceEt);
        if (MyApp.utils.getMonNotes().size() > 0) { //如果已有月账记录自动填充上次结余，不得手动输入
            String actual_balance = MyApp.utils.getMonNotes().get(MyApp.utils.getMonNotes().size() - 1).getActual_balance();
            last_balanceEt.setText(StringUtils.formatePrice(actual_balance));
            last_balanceEt.setEnabled(false);
        }

        payEt = (EditText) findViewById(R.id.payEt);
        List<DayNote> dayNotes = MyApp.utils.getDayNotes();
        if (dayNotes != null && dayNotes.size() > 0) { //如果已有日账记录自动填充本次支出，不得手动输入
            payEt.setText(StringUtils.formatePrice(DayNote.getAllSum() + ""));
            payEt.setEnabled(false);
        }
        salaryEt = (EditText) findViewById(R.id.salaryEt);
        incomeEt = (EditText) findViewById(R.id.incomeEt);
        if (IncomeNote.getUnRecordSum() > 0) { //计算本次收益
            incomeEt.setText(StringUtils.formatePrice("" + IncomeNote.getUnRecordSum()));
            incomeEt.setEnabled(false);
        }
        homeuseEt = (EditText) findViewById(R.id.homeuseEt);
        balanceEt = (TextView) findViewById(R.id.balanceEt);
        durationEt = (TextView) findViewById(R.id.durationEt);
        remarkEt = (EditText) findViewById(R.id.remarkEt);
        actual_balanceEt = (EditText) findViewById(R.id.actual_balanceEt);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.durationEt) {
            ViewUtils.showDatePickerDialog(activity, durationEt, 0);

        } else if (v.getId() == R.id.balanceEt) {//自动结算结余
            final String last_balanceStr = StringUtils.formatePrice(last_balanceEt.getText().toString());
            final String payStr = StringUtils.formatePrice(payEt.getText().toString());
            final String salaryStr = StringUtils.formatePrice(salaryEt.getText().toString());
            final String incomeStr = StringUtils.formatePrice(incomeEt.getText().toString());
            final String homeuseStr = StringUtils.formatePrice(homeuseEt.getText().toString());

            if (!TextUtils.isEmpty(last_balanceStr) &&
                    !TextUtils.isEmpty(payStr) &&
                    !TextUtils.isEmpty(salaryStr)) {
                String meaasge = last_balanceStr + " - "
                        + payStr + " + "
                        + salaryStr;

                Double last_balance = Double.parseDouble(last_balanceStr);
                Double pay = Double.parseDouble(payStr);
                Double salary = Double.parseDouble(salaryStr);
                Double income = 0.00;
                if (!TextUtils.isEmpty(incomeStr)) {
                    income = Double.parseDouble(incomeStr);
                    meaasge += " + " + incomeStr;
                }

                Double homeuse = 0.00;
                if (!TextUtils.isEmpty(homeuseStr)) {
                    homeuse = Double.parseDouble(homeuseStr);
                    meaasge += " - " + homeuseStr;
                }

                final String calculate = (last_balance - pay + salary + income - homeuse) + "";

                DialogUtils.showMsgDialog(activity, "计算结余", meaasge,
                        new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                balanceEt.setText(StringUtils.formatePrice(calculate));
                            }
                        }, new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                            }
                        });
            } else ToastUtils.showToast(context, true, "上次结余，支出，工资等信息不能为空！");

        } else if (v.getId() == R.id.commitNote) {
            String last_balance = StringUtils.formatePrice(last_balanceEt.getText().toString());
            String pay = StringUtils.formatePrice(payEt.getText().toString());
            String salary = StringUtils.formatePrice(salaryEt.getText().toString());
            String income = StringUtils.formatePrice(incomeEt.getText().toString());
            String homeuse = StringUtils.formatePrice(homeuseEt.getText().toString());
            String balance = StringUtils.formatePrice(balanceEt.getText().toString());
            String actual_balance = StringUtils.formatePrice(actual_balanceEt.getText().toString());
            String duration = durationEt.getText().toString();
            String remark = remarkEt.getText().toString();

            if (!TextUtils.isEmpty(last_balance) && !TextUtils.isEmpty(pay) &&
                    !TextUtils.isEmpty(salary) && !TextUtils.isEmpty(balance)
                    && !TextUtils.isEmpty(actual_balance) && !TextUtils.isEmpty(duration)) {
                final MonthNote monthNote = new MonthNote(last_balance, pay, salary, income, homeuse,
                        balance, actual_balance, duration, remark, DateUtils.formatDateTime());
                LogUtils.i("commit", monthNote.toString());
                DialogUtils.showMsgDialog(activity, "新增月账单",
                        "上次结余：" + StringUtils.showPrice(monthNote.getLast_balance()) +
                                "\n本次支出：" + StringUtils.showPrice(monthNote.getPay()) +
                                "\n本次工资：" + StringUtils.showPrice(monthNote.getSalary()) +
                                "\n本次收益：" + StringUtils.showPrice(monthNote.getIncome()) +
                                "\n家用补贴：" + StringUtils.showPrice(monthNote.getHomeuse()) +
                                "\n本次结余：" + StringUtils.showPrice(monthNote.getBalance()) +
                                "\n实际结余：" + StringUtils.showPrice(monthNote.getActual_balance()) +
                                "\n月结说明：" + monthNote.getRemark(),
                        new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                if (MyApp.utils.newMNote(monthNote)) {
                                    ToastUtils.showSucessLong(context, "新增月账单成功！");
                                    ExcelUtils.exportMonthNote(null);
                                    if (getIntent().hasExtra("list"))
                                        sendBroadcast(new Intent(ContansUtils.ACTION_MONTH));
                                    if (MyApp.utils.getMonNotes().size() > 1) { //移植日账单到历史日账单
                                        if (MyApp.utils.newDNotes4History(monthNote.getDuration())) {
                                            LogUtils.i("newDNotes4History", MyApp.utils.getDayNotes4History(monthNote.getDuration()).toString());
                                            ExcelUtils.exportDayNote4History(null);
                                        }
                                    }
                                    finish();
                                } else ToastUtils.showErrorLong(context, "新增月账单失败！");
                            }
                        }, new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                            }
                        });
            } else {
                ToastUtils.showToast(context, true, "请完善必填信息！");
            }
        }
    }

}
