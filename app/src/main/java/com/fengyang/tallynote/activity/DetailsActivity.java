package com.fengyang.tallynote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fengyang.tallynote.MyApp;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.IncomeNote;
import com.fengyang.tallynote.model.MonthNote;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.StringUtils;

/**
 * Created by wuhuihui on 2017/7/3.
 */
public class DetailsActivity extends BaseActivity {

    private DayNote dayNote;
    private MonthNote monthNote;
    private IncomeNote incomeNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("日账记录详情", R.layout.activity_detail);

        initView();

    }

    private void initView() {
        TextView detail_info = (TextView) findViewById(R.id.detail_info);
        Intent intent = getIntent();
        String message ;
        final int type = intent.getIntExtra("type", MyApp.DAY);
        if (type == MyApp.DAY) {
            setTitle("日账记录详情");
            dayNote = (DayNote) intent.getSerializableExtra("note");
            message = dayNote.getUsage() + "：" + StringUtils.showPrice(dayNote.getMoney()) + " (" + dayNote.getRemark() + ")" +
                    "\n记录时间：" + dayNote.getTime();
        } else if (type == MyApp.MONTH) {
            setTitle("月账记录详情");
            monthNote = (MonthNote) intent.getSerializableExtra("note");
            message = "上次结余：" + StringUtils.showPrice(monthNote.getLast_balance()) +
                    "\n本次支出：" + StringUtils.showPrice(monthNote.getPay()) +
                    "\n本次工资：" + StringUtils.showPrice(monthNote.getSalary()) +
                    "\n本次收益：" + StringUtils.showPrice(monthNote.getIncome()) +
                    "\n家用补贴：" + StringUtils.showPrice(monthNote.getHomeuse()) +
                    "\n本次结余：" + StringUtils.showPrice(monthNote.getBalance()) +
                    "\n实际结余：" + StringUtils.showPrice(monthNote.getActual_balance()) +
                    "\n月结备注：" + monthNote.getRemark() +
                    "\n记录时间：" + monthNote.getTime();
        } else {
            setTitle("理财记录详情");
            incomeNote = (IncomeNote) intent.getSerializableExtra("note");
            message = "投入金额：" + StringUtils.showPrice(incomeNote.getMoney())  +
                    "\n预期年化：" + incomeNote.getIncomeRatio()  +
                    "\n投资期限：" + incomeNote.getDays()  +
                    "\n投资时期：" + incomeNote.getDurtion()  +
                    "\n拟日收益：" + StringUtils.showPrice(incomeNote.getDayIncome())  +
                    "\n最终收益：" + StringUtils.showPrice(incomeNote.getFinalIncome()) +
                    "\n记录时间：" + incomeNote.getTime();
        }
        
        detail_info.setText(message);

        if (intent.hasExtra("last")) {
            setRightBtnListener("删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.showMsgDialog(activity, "删除提示", "是否确定删除此条记录", new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            if (type == MyApp.DAY) {
                                MyApp.utils.delDNote(dayNote);

                            } else if (type == MyApp.MONTH){
                                MyApp.utils.delMNote(monthNote);

                            } else {
                                MyApp.utils.delIncome(incomeNote);

                            }
                            finish();
                        }
                    }, new DialogUtils.DialogListener(){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                        }
                    });

                }
            });
        }
    }
}
