package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.PortNotesActivity;
import com.fengyang.tallynote.activity.ReSetPwdKeyActivity;
import com.fengyang.tallynote.adapter.SettingAdapter;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment{


	private static final String TAG = "TallyFragment";
	private Activity activity;
	private View content;//内容布局

	private ListView settingList;
	private List<String> settings = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		content = inflater.inflate(R.layout.fragment_mine, container, false);
		activity = getActivity();
		initView();
		return content;
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		settingList = (ListView) content.findViewById(R.id.settingList);
		settings.add("导入/导出");
		settings.add("重置密保");
		settings.add("清除数据");
		settingList.setAdapter(new SettingAdapter(activity, settings));
		settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0://导入/导出
						startActivity(new Intent(activity, PortNotesActivity.class));
						break;
					case 1://重置密保
						startActivity(new Intent(activity, ReSetPwdKeyActivity.class));
						break;
					case 2://清除数据
						ContansUtils.clear();
						FileUtils.delete(FileUtils.getAppDir());
						break;
				}
			}
		});

	}

}
