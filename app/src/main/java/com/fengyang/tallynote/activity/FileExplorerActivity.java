package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.FileExplorerAdapter;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.ToastUtils;
import com.fengyang.tallynote.utils.ViewUtils;
import com.fengyang.tallynote.utils.WPSUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件浏览
 * Created by wuhuihui on 2017/7/13.
 */
public class FileExplorerActivity extends BaseActivity {

    private List<File> fileList = new ArrayList<>();
    private FileExplorerAdapter adapter;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("文件浏览", R.layout.activity_file_explorer);

        init();
    }

    private void init() {
        final ListView listView = (ListView) findViewById(R.id.listView);

        File excelDir = FileUtils.getExcelDir();
        final File files[] = excelDir.listFiles();
        fileList.clear();
        for (int i = 0; i < files.length; i++) fileList.add(files[i]);
        Collections.sort(fileList, new FileComparator());
        if (fileList.size() > 0) {
            adapter = new FileExplorerAdapter(context, fileList);
            adapter.setSelect(false);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new MyItemClickListener(false));
        }

        listView.setEmptyView(findViewById(R.id.emptyView));

        setRightImgBtnListener(R.drawable.file_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout choose_layout = (LinearLayout) findViewById(R.id.choose_layout);
                choose_layout.setVisibility(View.VISIBLE);
                listView.setOnItemClickListener(new MyItemClickListener(true));
                adapter.setSelect(true);
                findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.selectAll(true);
                    }
                });

                findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.selList.clear();
                        choose_layout.setVisibility(View.GONE);
                        init();
                    }
                });

                findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.selList.size() > 0) {
                            DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除选定的" + adapter.selList.size() + "文件", new DialogUtils.DialogListener() {
                                @Override
                                public void onClick(View v) {
                                    super.onClick(v);
                                    for (int i = 0; i < adapter.selList.size(); i++)
                                        adapter.selList.get(i).delete();
                                    adapter.selList.clear();
                                    choose_layout.setVisibility(View.GONE);
                                    init();
                                }
                            }, new DialogUtils.DialogListener() {
                                @Override
                                public void onClick(View v) {
                                    super.onClick(v);
                                }
                            });
                        } else ToastUtils.showWarningShort(context, "请至少选择1个文件！");

                    }
                });

            }

        });
    }

    private class MyItemClickListener implements AdapterView.OnItemClickListener {
        private boolean isSellect;

        public MyItemClickListener(boolean isSellect) {
            this.isSellect = isSellect;
        }

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
                        try {
                            WPSUtils.openFile(getApplicationContext(), file.getPath());
                        } catch (Exception e) {
                            ToastUtils.showWarningShort(context, "没有找到可打开" + file.getName() + "的应用！");
                        }
                    }
                }
        );
        layout.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //http://blog.csdn.net/yuxiaohui78/article/details/8232402
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.setType("*/*");//此处可发送多种文件
                startActivity(Intent.createChooser(share, "发送文件"));

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
