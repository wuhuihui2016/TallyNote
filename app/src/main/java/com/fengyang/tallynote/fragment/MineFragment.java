package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.CalculateActivity;
import com.fengyang.tallynote.activity.FileExplorerActivity;
import com.fengyang.tallynote.activity.ImportExportActivity;
import com.fengyang.tallynote.activity.MemoNoteListActivity;
import com.fengyang.tallynote.activity.NotePadListActivity;
import com.fengyang.tallynote.activity.ReSetPwdKeyActivity;
import com.fengyang.tallynote.adapter.Setting4GridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 */
public class MineFragment extends Fragment {


    private static final String TAG = "TallyFragment";
    private Activity activity;
    private View content;//内容布局

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

        //3的倍数
        settingGrid = (GridView) content.findViewById(R.id.settingGrid);
        settings.clear();
        drawableRes.clear();

        settings.add("备忘录");
        drawableRes.add(R.drawable.memo);
        settings.add("记事本");
        drawableRes.add(R.drawable.notepad);
        settings.add("导入/导出");
        drawableRes.add(R.drawable.import_export);
        settings.add("重置密保");
        drawableRes.add(R.drawable.pwdkey);
        settings.add("计算日收益");
        drawableRes.add(R.drawable.calculate);
        settings.add("文件浏览");
        drawableRes.add(R.drawable.file_explorer);

        settingGrid.setAdapter(new Setting4GridAdapter(activity, drawableRes, settings));
        settingGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //备忘录
                        MyApp.dbHelper.newTable("create table if not exists memo_note(_id integer primary key," +
                                "content varchar(200),status integer,time varchar(20))");
                        startActivity(new Intent(activity, MemoNoteListActivity.class));
                        break;

                    case 1: //记事本
                        MyApp.dbHelper.newTable("create table if not exists note_pad(_id integer primary key," +
                                "tag integer,words varchar(200),time varchar(20))");
                        startActivity(new Intent(activity, NotePadListActivity.class));
                        break;

                    case 2: //导入/导出
                        startActivity(new Intent(activity, ImportExportActivity.class));
                        break;

                    case 3: //重置密保
                        startActivity(new Intent(activity, ReSetPwdKeyActivity.class));
                        break;

                    case 4: //计算日收益
                        startActivity(new Intent(activity, CalculateActivity.class));
                        break;

                    case 5: //文件浏览
                        startActivity(new Intent(activity, FileExplorerActivity.class));
                        break;
                }
            }
        });

    }

}
