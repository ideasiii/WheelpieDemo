package org.iii.wheelpiedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.login.LoginActivity;
import org.iii.wheelpiedemo.sample.ApiActivity;
import org.iii.wheelpiedemo.sample.LineChart;
import org.iii.wheelpiedemo.sample.SnowActivity;
import org.iii.wheelpiedemo.chat.SpeechActivity;
import org.iii.wheelpiedemo.sample.TrainingSampleActivity;
import org.iii.wheelpiedemo.sample.VideoActivity;
import org.iii.wheelpiedemo.training.TrainingActivity;

import java.util.HashMap;

import android.os.Handler;

public class MainActivity extends Activity
{
    private final int MSG_RUN_LOGIN = 0;

    static final HashMap<Integer, Class<?>> mapActivity = new HashMap<Integer, Class<?>>()
    {{
        put(R.id.imageViewLineChart, LineChart.class);
        put(R.id.imageViewApi, ApiActivity.class);
        put(R.id.imageViewVideoBtn, VideoActivity.class);
        put(R.id.imageViewANT, com.dsi.ant.antplus.pluginsampler.heartrate
                .Activity_SearchUiHeartRateSampler.class);
        put(R.id.imageViewSpeech, SpeechActivity.class);
        put(R.id.imageViewSnow, SnowActivity.class);
        put(R.id.imageViewTrainging, TrainingSampleActivity.class);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int ImageViewId : mapActivity.keySet())
            findViewById(ImageViewId).setOnClickListener(viewOnClick);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        handler.sendEmptyMessageDelayed(MSG_RUN_LOGIN, 5000);
    }

    @Override
    protected void onPause()
    {
        handler.removeMessages(MSG_RUN_LOGIN);
        super.onPause();

    }

    private View.OnClickListener viewOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            handler.removeMessages(MSG_RUN_LOGIN);
            Intent intent = null;
            intent = new Intent(MainActivity.this, mapActivity.get(v.getId()));
            startActivity(intent);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_RUN_LOGIN:
                    handler.removeMessages(MSG_RUN_LOGIN);
                    Logs.showTrace("show login");
                    Intent intent = null;
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
