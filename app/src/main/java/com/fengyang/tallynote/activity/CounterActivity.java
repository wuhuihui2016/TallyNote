package com.fengyang.tallynote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.DialogListener;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 计数器
 * Created by wuhuihui on 2018/07/05.
 */
public class CounterActivity extends BaseActivity {

    private String COUNT = "counter";

    private ListView listView;
    private TextView emptyView;
    private String counts;
    private List<String> countList = new ArrayList<>();
    private CounterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("计数器", R.layout.activity_counter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        adapter = new CounterAdapter(countList);

        setRightBtnListener("增删", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showMsgDialog(activity, "选择操作", "录入", new DialogListener() {
                    @Override
                    public void onClick() {
                        initPopupWindow();
                    }
                }, "清除", new DialogListener() {
                    @Override
                    public void onClick() {
                        DialogUtils.showMsgDialog(activity, "确认清除数据？", "清除", new DialogListener() {
                            @Override
                            public void onClick() {
                                ContansUtils.remove(COUNT);
                                refreshView();
                            }
                        }, "取消", new DialogListener() {
                            @Override
                            public void onClick() {

                            }
                        });
                    }
                });
            }
        });

        refreshView();

    }

    /**
     * 刷新并显示数据
     */
    private void refreshView() {
        counts = (String) ContansUtils.get(COUNT, "");

        if (TextUtils.isEmpty(counts)) {
            countList.clear();
            adapter.notifyDataSetChanged();
            new DelayTask(200, new DelayTask.ICallBack() {
                @Override
                public void deal() {
                    initPopupWindow();
                }
            }).execute();
        } else {
            LogUtils.i(TAG, counts);
            String[] countsArr = counts.split(",");
            countList.clear();
            for (int i = 0; i < countsArr.length; i++) {
                countList.add(countsArr[i]);
            }
            Collections.reverse(countList);
            listView.setAdapter(adapter);
        }
    }

    class CounterAdapter extends BaseAdapter {

        private List<String> countList;

        public CounterAdapter(List<String> countList) {
            this.countList = countList;
        }

        @Override
        public int getCount() {
            return countList.size();
        }

        @Override
        public Object getItem(int position) {
            return countList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.counter_item_layout, null);

                viewHolder = new ViewHolder();
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.count_diff = (TextView) convertView.findViewById(R.id.count_diff);
                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //获取当前对象
            final String getCount = countList.get(position);
            viewHolder.time.setText(getCount.split(":")[0]);
            String count = getCount.split(":")[1];

            if (position != countList.size() - 1) {
                String lastCount = countList.get(position + 1).split(":")[1];
                int diff = Integer.parseInt(count) - Integer.parseInt(lastCount);
                if (diff > 0)
                    viewHolder.count_diff.setText("+" + diff);
                else
                    viewHolder.count_diff.setText("" + diff);
            } else viewHolder.count_diff.setText("");

            viewHolder.count.setText(count);

            return convertView;
        }

        class ViewHolder {
            TextView time, count_diff, count;
        }
    }

    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_count_pop, null);
            popupWindow = new PopupWindow(layout, 800, LinearLayout.LayoutParams.WRAP_CONTENT);
            ViewUtils.setPopupWindow(activity, popupWindow);
//            相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAtLocation(findViewById(R.id.list_layout), Gravity.CENTER, 0, 0);

            final EditText editText = (EditText) layout.findViewById(R.id.editText);

            layout.findViewById(R.id.comfire).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                popupWindow.dismiss();
                                String count = editText.getText().toString();
                                if (!TextUtils.isEmpty(count)) {

                                    LogUtils.i(TAG, counts);
                                    counts = (String) ContansUtils.get(COUNT, "");
                                    if (!TextUtils.isEmpty(counts)) {
                                        counts += "," + DateUtils.formatDate() + ":" + count;
                                    } else {
                                        counts = DateUtils.formatDate() + ":" + count;
                                    }

                                    ContansUtils.put(COUNT, counts);

                                    refreshView();

                                }
                            } catch (Exception e) {
                            }
                        }
                    }
            );
            layout.findViewById(R.id.cancel).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    }
            );
        }
    }


}
