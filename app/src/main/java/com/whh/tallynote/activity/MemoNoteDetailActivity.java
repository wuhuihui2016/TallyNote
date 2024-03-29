package com.whh.tallynote.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.MemoNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * 备忘录详情
 * Created by wuhuihui on 2017/8/02.
 */
public class MemoNoteDetailActivity extends BaseActivity {

    @BindView(R.id.time)
    public TextView time;
    @BindView(R.id.content)
    public TextView content;

    private MemoNote memoNote;
    public static final int REQUEST_TRANSFOR = 1;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("备忘录", R.layout.activity_memo_detail);
        memoNote = (MemoNote) getIntent().getSerializableExtra("memoNote");
    }

    @Override
    protected void initView() {

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

    @Override
    protected void initEvent() {

    }

    /**
     * 显示备忘录的状态
     *
     * @param isFinished
     */
    private void showStatus(boolean isFinished) {
        if (!isFinished) {
            setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    initToolPopup();
                }
            });
        } else {
            //增加删除线
            content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            setRightImgBtnListener(R.drawable.note_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                            "删除", new MyClickListener() {
                                @Override
                                public void onClick() {
                                    MyApp.memoNoteDBHandle.delMemoNote(memoNote);
                                    ExcelUtils.exportMemoNote(null);
                                    EventBus.getDefault().post(ContansUtils.ACTION_MEMO);
                                    finish();

                                }
                            }, "取消", new MyClickListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                }
            });
        }
    }


    /**
     * 初始化popupWindow
     */
    private void initToolPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_memo_tool_pop, null);
            popupWindow = new PopupWindow(layout, 200, 400);
            ViewUtils.setPopupWindow(activity, popupWindow);
            // 相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAsDropDown(findViewById(R.id.right_imgbtn), 50, 20);
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);

            layout.findViewById(R.id.editMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMemoActivity.class, "memoNote", memoNote, REQUEST_TRANSFOR);
                        }
                    }
            );

            layout.findViewById(R.id.finishMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            DialogUtils.showMsgDialog(activity, "是否确定完成此条备忘录",
                                    "提交", new MyClickListener() {
                                        @Override
                                        public void onClick() {
                                            MyApp.memoNoteDBHandle.finishMemoNote(memoNote);
                                            ExcelUtils.exportMemoNote(null);
                                            ToastUtils.showSucessLong(activity, "完成备忘录成功！");
                                            showStatus(true);
                                            EventBus.getDefault().post(ContansUtils.ACTION_MEMO);
                                        }
                                    }, "取消", new MyClickListener() {
                                        @Override
                                        public void onClick() {
                                        }
                                    });
                        }
                    }
            );

        }
    }


    /**
     * 参数回传，刷新数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_TRANSFOR) {
            memoNote = (MemoNote) data.getSerializableExtra("memoNote");
            LogUtils.i(TAG + "--onActivityResult", memoNote.toString());
            initView();
        }
    }
}
