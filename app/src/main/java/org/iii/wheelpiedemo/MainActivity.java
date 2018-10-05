package org.iii.wheelpiedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.iii.wheelpiedemo.sample.ApiActivity;
import org.iii.wheelpiedemo.sample.LineChart;
import org.iii.wheelpiedemo.sample.SpeechActivity;
import org.iii.wheelpiedemo.sample.VideoActivity;

import java.util.HashMap;

public class MainActivity extends Activity
{
    static final HashMap<Integer, Class<?>> mapActivity = new HashMap<Integer, Class<?>>()
    {{
        put(R.id.imageViewLineChart, LineChart.class);
        put(R.id.imageViewApi, ApiActivity.class);
        put(R.id.imageViewVideoBtn, VideoActivity.class);
        put(R.id.imageViewANT, com.dsi.ant.antplus.pluginsampler.heartrate
                .Activity_SearchUiHeartRateSampler.class);
        put(R.id.imageViewSpeech, SpeechActivity.class);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int ImageViewId : mapActivity.keySet())
            findViewById(ImageViewId).setOnClickListener(viewOnClick);
    }

    private View.OnClickListener viewOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = null;
            intent = new Intent(MainActivity.this, mapActivity.get(v.getId()));
            startActivity(intent);
        }
    };
}
