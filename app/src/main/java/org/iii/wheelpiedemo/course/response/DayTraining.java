package org.iii.wheelpiedemo.course.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DayTraining {
    private String dayInfo = "";
    private boolean trainable = false;
    private ArrayList<TrainingContent> contents = new ArrayList<TrainingContent>();

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
}

