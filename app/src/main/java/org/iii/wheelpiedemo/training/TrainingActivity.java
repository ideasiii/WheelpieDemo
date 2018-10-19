package org.iii.wheelpiedemo.training;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsi.ant.antplus.pluginsampler.heartrate.Activity_HeartRateDisplayBase;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.MainActivity;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.login.LoginActivity;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by YCTsai on 2018/10/8
 */

@SuppressLint("Registered")
public class TrainingActivity extends Activity_HeartRateDisplayBase
{
    private TextView timer;
    private TextView TrainingType;
    private TextView TrainingMode;
    public TextView tv_status;
    public TextView tv_estTimestamp;
    public TextView tv_rssi;
    public TextView tv_computedHeartRate;
    public TextView tv_heartBeatCounter;
    public TextView tv_heartBeatEventTime;
    public TextView tv_manufacturerSpecificByte;
    public TextView tv_previousHeartBeatEventTime;
    public TextView tv_calculatedRrInterval;
    public TextView tv_cumulativeOperatingTime;
    public TextView tv_manufacturerID;
    public TextView tv_serialNumber;
    public TextView tv_hardwareVersion;
    public TextView tv_softwareVersion;
    public TextView tv_modelNumber;
    public TextView tv_dataStatus;
    public TextView tv_rrFlag;
    private boolean startflag = false;
    private int tsec = 0, csec = 0, cmin = 0, chr = 0;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String trainingAPIURL = "https://dsicoach.win/api/plan/my-training/dayTraining";
    private static String courseAPIURL = "https://dsicoach.win/api/plan/my-course/plan/day-view";
    private final int MSG_DAY_TRAINING_API_RESPONSE = 0;
    private final int MSG_DAY_VIEW_API_RESPONSE = 1;
    
