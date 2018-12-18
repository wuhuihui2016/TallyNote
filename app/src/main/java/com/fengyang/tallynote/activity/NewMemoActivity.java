package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.MemoNoteDao;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.ToastUtils;

/**
 * 新建备忘录
 * Created by wuhuihui on 2017/8/02.
 */
public class NewMemoActivity extends BaseActivity {

    private TextView words_numTv;
    private EditText contentEt;
    private boolean isAlter = false;
    private MemoNote isAlterMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("接下来要做什么...", R.layout.activity_memo);

        init();
    }

    private void init() {

        words_numTv = (TextView) findViewById(R.id.words_numTv);
        contentEt = (EditText) findViewById(R.id.contentEt);

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
                if (s.length() > 0) findViewById(R.id.right_btn).setEnabled(true);
                else findViewById(R.id.right_btn).setEnabled(false);
            }
        });

        setRightBtnListener("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = contentEt.getText().toString();
                if (!TextUtils.isEmpty(words)) {
                    if (isAlter) {
                        isAlterMemo.setContent(words);
                        if (MemoNoteDao.alterMemoNote(isAlterMemo)) {
                            ToastUtils.showSucessLong(activity, "编辑成功！");
                            ExcelUtils.exportMemoNote(null);

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("memoNote", isAlterMemo);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                            finish();
                        } else {
                            ToastUtils.showErrorLong(activity, "编辑失败！");
                        }

                    } else {
                        MemoNote memoNote = new MemoNote(contentEt.getText().toString(), DateUtils.formatDateTime());
                        if (MemoNoteDao.newMemoNote(memoNote)) {
                            ToastUtils.showSucessLong(activity, "保存成功！");
                            ExcelUtils.exportMemoNote(null);
                            if (getIntent().hasExtra("list")) {
                                sendBroadcast(new Intent(ContansUtils.ACTION_MEMO));
                            } else {
                                startActivity(new Intent(activity, MemoNoteListActivity.class));
                            }
                            finish();
                        } else ToastUtils.showErrorLong(activity, "保存失败！");
                    }
                } else ToastUtils.showToast(context, false, "请输入内容！");
            }
        });

    }

}
