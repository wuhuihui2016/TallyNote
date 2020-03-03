package com.whh.tallynote.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.DayNoteAdapter;
import com.whh.tallynote.database.DayNoteDao;
import com.whh.tallynote.model.DayNote;
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

/**
 * 日账单明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4DayActivity extends BaseActivity {

    private ListView listView;

    private List<DayNote> dayNotes;
    private List<DayNote> list = new ArrayList<>();
    private DayNoteAdapter dayNoteAdapter;

    private TextView info, all, consume, account_out, account_in, homeuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("日账单明细", R.layout.activity_day_list);

        info = (TextView) findViewById(R.id.info);
        all = (TextView) findViewById(R.id.all);
        consume = (TextView) findViewById(R.id.consume);
        account_out = (TextView) findViewById(R.id.account_out);
        account_in = (TextView) findViewById(R.id.account_in);
        homeuse = (TextView) findViewById(R.id.homeuse);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
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
                            Intent intent = new Intent(activity, NewDayActivity.class);
                            intent.putExtra("list", true);
                            startActivity(intent);
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
                startActivity(new Intent(activity, List4DayOfHighConsumeActivity.class));
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
        dayNotes = DayNoteDao.getDayNotes();
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
