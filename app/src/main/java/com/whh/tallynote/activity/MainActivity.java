package com.whh.tallynote.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.FragmentViewPagerAdapter;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.fragment.IncomeFragment;
import com.whh.tallynote.fragment.MineFragment;
import com.whh.tallynote.fragment.TallyFragment;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.NotificationUtils;
import com.whh.tallynote.utils.PermissionUtils;
import com.whh.tallynote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的账本
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.main_viewPager)
    public ViewPager viewPager;
    @BindView(R.id.tally_title)
    public TextView tally_title;
    @BindView(R.id.income_title)
    public TextView income_title;
    @BindView(R.id.mine_title)
    public TextView mine_title;

    @BindView(R.id.tally)
    public LinearLayout tally;
    @BindView(R.id.income)
    public LinearLayout income;
    @BindView(R.id.mine)
    public LinearLayout mine;

    @BindView(R.id.app_bottom)
    public LinearLayout app_bottom;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentViewPagerAdapter adapter;
    private int frag_index = 0; //当前加载fragment标志位
    private boolean canShow = false; //tabBar动画显示标志(仅在界面重新激活时为true,动画效果才实现)
    private boolean initViewed = false; //权限获取成功后初始界面标志

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("我的账本", R.layout.activity_main);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initEvent() {
        NotificationUtils.notifyIncome(context); //理财到期提醒
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检测权限后显示界面
        PermissionUtils.checkSDcardPermission(activity, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isSucess) {
                    if (!initViewed) {
                        fragments.add(new TallyFragment());
                        fragments.add(new IncomeFragment());
                        fragments.add(new MineFragment());

                        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), viewPager, fragments);
                        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
                            @Override
                            public void onExtraPageSelected(int position) {
                                setCheked(position);//设置当前显示标签页为第一页
                            }

                            @Override
                            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }
                        });

                        initViewed = true;
                    }

                    viewPager.setCurrentItem(frag_index);
                    setCheked(frag_index);

                    isShow(true);


                } else {
                    PermissionUtils.notSDCardPermission(activity);
                    ToastUtils.showToast(context, true, "可能读取SDCard权限未打开，请检查后重试！");
                }
            }
        });

    }

    /**
     * 设置当前tab颜色
     */
    private void setCheked(int index) {
        viewPager.setCurrentItem(index);

        frag_index = index;

        tally.setBackgroundColor(getResources().getColor(R.color.index_tally));
        income.setBackgroundColor(getResources().getColor(R.color.index_tally));
        mine.setBackgroundColor(getResources().getColor(R.color.index_tally));
        tally_title.setTextColor(Color.BLACK);
        income_title.setTextColor(Color.BLACK);
        mine_title.setTextColor(Color.BLACK);

        switch (frag_index) {
            case 0:
                setTitle("记账本");
                tally.setBackgroundResource(R.color.day_record);
                tally_title.setTextColor(Color.WHITE);

                //搜索
                setRightImgBtnListener(R.drawable.ic_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppManager.transfer(activity, SearchActivity.class);
                    }
                });
                break;

            case 1:
                setTitle("理财");
                income.setBackgroundResource(R.color.income_record);
                income_title.setTextColor(Color.WHITE);
                break;

            case 2:
                setTitle("我的");
                mine.setBackgroundResource(R.color.red);
                mine_title.setTextColor(Color.WHITE);
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
                setCheked(0); //记账本
                break;
            case R.id.income:
                setCheked(1); //理财
                break;
            case R.id.mine:
                setCheked(2); //我的
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
            Animation animation;
            LayoutAnimationController controller;
            if (isShow) animation = AnimationUtils.loadAnimation(context, R.anim.tabbar_show);
            else animation = AnimationUtils.loadAnimation(context, R.anim.tabbar_hidden);
            controller = new LayoutAnimationController(animation);

            app_bottom.setLayoutAnimation(controller);// 设置动画
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContansUtils.put(ContansUtils.ISBACK, false); //写入非后台运行标记
    }

    /**
     * 再按一次退出程序
     */
    private long mExitTime;//返回退出时间间隔标志

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast(activity, true, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                NotificationUtils.cancel();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
