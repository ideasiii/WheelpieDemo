package org.iii.wheelpiedemo.setting.response;

import org.json.JSONObject;

public class PhysicalInfo {
    private int maxHeartRate;
    private int restHeartRate;

    public PhysicalInfo(JSONObject response) {
        if (response != null) {
            this.maxHeartRate = response.optInt("maxHeartRate");
            this.restHeartRate = response.optInt("restHeartRate");
        }
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public int getRestHeartRate() {
        return restHeartRate;
    }
}
