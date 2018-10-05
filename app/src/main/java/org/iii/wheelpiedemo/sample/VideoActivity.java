package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;

import org.iii.wheelpiedemo.R;

public class VideoActivity extends Activity {
    private VideoPlayer videoPlayer1 = null;
    private VideoPlayer videoPlayer2 = null;
    private VideoPlayer videoPlayer3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // 播放video stream
        videoPlayer1 = findViewById(R.id.videoPlayer1);
        videoPlayer1.showController(true);
        Uri vidUri = Uri.parse("http://175.98.119.122/video/horisoz.mp4");
        videoPlayer1.setVideo(vidUri);


        videoPlayer2 = findViewById(R.id.videoPlayer2);
        videoPlayer2.showController(true);
        videoPlayer2.setVideo("android.resource://" + getPackageName() + "/" + R.raw.skidrow);

        // videoPlayer3 不顯示控制條
        videoPlayer3 = findViewById(R.id.videoPlayer3);
        videoPlayer3.setVideo("android.resource://" + getPackageName() + "/" + R.raw.dio);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoPlayer1.play();
    }

    @Override
    protected void onStop() {
        videoPlayer1.stop();
        super.onStop();
    }
}
