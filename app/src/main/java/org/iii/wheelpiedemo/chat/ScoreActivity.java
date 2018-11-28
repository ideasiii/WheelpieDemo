package org.iii.wheelpiedemo.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.provider.Contacts;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Handler;

/**
 * Created by Ruska on 11/12/2018
 */
public class ScoreActivity extends Activity implements TextToSpeech.OnInitListener
{
    private ImageView circleAddColor = null;
    private TextView TRIMPScore = null;
    private TextView TRIMPScoreResponse1 = null;
    private TextView TRIMPScoreResponse2 = null;
    private double TRIMPScore_INPUT_API = 2.5;
    private String text = null;
    private final String PREF_USER_TOKEN_KEY = "userToken";
    private String userToken;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String ScoreAPIURL = "https://dsicoach.win/api/status/status-indicator";
    private final int ACT_CHECK_TTS_DATA = 1000;
    private final int MSG_SCORE_ASK_API_RESPONSE = 0;
    private final int _SCORE_REQUIRE_ = 7806;
    private final int _SCORE_ASK_API_RESPONSE_ = 7807;
    private boolean isUserLoggedIn()
    {
        boolean isLoggedIn = false;
        
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        userToken = sharedPref.getString(PREF_USER_TOKEN_KEY, null);
        
        if (userToken != null)
        {
            isLoggedIn = true;
        }
        
        return isLoggedIn;
    }
    TextToSpeech mTTS= null;
    
    private String extractTRIMPScore(String jsonString)
    {
        String TRIMPScore = null;
        if (jsonString == null || jsonString.length() == 0)
        {
            return null;
        }
        
        try
        {
            JSONObject resp = new JSONObject(jsonString);
            JSONObject statusIndicator = resp.getJSONObject("statusIndicator");
            JSONObject chartInfo = statusIndicator.getJSONObject("chartInfo");
            JSONObject subtitle = chartInfo.getJSONObject("subtitle");
            TRIMPScore = String.valueOf(subtitle.getInt("text"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return TRIMPScore;
    }
    
    @SuppressLint("HandlerLeak")
    private android.os.Handler handler = new android.os.Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String strMsg = null;
            switch (msg.what)
            {
                case _SCORE_ASK_API_RESPONSE_:
                    Logs.showTrace("fuck here");
                    
                    Double TRIMPScore_INPUT_API = (Double) msg.obj;
                    Logs.showTrace("<case eat> MsgAskResponse: " + TRIMPScore_INPUT_API);
                    TRIMPScore.setText(String.valueOf(TRIMPScore_INPUT_API));
                    Logs.showTrace("PRINT OK");
                    if (TRIMPScore_INPUT_API>=1){
                        TRIMPScoreResponse1.setText("狀況良好");
                        TRIMPScoreResponse2.setText("你的分數在平均之上，練得很棒！繼續保持！");
                        text = "你的分數在平均之上，練得很棒！繼續保持！";
                    } else if (TRIMPScore_INPUT_API<1 & TRIMPScore_INPUT_API>=-2){
                        TRIMPScoreResponse1.setText("狀況普通");
                        TRIMPScoreResponse2.setText("你的分數在70%的跑者當中，不要氣餒，再接再厲！");
                    } else {
                        TRIMPScoreResponse1.setText("狀況偏弱");
                        TRIMPScoreResponse2.setText("你的分數落於70%跑者以外，再加把勁！讓教練幫你破PB");
                    }
                    saySomething(text.toString().trim(), 1);
                    Logs.showTrace("SOUND OK");
                    break;
                
//                case _SpeechTextOut_:
//                    String text_handle = (String) msg.obj;
//                    Logs.showTrace("<case eat> SpeechText: " + text_handle);
//                    try
//                    {
//                        requestMsgAskAPI(text_handle);
//                        Logs.showTrace("<case action> put SpeechText to requestMsgAskAPI");
//                    }
//                    catch (JSONException e)
//                    {
//                        // e.printStackTrace();
//                        Logs.showTrace(e.toString());
//                    }
//                case _SCORE_REQUIRE_:
//                    String text_handle = (String) msg.obj;
//                    Logs.showTrace("<case eat> SpeechText: " + text_handle);
//                    try
//                    {
//                        requestScoreAskAPI(text_handle);
//                        Logs.showTrace("<case action> put SpeechText to requestMsgAskAPI");
//                    }
//                    catch (JSONException e)
//                    {
//                        // e.printStackTrace();
//                        Logs.showTrace(e.toString());
//                    }
                case MSG_SCORE_ASK_API_RESPONSE:
                    JSONObject resp = (JSONObject) msg.obj;
//                    try
//                    {
                        //String resp_code = resp.getString("code");
                        //if ("-1".equals(resp_code)) //因為java屬於物件導向的程式語言,注意在string的比較中要用equals去比
            
//                        int resp_code = resp.getInt("code");
//                        if (resp_code == -1)
//                        {
//                            TRIMPScoreResponse1.setText("--");
//                            TRIMPScoreResponse2.setText("--");
//                        }
//                        else
//                        {
                    strMsg = getResponseJSONString((JSONObject) msg.obj);
                    Logs.showTrace("show me [MSG_SCORE_ASK_API_RESPONSE] input:" + strMsg);
                    String TRIMPScore = extractTRIMPScore(strMsg);
                    Logs.showTrace("show me [extractTRIMPScore] output:" + TRIMPScore);
//                            JSONObject dayPlanJsonObj = new JSONObject(strMsg);
//                            JSONObject dayPlan = new JSONObject(dayPlanJsonObj.getString("dayPlan"));
//                            String excerciseType = dayPlan.getString("excerciseType");
//                            String excerciseMode = dayPlan.getString("excerciseMode");
                    //Logs.showTrace("show me data" + excerciseType);
                    //Logs.showTrace("show me data" + excerciseMode);
//                            TRIMPScoreResponse1.setText(excerciseMode);
                    TRIMPScoreResponse2.setText(TRIMPScore);
//                        }
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
                    break;
//                    }
            
            }
        }
    };
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
    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_score);
        Logs.showTrace("[state] onCreate");
        circleAddColor = (ImageView)findViewById(R.id.chat_imageViewp2Circle);
        TRIMPScore = (TextView)findViewById(R.id.chat_textViewp2Score);
        TRIMPScoreResponse1 = (TextView)findViewById(R.id.chat_textViewp2Comment1);
        TRIMPScoreResponse2 = (TextView)findViewById(R.id.chat_textViewp2Comment2);
        Logs.showTrace("[state] onCreate end");
//        saySomething("你覺得國王喜歡三隻小豬嗎",1);
//        mTTS = new TextToSpeech(this, this);
//        saySomething(text, 1)
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
    }
    
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//        //         Check to see if we have TTS voice data
//        Logs.showTrace("[state] onStart");
//        Intent ttsIntent = new Intent();
//        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
//        Logs.showTrace("[state] onStart end");
//    }

