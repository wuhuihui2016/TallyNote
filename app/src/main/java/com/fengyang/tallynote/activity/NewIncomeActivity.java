package com.fengyang.tallynote.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.Calendar;

public class NewIncomeActivity extends BaseActivity{

	private EditText moneyEt, incomeRatioEt, daysEt, finalIncomeEt, remarkEt;
	private TextView durationEt, dayIncomeEt;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("记理财", R.layout.activity_income);

		initView();
	}

	private void initView() {
		moneyEt = (EditText) findViewById(R.id.moneyEt);
		incomeRatioEt = (EditText) findViewById(R.id.incomeRatioEt);
		daysEt = (EditText) findViewById(R.id.daysEt);
		finalIncomeEt = (EditText) findViewById(R.id.finalIncomeEt);
		dayIncomeEt = (TextView) findViewById(R.id.dayIncomeEt);
		durationEt = (TextView) findViewById(R.id.durtionEt);
		remarkEt = (EditText) findViewById(R.id.remarkEt);

		calendar = Calendar.getInstance();

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.durtionEt) {
			ToastUtils.showToast(context, true, "请选择投资起始日期");
			new DatePickerDialog(activity, mdateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();

		} else if(v.getId() == R.id.dayIncomeEt) {//自动结算日收益
			final String moneyStr = StringUtils.formatePrice(moneyEt.getText().toString());
			final String finalIncomeStr = StringUtils.formatePrice(finalIncomeEt.getText().toString());
			final String daysStr = daysEt.getText().toString();

			if (! TextUtils.isEmpty(moneyStr) &&
					! TextUtils.isEmpty(finalIncomeStr) &&
					! TextUtils.isEmpty(daysStr)) {
				DialogUtils.showMsgDialog(activity, "计算日收益", finalIncomeStr + " / " + daysStr + " / " + moneyStr,
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								double result = Double.parseDouble(finalIncomeStr) / Double.parseDouble(daysStr)
										/ (Double.parseDouble(moneyStr) / 10000);
								DecimalFormat df = new DecimalFormat("0.000");
								dayIncomeEt.setText(df.format(result));
							}
						}, new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
							}
						});
			} else ToastUtils.showToast(context, true, "投资金额，投资期限，最终收益等信息不能为空！");


		} else if(v.getId() == R.id.commitNote) {
			String money = StringUtils.formatePrice(moneyEt.getText().toString());
			String incomeRatio = incomeRatioEt.getText().toString();
			String days = daysEt.getText().toString();
			String dayIncome = StringUtils.formatePrice(dayIncomeEt.getText().toString());
			String finalIncome = StringUtils.formatePrice(finalIncomeEt.getText().toString());
			String duration = durationEt.getText().toString();
			String remark = remarkEt.getText().toString();

			if (! TextUtils.isEmpty(money) && !TextUtils.isEmpty(incomeRatio) &&
					! TextUtils.isEmpty(days) && !TextUtils.isEmpty(dayIncome) &&
					! TextUtils.isEmpty(finalIncome) && ! TextUtils.isEmpty(duration)) {
				final IncomeNote incomeNote = new IncomeNote(money, incomeRatio, days, duration, dayIncome, finalIncome,
						null, null, 0, remark, DateUtils.formatDateTime());
				LogUtils.i("commit", incomeNote.toString());
				DialogUtils.showMsgDialog(activity, "新增理财",
						"投入金额：" + StringUtils.showPrice(incomeNote.getMoney())  +
								"\n预期年化：" + incomeNote.getIncomeRatio()  +
								" %\n投资期限：" + incomeNote.getDays()  +
								" 天\n投资时期：" + incomeNote.getDurtion()  +
								" \n拟日收益：" + StringUtils.showPrice(incomeNote.getDayIncome())  +
								" 元万/天\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) +
								"\n投资说明：" + incomeNote.getRemark(),
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.newINote(incomeNote)) {
									ToastUtils.showSucessLong(context, "新增理财成功！");
									ExcelUtils.exportIncomeNote(null);
									finish();
								} else ToastUtils.showErrorLong(context, "新增理财失败！");
							}
						}, new DialogUtils.DialogListener(){
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

	private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			monthOfYear = monthOfYear + 1;
			String month;
			if (monthOfYear < 10) month = "0" + monthOfYear;
			else month = "" + monthOfYear;
			String day;
			if (dayOfMonth < 10) day = "0" + dayOfMonth;
			else day = "" + dayOfMonth;

			String curDuration = durationEt.getText().toString();
			if (curDuration.length() > 0) {
				if (curDuration.endsWith("-")) {
					durationEt.setText(curDuration + year + month + day);
				} else {
					ToastUtils.showToast(context, true, "请选择投资终止日期");
					durationEt.setText(year + month + day + "-");
					new DatePickerDialog(activity, mdateListener,
							calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			} else {
				ToastUtils.showToast(context, true, "请选择投资终止日期");
				durationEt.setText(year + month + day + "-");
				new DatePickerDialog(activity, mdateListener,
						calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		}
	};

	@Override
	public void onBackPressed() {
		DialogUtils.showMsgDialog(activity, "退出编辑提示", "退出本次编辑",
				new DialogUtils.DialogListener(){
					@Override
					public void onClick(View v) {
						super.onClick(v);
						finish();

					}
				}, new DialogUtils.DialogListener(){
					@Override
					public void onClick(View v) {
						super.onClick(v);
					}
				});

	}
}
