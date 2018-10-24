package org.iii.wheelpiedemo.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.more.restapiclient.RestApiClient;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.iii.wheelpiedemo.dashboard.DashboardActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SpeechActivity extends Activity
{
    
    //    private ImageView recordVoice = null;
    private int voiceRecognitionRequestCode = 777;
    //    private int voiceRecognitionGetCode = 777;
    private static RestApiClient restApiClient = new RestApiClient();
    private static String chatingAPIURL = "http://13.230.154.2:8000/appd";
    private TextView textToAPIResp = null;
    private ImageView testButton = null;
    private ImageView textToDashboard = null;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private final int MSG_DAY_TRAINING_API_RESPONSE = 0;
//    private String MSG_INPUT_API = "今天天氣好嗎";
    private String MSG_INPUT_API = null;
    private String MSG_REPLY = "好吧";
    private String ABC_REPLY = null;
    private final int AJJJJJJJJJJJJ = 5;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String strMsg = null;
            switch (msg.what)
            {
            
//                case MSG_DAY_TRAINING_API_RESPONSE:
//                    Logs.showTrace("fuck here");
//                    String rtttttttt = (String) msg.obj;
//                    Logs.showTrace("dfsfsdff" + rtttttttt);
//                    textToAPIResp.setText(rtttttttt);
//                    try
//                    {
//                        //String resp_code = resp.getString("code");
//                        //if ("-1".equals(resp_code)) //因為java屬於物件導向的程式語言,注意在string的比較中要用equals去比
//
//                        int resp_code = resp.getInt("code");
//                        if (resp_code == -1)
//                        {
//                            textToAPIResp.setText("--");
//                        }
//                        else
//                        {
//                            strMsg = getResponseJSONString((JSONObject) msg.obj);
//                            String MSG_REPLY = extractMsgReply(strMsg);
//                            //Logs.showTrace("show me data" + excerciseType);
//                            //Logs.showTrace("show me data" + excerciseMode);
//                            textToAPIResp.setText(MSG_REPLY);
//
//                        }
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                        break;
//                    }
                case AJJJJJJJJJJJJ:
                    String rttttttt = (String) msg.obj;
                    Logs.showTrace("YYYYYYYYYYYYYYYY" + rttttttt);
                    
//                    try
//                    {
//                        requestMsgAskAPI(rttttttt);
//                        Logs.showTrace("LPLPLPLPLPLPLPLP" + requestMsgAskAPI(rttttttt));
//                        Logs.showTrace("PPPPPPPPPPPPPPP" + rttttttt);
//
////                    Logs.showTrace("ZZZZZZZZZZZZZZ" + requestMsgAskAPI(rttttttt));
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
//        recordVoice = (ImageView) findViewById(R.id.imageViewSpeechBtn);
        textToAPIResp = (TextView) findViewById(R.id.chat_textViewAnswer);
        testButton = (ImageView) findViewById(R.id.chat_apiImage);
        testButton.setOnClickListener(textToAPIDo);
        textToDashboard = (ImageView) findViewById(R.id.chat_imageBack);
//        Logs.showTrace("sfsfsdfsdfsd");
        textToDashboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Logs.showTrace("====__+=======");
                Intent intent = null;
                intent = new Intent(SpeechActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
//        recordVoice.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent)
//            {
//                switch(motionEvent.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        recordVoice.setImageResource(R.drawable.chat_record_logo_green_click);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        recordVoice.setImageResource(R.drawable.chat_record_logo_green_status);
//                        break;
//                }
//                return false;
//            }
//        });
        findViewById(R.id.chat_imageViewSpeechBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startVoiceRecognitionActivity();
            }
        });
//        public void onGetSpeech(String voiceRecognitionGetCode){
//            requestMsgAskAPI(MSG_INPUT_API);
//    }
//        Log.d("MSG_INPUT","Hello Input!");
//        requestMsgAskAPI(MSG_INPUT_API);
//        Log.d("MSG_INPUT_end","Is this your answer?");
//        strMsg = getResponseJSONString((JSONObject) msg.obj);
    }
    
    private View.OnClickListener textToAPIDo = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            textToAPIResp.setText("你按到我了！");
        }
    };
    private View.OnClickListener textToDashboardDo = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Logs.showTrace("====__+=======");
            Intent intent = null;
            intent = new Intent(SpeechActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    };
    
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "QQ請說出您現在的狀況");
        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        
        startActivityForResult(intent, voiceRecognitionRequestCode);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == voiceRecognitionRequestCode && resultCode == Activity.RESULT_OK)
        {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // 語音識別會有多個結果，第一個是最精確的
            String text = matches.get(0);
            Logs.showTrace("speech: " + text);
            ((TextView) findViewById(R.id.chat_textViewSpeech)).setText(text);
            MSG_INPUT_API = text;
            Message message = new Message();
            message.what = AJJJJJJJJJJJJ;
            message.obj = MSG_INPUT_API;
            handler.sendMessage(message);
            
//            Log.d("response","hello INPUT API");
//            Log.d("MSG_INPUT_API__",text+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            Message message = new Message();
//            message.what = MSG_INPUT_API;
//            message.obj = jsonObject;
//            handler.sendMessage(MSG_INPUT_API);
//            Log.d("response",String.valueOf(requestMsgAskAPI(MSG_INPUT_API).Data));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private String requestMsgAskAPI(String msgString) throws JSONException
    {
        restApiHeaderClient.setResponseListener(todayTrainingResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("ask", msgString);
        HashMap<String, String> headers = new HashMap<String, String>();
//        headers.put("Authorization", "Bearer " +
//                "2h39l3nV4iiYucuXax7Mw6PEQMh4cjkFX7AeW3yVcaiLyIhAHRdAPLixkgS5Mvpv0FcWJMnXUyO9ssEkeb60VyBWm4yEVoPZ1jXIAcnO3ZM9qIgcRXiTKdEYkOTcZWFryyo2hFTgQwMVpprXDpGyBlHJUru8g9QOeOYNYET9jsRUz0IX6e6bPuw3K3FNsBfHmUbukwYgEnDBLP6VYOAul9njlS4DKVda3yD6WGFXcjkbKeRtPb8dY98dJkpXsWUg");
        Response response = new Response();
        int nResponse_id = restApiHeaderClient.HttpGet(chatingAPIURL, Config.HTTP_DATA_TYPE.X_WWW_FORM,
                param, response,headers);
//        Logs.showTrace("[API] http response id: " + nResponse_id);
        Log.d("OBVIOUSssssssssssss",response.Data);
        JSONObject lkelkrwlr = new JSONObject(response.Data);
        String testttttt = lkelkrwlr.getString("msg_reply");
        Log.d("OBVIOUSssssssssssss",testttttt);
        Log.d("OBVIOUS_code",String.valueOf(response.Code));
        Log.d("OBVIOUS",String.valueOf(response.Data));
//        Log.d("response",String.valueOf(nResponse_id));
        return testttttt;


        
    }
    private RestApiHeaderClient.ResponseListener todayTrainingResponseListener = new RestApiHeaderClient
            .ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Log.d("response","Hello response!!");
            Log.d("response",jsonObject.toString());
            try
            {
                Logs.showTrace("[API] onResponse Data: " + jsonObject.getString("data"));
                JSONObject resp = new JSONObject(jsonObject.getString("data"));
                Logs.showTrace("[API] onResponse Data: " + resp.getString("msg_reply"));
                String ABC_REPLY = resp.getString("msg_reply");
                Message message = new Message();
                message.what = MSG_DAY_TRAINING_API_RESPONSE;
                message.obj = ABC_REPLY;
                handler.sendMessage(message);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            
        }
    };
    private String extractMsgReply(String jsonString)
    {
        String msgReply = null;
        if (jsonString == null || jsonString.length() == 0)
        {
            return null;
        }
        
        try
        {
            JSONObject resp = new JSONObject(jsonString);
            msgReply = String.valueOf(resp.getInt("msg_reply"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return msgReply;
    }
}