    private String getResponseJSONString(JSONObject clientResp)
    {
        String jsonString = null;
        if (clientResp != null)
        {
            try
            {
                jsonString = ((JSONObject) clientResp).getString("data");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonString;
    }
    
    private String extractDayTrainingId(String jsonString)
    {
        String id = null;
        if (jsonString == null || jsonString.length() == 0)
        {
            return null;
        }
        
        try
        {
            JSONObject resp = new JSONObject(jsonString);
            JSONObject dayPlan = resp.getJSONObject("dayPlan");
            JSONObject dayTraining = dayPlan.getJSONObject("dayTraining");
            id = String.valueOf(dayTraining.getInt("id"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return id;
    }
    
    private RestApiHeaderClient.ResponseListener dayViewResponseListener = new RestApiHeaderClient
            .ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
//            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = new Message();
            message.what = MSG_DAY_VIEW_API_RESPONSE;
            message.obj = jsonObject;
            handler.sendMessage(message);
        }
    };
    
    private void requestCourseDayViewAPI(String dayTrainingId)
    {
        restApiHeaderClient.setResponseListener(dayViewResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", dayTrainingId);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " +
                "2h39l3nV4iiYucuXax7Mw6PEQMh4cjkFX7AeW3yVcaiLyIhAHRdAPLixkgS5Mvpv0FcWJMnXUyO9ssEkeb60VyBWm4yEVoPZ1jXIAcnO3ZM9qIgcRXiTKdEYkOTcZWFryyo2hFTgQwMVpprXDpGyBlHJUru8g9QOeOYNYET9jsRUz0IX6e6bPuw3K3FNsBfHmUbukwYgEnDBLP6VYOAul9njlS4DKVda3yD6WGFXcjkbKeRtPb8dY98dJkpXsWUg");
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpsGet(courseAPIURL, Config.HTTP_DATA_TYPE.X_WWW_FORM,
                param, response, headers);
//        Logs.showTrace("[API] http response id: " + nResponse_id);
    }
    
    //TimerTask無法直接改變元件因此要透過Handler來當橋樑
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String strMsg = null;
            switch (msg.what)
            {
                case 1:
                    csec = tsec % 60;
                    cmin = tsec / 60;
                    
                    if (cmin >= 60)
                    {
                        cmin = cmin % 60;
                    }
                    
                    chr = tsec / 3600;
                    if (chr >= 24)
                    {
                        chr = chr % 24;
                    }
                    
                    String s = "";
                    //定義進位時的顯示情況
                    if (chr < 10)
                    {
                        s = "0" + chr;
                    }
                    else
                    {
                        s = "" + chr;
                    }
                    
                    if (cmin < 10)
                    {
                        s = s + ":0" + cmin;
                    }
                    else
                    {
                        s = s + ":" + cmin;
                    }
                    
                    if (csec < 10)
                    {
                        s = s + ":0" + csec;
                    }
                    else
                    {
                        s = s + ":" + csec;
                    }
                    
                    //s字串為00:00:00格式
                    timer.setText(s);
                    break;
                
                case 3:
                    break;
                
                case MSG_DAY_TRAINING_API_RESPONSE:
                    strMsg = getResponseJSONString((JSONObject) msg.obj);
                    String trainingId = extractDayTrainingId(strMsg);
                    
                    try
                    {
                        JSONObject dayPlanJsonObj = new JSONObject(strMsg);
                        JSONObject dayPlan = new JSONObject(dayPlanJsonObj.getString("dayPlan"));
                        String excerciseType = dayPlan.getString("excerciseType");
                        String excerciseMode = dayPlan.getString("excerciseMode");
//                        Logs.showTrace("show me data" + excerciseType);
//                        Logs.showTrace("show me data" + excerciseMode);
                        TrainingMode.setText(excerciseMode);
                        TrainingType.setText(excerciseType);
                    }
                    catch (JSONException e)
                    {
                        Logs.showTrace("can not find the json object");
                        e.printStackTrace();
                    }
                    
                    // 呼叫當日課程說明API
                    requestCourseDayViewAPI(trainingId);
                    
                    break;
            }
        }
    };
    
    private TimerTask task = new TimerTask()
    {
        
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            
            //一開始的時候message先丟3
//            Message IniMessage = new Message();
//            IniMessage.what = 3;
//            handler.sendMessage(IniMessage);
            
            if (startflag)
            {
                //如果startflag是true則每秒tsec+1
                
                tsec++;
                Message message = new Message();
                
                //傳送訊息1
                message.what = 1;
                handler.sendMessage(message);
            }
            ;
            if (!startflag)
            {
                //如果startflag是false則重製秒數
                // (注意:畫面一開始停留的頁面即是flag==false的時候,所以這時候local value會被重製一次)
                tsec = 0;
                csec = 0;
                cmin = 0;
                chr = 0;
                Message message = new Message();
                
                //傳送訊息1
                message.what = 1;
                handler.sendMessage(message);
            }
        }
        
    };
    
    private void requestTodayTrainingAPI(String dateString)
    {
        restApiHeaderClient.setResponseListener(todayTrainingResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("trainingDate", dateString);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " +
                "2h39l3nV4iiYucuXax7Mw6PEQMh4cjkFX7AeW3yVcaiLyIhAHRdAPLixkgS5Mvpv0FcWJMnXUyO9ssEkeb60VyBWm4yEVoPZ1jXIAcnO3ZM9qIgcRXiTKdEYkOTcZWFryyo2hFTgQwMVpprXDpGyBlHJUru8g9QOeOYNYET9jsRUz0IX6e6bPuw3K3FNsBfHmUbukwYgEnDBLP6VYOAul9njlS4DKVda3yD6WGFXcjkbKeRtPb8dY98dJkpXsWUg");
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpsGet(trainingAPIURL, Config.HTTP_DATA_TYPE.X_WWW_FORM,
                param, response, headers);
//        Logs.showTrace("[API] http response id: " + nResponse_id);
    }
    
