package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.MemoNoteDao;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;

/**
 * 备忘录详情
 * Created by wuhuihui on 2017/8/02.
 */
public class MemoNoteDetailActivity extends BaseActivity {

    private TextView time, content;
    private MemoNote memoNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("备忘录", R.layout.activity_memo_detail);

        init();
    }

    private void init() {

        time = (TextView) findViewById(R.id.time);
        content = (TextView) findViewById(R.id.content);

        memoNote = (MemoNote) getIntent().getSerializableExtra("memoNote");

        if (memoNote != null) {
            time.setText(DateUtils.diffTime(memoNote.getTime()));
            content.setText(memoNote.getContent());

            if (memoNote.getStatus() == MemoNote.ON) {

                setRightImgBtnListener(R.drawable.memo_finished, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DialogUtils.showMsgDialog(activity, "完成提示", "是否确定完成此条备注", new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                MemoNoteDao.finishMemoNote(memoNote);
                                sendBroadcast(new Intent(ContansUtils.ACTION_MEMO));
                                finish();
                            }
                        }, new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                            }
                        });
                    }
                });
            } else {

                //增加删除线
                content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                setRightImgBtnListener(R.drawable.note_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除此条记录", new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                                MemoNoteDao.delMemoNote(memoNote);
                                sendBroadcast(new Intent(ContansUtils.ACTION_MEMO));
                                finish();

                            }
                        }, new DialogUtils.DialogListener() {
                            @Override
                            public void onClick(View v) {
                                super.onClick(v);
                            }
                        });
                    }
                });
            }

        } else finish();

    }

}
