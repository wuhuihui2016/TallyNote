package com.fengyang.tallynote.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.Calendar;

public class MonthActivity extends BaseActivity{

	private EditText last_balanceEt, payEt, salaryEt, incomeEt, homeuseEt, balanceEt, actual_balanceEt, remarkEt;
	private TextView durationEt;
	private Button commitNote;

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
		commitNote = (Button) findViewById(R.id.commitNote);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.durationEt) {
			Calendar calendar = Calendar.getInstance();
			new DatePickerDialog(this, mdateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();

		} else if(v.getId() == R.id.commitNote) {
			String last_balance = StringUtils.formateDouble(last_balanceEt.getText().toString());
			String pay = StringUtils.formateDouble(payEt.getText().toString());
			String salary = StringUtils.formateDouble(salaryEt.getText().toString());
			String income = StringUtils.formateDouble(incomeEt.getText().toString());
			String homeuse = StringUtils.formateDouble(homeuseEt.getText().toString());
			String balance = StringUtils.formateDouble(balanceEt.getText().toString());
			String actual_balance = StringUtils.formateDouble(actual_balanceEt.getText().toString());
			String duration = durationEt.getText().toString();
			String remark = remarkEt.getText().toString();

			if (! TextUtils.isEmpty(last_balance) && !TextUtils.isEmpty(pay) &&
					! TextUtils.isEmpty(salary) && !TextUtils.isEmpty(income) &&
					! TextUtils.isEmpty(balance) && !TextUtils.isEmpty(actual_balance)
					&& ! TextUtils.isEmpty(duration)) {
				final MonthNote monthNote = new MonthNote(last_balance, pay, salary, income, homeuse,
						balance, actual_balance, duration, remark, DateUtils.formatDateTime());
				LogUtils.i("commit", monthNote.toString());
				DialogUtils.showMsgDialog(activity, "新增月账单",
						"上次结余:" + monthNote.getLast_balance() + "\n" +
								"本次支出:" + monthNote.getPay() + "\n" +
								"本次工资:" + monthNote.getSalary() + "\n" +
								"本次收益:" + monthNote.getIncome() + "\n" +
								"家用补贴:" + monthNote.getHomeuse() + "\n" +
								"本次结余:" + monthNote.getBalance() + "\n" +
								"实际结余:" + monthNote.getActual_balance() + "\n" +
								"备    注:" + monthNote.getRemark(),
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.newMNote(monthNote)) {
									StringUtils.show1Toast(activity, "新增月账单成功！");
									finish();
								} else StringUtils.show1Toast(activity, "新增月账单失败！");
							}
						});
			} else {
				StringUtils.show1Toast(activity, "请完善必填信息！");
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		//检测权限后显示界面
		PermissionUtils.checkSDcardPermission(MonthActivity.this, new PermissionUtils.OnCheckCallback() {
			@Override
			public void onCheck(boolean isSucess) {
				commitNote.setEnabled(isSucess);
				if (! isSucess) {
					PermissionUtils.notPermission(MonthActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
					StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
				}
			}
		});
	}

	private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			String month;
			if (monthOfYear < 10) month = "0" + monthOfYear;
			else month = "" + monthOfYear;
			String day;
			if (dayOfMonth < 10) day = "0" + dayOfMonth;
			else day = "" + dayOfMonth;
			durationEt.setText(year + month + day + "-" +  DateUtils.formatDate());
		}
	};

}
