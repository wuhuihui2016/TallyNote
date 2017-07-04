package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity{

	private TextView year, month, day, last_balanceTv, current_pay;
	private boolean isSeen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("我的账本", R.layout.activity_main);

	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		PermissionUtils.checkSDcardPermission(activity, new PermissionUtils.OnCheckCallback() {
			@Override
			public void onCheck(boolean isSucess) {
				findViewById(R.id.commitDNote).setEnabled(isSucess);
				findViewById(R.id.commitMNote).setEnabled(isSucess);
				findViewById(R.id.commitIncome).setEnabled(isSucess);
				if (! isSucess) {
					PermissionUtils.notPermission(activity, PermissionUtils.PERMISSIONS_STORAGE);
					StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
				}
			}
		});

		year = (TextView) findViewById(R.id.year);
		month = (TextView) findViewById(R.id.month);
		day = (TextView) findViewById(R.id.day);

		last_balanceTv = (TextView) findViewById(R.id.last_balanceTv);
		last_balanceTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		current_pay = (TextView) findViewById(R.id.current_pay);
		current_pay.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

		Calendar calendar = Calendar.getInstance();
		year.setText(calendar.get(Calendar.YEAR) + ""); // 获取当前年份
		month.setText((calendar.get(Calendar.MONTH) + 1) + ""); // 获取当前月份
		day.setText(calendar.get(Calendar.DAY_OF_MONTH) + ""); // 获取当前日

		showDayNote();
		showMonthNote();
		showIncomeNote();

	}

	/**
	 * 显示最近一次日账单记录
	 */
	private void showDayNote() {
		List<DayNote> dayNotes = MyApp.utils.getDayNotes();
		LinearLayout cur_layout = (LinearLayout) findViewById(R.id.cur_layout);
		if (dayNotes.size() > 0) {
			//显示本次月记录总支出
			double sum = 0.00;
			for (int i = 0; i < dayNotes.size(); i ++) {
				sum = sum + Double.parseDouble(dayNotes.get(i).getMoney());
			}
			current_pay.setText(StringUtils.showPrice(sum + ""));

			//显示最近一次日记录支出
			cur_layout.setVisibility(View.VISIBLE);
			DayNote dayNote = dayNotes.get(dayNotes.size() - 1);
			TextView time = (TextView) findViewById(R.id.time); time.setText(DateUtils.diffTime(dayNote.getTime()));
			TextView usage = (TextView) findViewById(R.id.usage); usage.setText(dayNote.getUsage());
			TextView money = (TextView) findViewById(R.id.money); money.setText(StringUtils.showPrice(dayNote.getMoney()) + " 元");
			if (! TextUtils.isEmpty(dayNote.getRemark())) {
				TextView remask = (TextView) findViewById(R.id.remask);
				remask.setText(dayNote.getRemark());
			}

			findViewById(R.id.item_day_layout).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, ListViewActivity.class);
					intent.putExtra("type", MyApp.DAY);
					startActivity(intent);
				}
			});
		} else {
			cur_layout.setVisibility(View.GONE);
			current_pay.setText("当月还没有记录~~");
		}

	}

	/**
	 * 显示最近一次月结算记录
	 */
	private void showMonthNote() {
		List<MonthNote> monthNotes = MyApp.utils.getMonNotes();
		LinearLayout last_layout = (LinearLayout) findViewById(R.id.last_layout);
		if (monthNotes.size() > 0) {
			last_layout.setVisibility(View.VISIBLE);
			last_balanceTv.setText(StringUtils.showPrice(monthNotes.get(monthNotes.size() - 1).getActual_balance()) + " 元");
		} else {
			last_layout.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示最近一次收益的理财记录
	 */
	private void showIncomeNote() {
		List<IncomeNote> incomes = MyApp.utils.getIncomes();
		RelativeLayout income_layout = (RelativeLayout) findViewById(R.id.income_layout);
		if (incomes.size() > 0) {
			income_layout.setVisibility(View.VISIBLE);
			//显示最近一次收益的理财记录
			IncomeNote laterIncome = incomes.get(0);
			for (int i = 0; i < incomes.size(); i ++) {
				String date = incomes.get(i).getDurtion().split("-")[1];
				String lasterDate = laterIncome.getDurtion().split("-")[1];
				if (DateUtils.daysBetween(date) < DateUtils.daysBetween(lasterDate)) {
					laterIncome = incomes.get(i);
				}
			}

			LogUtils.i("laterIncome", laterIncome.toString());
			TextView income_id = (TextView) findViewById(R.id.income_id);
			TextView income_money = (TextView) findViewById(R.id.income_money);
			TextView income_finished = (TextView) findViewById(R.id.income_finished);
			TextView income_info = (TextView) findViewById(R.id.income_info);
			income_id.setText(laterIncome.getDurtion().split("-")[0].substring(4, 6));

			income_money.setText("投入金额： " + StringUtils.showPrice(laterIncome.getMoney()) + " 元\n" +
					"预期年化： " + StringUtils.formatePrice(laterIncome.getIncomeRatio()) + " %");
			income_info.setText("投资期限： " + laterIncome.getDays() + " 天\n" +
					"投资时期： " + laterIncome.getDurtion());

			if (laterIncome.getFinished() == 0) {
				int day = DateUtils.daysBetween(laterIncome.getDurtion().split("-")[1]);
				if (day < 0) {
					income_finished.setText("已经结束,请完成 >");
				} else if (day == 0) {
					income_finished.setText("今日到期！可完成 >");
				} else {
					income_finished.setText("计息中,还剩 " + DateUtils.daysBetween(laterIncome.getDurtion().split("-")[1]) + " 天");
				}
			}
			else {
				income_finished.setTextColor(Color.GRAY);
				income_finished.setText("已完成！");
			}
			income_money.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ListViewActivity.class);
					intent.putExtra("type", MyApp.INCOME);
					startActivity(intent);

				}
			});
		} else {
			income_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = new Intent(activity, ListViewActivity.class);
		switch (v.getId()) {
			case R.id.seenCheck:
				//密文明文显示
				if (isSeen) {
					isSeen = false;
					//设置EditText文本为隐藏的
					last_balanceTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					current_pay.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					isSeen = true;
					//设置EditText文本为可见的
					last_balanceTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
					current_pay.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				break;

			case R.id.current_pay:
			case R.id.todayNotes:
				intent.putExtra("type", MyApp.DAY);
				startActivity(intent);
				break;

			case R.id.last_balanceTv:
			case R.id.toMonthNotes:
				intent.putExtra("type", MyApp.MONTH);
				startActivity(intent);
				break;

			case R.id.commitDNote:
				startActivity(new Intent(activity, NewDayActivity.class));
				break;

			case R.id.commitMNote:
				startActivity(new Intent(activity, NewMonthActivity.class));
				break;

			case R.id.commitIncome:
				startActivity(new Intent(activity, NewIncomeActivity.class));
				break;
		}
	}

	/**
	 * 再按一次退出程序
	 */
	private long mExitTime;//返回退出时间间隔标志
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
