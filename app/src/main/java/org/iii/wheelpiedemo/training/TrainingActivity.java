/**
 * Created by YCTsai on 2018/10/8
 */
package org.iii.wheelpiedemo.training;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.datatransfer.WheelPiesClient;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.chat.SpeechActivity;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.iii.wheelpiedemo.sample.LineChart;
import org.json.JSONException;
import org.json.JSONObject;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import org.iii.wheelpiedemo.training.ObservableHeartRate;
import org.iii.wheelpiedemo.training.ObserverHeartRateChanged;

@SuppressLint("Registered")
public class TrainingActivity extends Activity
{

    /**
     * Layout
     */
    private TextView timer;
    private TextView TrainingType;
    private TextView TrainingMode;
    private boolean startflag = false;
    private int tsec = 0, csec = 0, cmin = 0, chr = 0;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String trainingAPIURL = "https://dsicoach.win/api/plan/my-training/dayTraining";
    private static String courseAPIURL = "https://dsicoach.win/api/plan/my-course/plan/day-view";
    private final int MSG_DAY_TRAINING_API_RESPONSE = 0;
    private final int MSG_DAY_VIEW_API_RESPONSE = 1;
    private TextView tv_status;
    private TextView tv_estTimestamp;
    private TextView tv_rssi;
    private TextView textView_ComputedHeartRate;
    private TextView tv_heartBeatCounter;
    private TextView tv_heartBeatEventTime;
    private TextView tv_manufacturerSpecificByte;
    private TextView tv_previousHeartBeatEventTime;
    private TextView tv_calculatedRrInterval;
    private TextView tv_cumulativeOperatingTime;
    private TextView tv_manufacturerID;
    private TextView tv_serialNumber;
    private TextView tv_hardwareVersion;
    private TextView tv_softwareVersion;
    private TextView tv_modelNumber;
    private TextView tv_dataStatus;
    private TextView tv_rrFlag;
    static int nXData = 0;
    Boolean bRun = false;
    LineChartView lineChartView;
    String[] axisData = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    Timer timer02 = new Timer(true);

    /**
     * Initialization for HeartRate Supervision
     */
    private ObservableHeartRate hrObservable = new ObservableHeartRate();
    private ObserverHeartRateChanged hrObserver = new ObserverHeartRateChanged();

    /**
     * ANT+ Library
     */
    int mnState;
    private AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;
    WheelPiesClient wheelPiesClient = null;
    String mstrUUID = "";

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

    //任何Task(如:TimerTask)無法直接改變元件因此要透過Handler來當橋樑
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
                    JSONObject resp = (JSONObject) msg.obj;
                    try
                    {
                        //String resp_code = resp.getString("code");
                        //if ("-1".equals(resp_code)) //因為java屬於物件導向的程式語言,注意在string的比較中要用equals去比

                        int resp_code = resp.getInt("code");
                        if (resp_code == -1)
                        {
                            TrainingMode.setText("--");
                            TrainingType.setText("--");
                        }
                        else
                        {
                            strMsg = getResponseJSONString((JSONObject) msg.obj);
                            String trainingId = extractDayTrainingId(strMsg);
                            JSONObject dayPlanJsonObj = new JSONObject(strMsg);
                            JSONObject dayPlan = new JSONObject(dayPlanJsonObj.getString("dayPlan"));
                            String excerciseType = dayPlan.getString("excerciseType");
                            String excerciseMode = dayPlan.getString("excerciseMode");
                            //Logs.showTrace("show me data" + excerciseType);
                            //Logs.showTrace("show me data" + excerciseMode);
                            TrainingMode.setText(excerciseMode);
                            TrainingType.setText(excerciseType);

                            // 呼叫當日課程說明API
                            requestCourseDayViewAPI(trainingId);

                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        break;
                    }

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
//                tsec = 0;
//                csec = 0;
//                cmin = 0;
//                chr = 0;
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.training_main);

        /**
         * Make observer subscribes to observable(HeartRate) for HeartRate Supervision
         */
        hrObservable.addObserver(hrObserver.HeartRateChanged);

