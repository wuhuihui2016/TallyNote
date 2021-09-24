package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * 完成理财
 */
public class FinishIncomeActivity extends BaseActivity {

    private IncomeNote incomeNote;
    @BindView(R.id.finalCashEt)
    public EditText finalCashEt;
    @BindView(R.id.finalCashGoEt)
    public EditText finalCashGoEt;
    @BindView(R.id.income_info)
    public TextView income_info;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("完成理财", R.layout.activity_finish_income);
    }

    @Override
    protected void initView() {

        //设置右上角“完成理财”按钮点击事件
        setRightBtnListener("完成理财", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalCash = StringUtils.formatePrice(finalCashEt.getText().toString());
                String finalCashGo = finalCashGoEt.getText().toString();

                if (!TextUtils.isEmpty(finalCash) && !TextUtils.isEmpty(finalCashGo)) {
                    incomeNote.setFinalCash(finalCash);
                    incomeNote.setFinalCashGo(finalCashGo);
                    LogUtils.i("commit", incomeNote.toString());
                    DialogUtils.showMsgDialog(activity, "完成理财\n" +
                                    "投入金额：" + StringUtils.showPrice(incomeNote.getMoney()) +
                                    "\n预期年化：" + incomeNote.getIncomeRatio() +
                                    " %\n投资期限：" + incomeNote.getDays() +
                                    " 天\n投资时段：" + incomeNote.getDurtion() +
                                    " \n拟日收益：" + incomeNote.getDayIncome() +
                                    " 元万/天\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) +
                                    "\n投资说明：" + incomeNote.getRemark() +
                                    "\n最终提现：" + StringUtils.showPrice(incomeNote.getFinalCash()) +
                                    "\n提现去处：" + incomeNote.getFinalCashGo(),
                            "提交", new MyClickListener() {
                                @Override
                                public void onClick() {
                                    MyApp.incomeNoteDBHandle.finishIncome(incomeNote);
                                    ToastUtils.showSucessLong(activity, "完成理财成功！");
                                    ExcelUtils.exportIncomeNote(null);
                                    EventBus.getDefault().post(ContansUtils.ACTION_INCOME);
                                    finish();
                                }
                            },
                            "返回查看", new MyClickListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                } else {
                    ToastUtils.showToast(context, true, "请完善必填信息！");
                }
            }
        });

        incomeNote = (IncomeNote) getIntent().getSerializableExtra("incomeNote");
        income_info.setText("理财ID：" + StringUtils.showPrice(incomeNote.getId()) +
                "投入金额：" + StringUtils.showPrice(incomeNote.getMoney()) +
                "\n预期年化：" + incomeNote.getIncomeRatio() +
                " %\n投资期限：" + incomeNote.getDays() +
                " 天\n投资时段：" + incomeNote.getDurtion() +
                " \n拟日收益：" + incomeNote.getDayIncome() +
                " 元万/天\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) +
                "\n投资说明：" + incomeNote.getRemark());
    }

    @Override
    protected void initEvent() {

    }

}
