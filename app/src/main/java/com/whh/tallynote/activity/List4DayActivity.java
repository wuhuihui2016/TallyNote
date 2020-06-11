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
import com.whh.tallynote.adapter.DayNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 日账单明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4DayActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.all)
    public TextView all;
    @BindView(R.id.consume)
    public TextView consume;
    @BindView(R.id.account_out)
    public TextView account_out;
    @BindView(R.id.account_in)
    public TextView account_in;
    @BindView(R.id.homeuse)
    public TextView homeuse;

    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private List<DayNote> dayNotes;
    private List<DayNote> list = new ArrayList<>();
    private DayNoteAdapter dayNoteAdapter;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("编辑日账单", R.layout.activity_day_list);
    }

    @Override
    protected void initView() {
        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });
    }

    @Override
    protected void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppManager.transfer(activity, NewDayActivity.class, "editDayNote", dayNotes.get(position));
            }
        });
    }

    //删除后刷新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
        if (msg.equals(ContansUtils.ACTION_DAY)) {
            getAll();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAll();
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
                            AppManager.transfer(activity, NewDayActivity.class,"list", true);
                        }
                    }
            );
            layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    ExcelUtils.exportDayNote(callBackExport);
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all:
                getAll();
                break;
            case R.id.consume:
                getAll4UseType(DayNote.consume);
                break;
            case R.id.high_consume:
                AppManager.transfer(activity, List4DayOfHighConsumeActivity.class);
                break;
            case R.id.account_out:
                getAll4UseType(DayNote.account_out);
                break;
            case R.id.account_in:
                getAll4UseType(DayNote.account_in);
                break;
            case R.id.homeuse:
                getAll4UseType(DayNote.homeuse);
                break;
        }
    }

    /**
     * 总账单记录
     */
    private void getAll() {
        all.setTextColor(Color.parseColor("#007500"));
        all.setBackgroundResource(R.drawable.shape_type_btn);
        consume.setTextColor(Color.GRAY);
        consume.setBackgroundResource(R.drawable.shape_type);
        account_out.setTextColor(Color.GRAY);
        account_out.setBackgroundResource(R.drawable.shape_type);
        account_in.setTextColor(Color.GRAY);
        account_in.setBackgroundResource(R.drawable.shape_type);
        homeuse.setTextColor(Color.GRAY);
        homeuse.setBackgroundResource(R.drawable.shape_type);
        dayNotes = MyApp.dayNoteDBHandle.getDayNotes();
        LogUtils.i("dayNotes", dayNotes.toString());
        Collections.reverse(dayNotes);
        info.setText("账单记录：" + dayNotes.size()
                + "，支出 + 转账 - 入账 + 家用：" + StringUtils.showPrice(DayNote.getAllSum() + ""));
        dayNoteAdapter = new DayNoteAdapter(activity, dayNotes, true);
        listView.setAdapter(dayNoteAdapter);
    }

    /**
     * 依据类型显示
     */
    private void getAll4UseType(int type) {
        all.setTextColor(Color.GRAY);
        all.setBackgroundResource(R.drawable.shape_type);
        consume.setTextColor(Color.GRAY);
        consume.setBackgroundResource(R.drawable.shape_type);
        account_out.setTextColor(Color.GRAY);
        account_out.setBackgroundResource(R.drawable.shape_type);
        account_in.setTextColor(Color.GRAY);
        account_in.setBackgroundResource(R.drawable.shape_type);
        homeuse.setTextColor(Color.GRAY);
        homeuse.setBackgroundResource(R.drawable.shape_type);
        list.clear();
        Double sum = 0.00;
        for (int i = 0; i < dayNotes.size(); i++) {
            if (dayNotes.get(i).getUseType() == type) {
                list.add(dayNotes.get(i));
                sum += Double.parseDouble(dayNotes.get(i).getMoney());
            }
        }
        if (type == DayNote.consume) {
            consume.setTextColor(Color.parseColor("#007500"));
            consume.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("支出记录：" + list.size() + "，支出金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_out) {
            account_out.setTextColor(Color.parseColor("#007500"));
            account_out.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("转账记录：" + list.size() + "，转账金额：" + StringUtils.showPrice(sum + ""));
        } else if (type == DayNote.account_in) {
            account_in.setTextColor(Color.parseColor("#007500"));
            account_in.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("入账记录： " + list.size() + "，入账金额：" + StringUtils.showPrice(sum + ""));
        } else {
            homeuse.setTextColor(Color.parseColor("#007500"));
            homeuse.setBackgroundResource(R.drawable.shape_type_btn);
            info.setText("家用记录： " + list.size() + "，家用金额：" + StringUtils.showPrice(sum + ""));
        }
        dayNoteAdapter = new DayNoteAdapter(activity, list, false);
        listView.setAdapter(dayNoteAdapter);
    }

}
