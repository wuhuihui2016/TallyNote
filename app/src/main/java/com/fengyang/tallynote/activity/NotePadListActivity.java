package com.fengyang.tallynote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.NotePadAdapter;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.ExcelUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.ViewUtils;
import com.fengyang.tallynote.view.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 记事本列表
 * Created by wuhuihui on 2017/6/27.
 */
public class NotePadListActivity extends BaseActivity {

    private FlowLayout flowLayout;
    private TextView info;
    private ListView listView;

    private List<NotePad> notePads;
    private NotePadAdapter notePadAdapter;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("我的记事本", R.layout.activity_notepad_list);
        //删除后广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ContansUtils.ACTION_NOTE);
        registerReceiver(myReceiver, intentFilter);

        info = (TextView) findViewById(R.id.info);
        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
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
                    getAll4Tag(finalI);
                }
            });
            flowLayout.addView(view);
        }

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAll();
            }
        });

        getAll();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position4Detail = position; //记录标志位
                Intent intent = new Intent(context, NotePadDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("notepad", notePads.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private int position4Detail = -1; //列表点击item的标志位

    //删除后刷新界面
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ContansUtils.ACTION_NOTE)) {
                if (position4Detail != -1) {
                    try {
                        //标志位被删除，刷新列表
                        notePads.remove(notePads.get(position4Detail));
                        notePadAdapter.notifyDataSetChanged();
                    } catch (Exception e){

                    }
                }
            }
        }
    };

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
                            Intent intent = new Intent(activity, NewNotePadActivity.class);
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
            layout.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    FileUtils.uploadFile(activity);
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

    /**
     * 总记事本记录
     */
    private void getAll() {
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            TextView textView = (TextView) flowLayout.getChildAt(i);
            textView.setTextColor(Color.BLACK);
        }

        notePads = NotePadDao.getNotePads();
        Collections.reverse(notePads);
        info.setText("我的记事本：" + notePads.size());
        notePadAdapter = new NotePadAdapter(activity, notePads, true);
        listView.setAdapter(notePadAdapter);

        //如果当前无记录，则跳转写记录页面
        if (isFirst && notePads.size() == 0) new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                isFirst = false;
                Intent intent = new Intent(activity, NewNotePadActivity.class);
                intent.putExtra("list", true);
                startActivity(intent);
            }
        }).execute();
    }

    /*
     * 依据标签过滤显示
     */
    private void getAll4Tag(int tag) {

        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            TextView textView = (TextView) flowLayout.getChildAt(i);
            textView.setTextColor(Color.BLACK);
        }
        TextView textView = (TextView) flowLayout.getChildAt(tag);
        textView.setTextColor(Color.RED);

        notePads = NotePadDao.getNotePads();
        Collections.reverse(notePads);

        List<NotePad> list = new ArrayList<>();
        for (int i = 0; i < notePads.size(); i++) {
            if (notePads.get(i).getTag() == tag) {
                list.add(notePads.get(i));
            }
        }
        info.setText(NotePad.getTagList().get(tag) + "：" + list.size());
        notePadAdapter = new NotePadAdapter(activity, list, false);
        listView.setAdapter(notePadAdapter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }
}
