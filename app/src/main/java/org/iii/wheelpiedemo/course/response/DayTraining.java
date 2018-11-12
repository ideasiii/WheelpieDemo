package org.iii.wheelpiedemo.course.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DayTraining {
    private String dayInfo = "";
    private boolean trainable = false;
    private ArrayList<TrainingContent> contents = new ArrayList<TrainingContent>();
    private ClassInfo classInfo;

    public DayTraining(JSONObject response) {
        if (response != null) {
            //取出訓練類型及第幾天
            this.dayInfo = response.optString("dayInfo");
            // 取出訓練課程內容
            JSONArray trainingContents = response.optJSONArray("contents");
            if (trainingContents != null) {
                for (int i=0; i<trainingContents.length(); i+=1) {
                    JSONObject content = trainingContents.optJSONObject(i);
                    if (content != null) {
                        contents.add(new TrainingContent(content));
                    }
                }
            }
            // 取出是否可訓練
            this.trainable = response.optBoolean("trainable");
            // 取出課程相關資訊
            JSONObject info = response.optJSONObject("classInfo");
            if (info != null) {
                classInfo = new ClassInfo(info);
            }
        }
    }

    public String getDayInfo() {
        return dayInfo;
    }

    public ArrayList<TrainingContent> getContents() {
        return contents;
    }

    public boolean isTrainable() {
        return trainable;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    static public DayTraining parseResponse (String apiResponse) {
        DayTraining dt = null;
        if (apiResponse != null && apiResponse.length() != 0) {
            try {
                JSONObject jsonResp = new JSONObject(apiResponse);
                JSONObject dayView = jsonResp.getJSONObject("planDayView");
                JSONObject dayTraining = dayView.getJSONObject("dayTraining");
                dt = new DayTraining(dayTraining);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dt;
    }
}

