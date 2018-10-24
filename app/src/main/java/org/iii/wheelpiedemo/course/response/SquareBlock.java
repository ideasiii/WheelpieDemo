package org.iii.wheelpiedemo.course.response;

import org.json.JSONException;
import org.json.JSONObject;

public class SquareBlock {
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
