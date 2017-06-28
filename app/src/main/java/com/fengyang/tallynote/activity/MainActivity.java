package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
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

		List<MonthNote> monthNotes = MyApp.utils.getMonNotes();
		LinearLayout last_layout = (LinearLayout) findViewById(R.id.last_layout);
		if (monthNotes.size() > 0) {
			last_layout.setVisibility(View.VISIBLE);
			last_balanceTv.setText(StringUtils.formate2Double(monthNotes.get(monthNotes.size() - 1).getActual_balance()));
		} else {
			last_layout.setVisibility(View.GONE);
		}

		List<DayNote> dayNotes = MyApp.utils.getDayNotes();
		LinearLayout cur_layout = (LinearLayout) findViewById(R.id.cur_layout);
		if (dayNotes.size() > 0) {
			//显示本次月记录总支出
			double sum = 0.00;
			for (int i = 0; i < dayNotes.size(); i ++) {
				sum = sum + Double.parseDouble(dayNotes.get(i).getMoney());
			}
			current_pay.setText(StringUtils.formate2Double(sum + ""));

			//显示最近一次日记录支出
			cur_layout.setVisibility(View.VISIBLE);
			DayNote dayNote = dayNotes.get(dayNotes.size() - 1);
			TextView usage = (TextView) findViewById(R.id.usage); usage.setText(DateUtils.diffTime(dayNote.getTime()) + " :" + dayNote.getUsage());
			TextView money = (TextView) findViewById(R.id.money); money.setText(StringUtils.formate2Double(dayNote.getMoney()));
			if (! TextUtils.isEmpty(dayNote.getRemark())) {
				TextView remask = (TextView) findViewById(R.id.remask);
				remask.setText(dayNote.getRemark());
			}
		} else {
			cur_layout.setVisibility(View.GONE);
			current_pay.setText("当月还没有记录~~");
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = new Intent(activity, ListViewActivity.class);
		switch (v.getId()) {
			case  R.id.seenCheck:
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

			case  R.id.todayNotes:
				intent.putExtra("isDayList", true);
				startActivity(intent);
				break;

			case  R.id.toMonthNotes:
				intent.putExtra("isDayList", false);
				startActivity(intent);
				break;

			case  R.id.commitDNote:
				startActivity(new Intent(activity, DayActivity.class));
				break;

			case  R.id.commitMNote:
				startActivity(new Intent(activity, MonthActivity.class));
				break;
		}
	}
}
