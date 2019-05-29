package com.whh.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.whh.tallynote.R;
import com.whh.tallynote.database.DayNoteDao;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.DecimalInputTextWatcher;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

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
        moneyEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        moneyEt.addTextChangedListener(new DecimalInputTextWatcher(moneyEt));//小数最多2位

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

        setRightBtnListener("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = StringUtils.formatePrice(moneyEt.getText().toString());
                String remark = remarkEt.getText().toString();

                if (!TextUtils.isEmpty(remark) && !TextUtils.isEmpty(money)) {
                    final DayNote dayNote = new DayNote(type, money, remark, DateUtils.formatDateTime());
                    LogUtils.i("commit", dayNote.toString());
                    String message;
                    message = dayNote.getRemark() + DayNote.getUserType(dayNote.getUseType()) + StringUtils.showPrice(dayNote.getMoney());
                    DialogUtils.showMsgDialog(activity, "提交日账单\n" + message,
                            "提交", new DialogListener() {
                                @Override
                                public void onClick() {
                                    if (DayNoteDao.newDNote(dayNote)) {
                                        ToastUtils.showSucessLong(activity, "提交日账单成功！");
                                        ExcelUtils.exportDayNote(null);
                                        if (getIntent().hasExtra("list")) {
                                            sendBroadcast(new Intent(ContansUtils.ACTION_DAY));
                                        } else {
                                            startActivity(new Intent(activity, DayListActivity.class));
                                        }
                                        finish();
                                    } else ToastUtils.showErrorLong(activity, "提交日账单失败！");
                                }
                            },  "取消", new DialogListener() {
                                @Override
                                public void onClick() {

                                }
                            });

                } else {
                    ToastUtils.showToast(context, true, "请完善必填信息！");
                }
            }
        });

    }

}
