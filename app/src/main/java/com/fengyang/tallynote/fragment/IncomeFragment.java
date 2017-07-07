package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.ListViewActivity;
import com.fengyang.tallynote.activity.NewIncomeActivity;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.view.IOSScrollView;

import java.util.List;

public class IncomeFragment extends Fragment{

	private static final String TAG = "IncomeFragment";
	private Activity activity;
	private View content;//内容布局

	private boolean isSeen = false;
	private TextView currIncomeSum;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		content = inflater.inflate(R.layout.fragment_income, container, false);
		LogUtils.i("fragment", TAG);
		activity = getActivity();
		initView();
		return content;
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

		showIncomeNote();

	}

	/**
	 * 显示最近一次收益的理财记录
	 */
	private void showIncomeNote() {

		List<IncomeNote> incomes = MyApp.utils.getIncomes();
		RelativeLayout income_layout = (RelativeLayout) content.findViewById(R.id.income_layout);
		currIncomeSum = (TextView) content.findViewById(R.id.currIncomeSum);
		if (incomes.size() > 0) {
			//显示当前未完成的理财投资的总金额
			Double sum = 0.00;
			for (int i = 0; i < incomes.size(); i ++) {
				if(incomes.get(i).getFinished() == 0) {
					sum += Double.parseDouble(incomes.get(i).getMoney());
				}
			}
			currIncomeSum.setText("当前投资总金额：" + StringUtils.showPrice(sum + ""));

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
			TextView income_id = (TextView) content.findViewById(R.id.income_id);
			TextView income_money = (TextView) content.findViewById(R.id.income_money);
			TextView income_finished = (TextView) content.findViewById(R.id.income_finished);
			TextView income_info = (TextView) content.findViewById(R.id.income_info);
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
					Intent intent = new Intent(activity, ListViewActivity.class);
					intent.putExtra("type", MyApp.INCOME);
					startActivity(intent);

				}
			});
		} else {
			income_layout.setVisibility(View.GONE);
		}

		content.findViewById(R.id.seenCheck).setOnClickListener(clickListener);
		content.findViewById(R.id.commitIncome).setOnClickListener(clickListener);
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
						currIncomeSum.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					} else {
						isSeen = true;
						seenCheck.setImageResource(R.drawable.eye_close_pwd);
						currIncomeSum.setTransformationMethod(PasswordTransformationMethod.getInstance());
					}
					break;

				case R.id.commitIncome:
					startActivity(new Intent(activity, NewIncomeActivity.class));
					break;
			}
		}
	};
}
