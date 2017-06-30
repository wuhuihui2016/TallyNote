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
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.Calendar;

public class NewMonthActivity extends BaseActivity{

	private EditText last_balanceEt, payEt, salaryEt, incomeEt, homeuseEt, balanceEt, actual_balanceEt, remarkEt;
	private TextView durationEt;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("记月账", R.layout.activity_month);

		initView();
	}

	private void initView() {
		last_balanceEt = (EditText) findViewById(R.id.last_balanceEt);
		payEt = (EditText) findViewById(R.id.payEt);
		salaryEt = (EditText) findViewById(R.id.salaryEt);
		incomeEt = (EditText) findViewById(R.id.incomeEt);
		homeuseEt = (EditText) findViewById(R.id.incomeEt);
		balanceEt = (EditText) findViewById(R.id.balanceEt);
		actual_balanceEt = (EditText) findViewById(R.id.actual_balanceEt);
		durationEt = (TextView) findViewById(R.id.durationEt);
		remarkEt = (EditText) findViewById(R.id.remarkEt);

		calendar = Calendar.getInstance();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.durationEt) {
			StringUtils.show1Toast(activity, "请选择月结算起始日期");
			new DatePickerDialog(activity, mdateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();

		} else if(v.getId() == R.id.commitNote) {
			String last_balance = StringUtils.formatePrice(last_balanceEt.getText().toString());
			String pay = StringUtils.formatePrice(payEt.getText().toString());
			String salary = StringUtils.formatePrice(salaryEt.getText().toString());
			String income = StringUtils.formatePrice(incomeEt.getText().toString());
			String homeuse = StringUtils.formatePrice(homeuseEt.getText().toString());
			String balance = StringUtils.formatePrice(balanceEt.getText().toString());
			String actual_balance = StringUtils.formatePrice(actual_balanceEt.getText().toString());
			String duration = durationEt.getText().toString();
			String remark = remarkEt.getText().toString();

			if (! TextUtils.isEmpty(last_balance) && !TextUtils.isEmpty(pay) &&
					! TextUtils.isEmpty(salary) && ! TextUtils.isEmpty(balance)
					&& !TextUtils.isEmpty(actual_balance) && ! TextUtils.isEmpty(duration)) {
				final MonthNote monthNote = new MonthNote(last_balance, pay, salary, income, homeuse,
						balance, actual_balance, duration, remark, DateUtils.formatDateTime());
				LogUtils.i("commit", monthNote.toString());
				DialogUtils.showMsgDialog(activity, "新增月账单",
						"上次结余:" + StringUtils.showPrice(monthNote.getLast_balance()) + "\n" +
								"本次支出:" + StringUtils.showPrice(monthNote.getPay()) + "\n" +
								"本次工资:" + StringUtils.showPrice(monthNote.getSalary()) + "\n" +
								"本次收益:" + StringUtils.showPrice(monthNote.getIncome()) + "\n" +
								"家用补贴:" + StringUtils.showPrice(monthNote.getHomeuse()) + "\n" +
								"本次结余:" + StringUtils.showPrice(monthNote.getBalance()) + "\n" +
								"实际结余:" + StringUtils.showPrice(monthNote.getActual_balance()) + "\n" +
								"月结备注:" + monthNote.getRemark(),
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.newMNote(monthNote)) {
									StringUtils.show1Toast(activity, "新增月账单成功！");
									finish();
								} else StringUtils.show1Toast(activity, "新增月账单失败！");
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
					StringUtils.show1Toast(activity, "请选择月结算终止日期");
					durationEt.setText(year + month + day + "-");
					new DatePickerDialog(activity, mdateListener,
							calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			} else {
				StringUtils.show1Toast(activity, "请选择月结算终止日期");
				durationEt.setText(year + month + day + "-");
				new DatePickerDialog(activity, mdateListener,
						calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		}
	};

}
