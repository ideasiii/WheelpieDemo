package org.iii.wheelpiedemo.sample;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Formatter;
import java.util.Locale;

public class MediaController extends FrameLayout
{
    
    private VideoView mPlayer;
    private Context mContext;
    private View mAnchor;
    private View mRoot;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mUseFastForward;
    private boolean mListenersSet;
    private OnClickListener mNextListener, mPrevListener;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private ImageButton mPauseButton;
    private ImageButton mFfwdButton;
    private ImageButton mRewButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    
    public MediaController(Context context)
    {
        this(context, true);
    }
    
    public MediaController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mRoot = this;
        mContext = context;
        mUseFastForward = true;
    }
    
    public MediaController(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void onFinishInflate()
    {
        if (mRoot != null)
        {
            initControllerView(mRoot);
        }
    }
    
    public MediaController(Context context, boolean useFastForward)
    {
        super(context);
        mContext = context;
        mUseFastForward = useFastForward;
        init();
    }
    
    private void init()
    {
        hide();
    }
    
    // This is called whenever mAnchor's layout bound changes
    private OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener()
    {
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                int oldTop, int oldRight, int oldBottom)
        {
            
            if (mShowing)
            {
            
            }
        }
    };
    
    public void setMediaPlayer(VideoView player)
    {
        mPlayer = player;
        updatePausePlay();
    }
    
    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view)
    {
        if (mAnchor != null)
        {
            mAnchor.removeOnLayoutChangeListener(mLayoutChangeListener);
        }
        mAnchor = view;
        if (mAnchor != null)
        {
            mAnchor.addOnLayoutChangeListener(mLayoutChangeListener);
        }
        
        LayoutParams frameParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        
        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }
    
    private int getResourceId(String strName, String strDefType)
    {
        int ResId = mContext.getResources().getIdentifier(strName, strDefType, mContext
                .getPackageName());
        return ResId;
    }
    
    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView()
    {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(getResourceId("media_controller", "layout"), null);
        
        initControllerView(mRoot);
        
        return mRoot;
    }
    
    private void initControllerView(View v)
    {
        mPauseButton = (ImageButton) v.findViewById(getResourceId("pause", "id"));
        if (mPauseButton != null)
        {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }
        
        mFfwdButton = (ImageButton) v.findViewById(getResourceId("ffwd", "id"));
        if (mFfwdButton != null)
        {
            mFfwdButton.setOnClickListener(mFfwdListener);
            mFfwdButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
        }
        
        mRewButton = (ImageButton) v.findViewById(getResourceId("rew", "id"));
        if (mRewButton != null)
        {
            mRewButton.setOnClickListener(mRewListener);
            mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
        }
        
        // By default these are hidden. They will be enabled when setPrevNextListeners() is called
        mNextButton = (ImageButton) v.findViewById(getResourceId("next", "id"));
        if (mNextButton != null && !mListenersSet)
        {
            mNextButton.setVisibility(View.GONE);
        }
        mPrevButton = (ImageButton) v.findViewById(getResourceId("prev", "id"));
        if (mPrevButton != null && !mListenersSet)
        {
            mPrevButton.setVisibility(View.GONE);
        }
        
        mProgress = (ProgressBar) v.findViewById(getResourceId("mediacontroller_progress", "id"));
        if (mProgress != null)
        {
            if (mProgress instanceof SeekBar)
            {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }
        
        mEndTime = (TextView) v.findViewById(getResourceId("time", "id"));
        mCurrentTime = (TextView) v.findViewById(getResourceId("time_current", "id"));
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        
        installPrevNextListeners();
    }
    
    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show()
    {
        show(sDefaultTimeout);
    }
    
    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons()
    {
        try
        {
            if (mPauseButton != null && !mPlayer.canPause())
            {
                mPauseButton.setEnabled(false);
            }
            if (mRewButton != null && !mPlayer.canSeekBackward())
            {
                mRewButton.setEnabled(false);
            }
            if (mFfwdButton != null && !mPlayer.canSeekForward())
            {
                mFfwdButton.setEnabled(false);
            }
        }
        catch (IncompatibleClassChangeError ex)
        {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }
    
    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show
     *                the controller until hide() is called.
     */
    public void show(int timeout)
    {
        
        mShowing = true;
        setProgress();
        if (mPauseButton != null)
        {
            mPauseButton.requestFocus();
        }
        disableUnsupportedButtons();
        
        updatePausePlay();
        
        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        
        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0)
        {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
        
        this.setVisibility(View.VISIBLE);
    }
    
    public boolean isShowing()
    {
        return mShowing;
    }
    
    /**
     * Remove the controller from the screen.
     */
    public void hide()
    {
        mShowing = false;
        this.setVisibility(View.GONE);
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            int pos;
            switch (msg.what)
            {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (MediaController.this.getVerticalFadingEdgeLength() == View.VISIBLE &&
                            mPlayer.isPlaying())
                    {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };
    
    private String stringForTime(int timeMs)
    {
        int totalSeconds = timeMs / 1000;
        
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        
        mFormatBuilder.setLength(0);
        if (hours > 0)
        {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }
        else
        {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    
    private int setProgress()
    {
        if (mPlayer == null || mDragging)
        {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null)
        {
            if (duration > 0)
            {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }
    
        if (mEndTime != null)
        {
            mEndTime.setText(stringForTime(duration));
        }
        if (mCurrentTime != null)
        {
            mCurrentTime.setText(stringForTime(position));
        }
        
        return position;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        show(sDefaultTimeout);
        return true;
    }
    
    @Override
    public boolean onTrackballEvent(MotionEvent ev)
    {
        show(sDefaultTimeout);
        return false;
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent
                .ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent
                .KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)
        {
            if (uniqueDown)
            {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null)
                {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY)
        {
            if (uniqueDown && !mPlayer.isPlaying())
            {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE)
        {
            if (uniqueDown && mPlayer.isPlaying())
            {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_CAMERA)
        {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)
        {
            if (uniqueDown)
            {
                hide();
            }
            return true;
        }
        
        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }
    
    private OnClickListener mPauseListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };
    
    private void updatePausePlay()
    {
        if (mPlayer.isPlaying())
        {
            //mPauseButton.getContext().setTheme(android.R.style.MediaButton_Pause);
            mPauseButton.setImageResource(getResourceId("ic_media_pause", "drawable"));
        }
        else
        {
            //mPauseButton.getContext().setTheme(android.R.style.MediaButton_Play);
            mPauseButton.setImageResource(getResourceId("ic_media_play", "drawable"));
        }
    }
    
    private void doPauseResume()
    {
        if (mPlayer.isPlaying())
        {
            mPlayer.pause();
        }
        else
        {
            mPlayer.start();
        }
        updatePausePlay();
    }
    
    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener()
    {
        public void onStartTrackingTouch(SeekBar bar)
        {
            show(3600000);
            
            mDragging = true;
            
            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }
        
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser)
        {
            if (!fromuser)
            {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }
            
            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null)
            {
                mCurrentTime.setText(stringForTime((int) newposition));
            }
        }
        
        public void onStopTrackingTouch(SeekBar bar)
        {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);
            
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
    
    @Override
    public void setEnabled(boolean enabled)
    {
        if (mPauseButton != null)
        {
            mPauseButton.setEnabled(enabled);
        }
        if (mFfwdButton != null)
        {
            mFfwdButton.setEnabled(enabled);
        }
        if (mRewButton != null)
        {
            mRewButton.setEnabled(enabled);
        }
        if (mNextButton != null)
        {
            mNextButton.setEnabled(enabled && mNextListener != null);
        }
        if (mPrevButton != null)
        {
            mPrevButton.setEnabled(enabled && mPrevListener != null);
        }
        if (mProgress != null)
        {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }
    
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event)
    {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MediaController.class.getName());
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info)
    {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MediaController.class.getName());
    }
    
    private OnClickListener mRewListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            int pos = mPlayer.getCurrentPosition();
            pos -= 5000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            
            show(sDefaultTimeout);
        }
    };
    
    private OnClickListener mFfwdListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            int pos = mPlayer.getCurrentPosition();
            pos += 15000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            
            show(sDefaultTimeout);
        }
    };
    
    private void installPrevNextListeners()
    {
        if (mNextButton != null)
        {
            mNextButton.setOnClickListener(mNextListener);
            mNextButton.setEnabled(mNextListener != null);
        }
        
        if (mPrevButton != null)
        {
            mPrevButton.setOnClickListener(mPrevListener);
            mPrevButton.setEnabled(mPrevListener != null);
        }
    }
    
    public void setPrevNextListeners(OnClickListener next, OnClickListener prev)
    {
        mNextListener = next;
        mPrevListener = prev;
        mListenersSet = true;
        
        if (mRoot != null)
        {
            installPrevNextListeners();
            
            if (mNextButton != null)
            {
                mNextButton.setVisibility(View.VISIBLE);
            }
            if (mPrevButton != null)
            {
                mPrevButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
