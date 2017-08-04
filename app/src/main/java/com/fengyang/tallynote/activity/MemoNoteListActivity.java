package com.fengyang.tallynote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.MemoNoteAdapter;
import com.fengyang.tallynote.database.MemoNoteDao;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/27.
 */
public class MemoNoteListActivity extends BaseActivity {

    private TextView info;
    private ListView listView;

    private List<MemoNote> memoNotes;
    private MemoNoteAdapter memoNoteAdapter;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("我的备忘录", R.layout.activity_memonote_list);
        //删除后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_MEMO);
        registerReceiver(myReceiver, intentFilter);

        info = (TextView) findViewById(R.id.info);
        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindow();
                }
            }
        });

        getAll();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, MemoNoteDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("memoNote", memoNotes.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //删除后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_NOTE)) {
                getAll();
            }
        }
    };

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
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
                        Intent intent = new Intent(activity, NewMemoNoteActivity.class);
                        intent.putExtra("list", true);
                        startActivity(intent);
                    }
                }
        );
        layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ExcelUtils.exportNotePad(callBackExport);
            }
        });
        layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all:
                getAll();
                break;
            case R.id.ongoing:
                getAll4Status(0);
                break;
            case R.id.completed:
                getAll4Status(1);
                break;
        }
    }

    /**
     * 总备忘录记录
     */
    private void getAll() {
        memoNotes = MemoNoteDao.getMemoNotes();
        Collections.reverse(memoNotes);
        info.setText("我的备忘录：" + memoNotes.size());
        memoNoteAdapter = new MemoNoteAdapter(activity, memoNotes);
        listView.setAdapter(memoNoteAdapter);

        //如果当前无记录，则跳转写记录页面
        if (isFirst && memoNotes.size() == 0) new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                isFirst = false;
                Intent intent = new Intent(activity, NewMemoNoteActivity.class);
                intent.putExtra("list", true);
                startActivity(intent);
            }
        }).execute();
    }

    /*
     * 依据状态过滤显示
     */
    private void getAll4Status(int status) {
        memoNotes = MemoNoteDao.getMemoNotes();
        Collections.reverse(memoNotes);

        List<MemoNote> list = new ArrayList<>();
        for (int i = 0; i < memoNotes.size(); i++) {
            if (memoNotes.get(i).getStatus() == status) {
                list.add(memoNotes.get(i));
            }
        }
        if (status == MemoNote.ON) info.setText("进行中：" + list.size());
        else info.setText("已完成：" + list.size());
        memoNoteAdapter = new MemoNoteAdapter(activity, list);
        listView.setAdapter(memoNoteAdapter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
