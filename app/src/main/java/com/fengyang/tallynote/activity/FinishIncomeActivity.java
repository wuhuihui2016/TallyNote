package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.Income;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;

public class FinishIncomeActivity extends BaseActivity{

	private Income income;
	private EditText finalCashEt, finalCashGoEt;
	private TextView income_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("完成理财", R.layout.activity_finish_income);

		initView();
	}

	private void initView() {
		finalCashEt = (EditText) findViewById(R.id.finalCashEt);
		finalCashGoEt = (EditText) findViewById(R.id.finalCashGoEt);

		income = (Income) getIntent().getSerializableExtra("income");
		income_info = (TextView) findViewById(R.id.income_info);
		income_info.setText("投入金额： " + StringUtils.showPrice(income.getMoney())  +
				"\n预期年化： " + income.getIncomeRatio()  +
				"\n投资期限： " + income.getDays()  +
				"\n投资时期： " + income.getDurtion()  +
				"\n拟日收益： " + StringUtils.showPrice(income.getDayIncome())  +
				"\n最终收益： " + StringUtils.showPrice(income.getFinalIncome()));
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.commitNote) {
			String finalCash = StringUtils.formatePrice(finalCashEt.getText().toString());
			String finalCashGo = StringUtils.formatePrice(finalCashGoEt.getText().toString());

			if (! TextUtils.isEmpty(finalCash) && !TextUtils.isEmpty(finalCashGo)) {
				income.setFinalCash(finalCash);
				income.setFinalCashGo(finalCashGo);
				LogUtils.i("commit", income.toString());
				DialogUtils.showMsgDialog(activity, "完成理财",
						"投入金额： " + StringUtils.showPrice(income.getMoney())  +
								"\n预期年化： " + income.getIncomeRatio()  +
								"\n投资期限：" + income.getDays()  +
								"\n投资时期：" + income.getDurtion()  +
								"\n拟日收益：" + StringUtils.showPrice(income.getDayIncome())  +
								"\n最终收益：" + StringUtils.showPrice(income.getFinalIncome())  +
								"\n最终提现：" + StringUtils.showPrice(income.getFinalCash())  +
								"\n提现去处：" + income.getFinalCashGo(),
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.finishIncome(income)) {
									StringUtils.show1Toast(activity, "完成理财成功！");
									finish();
								} else StringUtils.show1Toast(activity, "完成理财失败！");
							}
						}, new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
							}
						});
			} else {
				StringUtils.show1Toast(activity, "请完善必填信息！");
			}
		}
	}



}
