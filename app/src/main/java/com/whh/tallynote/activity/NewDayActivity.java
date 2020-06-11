package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.DecimalInputTextWatcher;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * 记日账
 */
public class NewDayActivity extends BaseActivity {

    @BindView(R.id.spinner)
    public Spinner spinner;
    @BindView(R.id.moneyEt)
    public EditText moneyEt;
    @BindView(R.id.remarkEt)
    public EditText remarkEt;

    private int type;

    private boolean isEditDayNote;
    private DayNote editDayNote;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("记日账", R.layout.activity_day);
    }

    @Override
    protected void initView() {

        if (getIntent().hasExtra("editDayNote")) {
            isEditDayNote = true;
            setTitle("编辑日账");
            editDayNote = (DayNote) getIntent().getSerializableExtra("editDayNote");
            LogUtils.e("whh0611", "editDayNote: getUserType = " + DayNote.getUserTypeStr(editDayNote.getUseType()));
        }

        moneyEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        moneyEt.addTextChangedListener(new DecimalInputTextWatcher(moneyEt));//小数最多2位

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DayNote.getUserTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        type = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        type = 0;
                    }
                }
        );

        if (isEditDayNote) {
            spinner.setSelection(editDayNote.getUseType()); //消费类型
            moneyEt.setText(editDayNote.getMoney()); //消费金额
            remarkEt.setText(editDayNote.getRemark()); //消费说明

            setRightBtnListener("修改", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String money = StringUtils.formatePrice(moneyEt.getText().toString());
                    String remark = remarkEt.getText().toString();

                    if (!TextUtils.isEmpty(remark) && !TextUtils.isEmpty(money)) {
                        editDayNote.setUseType(type);
                        editDayNote.setMoney(money);
                        editDayNote.setRemark(remark);
                        String message;
                        message = editDayNote.getRemark() + DayNote.getUserTypeStr(editDayNote.getUseType()) + StringUtils.showPrice(editDayNote.getMoney());
                        DialogUtils.showMsgDialog(activity, "修改日账\n" + message,
                                "修改", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                        MyApp.dayNoteDBHandle.updateDayNote(editDayNote);
                                        ToastUtils.showSucessLong(activity, "日账修改成功！");
                                        ExcelUtils.exportDayNote(null);
                                        if (getIntent().hasExtra("list")) {
                                            EventBus.getDefault().post(ContansUtils.ACTION_DAY);
                                        } else {
                                            AppManager.transfer(activity, List4DayActivity.class);
                                        }
                                        finish();
                                    }
                                }, "取消", new DialogListener() {
                                    @Override
                                    public void onClick() {

                                    }
                                });

                    } else {
                        ToastUtils.showToast(context, true, "请完善必填信息！");
                    }
                }
            });

            //删除日账
            Button btn_del_daynote = findViewById(R.id.btn_del_daynote);
            btn_del_daynote.setVisibility(View.VISIBLE);
            btn_del_daynote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                            "删除", new DialogListener() {
                                @Override
                                public void onClick() {
                                    MyApp.dayNoteDBHandle.delDayNote(editDayNote);
                                    ToastUtils.showSucessLong(activity, "日账已删除！");
                                    ExcelUtils.exportDayNote(null);
                                    if (getIntent().hasExtra("list")) {
                                        EventBus.getDefault().post(ContansUtils.ACTION_DAY);
                                    } else {
                                        AppManager.transfer(activity, List4DayActivity.class);
                                    }
                                    finish();
                                }
                            },
                            "取消", new DialogListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                }
            });


        } else {
            setRightBtnListener("提交", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String money = StringUtils.formatePrice(moneyEt.getText().toString());
                    String remark = remarkEt.getText().toString();

                    if (!TextUtils.isEmpty(remark) && !TextUtils.isEmpty(money)) {
                        final DayNote dayNote = new DayNote(type, money, remark, DateUtils.formatDateTime());
                        LogUtils.i("commit", dayNote.toString());
                        String message;
                        message = dayNote.getRemark() + DayNote.getUserTypeStr(dayNote.getUseType()) + StringUtils.showPrice(dayNote.getMoney());
                        DialogUtils.showMsgDialog(activity, "提交日账\n" + message,
                                "提交", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                        MyApp.dayNoteDBHandle.saveDayNote(dayNote);
                                        ToastUtils.showSucessLong(activity, "日账提交成功！");
                                        ExcelUtils.exportDayNote(null);
                                        if (getIntent().hasExtra("list")) {
                                            EventBus.getDefault().post(ContansUtils.ACTION_DAY);
                                        } else {
                                            AppManager.transfer(activity, List4DayActivity.class);
                                        }
                                        finish();
                                    }
                                }, "取消", new DialogListener() {
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

    @Override
    protected void initEvent() {

    }

}
