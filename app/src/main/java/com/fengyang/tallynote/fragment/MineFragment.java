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

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.CalculateActivity;
import com.fengyang.tallynote.activity.PortNotesActivity;
import com.fengyang.tallynote.activity.ReSetPwdKeyActivity;
import com.fengyang.tallynote.adapter.Setting4GridAdapter;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment{


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
		initView();
		return content;
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		settingGrid = (GridView) content.findViewById(R.id.settingGrid);
		settings.clear(); drawableRes.clear();
		settings.add("导入/导出"); drawableRes.add(R.drawable.import_export);
		settings.add("重置密保");  drawableRes.add(R.drawable.pwdkey);
		settings.add("计算日收益");  drawableRes.add(R.drawable.calculate);

		settingGrid.setAdapter(new Setting4GridAdapter(activity, drawableRes, settings));
		settingGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0://导入/导出
						startActivity(new Intent(activity, PortNotesActivity.class));
						break;
					case 1://重置密保
						startActivity(new Intent(activity, ReSetPwdKeyActivity.class));
						break;
					case 2://计算日收益
						startActivity(new Intent(activity, CalculateActivity.class));
						break;
				}
			}
		});

	}

}