package org.iii.wheelpiedemo.course;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.course.util.HttpUtils;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import static org.iii.wheelpiedemo.course.util.ViewUtils.*;

public class CourseActivity extends NavigationActivity {


    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String URL_API_COURSE_DAY_VIEW = "https://dsicoach.win/api/plan/my-course/plan/day-view";
    private static String URL_API_DAY_TRAINING = "https://dsicoach.win/api/plan/my-training/dayTraining";
    private static String URL_API_GENERATE_PLAN_API = "https://dsicoach.win/coach/analysis/training_plan";
    private static String URL_API_USER_INFO = "https://dsicoach.win/api/user/info";
    //private static String warmUpVideoURI = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //private static String warmUpVideoURI = "https://dsicoach.win/video/warm_up_YouTube_720.mp4";
    private static String warmUpVideoURI = "https://dsicoach.win/video/warm_up_360_high_profile.mp4";
    private final String PREF_USER_TOKEN_KEY = "userToken";
    private final int MSG_API_RESPONSE_DAY_TRAINING = 0;
    private final int MSG_API_RESPONSE_DAY_VIEW = 1;
    private final int MSG_API_RESPONSE_GENERATE_TRAINING_PLAN = 2;
    private final int MSG_API_RESPONSE_GET_USER_INFO = 3;
    private final int MSG_CONTENT_VIEW_LOGIN = 4;
    private final int DELAY_PLAN_GENERATION_TO_COURSE_DESC = 3000;
    private LinearLayout contentLayout;
    private VideoPlayer videoPlayer = null;
    private Button btnTraining = null;
    private String userToken;
    private ProgressDialog dialog;
    private ProgressBar progressBar;
    private ImageView mProgressBar;
    private AnimationDrawable animationDrawable;
    private long videoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 啟動課程說明頁
        setContentView(R.layout.nav_course);

        // 初始共用menu
        initCommonNavigationView();

        // 取得UI
        initLoadingDialog();

        // 顯示等待訊息框
        displayLoadingDialog();

