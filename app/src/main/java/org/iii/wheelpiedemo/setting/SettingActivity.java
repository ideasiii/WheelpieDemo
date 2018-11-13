package org.iii.wheelpiedemo.setting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.menu.NavigationActivity;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.iii.wheelpiedemo.course.util.ViewUtils;
import org.iii.wheelpiedemo.login.LoginActivity;
import org.iii.wheelpiedemo.setting.response.PhysicalInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SettingActivity extends NavigationActivity {

    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String URL_USER_PHYSICAL_INFO = "https://dsicoach.win/api/user/physicalInfo";
    private final int MSG_API_RESPONSE_GET_USER_PHYSICAL_INFO = 1;
    private final int MSG_API_RESPONSE_PUT_USER_PHYSICAL_INFO = 2;
    private final int MSG_CONTENT_VIEW_LOGIN = 0;
    private final String PREF_USER_TOKEN_KEY = "userToken";

    private String userToken;
    private ProgressDialog dialog;
    private EditText editTextMaxHeartRate;
    private EditText editTextRestHeartRate;
    private EditText editTextWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_setting);

        // 初始共用menu
        initCommonNavigationView();

        editTextMaxHeartRate = findViewById(R.id.edittext_max_heart_rate);
        editTextRestHeartRate = findViewById(R.id.edittext_rest_heart_rate);
        editTextWeight = findViewById(R.id.edittext_weight);
        // 顯示等待訊息框
        displayLoadingDialog();

        if (isUserLoggedIn()) {
            // 設定儲存按鈕
            findViewById(R.id.button_setting_save).setOnClickListener(btnSaveSettingOnClick);
            // 呼叫個人設定API
            requestAPI(URL_USER_PHYSICAL_INFO, "GET", null, userPhysicalInfoResponseListener);
        } else {
            theHandler.sendEmptyMessage(MSG_CONTENT_VIEW_LOGIN);
        }
    }

    @Override
    public int getBottomNavigationViewId() {
        return R.id.setting_nav;
    }

    private View.OnClickListener btnSaveSettingOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 顯示Loading
            displayLoadingDialog();
            // 取消focus
            editTextMaxHeartRate.setFocusable(false);
            editTextRestHeartRate.setFocusable(false);
            editTextWeight.setFocusable(false);
            // 取得從UI設定值
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("body", getPutUserPhysicalJSONBody());
            // 呼叫儲存API
            requestAPI(URL_USER_PHYSICAL_INFO, "PUT", params, putUserPhysicalInfoResponseListener);
        }
    };

    private String getPutUserPhysicalJSONBody() {
        JSONObject info = new JSONObject();
        try {
            // 從UI取得數值
            String maxText = editTextMaxHeartRate.getText().toString();
            String restText = editTextRestHeartRate.getText().toString();
            String weightText = editTextWeight.getText().toString();
            int max = Integer.parseInt(maxText, 10);
            int rest = Integer.parseInt(restText, 10);
            int weight = Integer.parseInt(weightText, 10);
            // 封裝數值為JSON物件
            JSONObject physicalInfo = new JSONObject();
            physicalInfo.put("maxHeartRate", max);
            physicalInfo.put("restHeartRate", rest);
            physicalInfo.put("weight", weight);
            info.put("physicalInfo", physicalInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info.toString();
    }

    private String getResponseJSONString(JSONObject clientResp) {
        String jsonString = null;
        if (clientResp instanceof JSONObject && clientResp.has("data")) {
            try {
                jsonString = ((JSONObject)clientResp).getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }

    private boolean isAPIResultSuccess(String apiResponse) {
        boolean result = false;
        if (apiResponse != null && apiResponse.length() != 0) {
            try {
                JSONObject jsonResp = new JSONObject(apiResponse);
                result = jsonResp.optBoolean("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void initViewByAPIResponse(PhysicalInfo physicalInfo) {
        if (physicalInfo != null) {
            //更新最大心率
            int max = physicalInfo.getMaxHeartRate();
            int rest = physicalInfo.getRestHeartRate();
            int weight = physicalInfo.getWeight();
            editTextMaxHeartRate.setText(max > 0 ? String.valueOf(max) : "");
            //更新安靜心率
            editTextRestHeartRate.setText(rest > 0 ? String.valueOf(rest) : "");
            //更新體重
            editTextWeight.setText(weight > 0 ? String.valueOf(weight) : "");
            //設定點擊後，取得focus
            editTextMaxHeartRate.setOnTouchListener(touchForEditableListener);
            editTextRestHeartRate.setOnTouchListener(touchForEditableListener);
            editTextWeight.setOnTouchListener(touchForEditableListener);
        }
    }

    private View.OnTouchListener touchForEditableListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        }
    };

    private void displayLoadingDialog() {
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
    }

    private void requestAPI(String url, String method, HashMap<String, String> param, RestApiHeaderClient.ResponseListener listener) {
        boolean isHTTPS = url != null && url.startsWith("https://");
        restApiHeaderClient.setResponseListener(listener);
        if (param == null) {
            param = new HashMap<String, String>();
        }
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Bearer %s", userToken));
        Response response = new Response();
        int nResponse_id = -1;
        switch(String.valueOf(method)) {
            case "POST":
                if (isHTTPS) {
                    restApiHeaderClient.HttpsPost(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                } else {
                    restApiHeaderClient.HttpPost(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                }
                break;
            case "PUT":
                if (isHTTPS) {
                    restApiHeaderClient.HttpsPut(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                } else {
                    restApiHeaderClient.HttpPut(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                }
                break;
            case "GET":
            default:
                if (isHTTPS) {
                    restApiHeaderClient.HttpsGet(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                } else {
                    restApiHeaderClient.HttpGet(url, Config.HTTP_DATA_TYPE.JSON, param, response, headers);
                }
        }
        Logs.showTrace("[API] http response id: " + nResponse_id);
    }

    private RestApiHeaderClient.ResponseListener userPhysicalInfoResponseListener = new RestApiHeaderClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = Message.obtain(
                    theHandler,
                    MSG_API_RESPONSE_GET_USER_PHYSICAL_INFO,
                    jsonObject
            );
            message.sendToTarget();
        }
    };

    private RestApiHeaderClient.ResponseListener putUserPhysicalInfoResponseListener = new RestApiHeaderClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            Logs.showTrace("[API] onResponse Data: " + jsonObject.toString());
            Message message = Message.obtain(
                    theHandler,
                    MSG_API_RESPONSE_PUT_USER_PHYSICAL_INFO,
                    jsonObject
            );
            message.sendToTarget();
        }
    };

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

    @SuppressLint("HandlerLeak")
    private Handler theHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String strMsg = null;
            switch (msg.what)
            {
                case MSG_API_RESPONSE_GET_USER_PHYSICAL_INFO:
                    strMsg = getResponseJSONString((JSONObject)msg.obj);
                    // 處理個人設定API結果
                    if (strMsg != null) {
                        PhysicalInfo pi = PhysicalInfo.parseResponse(strMsg);
                        initViewByAPIResponse(pi);
                        // 移除等待訊息框
                        dialog.dismiss();
                    } else {
                        // 移除等待訊息框
                        dialog.dismiss();
                        // 無個人設定資料，請輸入個人資訊
                        ViewUtils.showFloatingMessage(
                            getApplicationContext(),
                            "無個人設定資料，請輸入個人資訊"
                        );
                    }
                    break;
                case MSG_API_RESPONSE_PUT_USER_PHYSICAL_INFO:
                    strMsg = getResponseJSONString((JSONObject)msg.obj);
                    // 處理個人設定API結果
                    if (strMsg != null) {
                        //判斷是否更新成功
                        if (isAPIResultSuccess(strMsg)) {
                            // 移除等待訊息框
                            dialog.dismiss();
                            // 成功 -> Toast 成功
                            ViewUtils.showFloatingMessage(
                                getApplicationContext(),
                                "儲存成功"
                            );
                        } else {
                            // 移除等待訊息框
                            dialog.dismiss();
                            // 失敗 -> Toast 失敗
                            ViewUtils.showFloatingMessage(
                                getApplicationContext(),
                                "儲存失敗"
                            );
                        }
                    } else {
                        // 移除等待訊息框
                        dialog.dismiss();
                        // 目前網路有問題，儲存請稍後再試
                        ViewUtils.showFloatingMessage(
                            getApplicationContext(),
                            "目前網路有問題，儲存請稍後再試"
                        );
                    }
                    break;
                case MSG_CONTENT_VIEW_LOGIN:
                    Intent it = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(it);
            }
        }
    };
}
