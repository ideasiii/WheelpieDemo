package org.iii.wheelpiedemo.course;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.menu.MenuItemObject;
import org.iii.wheelpiedemo.menu.NavigationActivity;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.iii.wheelpiedemo.course.response.ClassContent;
import org.iii.wheelpiedemo.course.response.CourseChart;
import org.iii.wheelpiedemo.course.response.DayTraining;
import org.iii.wheelpiedemo.course.response.TrainingContent;
import org.iii.wheelpiedemo.course.util.AChartEngineUtils;
import org.iii.wheelpiedemo.course.util.JSONUtils;
import org.iii.wheelpiedemo.course.util.ViewUtils;
import org.iii.wheelpiedemo.login.LoginActivity;
import org.iii.wheelpiedemo.sample.VideoPlayer;
import org.iii.wheelpiedemo.training.TrainingActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import static org.iii.wheelpiedemo.course.util.ViewUtils.*;

public class CourseActivity extends NavigationActivity {


    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String courseAPIURL = "https://dsicoach.win/api/plan/my-course/plan/day-view";
    private static String trainingAPIURL = "https://dsicoach.win/api/plan/my-training/dayTraining";
    //private static String warmUpVideoURI = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //private static String warmUpVideoURI = "https://dsicoach.win/video/warm_up_YouTube_720.mp4";
    private static String warmUpVideoURI = "https://dsicoach.win/video/warm_up_360_high_profile.mp4";
    private final String PREF_USER_TOKEN_KEY = "userToken";
    private final int MSG_DAY_TRAINING_API_RESPONSE = 0;
    private final int MSG_DAY_VIEW_API_RESPONSE = 1;
    private final int MSG_CONTENT_VIEW_LOGIN = 2;
    private LinearLayout contentLayout;
    private VideoPlayer videoPlayer = null;
    private Button btnTraining = null;
    private String userToken;
    private ProgressDialog dialog;
    private long videoDuration;
    private String apiResponse = "{\"planDayView\":{\"id\":355,\"dayTraining\":{\"id\":32261,\"done\":false,\"action\":\"E30(HRR60%)+2ST\",\"description\":\"E心率區間60%跑30分鐘\\n快步跑2組\",\"contents\":[{\"title\":\"靜態熱身\",\"steps\":[\"上半身熱身操5分鐘\",\"下半身熱身操5分鐘\"]},{\"title\":\"第1階段訓練\",\"steps\":[\"輕鬆跑(維持儲備心律60%)30分鐘\"],\"hrrChartInfo\":{\"chart\":{\"type\":\"block\"},\"title\":{},\"subtitle\":{},\"xAxis\":{\"text\":\"時間(分鐘)\",\"tickInterval\":10},\"yAxis\":{\"text\":\"HRR心率(%)\",\"tickInterval\":20},\"series\":{\"data\":[{\"xStart\":0,\"xEnd\":30,\"yStart\":59,\"yEnd\":74,\"color\":\"#55FFFF\"}]}}},{\"title\":\"第2階段訓練\",\"steps\":[\"快步跑(維持步頻180)10秒鐘\",\"靜/動態休息45秒鐘\",\"快步跑(維持步頻180)10秒鐘\"],\"strideChartInfo\":{\"chart\":{},\"title\":{\"text\":\"第2階段訓練\"},\"subtitle\":{\"text\":\"\"},\"xAxis\":{\"text\":\"時間(sec)\"},\"yAxis\":{\"text\":\"步頻(spm)\"},\"series\":{\"data\":[{\"xStart\":0,\"xEnd\":10,\"yStart\":160,\"yEnd\":200},{\"xStart\":10,\"xEnd\":55,\"yStart\":0,\"yEnd\":160},{\"xStart\":55,\"xEnd\":65,\"yStart\":160,\"yEnd\":200}]}}},{\"title\":\"靜態收操\",\"steps\":[\"上半身收操5分鐘\",\"下半身收操5分鐘\"]}],\"trainable\":true,\"dayInfo\":\"輕鬆跑(DAY1)\",\"classInfo\":{\"code\":\"EZ00001\",\"contents\":[{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"跑前的暖身這是為了提高體溫、提升心跳、並增加關節與肌肉的活動範圍，如此能降低受傷的風險，且跑起步來也會更舒適流暢。暖身首部曲應該從腳踝、膝蓋、髖部、腰、肩、手腕、頸部依序進行，接著以快走或小跑步的方式讓身體熱起來，感覺到身體微微出汗即可\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=Ts5A0-n5-J8\",\"image\":\"\",\"description\":\"關節操\"},{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"輕鬆跑訓練是有氧耐力訓練的有效方法之一，體感是可以聊天且舒服的速度，如果你覺得喘不過氣來，就代表你的心率太高的，此時請放慢你的速度。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=7LCgUMsod1Q&feature=youtu.be\",\"image\":\"\",\"description\":\"Eazy Running HRR60%\"},{\"text\":\"快步跑是指在不耗盡體力的情況下提升你的整體速度並消除E配速與LSD的副作用。快步跑的重點是要加快你的「步頻」，再來才是腳掌上拉的「幅度」，如果前兩項都能做到再來才要求「速度」。也就是說速度並非快步跑的重點，步頻才是，跑此項目時，不要跨大步，不要管速度，甚至原地跑也可以。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=t0ufhZ8AULI&feature=youtu.be\",\"image\":\"\",\"description\":\"ST\"},{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"將主運動強度減緩，讓代謝恢復正常，同時排除運動時產生的代謝廢物，冷卻與再伸展合稱為「收操」。在這個逐漸放慢節奏的過程中，運動所產生的代謝廢物也會逐漸消散，原本沉重的雙腿會漸感輕鬆，不若剛練習完的當下那麼緊繃。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=dxDeO-5KT9o&feature=youtu.be\",\"image\":\"\",\"description\":\"收操\"}]}}},\"result\":true}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 啟動課程說明頁
        setContentView(R.layout.nav_course);

