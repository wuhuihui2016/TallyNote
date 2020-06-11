package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.view.FlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * 新建记事本
 * Created by wuhuihui on 2017/8/02.
 */
public class NewNotePadActivity extends BaseActivity {

    @BindView(R.id.note_tagTv)
    public TextView note_tagTv;
    @BindView(R.id.words_numTv)
    public TextView words_numTv;
    @BindView(R.id.flowLayout)
    public FlowLayout flowLayout;
    @BindView(R.id.wordsEt)
    public EditText wordsEt;
    @BindView(R.id.right_btn)
    public Button right_btn;

    private int tag;
    private String tagStr;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("想写点什么...", R.layout.activity_notepad);
    }

    @Override
    protected void initView() {


        flowLayout.removeAllViews();//避免多次执行后出现重复多余View

        final List<String> tagList = NotePad.getTagList();
        note_tagTv.setText("【" + tagList.get(0)+ "】");
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
                    note_tagTv.setText("【" + tagStr + "】");
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
                if (s.length() > 0) right_btn.setEnabled(true);
                else right_btn.setEnabled(false);
            }
        });

        setRightBtnListener("发表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = wordsEt.getText().toString();
                if (!TextUtils.isEmpty(words)) {
                    NotePad notePad = new NotePad(tag, wordsEt.getText().toString(), DateUtils.formatDateTime());
                    MyApp.notePadDBHandle.saveNotePad(notePad);
                        ToastUtils.showSucessLong(activity, "发表成功！");
                        ExcelUtils.exportNotePad(null);
                        if (getIntent().hasExtra("list")) {
                            EventBus.getDefault().post(ContansUtils.ACTION_NOTE);
                        } else {
                            AppManager.transfer(activity, List4NotePadActivity.class);
                        }
                        finish();
                } else ToastUtils.showToast(context, false, "请输入内容！");
            }
        });

    }

    @Override
    protected void initEvent() {

    }

}
