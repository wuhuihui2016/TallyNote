package com.whh.tallynote.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.activity.CalculateActivity;
import com.whh.tallynote.activity.CounterActivity;
import com.whh.tallynote.activity.FileExplorerActivity;
import com.whh.tallynote.activity.ImportExportActivity;
import com.whh.tallynote.activity.LogExplorerActivity;
import com.whh.tallynote.activity.List4MemoNoteActivity;
import com.whh.tallynote.activity.List4NotePadActivity;
import com.whh.tallynote.activity.SetGestureActivity;
import com.whh.tallynote.activity.SetOrCheckPwdActivity;
import com.whh.tallynote.adapter.Setting4GridAdapter;
import com.whh.tallynote.base.BaseFragment;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
public class MineFragment extends BaseFragment {

    private static final String TAG = "TallyFragment";
    private GridView settingGrid;
    private List<String> settings = new ArrayList<>();
    private List<Integer> drawableRes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_mine, container, false);
        activity = getActivity();
        return content;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {

        String phoneNum = (String) ContansUtils.get(ContansUtils.PHONENUM, "");

        //显示版本信息
        TextView versionInfo = (TextView) content.findViewById(R.id.versionInfo);
        versionInfo.setText(ContansUtils.get(ContansUtils.NICKNAME, "") + "  每天进步一点点\n"
                + phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length() - 4)
                + "\nV " + SystemUtils.getVersion(activity));

        //3的倍数
        settingGrid = (GridView) content.findViewById(R.id.settingGrid);
        settings.clear();
        drawableRes.clear();

        settings.add("备忘录");
        drawableRes.add(R.drawable.memo);
        settings.add("安全设置");
        drawableRes.add(R.drawable.pwdkey);
        settings.add("记事本");
        drawableRes.add(R.drawable.notepad);
        settings.add("计算日收益");
        drawableRes.add(R.drawable.calculate);
        settings.add("文件浏览");
        drawableRes.add(R.drawable.file_explorer);
        settings.add("导入/导出");
        drawableRes.add(R.drawable.import_export);
        settings.add("查看log");
        drawableRes.add(R.drawable.log_review);
        settings.add("计数器");
        drawableRes.add(R.drawable.counter);
        settings.add("敬请期待");
        drawableRes.add(R.drawable.doc_review);

        settingGrid.setAdapter(new Setting4GridAdapter(activity, drawableRes, settings));
        settingGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //备忘录
                        AppManager.transfer(activity, List4MemoNoteActivity.class);
                        break;

                    case 1: //安全设置
                        //判断验证方式：启动密码or手势密码
                        if (!TextUtils.isEmpty((String) ContansUtils.get(ContansUtils.GESTURE, ""))) { //手势密码不为空，关闭当前界面，验证手势密码
                            AppManager.transfer(activity, SetGestureActivity.class, "secureSet", true);
                        } else {
                            AppManager.transfer(activity, SetOrCheckPwdActivity.class, "secureSet", true);
                        }
                        break;

                    case 2: //记事本
                        AppManager.transfer(activity, List4NotePadActivity.class);
                        break;

                    case 3: //计算日收益
                        AppManager.transfer(activity, CalculateActivity.class);
                        break;

                    case 4: //文件浏览
                        AppManager.transfer(activity, FileExplorerActivity.class);
                        break;

                    case 5: //导入/导出
                        AppManager.transfer(activity, ImportExportActivity.class);
                        break;

                    case 6: //查看log
                        AppManager.transfer(activity, LogExplorerActivity.class);
                        break;

                    case 7: //计数器
                        AppManager.transfer(activity, CounterActivity.class);
                        break;

                    case 8: //敬请期待
                        ToastUtils.showToast(activity, false, "读文档，敬请期待...");
                        break;
                }
            }
        });
    }

}