        // 初始共用menu
        initCommonNavigationView();

        // 顯示等待訊息框
        displayLoadingDialog();

        if (isUserLoggedIn()) {
            // 設定開始訓練按鈕
            findViewById(R.id.button_start_training).setOnClickListener(btnTrainingStartOnClick);
            // 呼叫當日課程訓練API
            //requestTodayTrainingAPI("2018-10-08");
            requestTodayTrainingAPI(getTodayDate());
        } else {
            theHandler.sendEmptyMessage(MSG_CONTENT_VIEW_LOGIN);
        }
    }

    @Override
    public int getBottomNavigationViewId() {
        return R.id.course_nav;
    }

    private void displayLoadingDialog() {
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
    }

    private boolean isUserLoggedIn() {
        boolean isLoggedIn = false;

        SharedPreferences sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userToken = sharedPref.getString(PREF_USER_TOKEN_KEY, null);

        if (userToken != null) {
            isLoggedIn = true;
        }

        return isLoggedIn;
    }

    private View.OnClickListener btnTrainingStartOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            toView(MenuItemObject.TRAINING);
        }
    };

    private void requestTodayTrainingAPI(String dateString) {
        restApiHeaderClient.setResponseListener(todayTrainingResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("trainingDate", dateString);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Bearer %s", userToken));
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpsGet(trainingAPIURL, Config.HTTP_DATA_TYPE
                .X_WWW_FORM, param, response, headers);
        Logs.showTrace("[API] http response id: " + nResponse_id);
    }

    private void requestCourseDayViewAPI(String dayTrainingId) {
        restApiHeaderClient.setResponseListener(dayViewResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", dayTrainingId);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Bearer %s", userToken));
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpsGet(courseAPIURL, Config.HTTP_DATA_TYPE
                .X_WWW_FORM, param, response, headers);
        Logs.showTrace("[API] http response id: " + nResponse_id);
    }

    private RestApiHeaderClient.ResponseListener todayTrainingResponseListener = new RestApiHeaderClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = Message.obtain(
                theHandler,
                MSG_DAY_TRAINING_API_RESPONSE,
                jsonObject
            );
            message.sendToTarget();
        }
    };

    private String extractDayTrainingId(String jsonString) {
        String id = null;
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }

        try {
            JSONObject resp = new JSONObject(jsonString);
            JSONObject dayPlan = resp.getJSONObject("dayPlan");
            JSONObject dayTraining = dayPlan.getJSONObject("dayTraining");
            id = String.valueOf(dayTraining.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private RestApiHeaderClient.ResponseListener dayViewResponseListener = new RestApiHeaderClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = Message.obtain(
                theHandler,
                MSG_DAY_VIEW_API_RESPONSE,
                jsonObject
            );
            message.sendToTarget();
        }
    };

    private void initViewByAPIResponse(DayTraining dayTraining) {
        contentLayout = findViewById(R.id.content_layout);
        if (dayTraining != null) {
            //更新訓練類型及第幾天
            updateActionBar(dayTraining.getDayInfo());
            //取出相關課程說明的第一個url
            ArrayList<ClassContent> classContents = dayTraining.getClassInfo().getContents();
            for(ClassContent classContent : classContents) {
                if (classContent.isURLType()) {
                    //warmUpVideoURI = classContent.getUrl();
                    warmUpVideoURI = "https://dsicoach.win/video/warm_up_360_high_profile.mp4";
                    break;
                }
            }
            // 取出訓練課程內容
            for (TrainingContent content : dayTraining.getContents()) {
                String titleText = content.getTitle();
                TextView title = createTitle(titleText);
                contentLayout.addView(title);
                // 取出steps
                for (String step : content.getSteps()) {
                    TextView textviewStep = createStep(step);
                    contentLayout.addView(textviewStep);
                }
                // Insert熱身影片
                if (titleText instanceof String && titleText.endsWith("熱身")) {
//                    RelativeLayout videoRL = new RelativeLayout(this);
//                    setViewLayout(contentLayout, videoRL,
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            200);
//                    VideoView v = createVideo(
//                            "v_warm_up",
//                            "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
//                    );
//                    setViewLayout(videoRL, v,
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                    );
                    videoPlayer = new VideoPlayer(this);
                    videoPlayer.showController(true);
                    videoPlayer.setVideo(Uri.parse(warmUpVideoURI));
                    setViewLayout(contentLayout, videoPlayer, ViewGroup.LayoutParams.MATCH_PARENT, 200);
                    setMargins(videoPlayer, 0, 10, 0, 10);
                }
                // 取出圖表
                CourseChart chart = content.getHrrChartInfo() != null ?
                    content.getHrrChartInfo() :
                    content.getStrideChartInfo() != null ?
                        content.getStrideChartInfo() : null;

                // 處理圖表呈現
                if (chart != null) {
                    AChartEngineUtils.drawChart(this, contentLayout, chart);
                }
            }
            //是否可以訓練
            btnTraining = findViewById(R.id.button_start_training);
            btnTraining.setEnabled(dayTraining.isTrainable());
        }
    }

    private TextView createTitle(String text) {
        //return this.createTitle(text, "@style/title");
        return this.createTextView(text, "courseTitle");
    }
    private TextView createStep(String text) {

        //return this.createTitle(text, "@style/step");
        return this.createTextView(text, "courseStep");
    }

    private TextView createTextView(String text, String style) {
        int resId = getResources().getIdentifier(style, "style", getPackageName());
        TextView title = new TextView(this, null, 0, resId);
        title.setText(text);
        return title;
    }

    private VideoView createVideo(String id, String videoURI) {
        final VideoView vid = new VideoView(this);

        MediaController m = new MediaController(this);
        vid.setMediaController(m);

        try {
            final Uri u = Uri.parse(videoURI);
            vid.setVideoURI(u);
            vid.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Your code goes here
                    Log.d("Dbg", "OnErrorListener: onError: " + what + ", " + extra);
                    return true;
                }
            });
            vid.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    VideoView vid = (VideoView)v;

                    if (!vid.isPlaying()) {
                        vid.requestFocus();
                        vid.start();
                    } else {
                        vid.pause();
                    }
                    return false;
                }
            });

            vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //vid.stopPlayback(); //不可呼叫，不然會無法再法再度播放
                    //vid.seekTo(0);
                    //vid.setVideoURI(u); //爛方法，不設定的話，touch event的restart不會work
                    ViewUtils.showFloatingMessage(
                        getApplicationContext(),
                        "影片播放完畢..."
                    );
                }
            });

            vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoDuration = vid.getDuration();
                }
            });
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return vid;
    }

    private void updateActionBar(String title) {
        ActionBar ab = getSupportActionBar();
        if (ab != null && title != null) {
            ab.setTitle(title);
        }
    }

    private int getStyleResourceId(String name) {
        return getResourceId(name, "style");
    }

    private int getResourceId (String name, String defType) {
        return getResources().getIdentifier(name, defType, getPackageName());
    }

    private String getTodayDate() {
        return getTodayDate("UTC");
    }

    private String getTodayDate(String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(java.util.Calendar.getInstance().getTime());
    }

    @SuppressLint("HandlerLeak")
    private Handler theHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String strMsg = null;
            switch (msg.what)
            {
                case MSG_DAY_TRAINING_API_RESPONSE:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject)msg.obj);
                    String trainingId = extractDayTrainingId(strMsg);
                    // 呼叫當日課程說明API
                    if (trainingId != null) {
                        requestCourseDayViewAPI(trainingId);
                    } else {
                        // 移除等待訊息框
                        dialog.dismiss();
                        // 今天無訓練課程，請至CoachBot服務產生今日課程
                        ViewUtils.showFloatingMessage(
                            getApplicationContext(),
                            "今天無訓練課程，請至CoachBot服務產生今日訓練課程"
                        );
                    }
                    break;
                case MSG_DAY_VIEW_API_RESPONSE:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject)msg.obj);
                    // 依當課程說明API，初始化畫面
                    DayTraining dt = DayTraining.parseResponse(strMsg);
                    initViewByAPIResponse(dt);
                    // 移除等待訊息框
                    dialog.dismiss();
                    // 播放影片
                    if (videoPlayer != null) {
                        videoPlayer.play();
                    }
                    break;
                case MSG_CONTENT_VIEW_LOGIN:
                    Intent it = new Intent(CourseActivity.this, LoginActivity.class);
                    startActivity(it);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUserLoggedIn()) {
            theHandler.sendEmptyMessage(MSG_CONTENT_VIEW_LOGIN);
        };
    }

    @Override
    protected void onStop() {
        if (videoPlayer != null) {
            videoPlayer.stop();
        }
        super.onStop();
    }
}
