package com.whh.tallynote.activity;

import android.os.Bundle;

import com.whh.tallynote.R;
import com.whh.tallynote.database.MonthNoteDao;
import com.whh.tallynote.model.MonthNote;
import com.jn.chart.charts.BarChart;
import com.jn.chart.charts.LineChart;
import com.jn.chart.data.BarEntry;
import com.jn.chart.data.Entry;
import com.jn.chart.manager.BarChartManager;
import com.jn.chart.manager.LineChartManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 月账分析
 * Created by wuhuihui on 2017/8/14.
 */
public class MonthNotesAnalyseActivity extends BaseActivity {

    private List<MonthNote> monthNotes;
    private int size; //月账大小
    private ArrayList<String> xValues; //x轴的数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("月账分析", R.layout.activity_month_analyse);

        monthNotes = MonthNoteDao.getMonthNotes();
        size = monthNotes.size();

        xValues = (ArrayList<String>) MonthNote.formateDurations();
        BarChartManager.setUnit("单位：元");

        initBarchartData(); //柱状图分析
        initLineChartData(); //线型图分析

    }

    /**
     * 月账支出/工资/收益柱状图分析
     */
    private void initBarchartData() {
        //支出统计
        BarChart barChart4Pay = (BarChart) findViewById(R.id.barChart4Pay);
        barChart4Pay.setDescription("支出统计"); //设置图表的描述
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValues.add(new BarEntry(Float.parseFloat(monthNotes.get(i).getPay()), i));
        }
        BarChartManager.initBarChart(context, barChart4Pay, xValues, yValues);


        //工资统计
        BarChart barChart4Salary = (BarChart) findViewById(R.id.barChart4Salary);
        barChart4Salary.setDescription("工资统计");
        yValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            yValues.add(new BarEntry(Float.parseFloat(monthNotes.get(i).getSalary()), i));
        }
        BarChartManager.initBarChart(context, barChart4Salary, xValues, yValues);

        //收益统计
        BarChart barChart4Income = (BarChart) findViewById(R.id.barChart4Income);
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

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
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
