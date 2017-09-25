package com.fengyang.tallynote.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.activity.DayListActivity;
import com.fengyang.tallynote.activity.MemoNoteDetailActivity;
import com.fengyang.tallynote.activity.MemoNoteListActivity;
import com.fengyang.tallynote.activity.MonthListActivity;
import com.fengyang.tallynote.activity.NotePadListActivity;
import com.fengyang.tallynote.database.DayNoteDao;
import com.fengyang.tallynote.database.MonthNoteDao;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.LogUtils;
import com.fengyang.tallynote.utils.StringUtils;
import com.fengyang.tallynote.view.IOSScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TallyFragment extends Fragment {

    private static final String TAG = "TallyFragment";
    private Activity activity;
    private View content;//内容布局

    private TextView today, last_balanceTv, current_payTv;
    private String last_balance, current_pay;
    private boolean isSeen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_tally, container, false);
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

        //当页面滑动到中间位置，导致看不到顶部时显示到达顶部的按钮
        final IOSScrollView scrollView = (IOSScrollView) content.findViewById(R.id.scrollView);
        final ImageButton top_btn = (ImageButton) content.findViewById(R.id.top_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > 500) {
                        top_btn.setVisibility(View.VISIBLE);
                        top_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                    } else {
                        top_btn.setVisibility(View.GONE);
                    }
                }
            });
        }

        //显示当前日期
        today = (TextView) content.findViewById(R.id.today);
        today.setText(DateUtils.getDate());

        //默认隐藏当前结余和当前支出
        last_balanceTv = (TextView) content.findViewById(R.id.last_balanceTv);
        last_balanceTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        current_payTv = (TextView) content.findViewById(R.id.current_pay);
        current_payTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        showData();

    }

    /**
     * 显示数据
     */
    private void showData() {
        play(false);
        showDayNote(); //日账
        showMonthNote(); //月账
        clickListener(); //点击事件
        showMemo(); //备忘录
        showNotepad(); //记事本
    }

    /**
     * 显示最近一次日账单记录
     */
    private void showDayNote() {
        List<DayNote> dayNotes = DayNoteDao.getDayNotes();
        LinearLayout cur_layout = (LinearLayout) content.findViewById(R.id.cur_layout);
        if (dayNotes.size() > 0) {
            //显示本次月记录总支出
            current_pay = StringUtils.showPrice(DayNote.getAllSum() + "");
            current_payTv.setText("....");

            //显示最近一次日记录支出
            cur_layout.setVisibility(View.VISIBLE);
            DayNote dayNote = dayNotes.get(dayNotes.size() - 1);
            View spot = content.findViewById(R.id.spot);
            ImageView tag = (ImageView) content.findViewById(R.id.tag);
            TextView time = (TextView) content.findViewById(R.id.time);
            time.setText(DateUtils.diffTime(dayNote.getTime()));
            TextView usage = (TextView) content.findViewById(R.id.usage);
            time.setText(DateUtils.diffTime(dayNote.getTime()));
            usage.setText(DayNote.getUserTypeStr(dayNote.getUseType()));
            if (dayNote.getUseType() == DayNote.consume) {
                spot.setBackgroundResource(R.drawable.shape_day_consume_spot);
                tag.setImageResource(R.drawable.consume);
            } else if (dayNote.getUseType() == DayNote.account_out) {
                spot.setBackgroundResource(R.drawable.shape_day_out_spot);
                tag.setImageResource(R.drawable.account_out);
            } else if (dayNote.getUseType() == DayNote.account_in) {
                spot.setBackgroundResource(R.drawable.shape_day_in_spot);
                tag.setImageResource(R.drawable.account_in);
            }
            TextView money = (TextView) content.findViewById(R.id.money);
            money.setText(StringUtils.showPrice(dayNote.getMoney()));
            if (!TextUtils.isEmpty(dayNote.getRemark())) {
                TextView remask = (TextView) content.findViewById(R.id.remask);
                remask.setText(dayNote.getRemark());
            }

            content.findViewById(R.id.item_day_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, DayListActivity.class));
                }
            });
        } else {
            cur_layout.setVisibility(View.GONE);
            current_payTv.setText("当月还没有记录~~");
        }

    }

    /**
     * 显示最近一次月结算记录
     */
    private void showMonthNote() {
        List<MonthNote> monthNotes = MonthNoteDao.getMonthNotes();
        LinearLayout last_layout = (LinearLayout) content.findViewById(R.id.last_layout);
        if (monthNotes.size() > 0) {
            last_layout.setVisibility(View.VISIBLE);
            last_balance = StringUtils.showPrice(monthNotes.get(monthNotes.size() - 1).getActual_balance());
            last_balanceTv.setText("....");
        } else {
            last_layout.setVisibility(View.GONE);
        }
    }

    private void clickListener() {
        content.findViewById(R.id.seenCheck).setOnClickListener(clickListener);
        content.findViewById(R.id.reload).setOnClickListener(clickListener);
        content.findViewById(R.id.current_pay).setOnClickListener(clickListener);
        content.findViewById(R.id.todayNotes).setOnClickListener(clickListener);
        content.findViewById(R.id.last_balanceTv).setOnClickListener(clickListener);
        content.findViewById(R.id.toMonthNotes).setOnClickListener(clickListener);
        content.findViewById(R.id.more_memo).setOnClickListener(clickListener);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageButton seenCheck = (ImageButton) content.findViewById(R.id.seenCheck);
            switch (v.getId()) {
                case R.id.seenCheck:
                    //密文明文显示
                    if (isSeen) {
                        isSeen = false;
                        seenCheck.setImageResource(R.drawable.eye_open_pwd);
                        //设置EditText文本为隐藏的
                        last_balanceTv.setText(last_balance);
                        current_payTv.setText(current_pay);
                    } else {
                        isSeen = true;
                        seenCheck.setImageResource(R.drawable.eye_close_pwd);
                        //设置EditText文本为可见的
                        last_balanceTv.setText("....");
                        current_payTv.setText("....");
                    }
                    break;

                case R.id.reload:
                    showData();
                    break;

                case R.id.current_pay:
                case R.id.todayNotes:
                    startActivity(new Intent(activity, DayListActivity.class));
                    break;

                case R.id.last_balanceTv:
                case R.id.toMonthNotes:
                    startActivity(new Intent(activity, MonthListActivity.class));
                    break;

                case R.id.more_memo:
                    startActivity(new Intent(activity, MemoNoteListActivity.class));
                    break;
            }
        }
    };


    /**
     * 显示备忘录
     */
    private void showMemo() {
        try {
            TextView more_memo = (TextView) content.findViewById(R.id.more_memo);
            final List<MemoNote> memoNoteList = MemoNote.getUnFinish();
            LogUtils.i("showMemo", memoNoteList.size() + "--" + memoNoteList.toString());

            LinearLayout memo_layout = (LinearLayout) content.findViewById(R.id.memo_layout);
            memo_layout.removeAllViews();

            if (memoNoteList.size() > 0) {
                more_memo.setVisibility(View.VISIBLE);
                memo_layout.setVisibility(View.VISIBLE);

                int size;
                if (memoNoteList.size() > 3) {
                    size = 3;
                } else size = memoNoteList.size();

                for (int i = 0; i < size; i++) {
                    View memo_view = View.inflate(activity, R.layout.view_streaner_memo, null);
                    TextView memoView = (TextView) memo_view.findViewById(R.id.streamer_txt);
                    MemoNote memoNote = memoNoteList.get(i);
                    memoView.setText(DateUtils.diffTime(memoNote.getTime()) + "  " + memoNote.getContent());
                    final int finalI = i;
                    memo_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, MemoNoteDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("memoNote", memoNoteList.get(finalI));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    if (i == size - 1) {
                        memo_view.findViewById(R.id.line).setVisibility(View.GONE);
                    } else {
                        memo_view.findViewById(R.id.line).setVisibility(View.VISIBLE);
                    }
                    memo_layout.addView(memo_view);
                }

            } else {
                more_memo.setVisibility(View.GONE);
                memo_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogUtils.e(TAG + "-showMemo", e.toString());
        }

    }

    /**
     * 显示记事本
     */
    private boolean isRunning;
    private ViewAnimator animator_notepad;
    private List<NotePad> notePads = new ArrayList<>();

    private void showNotepad() {
        try {
            notePads = NotePadDao.getNotePads();
            Collections.reverse(notePads);
            if (notePads.size() > 0) {
                int size;
                if (notePads.size() > 3) {
                    size = 3;
                } else size = notePads.size();
                animator_notepad = (ViewAnimator) content.findViewById(R.id.animator_notepad);
                animator_notepad.setVisibility(View.VISIBLE);
                animator_notepad.removeAllViews();
                for (int i = 0; i < size; i++) {
                    final int finalI = i;
                    View notepad_view = View.inflate(activity, R.layout.view_streaner_notepad, null);
                    TextView streamer_txt = (TextView) notepad_view.findViewById(R.id.streamer_txt);
                    streamer_txt.setText(notePads.get(finalI).getWords());

                    notepad_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(activity, NotePadListActivity.class));
                        }
                    });

                    notepad_view.findViewById(R.id.line).setVisibility(View.GONE);
                    animator_notepad.addView(notepad_view);
                }

                play(true);
            }
        } catch (Exception e) {
            LogUtils.e(TAG + "-showNotepad", e.toString());
        }

    }

    /**
     * 横幅加载下一条,条幅仅1条则不轮播
     */
    private void showNext() {
        try {
            if (animator_notepad != null && notePads.size() > 1) {
                animator_notepad.setOutAnimation(activity, R.anim.slide_out_up);
                animator_notepad.setInAnimation(activity, R.anim.slide_in_down);
                animator_notepad.showNext();
            }
        } catch (Exception e) {
            LogUtils.e(TAG + "-showNext", e.toString());
        }
    }

    /**
     * 轮播控制
     *
     * @param flag true开始，false停止
     */
    private void play(boolean flag) {
        isRunning = flag;
        if (isRunning) handler.sendEmptyMessageDelayed(0, 3000);
        else handler.removeMessages(0);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            showNext();//条幅滚到下一条
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    };

}