    private RestApiHeaderClient.ResponseListener todayTrainingResponseListener = new RestApiHeaderClient
            .ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = new Message();
            message.what = MSG_DAY_TRAINING_API_RESPONSE;
            message.obj = jsonObject;
            handler.sendMessage(message);
        }
    };
    
    @Override
    protected void requestAccessToPcc()
    {
    
    }

    @Override
    protected void showDataDisplay(String status)
    {
        setContentView(R.layout.training_main);
        
        tv_status = (TextView) findViewById(R.id.textView_Status);
        tv_estTimestamp = (TextView) findViewById(R.id.textView_EstTimestamp);
        tv_rssi = (TextView) findViewById(R.id.textView_Rssi);
        tv_computedHeartRate = (TextView) findViewById(R.id.textView_ComputedHeartRate);
        tv_heartBeatCounter = (TextView) findViewById(R.id.textView_HeartBeatCounter);
        tv_heartBeatEventTime = (TextView) findViewById(R.id.textView_HeartBeatEventTime);
        tv_manufacturerSpecificByte = (TextView) findViewById(R.id.textView_ManufacturerSpecificByte);
        tv_previousHeartBeatEventTime = (TextView) findViewById(R.id.textView_PreviousHeartBeatEventTime);
        tv_calculatedRrInterval = (TextView) findViewById(R.id.textView_CalculatedRrInterval);
        tv_cumulativeOperatingTime = (TextView) findViewById(R.id.textView_CumulativeOperatingTime);
        tv_manufacturerID = (TextView) findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView) findViewById(R.id.textView_SerialNumber);
        tv_hardwareVersion = (TextView) findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView) findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView) findViewById(R.id.textView_ModelNumber);
        tv_dataStatus = (TextView) findViewById(R.id.textView_DataStatus);
        tv_rrFlag = (TextView) findViewById(R.id.textView_rRFlag);
    
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.training_main);
        
        //畫面切換
        LayoutInflater inflater = getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.training_main, null);//找出第一個視窗
        final View view2 = inflater.inflate(R.layout.training_device_connection, null);//找出第二個視窗
        setContentView(view1); //顯示目前第一個視窗
        final ImageView startbutton = (ImageView) view1.findViewById(R.id.startbutton);//找出第一個視窗中的按鈕
        TextView backbutton = (TextView) view2.findViewById(R.id.textView14);//找出第二個視窗中的按鈕
        
        TrainingMode = (TextView) view1.findViewById(R.id.exercise_mode_content);//找出第一個視窗中訓練類型的字串框格
        TrainingType = (TextView) view1.findViewById(R.id.exercise_type_content);//找出第一個視窗中訓練模式的字串框格
        timer = (TextView) view1.findViewById(R.id.timer_content);
        
        //宣告Timer
        Timer timer01 = new Timer();
        
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        timer01.schedule(task, 0, 1000);
        
        //Button監聽,第一種寫法,在上面先定義listener,function帶入即可
//        startbutton.setOnClickListener(listener);
        //Button監聽,第二種寫法,(第一個畫面在做的事情)
        startbutton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                switch (v.getId())
                {
                    case R.id.startbutton:
//                        setContentView(view2);//按了之後跳到第2個視窗
                        Intent intent = null;
                        intent = new Intent(TrainingActivity.this, com.dsi.ant.antplus.pluginsampler
                                .heartrate.Activity_SearchUiHeartRateSampler.class);
                        startActivity(intent);
                        
                        if (startflag)
                        {
                            startflag = false;
                            startbutton.setImageResource(R.drawable.training_startbutton);
                            
                        }
                        else
                        {
                            startflag = true;
                            startbutton.setImageResource(R.drawable.training_stopbutton);
                        }
                        break;
                    
                }
            }
        });
        
        backbutton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view1);//按了之後跳到第1個視窗
            }
        });

//        TrainingType.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//
//            }
//        });
        
        requestTodayTrainingAPI("2018-10-08");
        
    }
    
}