        //畫面切換
        LayoutInflater inflater = getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.training_main, null);//找出第一個視窗
        final View view2 = inflater.inflate(R.layout.training_device_connection, null);//找出第二個視窗
        setContentView(view1); //顯示目前第一個視窗

        final ImageView startbutton = (ImageView) view1.findViewById(R.id.startbutton);//找出第一個視窗中start的按鈕
        final ImageView stopbutton = (ImageView) view1.findViewById(R.id.stopbutton);//找出第一個視窗中stop的按鈕
//        TextView backbutton = (TextView) view2.findViewById(R.id.textView14);//找出第二個視窗中的按鈕
        startbutton.setTag(0);
        stopbutton.setTag(0);

        TrainingMode = (TextView) view1.findViewById(R.id.exercise_mode_content);//找出第一個視窗中訓練類型的字串框格
        TrainingType = (TextView) view1.findViewById(R.id.exercise_type_content);//找出第一個視窗中訓練模式的字串框格
        lineChartView = view1.findViewById(R.id.chartLine); //找出第一個視窗中折線圖的image

        timer = (TextView) view1.findViewById(R.id.timer_content);

        tv_status = (TextView) findViewById(R.id.textView_Status);
        tv_estTimestamp = (TextView) findViewById(R.id.textView_EstTimestamp);
        tv_rssi = (TextView) findViewById(R.id.textView_Rssi);
        textView_ComputedHeartRate = (TextView) view1.findViewById(R.id.textView_ComputedHeartRate);
        //找出第一個視窗中心率的字串框格
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
//        btn_Active = (Button) findViewById(R.id.button_active);

        wheelPiesClient = new WheelPiesClient();
        mnState = 0;
        //Button監聽,第一種寫法,在上面先定義listener,function帶入即可
//        startbutton.setOnClickListener(listener);
        //Button監聽,第二種寫法,(第一個畫面在做的事情)
        requestTodayTrainingAPI("2018-10-22");

        startbutton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logs.showTrace("startbutton onClick:" + v.getTag());
                int nRun = (int) v.getTag();

                if (nRun == 1)
                {
                    mnState = 2;
                    //mstrUUID = "";
                    v.setTag(0);
                }
                else
                {
                    mnState = 1;
                    UUID uuid = UUID.randomUUID();
                    mstrUUID = uuid.toString();
                    v.setTag(1);
                }

                // TODO Auto-generated method stub
                switch (v.getId())
                {
                    case R.id.startbutton:
//                        setContentView(view2);//按了之後跳到第2個視窗
//                        Intent intent = null;
//                        intent = new Intent(TrainingActivity.this, com.dsi.ant.antplus.pluginsampler
//                                .heartrate.Activity_SearchUiHeartRateSampler.class);
//                        startActivity(intent);

                        if (startflag)
                        {
                            startflag = false; //一開始的code,當點開始運動的時候flag會變成flase去啟動timer
//                            startbutton.setImageResource(R.drawable.training_startbutton);

                        }
                        else
                        {
                            startflag = true;
//                            startbutton.setImageResource(R.drawable.training_stopbutton);
                        }
                        break;

                }
                requestAccessToPcc(); //啟動ant+ device detect
                startbutton.setVisibility(View.GONE);
                run(); //啟動畫圖機制
                stopbutton.setVisibility(View.VISIBLE);
            }
        });

        stopbutton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int nRun = (int) v.getTag();

                if (nRun == 1)
                {
                    mnState = 2;
                    //mstrUUID = "";
                    v.setTag(0);
                }
                else
                {
                    mnState = 1;
                   UUID uuid = UUID.randomUUID();
                    mstrUUID = uuid.toString();
                    v.setTag(1);
                }

                if (startflag)
                {
                    startflag = false;
                }
                else
                {
                    startflag = true;
                }

                Intent intent = null;
                intent = new Intent(TrainingActivity.this, SpeechActivity.class);
                startActivity(intent);
            }
        });
    
        lineChartView.setInteractive(true);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setScrollEnabled(true);
        
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 200;
        viewport.bottom = 20;
        viewport.right = 10;
        viewport.left = 0;
        lineChartView.setViewportCalculationEnabled(false); //這行一定要加否則頁面在顯示的時候不會按照上面所設定的top/botton來跑
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
        
