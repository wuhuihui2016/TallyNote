package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;

public class NewDayActivity extends BaseActivity{

	private EditText usageEt, moneyEt, remarkEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("记日账", R.layout.activity_day);

		initView();
	}

	private void initView() {
		usageEt = (EditText) findViewById(R.id.usageEt);
		moneyEt = (EditText) findViewById(R.id.moneyEt);
		remarkEt = (EditText) findViewById(R.id.remarkEt);

		setRightBtnListener("新建月账单", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity, NewMonthActivity.class));
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.commitNote) {
			String usage = usageEt.getText().toString();
			String money = StringUtils.formatePrice(moneyEt.getText().toString());
			String remark = remarkEt.getText().toString();

			if (! TextUtils.isEmpty(usage) && ! TextUtils.isEmpty(money)) {
				final DayNote dayNote = new DayNote(usage, money, remark, DateUtils.formatDateTime());
				LogUtils.i("commit", dayNote.toString());
				String message;
				if (remark.length() > 0) message = dayNote.getUsage() + "：" + StringUtils.showPrice(dayNote.getMoney()) + " 元 (" + dayNote.getRemark() + ")";
				else message = dayNote.getUsage() + "：" + StringUtils.showPrice(dayNote.getMoney());
				DialogUtils.showMsgDialog(activity, "新增日账单", message,
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.newDNote(dayNote)) {
									StringUtils.show1Toast(activity, "新增日账单成功！");
									ExcelUtils.exportDayNote(activity);
									finish();
								} else StringUtils.show1Toast(activity, "新增日账单失败！");
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
