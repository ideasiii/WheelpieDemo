package org.iii.wheelpiedemo.course.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseChart {
    private static Pattern unitPattern = Pattern.compile("\\((.+)\\)");
    private int drawXRegularity;
    private String xUnit;
    private String xAxisTitle;
    private String yAxisTitle;
    public ArrayList<SquareBlock> data = new ArrayList<SquareBlock>();

    public CourseChart(JSONObject chart) {
        if (chart != null) {
            // 取得x軸名稱
            JSONObject xAxis = chart.optJSONObject("xAxis");
            if (xAxis != null) {
                this.xAxisTitle = xAxis.optString("text");
                xUnit = extractUnit(this.xAxisTitle);
            }
            // 取得y軸名稱
            JSONObject yAxis = chart.optJSONObject("yAxis");
            if (yAxis != null) {
                this.yAxisTitle = yAxis.optString("text");
            }
            // 取得data
            JSONObject series = chart.optJSONObject("series");
            JSONArray chartData = series != null ? series.optJSONArray("data") : null;
            if (chartData != null) {
                for (int i=0; i< chartData.length(); i+=1) {
                    JSONObject block = chartData.optJSONObject(i);
                    if (block !=null) {
                        data.add(new SquareBlock(block));
                    }
                }
            }
        }
    }

    public String getxAxisTitle() {
        return xAxisTitle;
    }

    public String getyAxisTitle() {
        return yAxisTitle;
    }

    private String extractUnit(String title) {
        String unit = "";
        if (title != null && title.length() > 0) {
            Matcher m = unitPattern.matcher(title);
            if (m.find()) {
                unit = m.group(1);
            }
        }
        return unit;
    }

    public String getxUnit() {
        return xUnit;
    }

    public int getMaxXAixsValue() {
        if (data.size() > 0) {
            return data.get(data.size()-1).getxEnd();
        }
        return 0;
    }

    public int getMaxYAixsValue() {
        if (data.size() > 0) {
            return data.get(data.size()-1).getyEnd();
        }
        return 0;
    }

    public void setDrawXRegularity(int drawXRegularity) {
        this.drawXRegularity = drawXRegularity;
    }

    public int getDrawXRegularity() {
        return drawXRegularity;
    }
}

