package org.iii.wheelpiedemo.course.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.iii.wheelpiedemo.course.response.CourseChart;
import org.iii.wheelpiedemo.course.response.SquareBlock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;

import static org.iii.wheelpiedemo.course.util.ViewUtils.*;

public class HelloChartUtils {
    public static void drawChart(Context context, LinearLayout contentLayout, JSONObject rawChartInfo) {
        BubbleChartView chartView = createBubbleChart(context);
        setViewLayout(contentLayout, chartView, ViewGroup.LayoutParams.MATCH_PARENT, 250);
        CourseChart info = extractChartInfo(rawChartInfo);
        updateBubbleChart(chartView, info);
    }

    public static BubbleChartView createBubbleChart(Context context) {
        BubbleChartView chart = new BubbleChartView(context);

//        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
//        chart.setScrollEnabled(true);
        Viewport viewport = new Viewport(chart.getMaximumViewport());
        viewport.top = 220;
        viewport.bottom = 0;
//        viewport.right = 10;
        viewport.left = 0;
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);

        return chart;
    }

    public static void updateBubbleChart(BubbleChartView chart, CourseChart chartInfo){
        int BUBBLES_NUM = chartInfo != null ? chartInfo.data.size() : 0;

        BubbleChartData data;
        boolean hasAxes = true;
        boolean hasAxesNames = true;
        ValueShape shape = ValueShape.SQUARE;
        boolean hasLabels = false;
        boolean hasLabelForSelected = false;


        List<BubbleValue> values = new ArrayList<BubbleValue>();
        //for (int i = 0; i < chartInfo.data.size(); ++i) {
        for (SquareBlock sb : chartInfo.data) {
            //BubbleValue value = new BubbleValue(i, (float) Math.random() * 100, (float) Math.random() * 1000);
            BubbleValue value = new BubbleValue(sb.getX(), sb.getY(), 50);
            value.setColor(ChartUtils.pickColor());
            value.setShape(shape);
            values.add(value);
        }

        data = new BubbleChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName(chartInfo.getxAxisTitle());
                axisY.setName(chartInfo.getyAxisTitle());
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setBubbleChartData(data);
    }
}
