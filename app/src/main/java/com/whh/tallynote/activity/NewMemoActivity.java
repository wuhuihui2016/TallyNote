package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.MemoNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * 新建备忘录
 * Created by wuhuihui on 2017/8/02.
 */
public class NewMemoActivity extends BaseActivity {

    @BindView(R.id.words_numTv)
    public TextView words_numTv;
    @BindView(R.id.contentEt)
    public EditText contentEt;
    @BindView(R.id.right_btn)
    public Button right_btn;

    private boolean isAlter = false;
    private MemoNote isAlterMemo;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("接下来要做什么...", R.layout.activity_memo);
    }

    @Override
    protected void initView() {

        if (getIntent().hasExtra("memoNote")) {
            isAlter = true;
            isAlterMemo = (MemoNote) getIntent().getSerializableExtra("memoNote");
            String content = isAlterMemo.getContent();
            contentEt.setText(content);
            contentEt.setSelection(content.length());
            words_numTv.setText(content.length() + "/200字");
        }

        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                words_numTv.setText(s.length() + "/200字");
            }

            @Override
            public void afterTextChanged(Editable s) {
                words_numTv.setText(s.length() + "/200字");
                if (s.length() > 0) right_btn.setEnabled(true);
                else right_btn.setEnabled(false);
            }
        });

        setRightBtnListener("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = contentEt.getText().toString();
                if (!TextUtils.isEmpty(words)) {
                    if (isAlter) {
                        isAlterMemo.setContent(words);
                        MyApp.memoNoteDBHandle.updateMemoNote(isAlterMemo);
                        ToastUtils.showSucessLong(activity, "编辑成功！");
                        ExcelUtils.exportMemoNote(null);

                        EventBus.getDefault().post(ContansUtils.ACTION_MEMO);
                        finish();

                    } else {
                        MemoNote memoNote = new MemoNote(contentEt.getText().toString(), DateUtils.formatDateTime());
                        MyApp.memoNoteDBHandle.saveMemoNote(memoNote);
                        ToastUtils.showSucessLong(activity, "保存成功！");
                        ExcelUtils.exportMemoNote(null);
                        if (getIntent().hasExtra("list")) {
                            EventBus.getDefault().post(ContansUtils.ACTION_MEMO);
                        } else {
                            AppManager.transfer(activity, List4MemoNoteActivity.class);
                        }
                        finish();
                    }
                } else ToastUtils.showToast(context, false, "请输入内容！");
            }
        });

    }

    @Override
    protected void initEvent() {

    }

}
