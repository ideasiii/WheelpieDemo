package org.iii.wheelpiedemo.course.response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassInfo {
    private String code;
    private ArrayList<ClassContent> contents = new ArrayList<ClassContent>();

    public ClassInfo(JSONObject info) {
        if (info != null) {
            this.code = info.optString("code");

            JSONArray contents = info.optJSONArray("contents");
            if (contents != null) {
                for (int i=0; i< contents.length(); i+=1) {
                    JSONObject content = contents.optJSONObject(i);
                    if (content !=null) {
                        this.contents.add(new ClassContent(content));
                    }
                }
            }
        }
    }

    public ArrayList<ClassContent> getContents() {
        return contents;
    }
}