        if (isUserLoggedIn()) {
            // 設定開始訓練按鈕
            findViewById(R.id.button_start_training).setOnClickListener(btnTrainingStartOnClick);
            // 呼叫取得使用資訊API
            requestUserInfoAPI();
        } else {
            theHandler.sendEmptyMessage(MSG_CONTENT_VIEW_LOGIN);
        }
    }

    @Override
    public int getBottomNavigationViewId() {
        return R.id.course_nav;
    }

    private void initLoadingDialog() {
        // use progressbar
//        progressBar = findViewById(R.id.course_progressBar);
        // use animation progress
        mProgressBar = findViewById(R.id.course_image_progress);
        mProgressBar.setBackgroundResource(R.drawable.course_loading_running_jumbo);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
    }

    private void displayLoadingDialog() {
//        dialog = ProgressDialog.show(this, "",
//                "Loading. Please wait...", true);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (mProgressBar != null && animationDrawable != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            animationDrawable.start();
        }
    }

    private void stopLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
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

    private void requestUserInfoAPI() {
        restApiHeaderClient.setResponseListener(getUserInfoResponseListener);
        HttpUtils.getAuthAPI(
            restApiHeaderClient,
            URL_API_USER_INFO,
            Config.HTTP_DATA_TYPE.X_WWW_FORM,
            null,
            userToken
        );
    }

    private void requestGenerateTrainingPlanAPI(String userId) {
        restApiHeaderClient.setResponseListener(generateTrainingPlanResponseListener);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("body", getTrainingPlanGenerationJSONBody(userId));
        HttpUtils.postAuthAPI(
            restApiHeaderClient,
            URL_API_GENERATE_PLAN_API,
            Config.HTTP_DATA_TYPE.JSON,
            params,
            userToken
        );
    }

    private String getTrainingPlanGenerationJSONBody(String userId) {
        JSONObject params = new JSONObject();
        try {
            // 封裝數值為JSON物件
            params.put("userId", userId);
            params.put("exercise_type", "treadmill");
            params.put("target", "competition");
            params.put("distance", "10K");
            params.put("finish_week", "8");
            params.put("exercise_times", "5");
            params.put("training_start_date", getTodayDate());
            params.put("BR_dist", "unknown");
            params.put("BR_time", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params.toString();
    }

    private void requestTodayTrainingAPI(String dateString) {
        restApiHeaderClient.setResponseListener(todayTrainingResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("trainingDate", dateString);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Bearer %s", userToken));
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpsGet(URL_API_DAY_TRAINING, Config.HTTP_DATA_TYPE
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
        int nResponse_id = restApiHeaderClient.HttpsGet(URL_API_COURSE_DAY_VIEW, Config.HTTP_DATA_TYPE
                .X_WWW_FORM, param, response, headers);
        Logs.showTrace("[API] http response id: " + nResponse_id);
    }

    private RestApiHeaderClient.ResponseListener getUserInfoResponseListener =
        new RestApiHeaderClient.ResponseListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
                Message message = Message.obtain(
                        theHandler,
                        MSG_API_RESPONSE_GET_USER_INFO,
                        jsonObject
                );
                message.sendToTarget();
            }
        };

    private RestApiHeaderClient.ResponseListener generateTrainingPlanResponseListener =
        new RestApiHeaderClient.ResponseListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
                Message message = Message.obtain(
                    theHandler,
                    MSG_API_RESPONSE_GENERATE_TRAINING_PLAN,
                    jsonObject
                );
                theHandler.sendMessageDelayed(message, DELAY_PLAN_GENERATION_TO_COURSE_DESC);
            }
        };

    private RestApiHeaderClient.ResponseListener todayTrainingResponseListener = new RestApiHeaderClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = Message.obtain(
                theHandler,
                MSG_API_RESPONSE_DAY_TRAINING,
                jsonObject
            );
            message.sendToTarget();
        }
    };

    private String extractUserId (String jsonString) {
        String id = null;
        if (JSONUtils.isAPIResultSuccess(jsonString)) {
            try {
                JSONObject resp = new JSONObject(jsonString);
                JSONObject user = resp.getJSONObject("user");
                id = user.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

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
                MSG_API_RESPONSE_DAY_VIEW,
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
            //btnTraining.setEnabled(dayTraining.isTrainable());
            btnTraining.setEnabled(true);
            btnTraining.setVisibility(View.VISIBLE);
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
                    ViewUtils.showShortFloatingMessage(
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
            switch (msg.what) {
                case MSG_API_RESPONSE_GET_USER_INFO:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject) msg.obj);
                    String userId = extractUserId(strMsg);
                    if (userId != null) {
                        // 取得使用者id成功，呼叫打造課程API
                        requestGenerateTrainingPlanAPI(userId);
                    } else {
                        // 移除等待訊息框
                        stopLoadingDialog();
                        // 取得使用者id失敗
                        ViewUtils.showShortFloatingMessage(
                            getApplicationContext(),
                            "目前網路有問題，請稍後再試"
                        );
                    }
                    break;
                case MSG_API_RESPONSE_GENERATE_TRAINING_PLAN:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject)msg.obj);
                    //if (JSONUtils.isAPIResultSuccess(strMsg)) {
                        // 打造成功, 呼叫當日課程訓練API
                        //requestTodayTrainingAPI("2018-10-08");
                        requestTodayTrainingAPI(getTodayDate());
                    //} else {
                        // 打造失敗
//                        ViewUtils.showShortFloatingMessage(
//                            getApplicationContext(),
//                            "目前網路有問題，請稍後再試"
//                        );
//                    }
                    break;
                case MSG_API_RESPONSE_DAY_TRAINING:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject)msg.obj);
                    String trainingId = extractDayTrainingId(strMsg);
                    // 呼叫當日課程說明API
                    if (trainingId != null) {
                        requestCourseDayViewAPI(trainingId);
                    } else {
                        // 移除等待訊息框
                        stopLoadingDialog();
                        // 今天無訓練課程，請至CoachBot服務產生今日課程
                        ViewUtils.showShortFloatingMessage(
                            getApplicationContext(),
                            "今天無訓練課程，請至CoachBot服務產生今日訓練課程"
                        );
                    }
                    break;
                case MSG_API_RESPONSE_DAY_VIEW:
                    strMsg = JSONUtils.getResponseJSONString((JSONObject)msg.obj);
                    // 依當課程說明API，初始化畫面
                    DayTraining dt = DayTraining.parseResponse(strMsg);
                    initViewByAPIResponse(dt);
                    // 移除等待訊息框
                    stopLoadingDialog();
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
