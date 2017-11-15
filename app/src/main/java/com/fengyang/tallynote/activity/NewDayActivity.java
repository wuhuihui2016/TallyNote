package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.DayNoteDao;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.utils.ToastUtils;

/**
 * 记日账
 */
public class NewDayActivity extends BaseActivity {

    private EditText moneyEt, remarkEt;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("记日账", R.layout.activity_day);

        initView();
    }

    private void initView() {
        moneyEt = (EditText) findViewById(R.id.moneyEt);
        remarkEt = (EditText) findViewById(R.id.remarkEt);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DayNote.getUserTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        type = position + 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        type = 1;
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.commitNote) {
            String money = StringUtils.formatePrice(moneyEt.getText().toString());
            String remark = remarkEt.getText().toString();

            if (!TextUtils.isEmpty(remark) && !TextUtils.isEmpty(money)) {
                final DayNote dayNote = new DayNote(type, money, remark, DateUtils.formatDateTime());
                LogUtils.i("commit", dayNote.toString());
                String message;
                message = DayNote.getUserType(dayNote.getUseType()) + StringUtils.showPrice(dayNote.getMoney()) + " " + dayNote.getRemark();
                DialogUtils.showMsgDialog(activity, "新增日账单", message,
                        new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                if (DayNoteDao.newDNote(dayNote)) {
                                    ToastUtils.showSucessLong(context, "新增日账单成功！");
                                    ExcelUtils.exportDayNote(null);
                                    if (getIntent().hasExtra("list")) {
                                        sendBroadcast(new Intent(ContansUtils.ACTION_DAY));
                                    } else {
                                        startActivity(new Intent(activity, DayListActivity.class));
                                    }
                                    finish();
                                } else ToastUtils.showErrorLong(context, "新增日账单失败！");
                            }
                        }, new DialogUtils.DialogListener() {
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

}
