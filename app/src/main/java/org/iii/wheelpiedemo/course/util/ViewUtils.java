package org.iii.wheelpiedemo.course.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.iii.wheelpiedemo.course.response.CourseChart;
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

    public static CourseChart extractChartInfo (JSONObject chart) {
        return new CourseChart(chart);
    }
}
