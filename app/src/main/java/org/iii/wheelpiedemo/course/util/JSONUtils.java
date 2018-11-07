package org.iii.wheelpiedemo.course.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    static public  String getResponseJSONString(JSONObject clientResp) {
        String jsonString = null;
        if (clientResp instanceof JSONObject && clientResp.has("data")) {
            try {
                jsonString = ((JSONObject)clientResp).getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }
}
