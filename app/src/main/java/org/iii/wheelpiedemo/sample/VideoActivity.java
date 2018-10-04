package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import org.iii.wheelpiedemo.R;

public class VideoActivity extends Activity
{
    RelativeLayout rlMain = null;
    private static VideoPlayer					videoPlayer		= null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        rlMain = findViewById(R.id.rlVideoMain);
    
       // videoPlayer = new VideoPlayer(this);
       // videoPlayer.showController(true);
    
       // rlMain.addView(videoPlayer);
    }
}
