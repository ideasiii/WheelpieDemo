package org.iii.wheelpiedemo.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.iii.wheelpiedemo.R;
import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.more.restapiclient.RestApiClient;
import org.iii.wheelpiedemo.common.Logs;
import org.json.JSONObject;

import java.util.HashMap;

import android.os.Handler;

public class ApiActivity extends Activity
{
    private static RestApiClient restApiClient = new RestApiClient();
    private static final String mstrURL = "http://54.199.198.94/api/test.jsp";
    private TextView textViewResp = null;
    private final int MSG_API_RESPONSE = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        textViewResp = findViewById(R.id.textViewResp);
        textViewResp.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.imageViewRun).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                restApiClient.setResponseListener(responseListener);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("account", "jugo");
                param.put("password", "forgot");
                Response response = new Response();
                int nResponse_id = restApiClient.HttpPost(mstrURL, Config.HTTP_DATA_TYPE
                        .X_WWW_FORM, param, response);
                Logs.showTrace("[API] http response id: " + nResponse_id);
                
            }
            
        });
    }
    
    private RestApiClient.ResponseListener responseListener = new RestApiClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = new Message();
            message.what = MSG_API_RESPONSE;
            message.obj = jsonObject.toString();
            theHandler.sendMessage(message);
        }
    };
    
    @SuppressLint("HandlerLeak")
    private Handler theHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String strMsg;
            switch (msg.what)
            {
                case MSG_API_RESPONSE:
                    strMsg = (String) msg.obj;
                    textViewResp.append(strMsg);
                    textViewResp.append("\n==========================\n");
                    break;
            }
        }
    };
}
