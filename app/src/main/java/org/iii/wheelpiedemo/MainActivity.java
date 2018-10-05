package org.iii.wheelpiedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.iii.wheelpiedemo.sample.ApiActivity;
import org.iii.wheelpiedemo.sample.LineChart;
import org.iii.wheelpiedemo.sample.VideoActivity;

public class MainActivity extends Activity
{
    /**
     * 定義所有activity編號
     */
    final int ACTIVITY_LINE_CHART = 0;
    final int ACTIVITY_API_CLIENT = 1;
    final int ACTIVITY_VIDEO = 2;
    final int ACTIVITY_ANT = 3;
    
    /**
     * 宣告視窗物件變數
     */
    ImageView imageViewLineChart = null;
    ImageView imageViewApi = null;
    ImageView imageViewVideo = null;
    ImageView imageViewANT = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
    private void initView()
    {
        imageViewLineChart = findViewById(R.id.imageViewLineChart);
        imageViewApi = findViewById(R.id.imageViewApi);
        imageViewVideo = findViewById(R.id.imageViewVideoBtn);
        imageViewANT = findViewById(R.id.imageViewANT);
        
        imageViewLineChart.setOnClickListener(viewOnClick);
        imageViewApi.setOnClickListener(viewOnClick);
        imageViewVideo.setOnClickListener(viewOnClick);
        imageViewANT.setOnClickListener(viewOnClick);
    }
    
    /**
     * View物件處理click callback
     */
    private View.OnClickListener viewOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int nId = v.getId();
            switch (nId)
            {
                case R.id.imageViewLineChart:
                    showActive(ACTIVITY_LINE_CHART);
                    break;
                case R.id.imageViewApi:
                    showActive(ACTIVITY_API_CLIENT);
                    break;
                case R.id.imageViewVideoBtn:
                    showActive(ACTIVITY_VIDEO);
                    break;
                case R.id.imageViewANT:
                    showActive(ACTIVITY_ANT);
                    break;
            }
            
        }
    };
    
    private void showActive(int nActivity)
    {
        Intent intent = null;
        
        switch (nActivity)
        {
            case ACTIVITY_LINE_CHART:
                intent = new Intent(this, LineChart.class);
                intent.putExtra("NAME", "line_chart");
                break;
            case ACTIVITY_API_CLIENT:
                intent = new Intent(this, ApiActivity.class);
                intent.putExtra("NAME", "api");
                break;
            case ACTIVITY_VIDEO:
                intent = new Intent(this, VideoActivity.class);
                break;
            case ACTIVITY_ANT:
                intent = new Intent(this, com.dsi.ant.antplus.pluginsampler.heartrate
                        .Activity_SearchUiHeartRateSampler.class);
                break;
        }
        
        startActivity(intent);
    }
    
}
