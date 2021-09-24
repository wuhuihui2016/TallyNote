package com.whh.tallynote.activity;

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
import com.whh.tallynote.adapter.MonthNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DelayTask;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;
import com.whh.tallynote.utils.WPSUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * 月账单明细
 * Created by wuhuihui on 2017/6/27.
 */
public class List4MonthActivity extends BaseActivity {

    @BindView(R.id.info)
    public TextView info;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    @BindView(R.id.chartAnalyse)
    public TextView chartAnalyse;

    private List<MonthNote> monthNotes;
    private MonthNoteAdapter monthNoteAdapter;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("月账单明细", R.layout.activity_month_list);
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

        initData();

        if (getIntent().hasExtra("flag")) {
            showDialog();
        }
    }

    @Override
    protected void initEvent() {

    }

    private void initData() {
        monthNotes = MyApp.monthNoteDBHandle.getMonthNotes();
        info.setText("月账单记录有" + monthNotes.size() + "笔");

        if (monthNotes.size() > 1) {
            chartAnalyse.setVisibility(View.INVISIBLE);
            chartAnalyse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.transfer(activity, MonthNotesAnalyseActivity.class);
                }
            });
        }

        Collections.reverse(monthNotes);//倒序排列
        monthNoteAdapter = new MonthNoteAdapter(activity, monthNotes);
        listView.setAdapter(monthNoteAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MyApp.dayNoteDBHandle.getDayNotes4History().size() > 0) {
                    AppManager.transfer(activity, List4DayOfMonthActivity.class, "duration", monthNotes.get(position).getDuration());
                } else {
                    ToastUtils.showErrorLong(activity, "月消费日账加载失败！");
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AppManager.transfer(activity, NewMonthActivity.class, "editMonthNote", monthNotes.get(position));
                return true; //长按后拦截单按事件
            }
        });
    }

    //提交月账监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
        if (msg.equals(ContansUtils.ACTION_MONTH)) {
            initData();
            showDialog();
        }
    }

    /**
     * 新建月账完成，提醒上传数据
     */
    private void showDialog() {
        new DelayTask(500, new DelayTask.ICallBack() {
            @Override
            public void deal() {
                DialogUtils.showMsgDialog(activity, "新月账单已生成,是否现在上传数据？",
                        "上传", new MyClickListener() {
                            @Override
                            public void onClick() {
                                FileUtils.uploadFile2WXCollect(activity);
                            }
                        }, "查看", new MyClickListener() {
                            @Override
                            public void onClick() {
                                File tallyNoteFile = FileUtils.getTallyNoteFile();
                                if (tallyNoteFile != null)
                                    WPSUtils.openFile(context, FileUtils.getTallyNoteFile().getPath());
                                else ToastUtils.showErrorLong(activity, "查看失败！");
                            }
                        });
            }
        }).execute();
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
                            AppManager.transfer(activity, NewMonthActivity.class, "list", true);
                        }
                    }
            );
            layout.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    ExcelUtils.exportMonthNote(callBackExport);
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
}
