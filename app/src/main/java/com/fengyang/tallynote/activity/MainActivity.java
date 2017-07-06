package com.fengyang.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.fragment.IncomeFragment;
import com.fengyang.tallynote.fragment.MineFragment;
import com.fengyang.tallynote.fragment.TallyFragment;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

	private ViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<>();
	private int frag_index = 0;//当前加载fragment标志位
	private TextView tally_title, income_title, mine_title;
	private View barView;//向导View
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

		barView = findViewById(R.id.barView);
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		// 得到显示屏宽度
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// 1/2屏幕宽度
		int tabLineLength = metrics.widthPixels / 3;
		ViewGroup.LayoutParams lp = barView.getLayoutParams();
		lp.width = tabLineLength;
		barView.setLayoutParams(lp);

		//检测权限后显示界面
		PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
			@Override
			public void onCheck(boolean isSucess) {
				if (isSucess) {
					//初始化ViewPager
					if (! canShow) {
						viewPager = (ViewPager)findViewById(R.id.viewpager);
						fragments.add(new TallyFragment());
						fragments.add(new IncomeFragment());
						fragments.add(new MineFragment());
						//给ViewPager设置适配器
						viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
						viewPager.setCurrentItem(0);//设置当前显示标签页为第一页
						setCheked();
						viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
					}

					isShow(true);

				} else {
					PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
					StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
				}
			}
		});

	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

	}

	/**
	 * 页面滑动监听
	 */
	class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// 取得该控件的实例
			FrameLayout.LayoutParams ll = (FrameLayout.LayoutParams) barView.getLayoutParams();

			if(frag_index == position){
				ll.leftMargin = (int) (frag_index * barView.getWidth() + positionOffset
						* barView.getWidth());
			}else if(frag_index > position){
				ll.leftMargin = (int) (frag_index * barView.getWidth() - (1 - positionOffset) * barView.getWidth());
			}
			barView.setLayoutParams(ll);
		}

		@Override
		public void onPageScrollStateChanged(int position) {}

		@Override
		public void onPageSelected(int state) {
			frag_index = state;
			setCheked();
		}
	}

	/**
	 * 设置当前tab颜色
	 */
	private void setCheked(){
		tally_title.setTextColor(Color.BLACK);
		income_title.setTextColor(Color.BLACK);
		mine_title.setTextColor(Color.BLACK);
		switch (frag_index) {
			case 0: setTitle("我的账本"); tally_title.setTextColor(Color.RED); break;
			case 1: setTitle("我的理财"); income_title.setTextColor(Color.RED); break;
			case 2: setTitle("我的"); mine_title.setTextColor(Color.RED); break;
		}
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
			case R.id.tally:  frag_index = 0; break;
			case R.id.income: frag_index = 1; break;
			case R.id.mine:   frag_index = 2; break;
		}

		viewPager.setCurrentItem(frag_index);
		setCheked();
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
			if (isShow) animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_show);
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
