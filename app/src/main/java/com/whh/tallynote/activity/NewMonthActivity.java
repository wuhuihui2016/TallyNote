package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.database.DayNoteDao;
import com.whh.tallynote.database.MonthNoteDao;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.model.MonthNote;
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

import java.util.List;

/**
 * 记月账
 */
public class NewMonthActivity extends BaseActivity {

    private EditText last_balanceEt, payEt, salaryEt, incomeEt, remarkEt;
    private TextView durationEt, balanceEt, actual_balanceEt;
    private String salaryStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("记月账", R.layout.activity_month);

        initView();
    }

    private void initView() {
        last_balanceEt = (EditText) findViewById(R.id.last_balanceEt);
        if (MonthNoteDao.getMonthNotes().size() > 0) { //如果已有月账记录自动填充上次结余，不得手动输入
            String actual_balance = MonthNoteDao.getMonthNotes().get(MonthNoteDao.getMonthNotes().size() - 1).getActual_balance();
            last_balanceEt.setText(StringUtils.formatePrice(actual_balance));
            last_balanceEt.setFocusable(false);
            last_balanceEt.setFocusableInTouchMode(false);//设置不可编辑状态；
        }

        payEt = (EditText) findViewById(R.id.payEt);
        List<DayNote> dayNotes = DayNoteDao.getDayNotes();
        if (dayNotes != null && dayNotes.size() > 0) { //如果已有日账记录自动填充本次支出，不得手动输入
            payEt.setText(StringUtils.formatePrice(DayNote.getAllSum() + ""));
            payEt.setFocusable(false);
            payEt.setFocusableInTouchMode(false);//设置不可编辑状态；
            payEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, List4DayActivity.class));
                }
            });
        }
        salaryEt = (EditText) findViewById(R.id.salaryEt);
        salaryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                salaryStr = StringUtils.formatePrice(salaryEt.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        incomeEt = (EditText) findViewById(R.id.incomeEt);
        if (IncomeNote.getUnRecordSum() > 0) { //计算本次收益
            incomeEt.setText(StringUtils.formatePrice("" + IncomeNote.getUnRecordSum()));
            incomeEt.setFocusable(false);
            incomeEt.setFocusableInTouchMode(false);//设置不可编辑状态；
            incomeEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, List4IncomeOfUnRecordActivity.class));
                }
            });
        }
        balanceEt = (TextView) findViewById(R.id.balanceEt);
        durationEt = (TextView) findViewById(R.id.durationEt);
        durationEt.setText(MonthNote.getAfterEndDate());

        remarkEt = (EditText) findViewById(R.id.remarkEt);
        actual_balanceEt = (TextView) findViewById(R.id.actual_balanceEt);

        setRightBtnListener("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String last_balance = StringUtils.formatePrice(last_balanceEt.getText().toString());
                String pay = StringUtils.formatePrice(payEt.getText().toString());
                String salary = StringUtils.formatePrice(salaryEt.getText().toString());
                String income = StringUtils.formatePrice(incomeEt.getText().toString());
                String balance = StringUtils.formatePrice(balanceEt.getText().toString());
                String actual_balance = StringUtils.formatePrice(actual_balanceEt.getText().toString());
                String duration = durationEt.getText().toString();
                String remark = remarkEt.getText().toString();

                if (!TextUtils.isEmpty(last_balance) && !TextUtils.isEmpty(pay) &&
                        !TextUtils.isEmpty(salary) && !TextUtils.isEmpty(balance)
                        && !TextUtils.isEmpty(actual_balance) && !TextUtils.isEmpty(duration)) {
                    final MonthNote monthNote = new MonthNote(last_balance, pay, salary, income,
                            balance, actual_balance, duration, remark, DateUtils.formatDateTime());
                    LogUtils.i("commit", monthNote.toString());
                    DialogUtils.showMsgDialog(activity, "提交月账单\n" +
                                    "上次结余：" + StringUtils.showPrice(monthNote.getLast_balance()) +
                                    "\n本次支出：" + StringUtils.showPrice(monthNote.getPay()) +
                                    "\n本次工资：" + StringUtils.showPrice(monthNote.getSalary()) +
                                    "\n本次收益：" + StringUtils.showPrice(monthNote.getIncome()) +
                                    "\n本次结余：" + StringUtils.showPrice(monthNote.getBalance()) +
                                    "\n实际结余：" + StringUtils.showPrice(monthNote.getActual_balance()) +
                                    "\n月结说明：" + monthNote.getRemark(),
                            "提交", new DialogListener() {
                                @Override
                                public void onClick() {
                                    if (MonthNoteDao.newMNote(monthNote)) {
                                        ToastUtils.showSucessLong(activity, "提交月账单成功！");
                                        ExcelUtils.exportMonthNote(null);
                                        if (MonthNoteDao.getMonthNotes().size() > 1) { //移植日账单到历史日账单
                                            if (DayNoteDao.newDNotes4History(monthNote.getDuration())) {
                                                LogUtils.i("newDNotes4History", DayNoteDao.getDayNotes4History(monthNote.getDuration()).toString());
                                                ExcelUtils.exportDayNote4History(null);
                                            }
                                        }
                                        if (getIntent().hasExtra("list")) {
                                            EventBus.getDefault().post(ContansUtils.ACTION_MONTH);
                                        } else {
                                            Intent intent = new Intent(activity, List4MonthActivity.class);
                                            intent.putExtra("flag", true);
                                            startActivity(intent);
                                        }
                                        finish();
                                    } else ToastUtils.showErrorLong(activity, "提交月账单失败！");
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
        if (v.getId() == R.id.durationEt) {
            ViewUtils.showDatePickerDialog(activity, durationEt, 0);

        } else if (v.getId() == R.id.balanceEt) {//自动结算结余
            final String last_balanceStr = StringUtils.formatePrice(last_balanceEt.getText().toString());
            final String payStr = StringUtils.formatePrice(payEt.getText().toString());
            final String incomeStr = StringUtils.formatePrice(incomeEt.getText().toString());

            if (!TextUtils.isEmpty(last_balanceStr) &&
                    !TextUtils.isEmpty(payStr) &&
                    !TextUtils.isEmpty(salaryStr)) {
                String meaasge = last_balanceStr + " - " + payStr;

                Double last_balance = Double.parseDouble(last_balanceStr);
                Double pay = Double.parseDouble(payStr);
                Double salary = Double.parseDouble(salaryStr);
                Double income = 0.00;
                if (!TextUtils.isEmpty(incomeStr)) {
                    income = Double.parseDouble(incomeStr);
                    meaasge += " + " + incomeStr + " + "
                            + salaryStr;
                } else {
                    meaasge += " + " + salaryStr;
                }

                final String calculate = (last_balance - pay + salary + income) + "";

                DialogUtils.showMsgDialog(activity, "计算结余：\n" + meaasge,
                        "计算", new DialogListener() {
                            @Override
                            public void onClick() {
                                balanceEt.setText(StringUtils.formatePrice(calculate));
                            }
                        }, "取消", new DialogListener() {
                            @Override
                            public void onClick() {
                            }
                        });
            } else ToastUtils.showToast(context, true, "上次结余，支出，工资等信息不能为空！");

        } else if (v.getId() == R.id.actual_balanceEt) {
            initPopupWindow();//输入各类资产结算实际结余

        }
    }

    /**
     * 初始化popupWindow
     */
    private Double asset_clearing;

    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_month_pop, null);
            popupWindow = new PopupWindow(layout, 800, LinearLayout.LayoutParams.WRAP_CONTENT);
            ViewUtils.setPopupWindow(activity, popupWindow);
//            相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAtLocation(findViewById(R.id.month_layout), Gravity.CENTER, 0, -300);

            final EditText editText1 = (EditText) layout.findViewById(R.id.editText1);
            final EditText editText2 = (EditText) layout.findViewById(R.id.editText2);
            final EditText editText3 = (EditText) layout.findViewById(R.id.editText3);
            if (!TextUtils.isEmpty(salaryStr)) {
                editText3.setText(StringUtils.formatePrice(salaryStr));
                final ImageButton clear_salary = (ImageButton) layout.findViewById(R.id.clear_salary);
                clear_salary.setVisibility(View.VISIBLE);
                clear_salary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText3.setText("");
                    }
                });

                editText3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) clear_salary.setVisibility(View.VISIBLE);
                        else clear_salary.setVisibility(View.GONE);

                    }
                });
            }
            final EditText editText4 = (EditText) layout.findViewById(R.id.editText4);
            if (IncomeNote.getEarningInComes().size() > 0)
                editText4.setText(StringUtils.formatePrice(IncomeNote.getEarningMoney() + ""));

            layout.findViewById(R.id.comfire).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                asset_clearing = 0.00;
                                popupWindow.dismiss();
                                if (editText1.getText().toString().length() > 0) {
                                    asset_clearing += Double.parseDouble(editText1.getText().toString());
                                }
                                if (editText2.getText().toString().length() > 0) {
                                    asset_clearing += Double.parseDouble(editText2.getText().toString());
                                }
                                if (editText3.getText().toString().length() > 0) {
                                    asset_clearing += Double.parseDouble(editText3.getText().toString());
                                }
                                if (editText4.getText().toString().length() > 0) {
                                    asset_clearing += Double.parseDouble(editText4.getText().toString());
                                }
                                actual_balanceEt.setText(StringUtils.formatePrice(asset_clearing + ""));

                                String getBalance = balanceEt.getText().toString();
                                if (!TextUtils.isEmpty(getBalance)) {
                                    Double balance = Double.parseDouble(getBalance);
                                    Double balanceDiff = asset_clearing - balance;
                                    TextView balance_diff = (TextView) findViewById(R.id.balance_diff);
                                    if (balanceDiff > 0) {
                                        balance_diff.setText("(差额：↑ " + balanceDiff);
                                    } else {
                                        balance_diff.setText("(差额：↓ " + balanceDiff);
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    }
            );
            layout.findViewById(R.id.cancel).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    }
            );
        }
    }

}
