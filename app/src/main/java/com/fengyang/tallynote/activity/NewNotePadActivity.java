package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.ToastUtils;
import com.fengyang.tallynote.view.FlowLayout;

import java.util.List;

/**
 * 新建记事本
 * Created by wuhuihui on 2017/8/02.
 */
public class NewNotePadActivity extends BaseActivity {

    private TextView note_tagTv, words_numTv;
    private FlowLayout flowLayout;
    private EditText wordsEt;

    private int tag;
    private String tagStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("想写点什么...", R.layout.activity_notepad);

        init();
    }

    private void init() {

        note_tagTv = (TextView) findViewById(R.id.note_tagTv);
        words_numTv = (TextView) findViewById(R.id.words_numTv);
        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        wordsEt = (EditText) findViewById(R.id.wordsEt);

        flowLayout.removeAllViews();//避免多次执行后出现重复多余View

        final List<String> tagList = NotePad.getTagList();
        for (int i = 0; i < tagList.size(); i++) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tag_view, null);
            TextView tagView = (TextView) view.findViewById(R.id.tagView);
            tagView.setText(tagList.get(i));
            final int finalI = i;
            tagView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < flowLayout.getChildCount(); i++) {
                        TextView textView = (TextView) flowLayout.getChildAt(i);
                        textView.setTextColor(Color.BLACK);
                    }
                    TextView textView = (TextView) flowLayout.getChildAt(finalI);
                    textView.setTextColor(Color.RED);
                    tag = finalI;
                    tagStr = tagList.get(finalI);
                    note_tagTv.setText(tagStr);
                }
            });
            flowLayout.addView(view);
        }

        wordsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                words_numTv.setText(s.length() + "/100字");
            }

            @Override
            public void afterTextChanged(Editable s) {
                words_numTv.setText(s.length() + "/100字");
                if (s.length() > 0) findViewById(R.id.right_btn).setEnabled(true);
                else findViewById(R.id.right_btn).setEnabled(false);
            }
        });

        setRightBtnListener("发表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = wordsEt.getText().toString();
                if (!TextUtils.isEmpty(words)) {
                    NotePad notePad = new NotePad(tag, wordsEt.getText().toString(), DateUtils.formatDateTime());
                    if (NotePadDao.newNotePad(notePad)) {
                        ToastUtils.showSucessLong(context, "发表成功！");
                        ExcelUtils.exportNotePad(null);
                        if (getIntent().hasExtra("list")) {
                            sendBroadcast(new Intent(ContansUtils.ACTION_NOTE));
                        } else {
                            startActivity(new Intent(activity, NotePadListActivity.class));
                        }
                        finish();
                    } else ToastUtils.showErrorLong(context, "发表失败！");
                } else ToastUtils.showToast(context, false, "请输入内容！");
            }
        });

    }

}
