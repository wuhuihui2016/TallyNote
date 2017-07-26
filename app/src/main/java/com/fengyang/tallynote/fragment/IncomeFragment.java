package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.IncomeListActivity;
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
	private String sumStr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		content = inflater.inflate(R.layout.fragment_income, container, false);
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

		showIncomeNote();

	}

	/**
	 * 显示最近一次收益的理财记录
	 */
	IncomeNote laterIncome = null;
	private void showIncomeNote() {

		List<IncomeNote> unFinisheds = IncomeNote.getUnFinished(); //未完成的
		currIncomeSum = (TextView) content.findViewById(R.id.currIncomeSum);
		LinearLayout income_layout = (LinearLayout) content.findViewById(R.id.income_layout);
		if (unFinisheds.size() > 0) {
			//显示当前未完成的理财投资的总金额
			Double sum = 0.00;
			for (int i = 0; i < unFinisheds.size(); i ++) {
				sum += Double.parseDouble(unFinisheds.get(i).getMoney());
			}

			sumStr = "当前投资总金额：" + StringUtils.showPrice(sum + "");
			currIncomeSum.setText("....");

			income_layout.setVisibility(View.VISIBLE);
			content.findViewById(R.id.detail_layout).setVisibility(View.GONE);
			content.findViewById(R.id.income_finished).setVisibility(View.GONE);

			//显示最近一次收益的理财记录
			laterIncome = unFinisheds.get(0);
			for (int i = 0; i < unFinisheds.size(); i ++) {
				String date = unFinisheds.get(i).getDurtion().split("-")[1];
				String lasterDate = laterIncome.getDurtion().split("-")[1];
				if (DateUtils.daysBetween(date) < DateUtils.daysBetween(lasterDate)) {
					laterIncome = unFinisheds.get(i);
				}
			}

			LogUtils.i("laterIncome", laterIncome.toString());
			TextView income_time = (TextView) content.findViewById(R.id.income_time);
			TextView income_money = (TextView) content.findViewById(R.id.income_money);
			TextView income_ratio = (TextView) content.findViewById(R.id.income_ratio);

			String id = laterIncome.getId();
			String time = laterIncome.getDurtion().split("-")[1].substring(4, 8);
			SpannableStringBuilder style = new SpannableStringBuilder(id + "\n" + time);
			style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, id.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new ForegroundColorSpan(Color.RED), id.length(), (id + "\n" + time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			income_time.setText(style);
			income_money.setText(StringUtils.showPrice(laterIncome.getMoney()));
			income_ratio.setText(laterIncome.getIncomeRatio() + " %");

			income_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, IncomeListActivity.class);
					intent.putExtra("position", MyApp.utils.getIncomes().size() - Integer.parseInt(laterIncome.getId()));
					startActivity(intent);

				}
			});
		} else {
			sumStr = "暂无投资";
			currIncomeSum.setText("....");
			income_layout.setVisibility(View.GONE);
		}

		content.findViewById(R.id.seenCheck).setOnClickListener(clickListener);
		content.findViewById(R.id.reload).setOnClickListener(clickListener);
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
						currIncomeSum.setText(sumStr);
					} else {
						isSeen = true;
						seenCheck.setImageResource(R.drawable.eye_close_pwd);
						currIncomeSum.setText("....");

					}
					break;

				case R.id.reload:
					LogUtils.i("reload", "showIncomeNote");
					showIncomeNote();
					break;

				case R.id.commitIncome:
					startActivity(new Intent(activity, NewIncomeActivity.class));
					break;
			}
		}
	};
}
