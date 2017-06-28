package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.StringUtils;

public class DayActivity extends BaseActivity{

	private EditText usageEt, moneyEt, remarkEt;
	private Button commitNote;

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
		commitNote = (Button) findViewById(R.id.commitNote);

		setRightBtnListener("新建月账单", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity, MonthActivity.class));
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.commitNote) {
			String usage = usageEt.getText().toString();
			String money = StringUtils.formateDouble(moneyEt.getText().toString());
			String remark = remarkEt.getText().toString();

			if (! TextUtils.isEmpty(usage) && ! TextUtils.isEmpty(money)) {
				final DayNote dayNote = new DayNote(usage, money, remark, DateUtils.formatDateTime());
				LogUtils.i("commit", dayNote.toString());
				DialogUtils.showMsgDialog(activity, "新增日账单",
						dayNote.getUsage() + "：" + dayNote.getMoney() + "\n" + dayNote.getRemark(),
						new DialogUtils.DialogListener(){
							@Override
							public void onClick(View v) {
								super.onClick(v);
								if (MyApp.utils.newDNote(dayNote)) {
									StringUtils.show1Toast(activity, "新增日账单成功！");
									finish();
								} else StringUtils.show1Toast(activity, "新增日账单失败！");
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
		PermissionUtils.checkSDcardPermission(DayActivity.this, new PermissionUtils.OnCheckCallback() {
			@Override
			public void onCheck(boolean isSucess) {
				commitNote.setEnabled(isSucess);
				if (! isSucess) {
					PermissionUtils.notPermission(DayActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
					StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
				}
			}
		});
	}
}
