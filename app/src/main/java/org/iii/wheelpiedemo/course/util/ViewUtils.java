package org.iii.wheelpiedemo.course.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ViewUtils {
    public static void setViewLayout(ViewGroup parent, View v, int widthDP, int heightDP) {
        if (parent !=null && v != null) {
            //
            parent.addView(v);
            v.getLayoutParams().width = convertDPtoPx(v.getContext(), widthDP);
            v.getLayoutParams().height = convertDPtoPx(v.getContext(), heightDP);
        }
    }

    public static  int convertDPtoPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);  // replace 100 with your dimensions
        return px;
    }

    public static void setMargins(View v, int left, int top, int right, int buttom) {
        if (v instanceof ViewGroup) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.setMargins(
                convertDPtoPx(v.getContext(), left),
                convertDPtoPx(v.getContext(), top),
                convertDPtoPx(v.getContext(), right),
                convertDPtoPx(v.getContext(), buttom)
            );
            v.requestLayout();
        }
    }

    public static class CourseChart {
        private String xAxisTitle;
        private String yAxisTitle;
        public ArrayList<SquareBlock> data = new ArrayList<SquareBlock>();

        public CourseChart(JSONObject chart) {
            if (chart != null) {
                // 取得x軸名稱
                try {
                    JSONObject xAxis = chart.getJSONObject("xAxis");
                    this.xAxisTitle = xAxis.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 取得y軸名稱
                try {
                    JSONObject xAxis = chart.getJSONObject("yAxis");
                    this.yAxisTitle = xAxis.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 取得data
                try {
                    JSONObject series = chart.getJSONObject("series");
                    JSONArray chartData = series.getJSONArray("data");
                    for (int i=0; i< chartData.length(); i+=1) {
                        data.add(new SquareBlock(chartData.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getxAxisTitle() {
            return xAxisTitle;
        }

        public String getyAxisTitle() {
            return yAxisTitle;
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
    }

    public static class SquareBlock {
        private int xStart = 0;
        private int xEnd = 0;
        private int yStart = 0;
        private int yEnd = 0;
        private int x = 0;
        private int y = 0;
        private int z = 0;

        public SquareBlock (JSONObject block) {
            if (block != null) {
                try {
                    xStart = block.getInt("xStart");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    xEnd = block.getInt("xEnd");
                    x = xStart + (xEnd - xStart )/2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    yStart = block.getInt("yStart");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    yEnd = block.getInt("yEnd");
                    y = yStart + (yEnd - yStart )/2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getxStart() {
            return xStart;
        }

        public int getxEnd() {
            return xEnd;
        }

        public int getyEnd() {
            return yEnd;
        }

        public int getyStart() {
            return yStart;
        }
    }

    public static CourseChart extractChartInfo (JSONObject chart) {
        return new CourseChart(chart);
    }
}
