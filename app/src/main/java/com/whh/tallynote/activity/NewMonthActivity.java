package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 记月账
 */
public class NewMonthActivity extends BaseActivity {

    @BindView(R.id.last_balanceEt)
    public EditText last_balanceEt;
    @BindView(R.id.payEt)
    public EditText payEt;
    @BindView(R.id.salaryEt)
    public EditText salaryEt;
    @BindView(R.id.incomeEt)
    public EditText incomeEt;
    @BindView(R.id.remarkEt)
    public EditText remarkEt;

    @BindView(R.id.durationEt)
    public TextView durationEt;
    @BindView(R.id.balanceEt)
    public TextView balanceEt;
    @BindView(R.id.balance_diff)
    public TextView balance_diff;
    @BindView(R.id.actual_balanceEt)
    public TextView actual_balanceEt;

    private String salaryStr = null;
    private StringBuffer currAssetsInfo; //现有资产清单

    //编辑月账
    boolean isEdit = false;
    MonthNote isEditMonthNote = null;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("记月账", R.layout.activity_month);
    }

    @Override
    protected void initView() {
        if (getIntent().hasExtra("editMonthNote")) {
            editMonthNote(); //编辑月账
            return;
        }

        if (MyApp.dbHandle.getCount4Record(ContansUtils.MONTH) > 0) { //如果已有月账记录自动填充上次结余，不得手动输入
            String actual_balance = MyApp.monthNoteDBHandle.getLastMonthNote().getActual_balance();
            last_balanceEt.setText(StringUtils.formatePrice(actual_balance));
            last_balanceEt.setFocusable(false);
            last_balanceEt.setFocusableInTouchMode(false);//设置不可编辑状态；
        }

        List<DayNote> dayNotes = MyApp.dayNoteDBHandle.getDayNotes();
        if (dayNotes != null && dayNotes.size() > 0) { //如果已有日账记录自动填充本次支出，不得手动输入
            payEt.setText(StringUtils.formatePrice(DayNote.getAllSum() + ""));
            payEt.setFocusable(false);
            payEt.setFocusableInTouchMode(false);//设置不可编辑状态；
            payEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.transfer(activity, List4DayActivity.class);
                }
            });
        }
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
        if (IncomeNote.getUnRecordSum() > 0) { //计算本次收益
            incomeEt.setText(StringUtils.formatePrice("" + IncomeNote.getUnRecordSum()));
            incomeEt.setFocusable(false);
            incomeEt.setFocusableInTouchMode(false);//设置不可编辑状态；
            incomeEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.transfer(activity, List4IncomeOfUnRecordActivity.class);
                }
            });
        }

        setRightBtnListener("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String last_balance = StringUtils.formatePrice(last_balanceEt.getText().toString());
                String pay = StringUtils.formatePrice(payEt.getText().toString());
                String salary = StringUtils.formatePrice(salaryEt.getText().toString());
                String balance = StringUtils.formatePrice(balanceEt.getText().toString());
                String actual_balance = StringUtils.formatePrice(actual_balanceEt.getText().toString());
                String income = StringUtils.formatePrice(incomeEt.getText().toString());
                String duration = durationEt.getText().toString();
                if (!TextUtils.isEmpty(duration) && duration.split("-").length != 2) {
                    ToastUtils.showErrorLong(activity, "月结时段格式不正确！");
                    return;
                }

                String remark = remarkEt.getText().toString();

                if (!TextUtils.isEmpty(last_balance) && !TextUtils.isEmpty(pay) && !TextUtils.isEmpty(salary)
                        && !TextUtils.isEmpty(balance) && !TextUtils.isEmpty(actual_balance)) {
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
                                    "\n" + monthNote.getRemark(),
                            "提交", new MyClickListener() {
                                @Override
                                public void onClick() {
                                    MyApp.monthNoteDBHandle.saveMonthNote(monthNote);
                                    ToastUtils.showSucessLong(activity, "提交月账单成功！");
                                    ExcelUtils.exportMonthNote(null);
                                    if (MyApp.dbHandle.getCount4Record(ContansUtils.MONTH) > 0) { //移植日账单到历史日账单
                                        if (MyApp.dayNoteDBHandle.saveDayNotes4History(monthNote.getDuration())) {
                                            //移植完成后，清除临时日账单数据
                                            MyApp.dbHandle.clearTableData(ContansUtils.DAY);
                                            ExcelUtils.exportDayNote4History(null);
                                        }
                                    }
                                    if (getIntent().hasExtra("list")) {
                                        EventBus.getDefault().post(ContansUtils.ACTION_MONTH);
                                    } else {
                                        AppManager.transfer(activity, List4MonthActivity.class, "flag", true);
                                    }
                                    finish();
                                }
                            }, "取消", new MyClickListener() {
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

    /**
     * 编辑月账
     * 仅实际结余和备注可编辑
     */
    private void editMonthNote() {
        setTitle("编辑月账");
        isEdit = true;
        isEditMonthNote = (MonthNote) getIntent().getSerializableExtra("editMonthNote");
        //上次结余
        last_balanceEt.setText(StringUtils.formatePrice(isEditMonthNote.getLast_balance()));
        last_balanceEt.setEnabled(false);
        //本次支出
        payEt.setText(StringUtils.formatePrice(isEditMonthNote.getPay()));
        payEt.setEnabled(false);
        //本次工资
        salaryEt.setText(StringUtils.formatePrice(isEditMonthNote.getSalary()));
        salaryEt.setEnabled(false);
        //本次结余
        balanceEt.setText(StringUtils.formatePrice(isEditMonthNote.getBalance()));
        balanceEt.setEnabled(false);
        //本次收益
        if (TextUtils.isEmpty(isEditMonthNote.getIncome())) {
            incomeEt.setText(StringUtils.formatePrice(isEditMonthNote.getIncome()));
            incomeEt.setEnabled(false);
        } else {incomeEt.setVisibility(View.GONE);}
        //结算时段
        durationEt.setText(isEditMonthNote.getDuration());
        durationEt.setEnabled(false);

        actual_balanceEt.setText(StringUtils.formatePrice(isEditMonthNote.getActual_balance()));  //实际结余

        String getRemask = isEditMonthNote.getRemark();
        if (getRemask.split("\n其他说明：").length == 2) {
            getRemask = getRemask.split("\n其他说明：")[1];
        }
        remarkEt.setText(getRemask); //月结说明

        setRightBtnListener("修改", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actual_balance = StringUtils.formatePrice(actual_balanceEt.getText().toString());
                String remark = remarkEt.getText().toString();

                if (!TextUtils.isEmpty(actual_balance) && !actual_balance.equals(isEditMonthNote.getActual_balance())) {
                    isEditMonthNote.setActual_balance(actual_balance);
                    isEditMonthNote.setRemark(currAssetsInfo.toString() + "\n其他说明：" + remark);

                    LogUtils.i("commit", isEditMonthNote.toString());
                    DialogUtils.showMsgDialog(activity, "修改月账单\n" +
                                    "实际结余：" + StringUtils.showPrice(isEditMonthNote.getActual_balance()) +
                                    "\n" + isEditMonthNote.getRemark(),
                            "提交", new MyClickListener() {
                                @Override
                                public void onClick() {
                                    MyApp.monthNoteDBHandle.updateMonthNote(isEditMonthNote);
                                    ToastUtils.showSucessLong(activity, "修改月账单成功！");
                                    ExcelUtils.exportMonthNote(null);
                                    EventBus.getDefault().post(ContansUtils.ACTION_MONTH);
                                    finish();
                                }
                            }, "取消", new MyClickListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                } else {
                    ToastUtils.showToast(context, true, "实际结余为空或未修改！");
                }
            }
        });
    }

    @Override
    protected void initEvent() {

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
                        "计算", new MyClickListener() {
                            @Override
                            public void onClick() {
                                balanceEt.setText(StringUtils.formatePrice(calculate));
                            }
                        }, "取消", new MyClickListener() {
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
            info.setText("工资：" + StringUtils.formatePrice(salaryStr) + "元，是否转存小金库？");
            if (isEdit) info.setText(isEditMonthNote.getRemark().split("\n其他说明：")[0]);
            final EditText editText1 = (EditText) layout.findViewById(R.id.editText1); //微信
            final EditText editText2 = (EditText) layout.findViewById(R.id.editText2); //支付宝
            final EditText editText3 = (EditText) layout.findViewById(R.id.editText3); //京东小金库
            final EditText editText4 = (EditText) layout.findViewById(R.id.editText4); //理财
            //自动填写理财总金额
//            if (IncomeNote.getEarningInComes().size() > 0) {
//                editText4.setText(StringUtils.formatePrice(IncomeNote.getEarningMoney() + ""));
//            }
            editText4.setText("0"); //理财over，为0

            layout.findViewById(R.id.comfire).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                asset_clearing = 0.00; //现有资产统计
                                popupWindow.dismiss();

                                //统计已输入资产总额
                                List<String> je = new ArrayList<>();
                                je.add(editText1.getText().toString());
                                je.add(editText2.getText().toString());
                                je.add(editText3.getText().toString());
                                je.add(editText4.getText().toString());

                                currAssetsInfo = new StringBuffer();
                                currAssetsInfo.append("当前资产= ");
                                for (int i = 0; i < je.size(); i++) {
                                    if (!TextUtils.isEmpty(je.get(i))) {
                                        double money = Double.parseDouble(je.get(i));
                                        asset_clearing += money;
                                        if (i == 0)
                                            currAssetsInfo.append(showPrice(je.get(i)) + "(微信) + ");
                                        if (i == 1)
                                            currAssetsInfo.append(showPrice(je.get(i)) + "(支付宝) + ");
                                        if (i == 2)
                                            currAssetsInfo.append(showPrice(je.get(i)) + "(京东小金库)");
                                    }
                                }

                                actual_balanceEt.setText(StringUtils.formatePrice(asset_clearing + ""));

                                String getBalance = balanceEt.getText().toString();
                                if (!TextUtils.isEmpty(getBalance)) {
                                    Double balance = Double.parseDouble(getBalance);
                                    Double balanceDiff = asset_clearing - balance;

                                    String diff = showPrice(balanceDiff + "");
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

    /**
     * 格式化显示价格
     * @param priceStr
     * @return
     */
    private String showPrice(String priceStr) {
        return StringUtils.showPrice(StringUtils.formatePrice(priceStr));
    }
}
