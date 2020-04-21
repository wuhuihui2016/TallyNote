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

import java.util.ArrayList;
import java.util.List;

/**
 * 记月账
 */
public class NewMonthActivity extends BaseActivity {

    private EditText last_balanceEt, payEt, salaryEt, incomeEt, remarkEt;
    private TextView durationEt, balanceEt, balance_diff, actual_balanceEt;
    private String salaryStr = null;
    private StringBuffer currAssetsInfo; //现有资产清单

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
        balance_diff = (TextView) findViewById(R.id.balance_diff);
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
                            balance, actual_balance, duration, currAssetsInfo.toString() + "\n其他说明：" + remark, DateUtils.formatDateTime());
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

        } else if (v.getId() == R.id.balanceEt) {//自动结算本次结余
            actual_balanceEt.setText("");
            balance_diff.setText("");

            final String last_balanceStr = StringUtils.formatePrice(last_balanceEt.getText().toString());
            final String payStr = StringUtils.formatePrice(payEt.getText().toString());
            final String incomeStr = StringUtils.formatePrice(incomeEt.getText().toString());

            if (!TextUtils.isEmpty(last_balanceStr) &&
                    !TextUtils.isEmpty(payStr) &&
                    !TextUtils.isEmpty(salaryStr)) {
                String meaasge = "";
                if (payStr.startsWith("-"))
                meaasge = last_balanceStr + " - (" + payStr + ")";
                else meaasge = last_balanceStr + " - " + payStr;

                Double last_balance = Double.parseDouble(last_balanceStr);
                Double pay = Double.parseDouble(payStr);
                Double salary = Double.parseDouble(salaryStr);
                Double income = 0.00;
                if (!TextUtils.isEmpty(incomeStr)) {
                    income = Double.parseDouble(incomeStr);
                    meaasge += " + " + incomeStr + " + " + salaryStr;
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
            actual_balanceEt.setText("");
            balance_diff.setText("");

            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_month_pop, null);
            popupWindow = new PopupWindow(layout, 800, LinearLayout.LayoutParams.WRAP_CONTENT);
            ViewUtils.setPopupWindow(activity, popupWindow);
//            相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAtLocation(findViewById(R.id.month_layout), Gravity.CENTER, 0, -300);

            TextView info = (TextView) layout.findViewById(R.id.info);
            info.setText("如工资还在工资卡中，工资项自动填充，请待月结算完成转入理财;" +
                    "否则请手动清除工资项金额，避免重复累加，导致实有资产结算错误！！" +
                    "京东金融中包含2W+回款银行理财，该平台的总金额不能作为累计，应取【小金库】的金额！！");
            final EditText editText1 = (EditText) layout.findViewById(R.id.editText1); //微信
            final EditText editText2 = (EditText) layout.findViewById(R.id.editText2); //支付宝
            final EditText editText3 = (EditText) layout.findViewById(R.id.editText3); //京东金融
            final EditText editText4 = (EditText) layout.findViewById(R.id.editText4); //工资
            if (!TextUtils.isEmpty(salaryStr)) { //自动填充工资金额
                editText4.setText(StringUtils.formatePrice(salaryStr));
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
            final EditText editText5 = (EditText) layout.findViewById(R.id.editText5); //理财
            //自动填写理财总金额
            if (IncomeNote.getEarningInComes().size() > 0)
                editText5.setText(StringUtils.formatePrice(IncomeNote.getEarningMoney() + ""));

            layout.findViewById(R.id.comfire).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                asset_clearing = 0.00;
                                popupWindow.dismiss();

                                //统计已输入资产总额
                                List<String> je = new ArrayList<>();
                                je.add(editText1.getText().toString());
                                je.add(editText2.getText().toString());
                                je.add(editText3.getText().toString());
                                je.add(editText4.getText().toString());
                                je.add(editText5.getText().toString());

                                currAssetsInfo =  new StringBuffer();
                                currAssetsInfo.append("现有资产：");
                                for (int i = 0; i < je.size(); i++) {
                                    if (!TextUtils.isEmpty(je.get(i))) {
                                        double money = Double.parseDouble(je.get(i));
                                        asset_clearing += money;
                                        if (i == 0) currAssetsInfo.append("微信：" + StringUtils.showPrice(StringUtils.formatePrice(je.get(i))));
                                        if (i == 1) currAssetsInfo.append("  支付宝："+ StringUtils.showPrice(StringUtils.formatePrice(je.get(i))));
                                        if (i == 2) currAssetsInfo.append("  京东金融："+ StringUtils.showPrice(StringUtils.formatePrice(je.get(i))));
                                    }
                                }

                                actual_balanceEt.setText(StringUtils.formatePrice(asset_clearing + ""));

                                String getBalance = balanceEt.getText().toString();
                                if (!TextUtils.isEmpty(getBalance)) {
                                    Double balance = Double.parseDouble(getBalance);
                                    Double balanceDiff = asset_clearing - balance;

                                    String diff = StringUtils.showPrice(StringUtils.formatePrice(balanceDiff + ""));
                                    if (balanceDiff > 0) {
                                        balance_diff.setText("(差额：↑ " + diff);
                                    } else {
                                        balance_diff.setText("(差额：↓ " + diff);
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
