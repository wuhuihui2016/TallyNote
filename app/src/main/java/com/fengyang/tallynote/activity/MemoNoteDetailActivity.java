package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.MemoNoteDao;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.ToastUtils;

/**
 * 备忘录详情
 * Created by wuhuihui on 2017/8/02.
 */
public class MemoNoteDetailActivity extends BaseActivity {

    private TextView time, content;
    private Button finishNote;
    private MemoNote memoNote;
    public static final int REQUEST_TRANSFOR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("备忘录", R.layout.activity_memo_detail);

        time = (TextView) findViewById(R.id.time);
        content = (TextView) findViewById(R.id.content);
        finishNote = (Button) findViewById(R.id.finishNote);

        memoNote = (MemoNote) getIntent().getSerializableExtra("memoNote");

        init();
    }

    private void init() {

        if (memoNote != null) {
            time.setText(DateUtils.showTime4Detail(memoNote.getTime()));
            content.setText(memoNote.getContent());

            if (memoNote.getStatus() == MemoNote.ON) {
                showStatus(false);
            } else {
                showStatus(true);
            }

        } else finish();

    }

    /**
     * 显示备忘录的状态
     * @param isFinished
     */
    private void showStatus(boolean isFinished) {
        if (!isFinished) {
            setRightImgBtnListener(R.drawable.note_edit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, NewMemoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("memoNote", memoNote);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_TRANSFOR);
                }
            });
            finishNote.setVisibility(View.VISIBLE);
            finishNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.showMsgDialog(activity, "完成提示", "是否确定完成此条备忘录", new DialogUtils.DialogListener() {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            if (MemoNoteDao.finishMemoNote(memoNote)) {
                                ExcelUtils.exportMemoNote(null);
                                ToastUtils.showSucessLong(context, "完成备忘录成功！");
                                showStatus(true);
                                sendBroadcast(new Intent(ContansUtils.ACTION_MEMO));
                            } else {
                                ToastUtils.showErrorLong(context, "完成备忘录失败！");
                            }
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
            finishNote.setVisibility(View.GONE);
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
    }

    /**
     * 参数回传，刷新数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_TRANSFOR){
            memoNote = (MemoNote) data.getSerializableExtra("memoNote");
            LogUtils.i(TAG + "--onActivityResult", memoNote.toString());
            init();
        }
    }
}
