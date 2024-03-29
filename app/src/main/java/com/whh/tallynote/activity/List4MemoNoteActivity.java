package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.MemoNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.MemoNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DelayTask;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 我的备忘录
 * Created by wuhuihui on 2017/6/27.
 */
public class List4MemoNoteActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.all)
    public TextView all;
    @BindView(R.id.ongoing)
    public TextView ongoing;
    @BindView(R.id.completed)
    public TextView completed;

    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private List<MemoNote> memoNotes;
    private MemoNoteAdapter memoNoteAdapter;
    private boolean isFirst = true;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("我的备忘录", R.layout.activity_memonote_list);
    }

    @Override
    protected void initView() {
        listView.setEmptyView(emptyView);

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppManager.transfer(activity, MemoNoteDetailActivity.class,"memoNote", memoNotes.get(position));
            }
        });
    }

    @Override
    protected void initEvent() {
        getAll();
    }

    //删除后刷新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
        if (msg.equals(ContansUtils.ACTION_MEMO)) {
            isFirst = false;
            getAll();
        }
    }


    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater inflater = (LayoutInflater) getApplication()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_list_pop, null);
            popupWindow = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            ViewUtils.setPopupWindow(context, popupWindow);
            popupWindow.showAtLocation(findViewById(R.id.list_layout), Gravity.BOTTOM, 0, 0);

            layout.findViewById(R.id.newNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMemoActivity.class, "list", true);
                        }
                    }
            );
            layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    ExcelUtils.exportMemoNote(callBackExport);
                }
            });
            layout.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    FileUtils.uploadFile2WXCollect(activity);
                }
            });
            layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all:
                getAll();
                break;
            case R.id.ongoing:
                getAll4Status(MemoNote.ON);
                break;
            case R.id.completed:
                getAll4Status(MemoNote.OFF);
                break;
        }
    }

    /**
     * 总备忘录记录
     */
    private void getAll() {
        all.setTextColor(Color.RED);
        ongoing.setTextColor(Color.GRAY);
        completed.setTextColor(Color.GRAY);
        memoNotes = MyApp.memoNoteDBHandle.getMemoNotes();
        Collections.reverse(memoNotes);
        info.setText("我的备忘录：" + memoNotes.size());
        memoNoteAdapter = new MemoNoteAdapter(activity, memoNotes);
        listView.setAdapter(memoNoteAdapter);

        //如果当前无记录，则跳转写记录页面
        if (isFirst && memoNotes.size() == 0) new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                isFirst = false;
                AppManager.transfer(activity, NewMemoActivity.class,"list", true);
            }
        }).execute();
    }

    /*
     * 依据状态过滤显示
     */
    private void getAll4Status(int status) {
        List<MemoNote> list;
        if (status == MemoNote.ON) {
            all.setTextColor(Color.GRAY);
            ongoing.setTextColor(Color.RED);
            completed.setTextColor(Color.GRAY);
            list = MemoNote.getUnFinish();
            info.setText("进行中：" + list.size());
        } else {
            all.setTextColor(Color.GRAY);
            ongoing.setTextColor(Color.GRAY);
            completed.setTextColor(Color.RED);
            list = MemoNote.getFinished();
            info.setText("已完成：" + list.size());
        }
        memoNoteAdapter = new MemoNoteAdapter(activity, list);
        listView.setAdapter(memoNoteAdapter);

    }

}
