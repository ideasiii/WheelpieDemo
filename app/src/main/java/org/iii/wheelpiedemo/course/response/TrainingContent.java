package org.iii.wheelpiedemo.course.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrainingContent {
    private String title;
    private ArrayList<String> steps = new ArrayList<String>();
    private CourseChart hrrChartInfo;
    private CourseChart strideChartInfo;

    public TrainingContent(JSONObject content) {
        JSONObject chart = null;

        if (content != null) {
            // 取出title
            this.title = content.optString("title");
            // 取出steps
            JSONArray steps = content.optJSONArray("steps");
            if (steps != null) {
                for(int stepIdx=0; stepIdx<steps.length(); stepIdx+=1) {
                    this.steps.add(steps.optString(stepIdx));
                }
            }
            // 取出圖表
            chart = content.optJSONObject("hrrChartInfo");
            if (chart != null) {
                hrrChartInfo = new CourseChart(chart);
            }
            chart = content.optJSONObject("strideChartInfo");
            if (chart != null) {
                strideChartInfo = new CourseChart(chart);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public CourseChart getHrrChartInfo() {
        return hrrChartInfo;
    }

    public CourseChart getStrideChartInfo() {
        return strideChartInfo;
    }
}
