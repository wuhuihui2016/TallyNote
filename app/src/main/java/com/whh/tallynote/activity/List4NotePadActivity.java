package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.NotePadAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DelayTask;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.ViewUtils;
import com.whh.tallynote.view.FlowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 记事本列表
 * Created by wuhuihui on 2017/6/27.
 */
public class List4NotePadActivity extends BaseActivity {

    @BindView(R.id.flowLayout)
    public FlowLayout flowLayout;
    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    @BindView(R.id.reload)
    public ImageButton reload;

    private List<NotePad> notePads;
    private NotePadAdapter notePadAdapter;
    private boolean isFirst = true;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("我的记事本", R.layout.activity_notepad_list);
    }

    @Override
    protected void initView() {
        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

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

        reload.setOnClickListener(new View.OnClickListener() {
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
                AppManager.transfer(activity, NotePadDetailActivity.class, "notepad", notePads.get(position));
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    private int position4Detail = -1; //列表点击item的标志位

    //删除后刷新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
        if (msg.equals(ContansUtils.ACTION_NOTE)) {
            if (position4Detail != -1) {
                try {
                    //标志位被删除，刷新列表
                    notePads.remove(notePads.get(position4Detail));
                    notePadAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
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
                            AppManager.transfer(activity, NewNotePadActivity.class, "list", true);
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

    /**
     * 总记事本记录
     */
    private void getAll() {
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            TextView textView = (TextView) flowLayout.getChildAt(i);
            textView.setTextColor(Color.BLACK);
        }

        notePads = MyApp.notePadDBHandle.getNotePads();
        Collections.reverse(notePads);
        info.setText("我的记事本：" + notePads.size());
        notePadAdapter = new NotePadAdapter(activity, notePads, true);
        listView.setAdapter(notePadAdapter);

        //如果当前无记录，则跳转写记录页面
        if (isFirst && notePads.size() == 0) new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                isFirst = false;
                AppManager.transfer(activity, NewNotePadActivity.class, "list", true);
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

        notePads = MyApp.notePadDBHandle.getNotePads();
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
}
