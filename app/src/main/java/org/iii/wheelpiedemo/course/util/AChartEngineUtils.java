package org.iii.wheelpiedemo.course.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.iii.wheelpiedemo.course.util.ViewUtils.*;

public class AChartEngineUtils {

    public static void drawChart(Context context, LinearLayout contentLayout, CourseChart chartInfo) {
        drawChart(context, contentLayout, chartInfo, 0);
    }

    public static void drawChart(Context context, LinearLayout contentLayout, CourseChart chartInfo, int heightDP) {
        if (heightDP == 0) {
            heightDP = 300;
        }
        //CourseChart info = extractChartInfo(rawChartInfo);
        GraphicalView chartView = createRangeBarChart(context, chartInfo);
        setViewLayout(contentLayout, chartView, ViewGroup.LayoutParams.MATCH_PARENT, heightDP);
    }

//    public static GraphicalView createRangeBarChart(Context context, CourseChart info) {
//        double[] minValues = new double[] { -24, -19, -10, -1, 7, 12, 15, 14, 9, 1, -11, -16 };
//        double[] maxValues = new double[] { 7, 12, 24, 28, 33, 35, 37, 36, 28, 19, 11, 4 };
//
//        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//        RangeCategorySeries series = new RangeCategorySeries("Temperature");
//        int length = minValues.length;
//        for (int k = 0; k < length; k++) {
//            series.add(minValues[k], maxValues[k]);
//        }
//        dataset.addSeries(series.toXYSeries());
//
//        int[] colors = new int[] { Color.CYAN };
//        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
//        setChartSettings(renderer, "Monthly temperature range", "Month", "Celsius degrees", 0.5, 12.5,
//                -30, 45, Color.GRAY, Color.LTGRAY);
//        renderer.setBarSpacing(0.5);
//        renderer.setXLabels(0);
//        renderer.setYLabels(10);
//        renderer.addXTextLabel(1, "Jan");
//        renderer.addXTextLabel(3, "Mar");
//        renderer.addXTextLabel(5, "May");
//        renderer.addXTextLabel(7, "Jul");
//        renderer.addXTextLabel(10, "Oct");
//        renderer.addXTextLabel(12, "Dec");
//        renderer.addYTextLabel(-25, "Very cold");
//        renderer.addYTextLabel(-10, "Cold");
//        renderer.addYTextLabel(5, "OK");
//        renderer.addYTextLabel(20, "Nice");
//        renderer.setMargins(new int[] {30, 70, 10, 0});
//        renderer.setYLabelsAlign(Paint.Align.RIGHT);
//
//        XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
//        r.setDisplayChartValues(true);
//        r.setChartValuesTextSize(12);
//        r.setChartValuesSpacing(3);
//        r.setGradientEnabled(true);
//        r.setGradientStart(-20, Color.BLUE);
//        //r.setGradientStop(20, Color.GREEN);
//        r.setGradientStop(50, Color.GREEN);
//
//        GraphicalView chartView = ChartFactory.getRangeBarChartView(
//                context, dataset, renderer, BarChart.Type.DEFAULT);
//        return chartView;
//    }

    private static double getXMax(CourseChart info) {
        //return info != null ?  Math.floor(info.getMaxXAixsValue())/10+ 0.7 : 0;
        return info != null ?  Math.floor(info.getMaxXAixsValue())/10 + 2 : 0;
    }

    private static double getYMax(CourseChart info) {
        return info != null ? info.getMaxYAixsValue() + 10 : 0;
    }

    private static ArrayList<RangeChartData> createRangeChartData(CourseChart info) {
        ArrayList<RangeChartData> data = new ArrayList<RangeChartData>();

        if (info!=null &&info.data.size()>0) {
            for(SquareBlock sb : info.data) {
                // x起始是否從可被10整除
                int xStart = sb.getxStart();
                int xStartRemainder = xStart % 10;
                if (xStartRemainder != 0) {
                    // 如果不是，把x切齊前的資料當一筆加入
                    xStart += xStartRemainder;
                    data.add(new RangeChartData(sb.getxStart(), xStart, sb.getyStart(), sb.getyEnd()));
                }
                // 從切齊的x開始，到結束的x範圍
                int xDiff = sb.getxEnd() - xStart;
                // 計算為10的n倍
                int splitNums = (xDiff) / 10;
                // 加入n筆資料
                for (int splitIdx=0; splitIdx < splitNums; splitIdx+=1) {
                    data.add(new RangeChartData(xStart, xStart+10, sb.getyStart(), sb.getyEnd()));
                    xStart += 10;
                }
                if (xDiff % 10 != 0) {
                    data.add(new RangeChartData(xStart, sb.getxEnd(), sb.getyStart(), sb.getyEnd()));
                }
            }
        }
        return data;
    }

    static private class RangeChartData {
        double xMin;
        double xMax;
        double yMin;
        double yMax;

        public RangeChartData(double xMin, double xMax, double yMin, double yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }
    }

    public static GraphicalView createRangeBarChart(Context context, CourseChart info) {
        // 轉換AChartEngine需要的資料
        ArrayList<RangeChartData> data = createRangeChartData(info);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        RangeCategorySeries series = new RangeCategorySeries("");

        for (RangeChartData rcd : data) {
            series.add((int)rcd.yMin, (int)rcd.yMax);
        }
        dataset.addSeries(series.toXYSeries());

        int[] colors = new int[] { Color.CYAN };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer,
            "",//"Monthly temperature range",
            info.getxAxisTitle(),
            info.getyAxisTitle(),
            0.5,
            AChartEngineUtils.getXMax(info), //6.5,
            0,
            AChartEngineUtils.getYMax(info),
            Color.GRAY, Color.LTGRAY
        );
        //renderer.setBarSpacing(0.5);
        renderer.setBarSpacing(0.1);
        renderer.setXLabels(0); // 不顯示X軸addXTextLabel的double值，而是Label值
        renderer.setYLabels(10);
        for (RangeChartData rcd : data) {
            String xLabel = String.format("%s-%s", (int)rcd.xMin, (int) rcd.xMax);
            renderer.addXTextLabel(data.indexOf(rcd)+1, xLabel);
        }
        renderer.addYTextLabel(50, "50");
        renderer.addYTextLabel(100, "100");
        renderer.addYTextLabel(150, "150");

        renderer.setMargins(new int[] {30, 70, 15, 10});
        renderer.setYLabelsAlign(Paint.Align.RIGHT);

        XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
        r.setDisplayChartValues(true);
        r.setChartValuesTextSize(12);
        r.setChartValuesSpacing(3);
        r.setGradientEnabled(true);
        r.setGradientStart(10, Color.BLUE);
        r.setGradientStop(AChartEngineUtils.getYMax(info) - 20, Color.GREEN);
        //r.setGradientStop(50, Color.GREEN); //會讓min怪掉

        GraphicalView chartView = ChartFactory.getRangeBarChartView(
                context, dataset, renderer, BarChart.Type.DEFAULT);
        return chartView;
    }

    protected static XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(22/*16*/);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        //disable zoom
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false, false);
        // disable lengend
        renderer.setShowLegend(false);
    }
}
