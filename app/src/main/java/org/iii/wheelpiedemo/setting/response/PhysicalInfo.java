package org.iii.wheelpiedemo.setting.response;

import org.json.JSONException;
import org.json.JSONObject;

public class PhysicalInfo {
    private int maxHeartRate;
    private int restHeartRate;
    private int weight;

    public PhysicalInfo(JSONObject response) {
        if (response != null) {
            this.maxHeartRate = response.optInt("maxHeartRate");
            this.restHeartRate = response.optInt("restHeartRate");
            this.weight = response.optInt("weight");
        }
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public int getRestHeartRate() {
        return restHeartRate;
    }

    public int getWeight() {
        return weight;
    }

    static public PhysicalInfo parseResponse (String apiResponse) {
        PhysicalInfo pi = null;
        if (apiResponse != null && apiResponse.length() != 0) {
            try {
                JSONObject jsonResp = new JSONObject(apiResponse);
                if (jsonResp.optBoolean("result")) {
                    JSONObject user = jsonResp.getJSONObject("user");
                    JSONObject physicalInfo = user.getJSONObject("physicalInfo");
                    if (physicalInfo != null) {
                        pi = new PhysicalInfo(physicalInfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pi;
    }
}
