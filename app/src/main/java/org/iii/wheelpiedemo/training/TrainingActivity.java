package org.iii.wheelpiedemo.training;

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
import org.json.JSONObject;

/**
 * Created by YCTsai on 2018/10/8
 */

@SuppressLint("Registered")
public class TrainingActivity extends AppCompatActivity
{
    private TextView timer;
    private boolean startflag = false;
    private int tsec = 0, csec = 0, cmin = 0, chr = 0;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String trainingAPIURL = "https://dsicoach.win/api/plan/my-training/dayTraining";
    private final int MSG_DAY_TRAINING_API_RESPONSE = 0;
    
    //TimerTask無法直接改變元件因此要透過Handler來當橋樑
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
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

//                    chr = cmin / 60;
                    
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
                    Log.d("yayaya", "333333");
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
        Logs.showTrace("[API] http response id: " + nResponse_id);
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_main);
        
        //畫面切換
        LayoutInflater inflater = getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.training_main, null);//找出第一個視窗
        final View view2 = inflater.inflate(R.layout.training_device_connection, null);//找出第二個視窗
        setContentView(view1); //顯示目前第一個視窗
        final ImageView startbutton = (ImageView) view1.findViewById(R.id.startbutton);//找出第一個視窗中的按鈕
        TextView backbutton = (TextView) view2.findViewById(R.id.textView14);//找出第二個視窗中的按鈕
        
        timer = (TextView) view1.findViewById(R.id.timer);
        
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
        
        
    }
    
}
