package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.fragment.IncomeFragment;
import com.fengyang.tallynote.fragment.MineFragment;
import com.fengyang.tallynote.fragment.TallyFragment;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

	private FragmentManager manager;
	private FragmentTransaction transaction;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private Fragment frag_tally, frag_income, frag_mine;//首页,我的
	private int frag_index = 0;//当前加载fragment标志位
	private TextView tally_title, income_title, mine_title;
	private boolean canShow = false;//tabBar动画显示标志(仅在界面重新激活时为true,动画效果才实现)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("我的账本", R.layout.activity_main);

	}

	@Override
	protected void onResume() {
		super.onResume();

		tally_title = (TextView) findViewById(R.id.tally_title);
		income_title = (TextView) findViewById(R.id.income_title);
		mine_title = (TextView) findViewById(R.id.mine_title);

		//检测权限后显示界面
		PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
			@Override
			public void onCheck(boolean isSucess) {
				if (isSucess) {
					if (frag_index == 0) selectTab(1);//默认加载首页
					else selectTab(frag_index);//如果已加载“我的”，则返回时加载“我的”
				} else {
					PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
					StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
				}
			}
		});

		isShow(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//底部TAB显示动画
		canShow = true;
		isShow(false);

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tally:  selectTab(1); break;
			case R.id.income: selectTab(2); break;
			case R.id.mine:   selectTab(3); break;
		}
	}

	/**
	 * 首页切换模块
	 * @param index
	 */
	private void selectTab(int index) {
		try {
			manager = getSupportFragmentManager();
			transaction = manager.beginTransaction();
			hideFragment(transaction);
			switch (index) {
				case 1://首页
					setTitle("记账本");
					tally_title.setTextColor(getResources().getColor(R.color.app_color));
					income_title.setTextColor(getResources().getColor(R.color.gray));
					mine_title.setTextColor(getResources().getColor(R.color.gray));
					if (frag_index == 1) {
						transaction.remove(frag_tally);
						transaction.commit();
						frag_tally = null;//制空
						frag_index = 0;//标志复位
						selectTab(1);//重新加载
						return;
					} else {
						if (frag_tally == null) {
							frag_tally = new TallyFragment();
							fragments.add(frag_tally);
						}
						if(! frag_tally.isAdded()){
							transaction.add(R.id.main_context, frag_tally);
						}
						transaction.show(frag_tally);
					}
					break;

				case 2://我的
					setTitle("理财");
					income_title.setTextColor(getResources().getColor(R.color.app_color));
					tally_title.setTextColor(getResources().getColor(R.color.gray));
					mine_title.setTextColor(getResources().getColor(R.color.gray));
					if (frag_index == 2) {
						transaction.remove(frag_income);
						transaction.commit();
						frag_income = null;
						frag_index = 0;
						selectTab(2);
						return;
					} else  {
						if (frag_income == null) {
							frag_income = new IncomeFragment();
							fragments.add(frag_income);
						}
						if(! frag_income.isAdded()){
							transaction.add(R.id.main_context, frag_income);
						}
						transaction.show(frag_income);
					}
					break;

				case 3://我的
					setTitle("");
					mine_title.setTextColor(getResources().getColor(R.color.app_color));
					tally_title.setTextColor(getResources().getColor(R.color.gray));
					income_title.setTextColor(getResources().getColor(R.color.gray));
					if (frag_index == 3) {
						transaction.remove(frag_mine);
						transaction.commit();
						frag_mine = null;
						frag_index = 0;
						selectTab(3);
						return;
					} else  {
						if (frag_mine == null) {
							frag_mine = new MineFragment();
							fragments.add(frag_mine);
						}
						if(! frag_mine.isAdded()){
							transaction.add(R.id.main_context, frag_mine);
						}
						transaction.show(frag_mine);
					}
					break;

			}
			transaction.commit();
			frag_index = index;//当前加载页标志
		} catch (Exception e) {
			LogUtils.i("Exception", e.toString());
		}
	}

	/**
	 * 隐藏fragments
	 * @param transaction
	 */
	private void hideFragment(FragmentTransaction transaction){
		for(Fragment fragment : fragments){
			if(fragment != null && fragment.isAdded() && fragment.isVisible()){
				transaction.hide(fragment);
			}
		}
	}

	/**
	 * TabBar显示隐藏动画控制
	 * @param isShow
	 */
	private void isShow (boolean isShow) {
		if (canShow) {
			LinearLayout app_buttom = (LinearLayout) findViewById(R.id.app_buttom);

			Animation animation;
			LayoutAnimationController controller;
			if (isShow)  animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_show);
			else animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_hidden);
			controller = new LayoutAnimationController(animation);

			app_buttom.setLayoutAnimation(controller);// 设置动画
		}

	}


	/**
	 * 再按一次退出程序
	 */
	private long mExitTime;//返回退出时间间隔标志
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
