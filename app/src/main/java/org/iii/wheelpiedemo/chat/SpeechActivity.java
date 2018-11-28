package org.iii.wheelpiedemo.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class SpeechActivity extends Activity implements TextToSpeech.OnInitListener
{
    
    private int voiceRecognitionRequestCode = 777;
//    private static RestApiClient restApiClient = new RestApiClient();
    private static String chatingAPIURL = "http://13.230.154.2:8000/appd4re";
    private TextView textToAPIResp = null;
    private ImageView testButton = null;
    private ImageView textToDashboard = null;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private final int _MSG_ASK_API_RESPONSE_ = 2;
    private String MSG_INPUT_API = null;
    private String ABC_REPLY = null;
    private final int _SpeechTextOut_ = 5;
    Response response = null;
    TextToSpeech mTTS = null;
    private final int ACT_CHECK_TTS_DATA = 1000;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String strMsg = null;
            switch (msg.what)
            {
            
                case _MSG_ASK_API_RESPONSE_:
                    Logs.showTrace("fuck here");
                    String ABC123 = (String) msg.obj;
                    Logs.showTrace("<case eat> MsgAskResponse: " + ABC123);
                    textToAPIResp.setText("ANS:" + ABC123);
                    Logs.showTrace("PRINT OK");
                    saySomething(ABC123.toString().trim(), 1);
//                    Logs.showTrace("SOUND OK");
                    break;
                case _SpeechTextOut_:
                    String text_handle = (String) msg.obj;
                    Logs.showTrace("<case eat> SpeechText: " + text_handle);
                    try
                    {
                        requestMsgAskAPI(text_handle);
                        Logs.showTrace("<case action> put SpeechText to requestMsgAskAPI");
                    }
                    catch (JSONException e)
                    {
                       // e.printStackTrace();
                        Logs.showTrace(e.toString());
                    }
            
            }
        }
    };
//    private String getResponseJSONString(JSONObject clientResp)
//    {
//        String jsonString = null;
//        if (clientResp != null)
//        {
//            try
//            {
//                jsonString = ((JSONObject) clientResp).getString("data");
//            }
//            catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        return jsonString;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
//        recordVoice = (ImageView) findViewById(R.id.imageViewSpeechBtn);
        textToAPIResp = (TextView) findViewById(R.id.chat_textViewAnswer);
        testButton = (ImageView) findViewById(R.id.chat_apiImage);
//        testButton.setOnClickListener(textToAPIDo);
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
        findViewById(R.id.chat_imageViewSpeechBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startVoiceRecognitionActivity();
            }
        });
        // Check to see if we have TTS voice data
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);

    }
    
//    private View.OnClickListener textToAPIDo = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View v)
//        {
//            textToAPIResp.setText("你按到我了！");
//        }
//    };
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說說您現在的狀況");
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
            message.what = _SpeechTextOut_;
            message.obj = MSG_INPUT_API;
            handler.sendMessage(message);
        }
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
    
    private void requestMsgAskAPI(String msgString) throws JSONException
    {
        restApiHeaderClient.setResponseListener(requestMsgAskResponseListener);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("ask", msgString);
        HashMap<String, String> headers = new HashMap<String, String>();
//        headers.put("Authorization", "Bearer " +
//                "2h39l3nV4iiYucuXax7Mw6PEQMh4cjkFX7AeW3yVcaiLyIhAHRdAPLixkgS5Mvpv0FcWJMnXUyO9ssEkeb60VyBWm4yEVoPZ1jXIAcnO3ZM9qIgcRXiTKdEYkOTcZWFryyo2hFTgQwMVpprXDpGyBlHJUru8g9QOeOYNYET9jsRUz0IX6e6bPuw3K3FNsBfHmUbukwYgEnDBLP6VYOAul9njlS4DKVda3yD6WGFXcjkbKeRtPb8dY98dJkpXsWUg");
        response = new Response();
        int nResponse_id = restApiHeaderClient.HttpGet(chatingAPIURL, Config.HTTP_DATA_TYPE.X_WWW_FORM,
                param, response,headers);
//        Logs.showTrace("[API] http response id: " + nResponse_id);
//        String testttttt = "";
//        if(null != response.Data)
//        {
//            Log.d("OBVIOUSssssssssssss", response.Data);
//            JSONObject lkelkrwlr = new JSONObject(response.Data);
//            testttttt = lkelkrwlr.getString("msg_reply");
//            Log.d("OBVIOUSssssssssssss", testttttt);
//            Log.d("OBVIOUS_code", String.valueOf(response.Code));
//            Log.d("OBVIOUS", String.valueOf(response.Data));
//        }
//        Log.d("response",String.valueOf(nResponse_id));
    
    }
    private RestApiHeaderClient.ResponseListener requestMsgAskResponseListener = new RestApiHeaderClient
            .ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Log.d("client gets","Hello response!!");
            Log.d("response",jsonObject.toString());
            try
            {
                Log.d("response.data", response.Data);
                Logs.showTrace("[API] onResponse Data: " + jsonObject.getString("data"));
                JSONObject resp = new JSONObject(jsonObject.getString("data"));
                Logs.showTrace("[API] onResponse Data [msg_reply]: " + resp.getString("msg_reply"));
                String ABC_REPLY = resp.getString("msg_reply");
                Log.d("ABC_REPLY","OK");
//                textToAPIResp.setText(ABC_REPLY);
                Message message = new Message();
                message.what = _MSG_ASK_API_RESPONSE_;
                message.obj = ABC_REPLY;
                Log.d("message","OK");
                handler.sendMessage(message);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            
        }
    };
    
    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTTS != null) {
                int result = mTTS.setLanguage(Locale.TAIWAN);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    saySomething("來吧，告訴我你喜歡什麼", 0);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }
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