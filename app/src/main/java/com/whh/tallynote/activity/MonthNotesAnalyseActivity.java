package com.whh.tallynote.activity;

import android.os.Bundle;

import com.jn.chart.charts.BarChart;
import com.jn.chart.charts.LineChart;
import com.jn.chart.data.BarEntry;
import com.jn.chart.data.Entry;
import com.jn.chart.manager.BarChartManager;
import com.jn.chart.manager.LineChartManager;
import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.MonthNote;
import com.whh.tallynote.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 月账分析
 * Created by wuhuihui on 2017/8/14.
 */
public class MonthNotesAnalyseActivity extends BaseActivity {

    @BindView(R.id.barChart4Pay)
    public BarChart barChart4Pay;
    @BindView(R.id.barChart4Salary)
    public BarChart barChart4Salary;
    @BindView(R.id.barChart4Income)
    public BarChart barChart4Income;
    @BindView(R.id.lineChart)
    public LineChart lineChart;

    private List<MonthNote> monthNotes = new ArrayList<>();
    private int size; //所需月账大小
    private ArrayList<String> xValues; //x轴的数据

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("月账分析", R.layout.activity_month_analyse);
    }

    @Override
    protected void initView() {
        List<MonthNote> allList = MyApp.monthNoteDBHandle.getMonthNotes();
        int allSize = allList.size();
        //为避免数据太多，导致图标显示拥挤，仅取得其中最多12条数据，12条数据按时间间断来获取。
        int index = allSize / 11;
        LogUtils.i("division", allSize + "/11=" + index);
        for (int i = 0; i < allSize; i++) {
            if (i % index == 0) {
                LogUtils.i("division", i + "%" + index + "=0");
                monthNotes.add(allList.get(i));
            } else if (i == allSize - 1 && i % index != 0) {
                monthNotes.add(allList.get(i));
            }
        }

        size = monthNotes.size();
        LogUtils.i("division", "size=" + size);

        xValues = (ArrayList<String>) MonthNote.formateDurations(monthNotes);
        BarChartManager.setUnit("单位：元");
    }

    @Override
    protected void initEvent() {
        initBarchartData(); //柱状图分析
        initLineChartData(); //线型图分析
    }

    /**
     * 月账支出/工资/收益柱状图分析
     */
    private void initBarchartData() {
        //支出统计
        barChart4Pay.setDescription("支出统计"); //设置图表的描述
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValues.add(new BarEntry(Float.parseFloat(monthNotes.get(i).getPay()), i));
        }
        BarChartManager.initBarChart(context, barChart4Pay, xValues, yValues);


        //工资统计
        barChart4Salary.setDescription("工资统计");
        yValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValues.add(new BarEntry(Float.parseFloat(monthNotes.get(i).getSalary()), i));
        }
        BarChartManager.initBarChart(context, barChart4Salary, xValues, yValues);

        //收益统计
        barChart4Income.setDescription("收益统计");
        yValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValues.add(new BarEntry(Float.parseFloat(monthNotes.get(i).getIncome()), i));
        }
        BarChartManager.initBarChart(context, barChart4Income, xValues, yValues);
    }


    /**
     * 月账支出/工资/收益线型图分析
     */
    private void initLineChartData() {

        //设置图表的描述
        lineChart.setDescription("月账统计");

        //设置第一条折线y轴的数据
        ArrayList<Entry> yValue = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValue.add(new Entry(Float.parseFloat(monthNotes.get(i).getPay()), i));
        }
        //设置折线的名称
        LineChartManager.setLineName("支出值");

        //设置第二条折线y轴的数据
        ArrayList<Entry> yValue1 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValue1.add(new Entry(Float.parseFloat(monthNotes.get(i).getSalary()), i));
        }
        LineChartManager.setLineName1("工资值");

        //设置第三条折线y轴的数据
        ArrayList<Entry> yValue2 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValue2.add(new Entry(Float.parseFloat(monthNotes.get(i).getIncome()), i));
        }
        LineChartManager.setLineName2("收益值");

        //创建三条折线的图表
        LineChartManager.initDoubleLineChart(context, lineChart, xValues, yValue, yValue1, yValue2);
    }

}
