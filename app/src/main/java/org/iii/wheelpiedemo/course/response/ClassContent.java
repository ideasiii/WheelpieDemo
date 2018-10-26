package org.iii.wheelpiedemo.course.response;

import org.json.JSONObject;

public class ClassContent {
    private String title;
    private String url;
    private String text;
    private String image;
    private String description;
    public ClassContent(JSONObject content) {
        if (content != null) {
            this.title = content.optString("title");
            this.url = content.optString("url");
            this.text = content.optString("text");
            this.image = content.optString("image");
            this.description = content.optString("description");
        }
    }

    public boolean isURLType() {
        return this.url != null &&
            (url.startsWith("https://") || url.startsWith("http://"));
    }

    public String getUrl() {
        return url;
    }
}
