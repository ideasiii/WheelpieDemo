package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.iii.wheelpiedemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChart extends Activity
{
    static int nXData = 0;
    static boolean bRun = false;
    Timer timer = new Timer(true);
    private int totalSeconds = 11;//總共有多少秒的數據顯示
    private float minY = 0f;//Y軸坐標最小值
    private float maxY = 150f;//Y軸坐標最大值
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
    private LineChartView lineChart;
    private String axesYName = "心率";//Y坐標名稱
    private String axesXName = "秒";
    
    
    private boolean hasLines = true;//是否要折線連接
    private boolean hasPoints = true;//數據點是否要標注
    private ValueShape shape = ValueShape.CIRCLE;//數據標注點形狀,這裡是圓形 （有三種 ：ValueShape.SQUARE
    // ValueShape.CIRCLE  ValueShape.DIAMOND）
    private boolean isFilled = false;//是否需要填充和X軸之間的空間
    private boolean isCubic = false;//曲線是否平滑，即是曲線還是折線
    private boolean hasLabels = false;//數據點是否顯示數據值
    private boolean hasLabelForSelected = true;//點擊數據坐標提示數據（設置了這個hasLabels(true);就無效）
    private boolean hasTiltedLabels = false;  //X坐標軸字體是斜的顯示還是直的，true是斜的顯示
    private String lineColor = "#FF0000";//折現顏色(#FF0000紅色)
    private int textColor = Color.WHITE;//設置字體顏色
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        
        lineChart = (LineChartView) findViewById(R.id.chartLine);
        
        initLineChart();//初始化
        
        lineChart.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                run();
            }
        });
    }
    
    private void initLineChart()
    {
        mAxisXValues.clear();
        mPointValues.clear();
        for (float i = minY; i <= maxY; i += 10)
        {
            mAxisYValues.add(new AxisValue(i).setLabel(i + ""));
        }
        for (int i = 0; i < totalSeconds; ++i)
        {
            mAxisXValues.add(i, new AxisValue(i).setLabel(String.valueOf(++nXData)));
            mPointValues.add(new PointValue(i, ThreadLocalRandom.current().nextInt(65, 150)));
        }
        
        Line line = new Line(mPointValues).setColor(Color.parseColor(lineColor));  //折線的顏色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(shape);//折線圖上每個數據點的形狀
        line.setCubic(isCubic);//曲線是否平滑，即是曲線還是折線
        line.setFilled(isFilled);//是否填充曲線的面積
        line.setHasLabels(hasLabels);//曲線的數據坐標是否加上備註
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);//點擊數據坐標提示數據
        line.setHasLines(hasLines);//是否用線顯示。如果為false 則沒有曲線只有點顯示
        line.setHasPoints(hasPoints);//是否顯示圓點
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        
        //坐標軸X
        Axis axisX = new Axis(); //X軸
        axisX.setHasTiltedLabels(hasTiltedLabels);  //X坐標軸字體是斜的顯示還是直的，true是斜的顯示
        axisX.setTextColor(textColor);  //設置字體顏色
        axisX.setName(axesXName);
        axisX.setMaxLabelChars(0);
        axisX.setValues(mAxisXValues);
        data.setAxisXBottom(axisX);//x 軸在底部
        
        //坐標軸Y
        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setName(axesYName);
        axisY.setValues(mAxisYValues);
        data.setAxisYLeft(axisY);
        
        //設置行為屬性，支持縮放、滑動以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 4);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.bottom = minY;
        v.top = maxY;
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);
        lineChart.setViewportCalculationEnabled(false);
    }
    
    private void run()
    {
        if (bRun)
        {
            bRun = false;
            timer.cancel();
        }
        else
        {
            bRun = true;
            timer = new Timer(true);
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    updateChart();
                }
            }, 1000, 1000);
        }
    }
    
    private void updateChart()
    {
        mPointValues.clear();
        mAxisXValues.clear();
        
        if (10 < nXData)
        {
            nXData -= 10;
        }
        
        for (int i = 0; i < 11; ++i)
        {
            mAxisXValues.add(i, new AxisValue(i).setLabel(String.valueOf(++nXData)));
            mPointValues.add(new PointValue(i, ThreadLocalRandom.current().nextInt(65, 150)));
        }
        
        Line line = new Line(mPointValues).setColor(Color.parseColor(lineColor));
        List lines = new ArrayList();
        lines.add(line);
        
        LineChartData data = lineChart.getLineChartData();
        data.setLines(lines);
        
        Axis axisX = new Axis(); //X軸
        axisX.setHasTiltedLabels(hasTiltedLabels);  //X坐標軸字體是斜的顯示還是直的，true是斜的顯示
        axisX.setTextColor(textColor);  //設置字體顏色
        axisX.setName(axesXName);
        axisX.setMaxLabelChars(0);
        axisX.setValues(mAxisXValues);
        data.setAxisXBottom(axisX);//x 軸在底部
        
        lineChart.setLineChartData(data);
    }
    
}
