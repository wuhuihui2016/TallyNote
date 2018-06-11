package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogListener;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;

/**
 * 记事本详情
 * Created by wuhuihui on 2017/8/02.
 */
public class NotePadDetailActivity extends BaseActivity {

    private TextView tag, time, words;
    private NotePad notePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("记事本", R.layout.activity_notepad_detail);

        init();
    }

    private void init() {

        tag = (TextView) findViewById(R.id.tag);
        time = (TextView) findViewById(R.id.time);
        words = (TextView) findViewById(R.id.words);

        notePad = (NotePad) getIntent().getSerializableExtra("notepad");

        if (notePad != null) {
            tag.setText(NotePad.getTagList().get(notePad.getTag()));
            time.setText(DateUtils.showTime4Detail(notePad.getTime()));
            words.setText(notePad.getWords());

            setRightImgBtnListener(R.drawable.note_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                            "删除", new DialogListener() {
                                @Override
                                public void onClick() {
                                    NotePadDao.delNotePad(notePad);
                                    ExcelUtils.exportNotePad(null);
                                    sendBroadcast(new Intent(ContansUtils.ACTION_NOTE));
                                    finish();

                                }
                            }, "取消", new DialogListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                }
            });

        } else finish();

    }

}
