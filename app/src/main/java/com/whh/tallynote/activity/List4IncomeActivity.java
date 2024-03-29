package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.IncomeNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.IncomeNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.NotificationUtils;
import com.whh.tallynote.utils.StringUtils;
import com.whh.tallynote.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 理财明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4IncomeActivity extends BaseActivity {

    @BindView(R.id.sort_info)
    public TextView sort_info;
    @BindView(R.id.income_earning)
    public TextView income_earning;
    @BindView(R.id.income_finished)
    public TextView income_finished;

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private int index = 0;
    private List<IncomeNote> incomeNotes4index;
    private IncomeNoteAdapter incomeNoteAdapter;
    private boolean isStart = true;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("理财明细", R.layout.activity_income_list);
    }

    @Override
    protected void initView() {
        income_earning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(0);
            }
        });
        income_finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_info.setText("按投资时间排序");
                initData(1);
            }
        });

        info.setText("投资账单记录有" + MyApp.dbHandle.getCount4Record(ContansUtils.INCOME) + "笔" +
                "\n计息中" + IncomeNote.getEarningInComes().size() + "笔" +
                "\n计息中的总金额：" + StringUtils.showPrice(IncomeNote.getEarningMoney() + ""));
        sort_info.setVisibility(View.VISIBLE);
        sort_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initpopupWindow();
                }
            }
        });

        setRightImgBtnListener(R.drawable.icon_action_bar_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

        NotificationUtils.cancel();//关闭通知
    }

    @Override
    protected void initEvent() {
        initData(0);
    }

    //提交或完成后刷新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
        if (msg.equals(ContansUtils.ACTION_INCOME)) {
            info.setText("投资账单记录有" + MyApp.dbHandle.getCount4Record(ContansUtils.INCOME) + "笔" +
                    "\n计息中" + IncomeNote.getEarningInComes().size() + "笔" +
                    "\n计息中的总金额：" + StringUtils.showPrice(IncomeNote.getEarningMoney() + ""));
            initData(0);
        }
    }

    /**
     * 依据计息中还是已完成来显示理财记录
     *
     * @param index
     */
    private void initData(int index) {
        income_earning.setBackgroundResource(R.color.transparent);
        income_finished.setBackgroundResource(R.color.transparent);
        this.index = index;
        if (index == 0) {
            isStart = true;
            sort_info.setText("按投资时间排序");
            income_earning.setBackgroundResource(R.drawable.shape_left_btn_bkg);
            incomeNotes4index = IncomeNote.getEarningInComes();

            //如果是首页理财条目点击进来按到期时间排序显示计息中的列表
            if (getIntent().hasExtra("income")) {
                sort4End();

                //如果是从通知点击跳转的，需要验证密码
                if (getIntent().hasExtra("notify")) {
                    if (!TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.GESTURE, ""))) {
                        AppManager.transfer(activity, SetGestureActivity.class,ContansUtils.ISBACK, true,"activityNum", 0);
                    } else {
                        AppManager.transfer(activity, SetOrCheckPwdActivity.class, ContansUtils.ISBACK, true);
                    }
                }

                return;
            }
        } else {
            isStart = false;
            sort_info.setText("按到期时间排序");
            income_finished.setBackgroundResource(R.drawable.shape_right_btn_bkg);
            incomeNotes4index = IncomeNote.getFinishedInComes();
        }
        Collections.reverse(incomeNotes4index);
        incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
        listView.setAdapter(incomeNoteAdapter);

    }

    /**
     * 初始化popupWindow
     */
    private void initpopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater inflater = (LayoutInflater) getApplication()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.layout_sort_pop, null);
            popupWindow = new PopupWindow(layout, 300, 200);
            ViewUtils.setPopupWindow(context, popupWindow);
            popupWindow.showAsDropDown(findViewById(R.id.sort_info), 60, -30);

            final TextView start_time = (TextView) layout.findViewById(R.id.start_time);
            final TextView end_time = (TextView) layout.findViewById(R.id.end_time);

            if (isStart) {
                start_time.setTextColor(Color.RED);
                end_time.setTextColor(Color.BLACK);
            } else {
                start_time.setTextColor(Color.BLACK);
                end_time.setTextColor(Color.RED);
            }

            start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_time.setTextColor(Color.RED);
                    end_time.setTextColor(Color.BLACK);
                    popupWindow.dismiss();
                    sort4Start();
                }
            });

            end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start_time.setTextColor(Color.BLACK);
                    end_time.setTextColor(Color.RED);
                    popupWindow.dismiss();
                    sort4End();
                }
            });

        }
    }

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
                        AppManager.transfer(activity, NewIncomeActivity.class, "list", true);
                    }
                }
        );
        layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ExcelUtils.exportIncomeNote(callBackExport);
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

    /**
     * 以投资时间排序
     */
    private void sort4Start() {
        if (incomeNotes4index.size() > 0) {
            sort_info.setText("按投资时间排序");
            if (index == 0) isStart = true;
            else isStart = false;
            Collections.reverse(incomeNotes4index);//倒序排列
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
            listView.setAdapter(incomeNoteAdapter);
        }
    }

    /**
     * 以到期时间排序
     */
    private void sort4End() {
        if (incomeNotes4index.size() > 0) {
            isStart = false;
            sort_info.setText("按到期时间排序");
            for (int i = 0; i < incomeNotes4index.size() - 1; i++) {
                for (int j = 1; j < incomeNotes4index.size() - i; j++) {
                    IncomeNote incomeNote;
                    int days1 = DateUtils.daysBetween(incomeNotes4index.get(j).getDurtion().split("-")[1]);
                    int days2 = DateUtils.daysBetween(incomeNotes4index.get(j - 1).getDurtion().split("-")[1]);
                    if (days1 < days2) {
                        incomeNote = incomeNotes4index.get(j - 1);
                        incomeNotes4index.set((j - 1), incomeNotes4index.get(j));
                        incomeNotes4index.set(j, incomeNote);
                    }
                }
            }
            LogUtils.i("sort", incomeNotes4index.toString());
            incomeNoteAdapter = new IncomeNoteAdapter(activity, incomeNotes4index);
            listView.setAdapter(incomeNoteAdapter);
        }
    }

}
