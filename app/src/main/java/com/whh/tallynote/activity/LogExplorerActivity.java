package com.whh.tallynote.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.FileExplorerAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * log文件浏览
 * Created by wuhuihui on 2018/7/5.
 */
public class LogExplorerActivity extends BaseActivity {

    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;
    @BindView(R.id.choose_layout)
    public LinearLayout choose_layout;

    @BindView(R.id.ok)
    public Button ok;
    @BindView(R.id.no)
    public Button no;
    @BindView(R.id.del)
    public Button del;

    private List<File> fileList = new ArrayList<>();
    private FileExplorerAdapter adapter;
    private PopupWindow popupWindow;
    private boolean isSellect = false;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("log文件浏览", R.layout.activity_file_explorer);
    }

    @Override
    protected void initView() {
        init();
    }

    @Override
    protected void initEvent() {
    }

    private void init() {
        listView.setEmptyView(emptyView);

        File logDir = FileUtils.getLogDir();
        final File files[] = logDir.listFiles();
        if (files != null && files.length > 0) {
            fileList.clear();
            for (int i = 0; i < files.length; i++) fileList.add(files[i]);
            Collections.sort(fileList, new FileComparator());
            if (fileList.size() > 0) {
                adapter = new FileExplorerAdapter(context, fileList);
                setSellect(false);
                listView.setAdapter(adapter);

                //长按进入选择模式
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!isSellect) {
                            setSellect(true);
                        }
                        return false;
                    }
                });

                //默认操作文件，选择状态下选择文件以删除
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File file = fileList.get(position);
                        if (isSellect) {
                            adapter.setSelected(file);
                        } else {
                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            } else {
                                initPopupWindow(file);
                            }

                        }
                    }

                });

            }
        }


    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow(final File file) {
        LayoutInflater inflater = (LayoutInflater) getApplication()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_file_pop, null);
        popupWindow = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ViewUtils.setPopupWindow(context, popupWindow);
        popupWindow.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);

        layout.findViewById(R.id.open).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        readFile(file.getPath());
                    }
                }
        );
        layout.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                FileUtils.shareFile(activity, file); //向其他APP分享文件

            }
        });
        layout.findViewById(R.id.uploadlayout).setVisibility(View.GONE);
        layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 读取文件并显示
     *
     * @param fileName
     */
    private void readFile(final String fileName) {
        LayoutInflater inflater = (LayoutInflater) getApplication()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_readtxt_pop, null);
        popupWindow = new PopupWindow(layout, SystemUtils.getWidth(activity), SystemUtils.getHeight(activity) / 12 * 11);
        ViewUtils.setPopupWindow(context, popupWindow);
        popupWindow.showAtLocation(findViewById(R.id.layout), Gravity.CENTER_VERTICAL, 0, 0);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(FileUtils.readTextFile(fileName));
        layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 选择模式的切换
     *
     * @param isSellect
     */
    private void setSellect(boolean isSellect) {
        this.isSellect = isSellect;
        adapter.setSelect(isSellect);

        if (isSellect) {
            choose_layout.setVisibility(View.VISIBLE);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.selectAll(true);
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.selList.clear();
                    choose_layout.setVisibility(View.GONE);
                    init();
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.selList.size() > 0) {
                        DialogUtils.showMsgDialog(activity, "是否确定删除选定的" + adapter.selList.size() + "文件",
                                "删除", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                        for (int i = 0; i < adapter.selList.size(); i++)
                                            adapter.selList.get(i).delete();
                                        adapter.selList.clear();
                                        choose_layout.setVisibility(View.GONE);
                                        init();
                                    }
                                }, "取消", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                    }
                                });
                    } else ToastUtils.showWarningShort(activity, "请至少选择1个文件！");

                }
            });

        }
    }


    /**
     * 将文件按时间降序排列
     */
    class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1; // 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

}