//        假如要透過點圖的方式啟動則開啟下列程式碼
//        lineChartView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                run();
//            }
//        });
        Intent intent = getIntent();
        String strName = intent.getStringExtra("NAME");
        Logs.showTrace("my name " + strName);
        updateChart();

//        backbutton.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                setContentView(view1);//按了之後跳到第1個視窗
//            }
//        });
    
    }

    private void requestAccessToPcc()
    {
        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearch.MultiDeviceSearchResult result = intent.getParcelableExtra
                    (Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, result.getAntDeviceNumber(), 0,
                    base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
        }
        else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, this, base_IPluginAccessResultReceiver,
                    base_IDeviceStateChangeReceiver);
        }
    }

    protected AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc>
            base_IPluginAccessResultReceiver = new AntPluginPcc
            .IPluginAccessResultReceiver<AntPlusHeartRatePcc>()
    {
        //Handle the result, connecting to events on success or reporting failure to user.
        @Override
        public void onResultReceived(AntPlusHeartRatePcc result, RequestAccessResult resultCode,
                DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    //宣告Timer
                    Timer timer01 = new Timer();

                    //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
                    timer01.schedule(task, 0, 1000);

                    startflag = true;//當裝置連結成功的時候去啟動timer

                    hrPcc = result;
//                            tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToHrEvents();
//                            if (!result.supportsRssi())
//                            {
//                                tv_rssi.setText("N/A");
//                            }
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(TrainingActivity.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                    // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast.makeText(TrainingActivity.this, "ANT Adapter Not " + "Available" + ". Built-in "
                            + "ANT hardware or external adapter " + "required" + ".", Toast.LENGTH_SHORT)
                            .show();
                    // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    //Note: Since we compose all the params ourself, we should never see
                    // this result
                    Toast.makeText(TrainingActivity.this, "Bad request parameters.", Toast.LENGTH_SHORT)
                            .show();
                    // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(TrainingActivity.this, "RequestAccess failed. " + "See" + " logcat for "
                            + "details.", Toast.LENGTH_SHORT).show();
                    //      tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    //    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(TrainingActivity.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\"" + AntPlusHeartRatePcc
                            .getMissingDependencyName() + "\"\n was not found. You need " + "to " +
                            "install the ANT+ Plugins service or you " + "may need to update your " +
                            "existing version if you already have it." + " Do you want to launch the " +
                            "Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
                                    AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            TrainingActivity.this.startActivity(startStore);
                        }
                    });
                    adlgBldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog waitDialog = adlgBldr.create();
                    waitDialog.show();
                    break;
                case USER_CANCELLED:
                    //     tv_status.setText("Cancelled. Do Menu->Reset.");
                    break;
                case UNRECOGNIZED:
                    Toast.makeText(TrainingActivity.this, "Failed: UNRECOGNIZED. " + "PluginLib Upgrade " +
                            "Required?", Toast.LENGTH_SHORT).show();
                    //       tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(TrainingActivity.this, "Unrecognized result: " + resultCode, Toast
                            .LENGTH_SHORT).show();
                    //    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }
    };

    //Receives state changes and shows it on the status display line
    protected AntPluginPcc.IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver = new AntPluginPcc
            .IDeviceStateChangeReceiver()
    {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // tv_status.setText(hrPcc.getDeviceName() + ": " +
                    // newDeviceState);
                }
            });

        }
    };

    public void subscribeToHrEvents()
    {
        wheelPiesClient.start();
        hrPcc.subscribeHeartRateDataEvent(new AntPlusHeartRatePcc.IHeartRateDataReceiver()
        {
            @Override
            public void onNewHeartRateData(final long estTimestamp, EnumSet<EventFlag> eventFlags, final
            int computedHeartRate, final long heartBeatCount, final BigDecimal heartBeatEventTime, final
            AntPlusHeartRatePcc.DataState dataState)
            {
                /**
                 * Update HeartRate Observable when New HeartRateData is received
                 */
                hrObservable.setValue(computedHeartRate);

                // Mark heart rate with asterisk if zero detected
                final String textHeartRate = String.valueOf(computedHeartRate) + ((AntPlusHeartRatePcc
                        .DataState.ZERO_DETECTED.equals(dataState)) ? "*" : "");

                // Mark heart beat count and heart beat event time with asterisk if initial value
                final String textHeartBeatCount = String.valueOf(heartBeatCount) + ((AntPlusHeartRatePcc
                        .DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");
                final String textHeartBeatEventTime = String.valueOf(heartBeatEventTime) + (
                        (AntPlusHeartRatePcc.DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        textView_ComputedHeartRate.setText(textHeartRate);
                        //  tv_heartBeatCounter.setText(textHeartBeatCount);
                        //  tv_heartBeatEventTime.setText(textHeartBeatEventTime);

                        //  tv_dataStatus.setText(dataState.toString());

                        sendData();
                    }
                });
            }
        });

        hrPcc.subscribePage4AddtDataEvent(new AntPlusHeartRatePcc.IPage4AddtDataReceiver()
        {
            @Override
            public void onNewPage4AddtData(final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final int manufacturerSpecificByte, final BigDecimal previousHeartBeatEventTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //       tv_manufacturerSpecificByte.setText(String.format("0x%02X",
                        //manufacturerSpecificByte));
                        //       tv_previousHeartBeatEventTime.setText(String.valueOf
                        //(previousHeartBeatEventTime));
                    }
                });
            }
        });

        hrPcc.subscribeCumulativeOperatingTimeEvent(new AntPlusLegacyCommonPcc
                .ICumulativeOperatingTimeReceiver()
        {
            @Override
            public void onNewCumulativeOperatingTime(final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final long cumulativeOperatingTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //        tv_cumulativeOperatingTime.setText(String.valueOf
                        // (cumulativeOperatingTime));
                    }
                });
            }
        });

        hrPcc.subscribeManufacturerAndSerialEvent(new AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver()
        {
            @Override
            public void onNewManufacturerAndSerial(final long estTimestamp, final EnumSet<EventFlag>
                    eventFlags, final int manufacturerID, final int serialNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //        tv_manufacturerID.setText(String.valueOf(manufacturerID));
                        //        tv_serialNumber.setText(String.valueOf(serialNumber));
                    }
                });
            }
        });

        hrPcc.subscribeVersionAndModelEvent(new AntPlusLegacyCommonPcc.IVersionAndModelReceiver()
        {
            @Override
            public void onNewVersionAndModel(final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final int hardwareVersion, final int softwareVersion, final int modelNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //       tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                        //       tv_softwareVersion.setText(String.valueOf(softwareVersion));
                        //       tv_modelNumber.setText(String.valueOf(modelNumber));
                    }
                });
            }
        });

        hrPcc.subscribeCalculatedRrIntervalEvent(new AntPlusHeartRatePcc.ICalculatedRrIntervalReceiver()
        {
            @Override
            public void onNewCalculatedRrInterval(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                    final BigDecimal rrInterval, final AntPlusHeartRatePcc.RrFlag flag)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        //       tv_rrFlag.setText(flag.toString());

                        // Mark RR with asterisk if source is not cached or page 4
                        if (flag.equals(AntPlusHeartRatePcc.RrFlag.DATA_SOURCE_CACHED) || flag.equals
                                (AntPlusHeartRatePcc.RrFlag.DATA_SOURCE_PAGE_4))

                        {
                            //           tv_calculatedRrInterval.setText(String.valueOf
                            // (rrInterval));
                        }
                        else
                        {
                            //           tv_calculatedRrInterval.setText(String.valueOf
                            // (rrInterval) + "*");
                        }
                    }
                });
            }
        });

        hrPcc.subscribeRssiEvent(new AntPlusCommonPcc.IRssiReceiver()
        {
            @Override
            public void onRssiData(final long estTimestamp, final EnumSet<EventFlag> evtFlags, final int rssi)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //       tv_estTimestamp.setText(String.valueOf(estTimestamp));
                        //       tv_rssi.setText(String.valueOf(rssi) + " dBm");
                    }
                });
            }
        });
    }

    private void sendData()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("activeId", mstrUUID);
            jsonObject.put("state", mnState);
            if (2 == mnState) // 運動結束
            {
                mstrUUID = "";
            }
            if (0 != mnState)
            {
                mnState = 0;
            }
            jsonObject.put("estTimestamp", tv_estTimestamp.getText().toString());
            jsonObject.put("computedHeartRate", textView_ComputedHeartRate.getText().toString());
            jsonObject.put("heartBeatCounter", tv_heartBeatCounter.getText().toString());
            jsonObject.put("heartBeatEventTime", tv_heartBeatEventTime.getText().toString());
            jsonObject.put("dataStatus", tv_dataStatus.getText().toString());
            jsonObject.put("manufacturerSpecificByte", tv_manufacturerSpecificByte.getText().toString());
            jsonObject.put("previousHeartBeatEventTime", tv_previousHeartBeatEventTime.getText().toString());
            jsonObject.put("cumulativeOperatingTime", tv_cumulativeOperatingTime.getText().toString());
            jsonObject.put("manufacturerID", tv_manufacturerID.getText().toString());
            jsonObject.put("serialNumber", tv_serialNumber.getText().toString());
            jsonObject.put("hardwareVersion", tv_hardwareVersion.getText().toString());
            jsonObject.put("softwareVersion", tv_softwareVersion.getText().toString());
            jsonObject.put("modelNumber", tv_modelNumber.getText().toString());
            jsonObject.put("rrFlag", tv_rrFlag.getText().toString());
            jsonObject.put("calculatedRrInterval", tv_calculatedRrInterval.getText().toString());
            jsonObject.put("rssi", tv_rssi.getText().toString());
            wheelPiesClient.send(jsonObject);
        }
        catch (Exception e)
        {
            com.dsi.ant.antplus.pluginsampler.datatransfer.Logs.showError("JSONObject Exception: " + e
                    .toString());
        }

    }

    //處理即時圖形顯示的區塊
    private void updateChart()
    {
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        String[] testlist = {"120", "130", "140", "150", "160", "170"};

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        String strLabel;
        if (10 < nXData)
        {
            nXData -= 10;
        }

        for (int i = 0; i < 11; i++)
        {
            strLabel = String.valueOf(nXData++);
            axisValues.add(i, new AxisValue(i).setLabel(strLabel));
//            yAxisValues.add(new AxisValue(i).setValue(i).setLabel(
//                    i +""));// 添加y轴显示的刻度值
    
        }
        
        float DevHrData = Float.parseFloat(textView_ComputedHeartRate.getText().toString());
        
        for (int i = 0; i < 11; ++i)
        {
//            yAxisValues.add(new PointValue(i, ThreadLocalRandom.current().nextInt(65, 110)));//自動random出幾筆數據來畫圖
            yAxisValues.add(new PointValue(i, DevHrData));//取出設備心率數值來畫圖
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //設定x座標軸
        Axis axis = new Axis();
        axis.setName("Seconds");

        axis.setValues(axisValues);
//        axis.setHasLines(true);
//        axis.setTextSize(9);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        //設定y座標軸
        Axis yAxis = new Axis();
        yAxis.setName("HeartRate");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        
//        yAxis.setTextSize(9);
//        yAxis.setValues(testlist);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
    }

    private void run()
    {
        if (bRun)
        {
            bRun = false;
            timer02.cancel();
        }
        else
        {
            bRun = true;
            timer02.schedule(new MyTimerTask(), 1000, 1000);
        }

    }

    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            updateChart();
        }
    }


}