//    @Override
//    protected void onStop()
//    {
//        super.onStop();
//        mTTS.stop();
//        mTTS.shutdown();
//    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logs.showTrace("[state] onActivity");
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mTTS = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//    private void requestScoreAskAPI(String startDateString, String endDateString)
//    {
//        restApiHeaderClient.setResponseListener(requestScoreResponseListener);
//        Logs.showTrace("[state] onAPIHeaderClient");
//        HashMap<String, String> param = new HashMap<String, String>();
//        param.put("startDate", startDateString);
//        param.put("endDate", endDateString);
//        HashMap<String, String> headers = new HashMap<String, String>();
////        headers.put("Authorization", "Bearer " +
////
//// "2h39l3nV4iiYucuXax7Mw6PEQMh4cjkFX7AeW3yVcaiLyIhAHRdAPLixkgS5Mvpv0FcWJMnXUyO9ssEkeb60VyBWm4yEVoPZ1jXIAcnO3ZM9qIgcRXiTKdEYkOTcZWFryyo2hFTgQwMVpprXDpGyBlHJUru8g9QOeOYNYET9jsRUz0IX6e6bPuw3K3FNsBfHmUbukwYgEnDBLP6VYOAul9njlS4DKVda3yD6WGFXcjkbKeRtPb8dY98dJkpXsWUg");
//        headers.put("Authorization", String.format("Bearer %s", userToken));
//        Response response = new Response();
//        int nResponse_id = restApiHeaderClient.HttpsGet(ScoreAPIURL, Config.HTTP_DATA_TYPE.X_WWW_FORM,
//                param, response, headers);
//        Logs.showTrace("[API] http response id: " + nResponse_id);
//    }
//
//    private RestApiHeaderClient.ResponseListener requestScoreResponseListener = new RestApiHeaderClient
//            .ResponseListener()
//    {
//        @Override
//        public void onResponse(JSONObject jsonObject)
//        {
//            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
//            Message message = new Message();
//            message.what = MSG_SCORE_ASK_API_RESPONSE;
//            message.obj = jsonObject;
//            handler.sendMessage(message);
//        }
//    };
    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onInit(int status) {
        Logs.showTrace("[state] onInit");
        if (status == TextToSpeech.SUCCESS) {
            if (mTTS != null) {
                int result = mTTS.setLanguage(Locale.TAIWAN);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
//                    saySomething("來吧，告訴我你喜歡什麼", 0);
                    // 收api體能指數
                    // 先塞一個常數 TRIMPScore_INPUT_API
//                    String startDate = getStartTime();
//                    Logs.showTrace("[API input 1] startDate: " + startDate);
//                    String endDate = getEndTime();
//                    Logs.showTrace("[API input 2] endDate: " + endDate);
//                    requestScoreAskAPI(startDate,endDate);
                    Message message = new Message();
                    message.what = _SCORE_ASK_API_RESPONSE_;
                    message.obj = TRIMPScore_INPUT_API;
                    Log.d("message","OK");
                    handler.sendMessage(message);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }
    }
    public String getStartTime(){
        
        Calendar currentTime = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(currentTime.get(Calendar.YEAR)).append("-");
        sb.append(currentTime.get(Calendar.MONTH)+1).append("-"); //沒有補0的月份
        sb.append(currentTime.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
        
    }
    public String getEndTime(){
        
        Calendar currentTime = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(currentTime.get(Calendar.YEAR)).append("-");
        sb.append(currentTime.get(Calendar.MONTH)+1).append("-"); //沒有補0的月份
        currentTime.add(Calendar.DATE, -7);
        sb.append(currentTime.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
        
    }
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
