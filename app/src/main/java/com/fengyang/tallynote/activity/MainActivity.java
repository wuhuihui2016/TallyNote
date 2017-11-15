package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.FragmentViewPagerAdapter;
import com.fengyang.tallynote.fragment.IncomeFragment;
import com.fengyang.tallynote.fragment.MineFragment;
import com.fengyang.tallynote.fragment.TallyFragment;
import com.fengyang.tallynote.utils.DelayTask;
import com.fengyang.tallynote.utils.NotificationUtils;
import com.fengyang.tallynote.utils.PermissionUtils;
import com.fengyang.tallynote.utils.SystemUtils;
import com.fengyang.tallynote.utils.ToastUtils;
import com.fengyang.tallynote.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的账本
 */
public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentViewPagerAdapter adapter;
    private int frag_index = 0; //当前加载fragment标志位
    private LinearLayout tally, income, mine;
    private TextView tally_title, income_title, mine_title;
    private RelativeLayout cur_tab; //向导游标View
    private boolean canShow = false; //tabBar动画显示标志(仅在界面重新激活时为true,动画效果才实现)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("我的账本", R.layout.activity_main);

        tally = (LinearLayout) findViewById(R.id.tally);
        income = (LinearLayout) findViewById(R.id.income);
        mine = (LinearLayout) findViewById(R.id.mine);
        tally_title = (TextView) findViewById(R.id.tally_title);
        income_title = (TextView) findViewById(R.id.income_title);
        mine_title = (TextView) findViewById(R.id.mine_title);

        cur_tab = (RelativeLayout) findViewById(R.id.cur_tab);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        // 得到显示屏宽度
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 1/3屏幕宽度
        int tabLineLength = metrics.widthPixels / 3;
        ViewGroup.LayoutParams lp = cur_tab.getLayoutParams();
        lp.width = tabLineLength;
        cur_tab.setLayoutParams(lp);

        NotificationUtils.notifyIncome(context); //理财到期提醒

        findViewById(R.id.addNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //检测权限后显示界面
        PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isSucess) {
                    if (!canShow) {
                        fragments.add(new TallyFragment());
                        fragments.add(new IncomeFragment());
                        fragments.add(new MineFragment());

                        viewPager = (ViewPager) findViewById(R.id.viewpager);
                        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), viewPager, fragments);
                        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
                            @Override
                            public void onExtraPageSelected(int position) {
                                setCheked(position);//设置当前显示标签页为第一页
                            }

                            @Override
                            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                // 取得该控件的实例
                                FrameLayout.LayoutParams ll = (FrameLayout.LayoutParams) cur_tab.getLayoutParams();

                                cur_tab.setVisibility(View.VISIBLE);

                                if (frag_index == position) {
                                    ll.leftMargin = (int) (frag_index * cur_tab.getWidth() + positionOffset
                                            * cur_tab.getWidth());
                                } else if (frag_index > position) {
                                    ll.leftMargin = (int) (frag_index * cur_tab.getWidth() - (1 - positionOffset) * cur_tab.getWidth());
                                }
                                cur_tab.setLayoutParams(ll);

                                new DelayTask(200, new DelayTask.ICallBack() {
                                    @Override
                                    public void deal() {//游标消失
                                        cur_tab.setVisibility(View.GONE);
                                    }
                                }).execute();
                            }
                        });

                        if (frag_index == 0) setCheked(frag_index);
                    }

                    isShow(true);

                } else {
                    PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
                    ToastUtils.showToast(context, true, "可能读取SDCard权限未打开，请检查后重试！");
                }
            }
        });

    }

    /**
     * 设置当前tab颜色
     */
    private void setCheked(int index) {

        frag_index = index;

        tally.setBackgroundColor(Color.WHITE);
        income.setBackgroundColor(Color.WHITE);
        mine.setBackgroundColor(Color.WHITE);
        tally_title.setTextColor(Color.BLACK);
        income_title.setTextColor(Color.BLACK);
        mine_title.setTextColor(Color.BLACK);

        switch (frag_index) {
            case 0:
                setTitle("账本");
                tally.setBackgroundResource(R.color.day_record);
                tally_title.setTextColor(Color.WHITE);

                //搜索
                setRightImgBtnListener(R.drawable.ic_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(activity, SearchActivity.class));
                    }
                });

                break;

            case 1:
                setTitle("理财");
                income.setBackgroundResource(R.color.income_record);
                income_title.setTextColor(Color.WHITE);

                setRightBtnListener("V " + SystemUtils.getVersion(activity), null);

                break;
            case 2:
                setTitle("我的");
                mine.setBackgroundResource(R.color.red);
                mine_title.setTextColor(Color.WHITE);

                setRightBtnListener("V " + SystemUtils.getVersion(activity), null);

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //底部TAB显示动画
        canShow = true;
        isShow(false);

    }

    /*
      tab点击事件
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tally:
                viewPager.setCurrentItem(0);
                setCheked(0);
                break;
            case R.id.income:
                viewPager.setCurrentItem(1);
                setCheked(1);
                break;
            case R.id.mine:
                viewPager.setCurrentItem(2);
                setCheked(2);
                break;
        }

    }

    /**
     * TabBar显示隐藏动画控制
     *
     * @param isShow
     */
    private void isShow(boolean isShow) {
        if (canShow) {
            LinearLayout app_bottom = (LinearLayout) findViewById(R.id.app_bottom);

            Animation animation;
            LayoutAnimationController controller;
            if (isShow) animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_show);
            else animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_hidden);
            controller = new LayoutAnimationController(animation);

            app_bottom.setLayoutAnimation(controller);// 设置动画
        }

    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_add_note_pop, null);
            popupWindow = new PopupWindow(layout, 400, 800);
            ViewUtils.setPopupWindow(activity, popupWindow);
            // 相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAsDropDown(findViewById(R.id.addNote), 50, 20);
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);

            layout.findViewById(R.id.commitDNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewDayActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.commitMNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewMonthActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.commitIncome).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewIncomeActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.commitMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewMemoActivity.class));
                        }
                    }
            );
            layout.findViewById(R.id.commitNotepad).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            startActivity(new Intent(activity, NewNotePadActivity.class));
                        }
                    }
            );
        }
    }

    /**
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast(this, true, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
