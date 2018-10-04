package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChart extends Activity
{
    static int nXData = 0;
    Timer timer = new Timer(true);
    Boolean bRun = false;
    LineChartView lineChartView;
    String[] axisData = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        lineChartView = findViewById(R.id.chartLine);
        lineChartView = findViewById(R.id.chartLine);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setScrollEnabled(true);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 120;
        viewport.bottom = 60;
        viewport.right = 10;
        viewport.left = 0;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
        lineChartView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                run();
            }
        });
    
        Intent intent = getIntent();
        String strName = intent.getStringExtra("NAME");
        Logs.showTrace("my name " + strName);
        updateChart();
    }
    
    private void updateChart()
    {
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));
        
        String strLabel;
        if (10 < nXData)
        {
            nXData -= 10;
        }
        
        for (int i = 0; i < 11; i++)
        {
            strLabel = String.valueOf(nXData++);
            axisValues.add(i, new AxisValue(i).setLabel(strLabel));
        }
        
        for (int i = 0; i < 11; ++i)
        {
            yAxisValues.add(new PointValue(i, ThreadLocalRandom.current().nextInt(65, 110)));
        }
        
        List lines = new ArrayList();
        lines.add(line);
        
        LineChartData data = new LineChartData();
        data.setLines(lines);
        
        Axis axis = new Axis();
        axis.setName("秒");
        axis.setValues(axisValues);
        axis.setTextSize(9);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);
        
        Axis yAxis = new Axis();
        yAxis.setName("心律");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(9);
        data.setAxisYLeft(yAxis);
        
        lineChartView.setLineChartData(data);
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
            timer.schedule(new MyTimerTask(), 1000, 1000);
        }
        
    }
    
    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            updateChart();
        }
    }
    
    ;
    
}
