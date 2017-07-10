package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.DayListActivity;
import com.fengyang.tallynote.activity.MonthListActivity;
import com.fengyang.tallynote.activity.NewDayActivity;
import com.fengyang.tallynote.activity.NewMonthActivity;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.view.IOSScrollView;

import java.util.Calendar;
import java.util.List;

public class TallyFragment extends Fragment{

	private static final String TAG = "TallyFragment";
	private Activity activity;
	private View content;//内容布局

	private TextView year, month, day, last_balanceTv, current_pay;
	private boolean isSeen = false;

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
		content = inflater.inflate(R.layout.fragment_tally, container, false);
		activity = getActivity();
		return content;
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		//当页面滑动到中间位置，导致看不到顶部时显示到达顶部的按钮
		final IOSScrollView scrollView = (IOSScrollView) content.findViewById(R.id.scrollView);
		final ImageButton top_btn = (ImageButton) content.findViewById(R.id.top_btn);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
				@Override
				public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
					if (scrollY > 500) {
						top_btn.setVisibility(View.VISIBLE);
						top_btn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								scrollView.fullScroll(ScrollView.FOCUS_UP);
							}
						});
					} else {
						top_btn.setVisibility(View.GONE);
					}
				}
			});
		}

		year = (TextView) content.findViewById(R.id.year);
		month = (TextView) content.findViewById(R.id.month);
		day = (TextView) content.findViewById(R.id.day);

		last_balanceTv = (TextView) content.findViewById(R.id.last_balanceTv);
		last_balanceTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		current_pay = (TextView) content.findViewById(R.id.current_pay);
		current_pay.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

		Calendar calendar = Calendar.getInstance();
		year.setText(calendar.get(Calendar.YEAR) + ""); // 获取当前年份
		month.setText((calendar.get(Calendar.MONTH) + 1) + ""); // 获取当前月份
		day.setText(calendar.get(Calendar.DAY_OF_MONTH) + ""); // 获取当前日

		showDayNote();
		showMonthNote();

		clickListener();

	}

	/**
	 * 显示最近一次日账单记录
	 */
	private void showDayNote() {
		List<DayNote> dayNotes = MyApp.utils.getDayNotes();
		LinearLayout cur_layout = (LinearLayout) content.findViewById(R.id.cur_layout);
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
			TextView time = (TextView) content.findViewById(R.id.time); time.setText(DateUtils.diffTime(dayNote.getTime()));
			String dayType = null;
			if (dayNote.getUseType() == DayNote.consume) dayType = "支出";
			if (dayNote.getUseType() == DayNote.account_out) dayType = "转账";
			if (dayNote.getUseType() == DayNote.account_in) dayType = "转入";
			TextView usage = (TextView) content.findViewById(R.id.usage); usage.setText(dayType);
			TextView money = (TextView) content.findViewById(R.id.money); money.setText(StringUtils.showPrice(dayNote.getMoney()) + " 元");
			if (! TextUtils.isEmpty(dayNote.getRemark())) {
				TextView remask = (TextView) content.findViewById(R.id.remask);
				remask.setText(dayNote.getRemark());
			}

			content.findViewById(R.id.item_day_layout).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(activity, DayListActivity.class));
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
		LinearLayout last_layout = (LinearLayout) content.findViewById(R.id.last_layout);
		if (monthNotes.size() > 0) {
			last_layout.setVisibility(View.VISIBLE);
			last_balanceTv.setText(StringUtils.showPrice(monthNotes.get(monthNotes.size() - 1).getActual_balance()) + " 元");
		} else {
			last_layout.setVisibility(View.GONE);
		}
	}


	private void clickListener () {
		content.findViewById(R.id.seenCheck).setOnClickListener(clickListener);
		content.findViewById(R.id.current_pay).setOnClickListener(clickListener);
		content.findViewById(R.id.todayNotes).setOnClickListener(clickListener);
		content.findViewById(R.id.last_balanceTv).setOnClickListener(clickListener);
		content.findViewById(R.id.toMonthNotes).setOnClickListener(clickListener);
		content.findViewById(R.id.commitDNote).setOnClickListener(clickListener);
		content.findViewById(R.id.commitMNote).setOnClickListener(clickListener);

	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ImageButton seenCheck = (ImageButton) content.findViewById(R.id.seenCheck);
			switch (v.getId()) {
				case R.id.seenCheck:
					//密文明文显示
					if (isSeen) {
						isSeen = false;
						seenCheck.setImageResource(R.drawable.eye_open_pwd);
						//设置EditText文本为隐藏的
						last_balanceTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						current_pay.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					} else {
						isSeen = true;
						seenCheck.setImageResource(R.drawable.eye_close_pwd);
						//设置EditText文本为可见的
						last_balanceTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
						current_pay.setTransformationMethod(PasswordTransformationMethod.getInstance());
					}
					break;

				case R.id.current_pay:
				case R.id.todayNotes:
					startActivity(new Intent(activity, DayListActivity.class));
					break;

				case R.id.last_balanceTv:
				case R.id.toMonthNotes:
					startActivity(new Intent(activity, MonthListActivity.class));
					break;

				case R.id.commitDNote:
					startActivity(new Intent(activity, NewDayActivity.class));
					break;

				case R.id.commitMNote:
					startActivity(new Intent(activity, NewMonthActivity.class));
					break;
			}
		}
	};

}
