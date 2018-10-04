package org.iii.wheelpiedemo.sample;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import org.iii.wheelpiedemo.common.Logs;

public class VideoPlayer extends RelativeLayout
{
    private VideoView videoView = null;
    private boolean mbIsLoop = false;
    private boolean mbShowController = false;
    private MediaController videoController = null;
    private GestureDetector gestureDetector = null;
    
    public VideoPlayer(Context context)
    {
        super(context);
        init(context);
    }
    
    public VideoPlayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public VideoPlayer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        if (videoView.isPlaying())
        {
            videoView.stopPlayback();
        }
        videoView = null;
        videoController = null;
        super.finalize();
    }
    
    private void init(Context context)
    {
        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
        this.setBackgroundResource(android.R.color.background_dark);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
				.MATCH_PARENT));
        this.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        
        videoView = new VideoView(context);
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnTouchListener(videoTouchListener);
        
        LayoutParams videoLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
				.MATCH_PARENT);
        videoLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(videoLayoutParams);
        
        this.addView(videoView);
        
        LayoutParams controlerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
        controlerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        videoController = new MediaController(context);
        videoController.setLayoutParams(controlerLayoutParams);
        videoController.setAnchorView(this);
        videoController.setMediaPlayer(videoView);
        this.addView(videoController);
    }
    
    public void setVideo(String strVideoPath)
    {
        videoView.setVideoPath(strVideoPath);
    }
    
    public void setVideo(Uri uri)
    {
        videoView.setVideoURI(uri);
    }
    
    public void play()
    {
        if (!videoView.isPlaying())
        {
            videoView.start();
            Logs.showTrace("Local video play");
        }
    }
    
    public void play(String strVideoPath)
    {
        setVideo(strVideoPath);
        videoView.start();
    }
    
    public void pause()
    {
        if (videoView.isPlaying())
        {
            videoView.pause();
        }
    }
    
    public void stop()
    {
        if (videoView.isPlaying())
        {
            videoView.stopPlayback();
            Logs.showTrace("Local video stop");
        }
    }
    
    public void seekTo(int nMsec)
    {
        videoView.seekTo(nMsec);
    }
    
    public void setDisplay(int nX, int nY, int nWidth, int nHeight)
    {
        this.setX(nX);
        this.setY(nY);
        this.setLayoutParams(new LayoutParams(nWidth, nHeight));
    }
    
    public void setLoop(boolean bLoop)
    {
        mbIsLoop = bLoop;
    }
    
    public void showController(boolean bShow)
    {
        mbShowController = bShow;
    }
    
    OnPreparedListener onPreparedListener = new OnPreparedListener()
    {
        @Override
        public void onPrepared(MediaPlayer mp)
        {
            mp.setLooping(mbIsLoop);
        }
    };
    
    OnTouchListener videoTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if (mbShowController)
                    {
                        if (videoController.isShown())
                        {
                            videoController.hide();
                        }
                        else
                        {
                            videoController.show();
                        }
                    }
                    else
                    {
                        if (!videoView.isPlaying())
                        {
                            videoView.start();
                        }
                    }
                    break;
            }
            gestureDetector.onTouchEvent(event);
            return true;
        }
    };
    
    SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener()
    {
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            return super.onDoubleTap(e);
        }
    };
}
