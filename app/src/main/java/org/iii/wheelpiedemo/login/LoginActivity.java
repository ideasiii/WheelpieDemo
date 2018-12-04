package org.iii.wheelpiedemo.login;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;
import org.iii.wheelpiedemo.course.util.JSONUtils;
import org.iii.wheelpiedemo.course.util.ViewUtils;
import org.iii.wheelpiedemo.dashboard.DashboardActivity;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.Context;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.course.CourseActivity;
import org.iii.wheelpiedemo.training.TrainingActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;

import org.iii.more.restapiclient.RestApiClient;
import org.json.JSONObject;

import java.util.HashMap;
import org.iii.wheelpiedemo.course.util.HttpUtils;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;

public class LoginActivity extends AppCompatActivity {
    private Button loginPopupBtn;
    private ImageButton closeLoginPopupBtn;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private String userFbAccessToken;
    private String userToken;
    private static RestApiClient restApiClient = new RestApiClient();
    private static final String mstrURL = "https://dsicoach.win/api/user/auth/facebook";
    private String LOG_TAG = "LoginActivity";
    private AlertDialog al;
    private View.OnClickListener loginPopupBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            initPopup(view);
        }
    };
    private String userId;
    private static RestApiHeaderClient restApiHeaderClient = new RestApiHeaderClient();
    private static String URL_API_USER_INFO = "https://dsicoach.win/api/user/info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        loginPopupBtn = findViewById(R.id.login_popup_btn);
        loginPopupBtn.setOnClickListener(loginPopupBtnOnClickListener);
        al = new AlertDialog.Builder(LoginActivity.this).create();

        //Setup Facebook LoginManager
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Get fb access token
                        userFbAccessToken = loginResult.getAccessToken().getToken();
                        Log.d(LOG_TAG, userFbAccessToken);

                        // UI shows 'Logging in'
                        al.setMessage("登入中...");
                        al.show();

                        // Call Virtual Coach API service to signup/signin user.
                        restApiClient.setResponseListener(responseListener);
                        HashMap<String, String> param = new HashMap<String, String>();
                        param.put("account", "facebook");
                        param.put("token", userFbAccessToken);
                        Response response = new Response();
                        int nResponse_id = restApiClient.HttpPost(mstrURL, Config.HTTP_DATA_TYPE
                                .X_WWW_FORM, param, response);
                        Log.d(LOG_TAG,"[API] http response id: " + nResponse_id);

                    }

                    @Override
                    public void onCancel() {
                        al.setMessage("取消登入...");
                        al.show();
                        Log.d(LOG_TAG, "onCancelled...");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        al.setMessage("登入失敗...");
                        al.show();
                        Log.d(LOG_TAG, "onError...");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initPopup(View view){
        View popupContentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupContentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,true);
        popupWindow.setAnimationStyle(R.style.login_popUpAnim);
        popupWindow.showAtLocation(view, 0,0,0);

        closeLoginPopupBtn = popupContentView.findViewById(R.id.imageButton2);
        closeLoginPopupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }

        });

    }

    private RestApiClient.ResponseListener responseListener = new RestApiClient.ResponseListener()
    {
        @Override
        public void onResponse(JSONObject jsonObject)
        {
            try{
                Integer statusCode = jsonObject.getInt("code");
                if(statusCode == 200){
                    // Extract user token from json response.
                    JSONObject dataObj = new JSONObject(jsonObject.getString("data"));
                    userToken = dataObj.getString("token");
                    Log.d(LOG_TAG, "verification succeed with user token :"+ userToken);

                    // Call userInfo api
                    restApiHeaderClient.setResponseListener(getUserInfoResponseListener);
                    HttpUtils.getAuthAPI(
                            restApiHeaderClient,
                            URL_API_USER_INFO,
                            Config.HTTP_DATA_TYPE.X_WWW_FORM,
                            null,
                            userToken
                    );

                    // Write the user token into shared preference for other activities to access
                    SharedPreferences sharedPref = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userToken", userToken);
                    editor.commit();

                    // Log out user from fb (temp solution to force user logging in every time in case token expired.)
                    LoginManager.getInstance().logOut();

                    // Close LoginActivity
//                    finish();
                }else{
                    Log.d(LOG_TAG, "verification failed due to /auth/facebook API Response not 200");
                    LoginManager.getInstance().logOut();
                    theHandler.sendEmptyMessage(0);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
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

    private RestApiHeaderClient.ResponseListener getUserInfoResponseListener =
            new RestApiHeaderClient.ResponseListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Logs.showTrace("[API User Info] onResponse Data: " + jsonObject.toString());
                    String strMsg = JSONUtils.getResponseJSONString(jsonObject);
                    userId = extractUserId(strMsg);
                    if (userId != null) {
                        // 取得使用者id成功，Write the userId into shared preference for other activities to access
                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userId", userId);
                        editor.commit();

                        // Switch to course activity
                        startActivity(new Intent(LoginActivity.this, TrainingActivity.class));
                    } else {
                        // 取得使用者id失敗
                        Logs.showTrace("[API User Info] onResponse Data: " + "取得使用者id失敗");
                    }
                }
            };

    @SuppressLint("HandlerLeak")
    private Handler theHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    al.setMessage("登入失敗...");
                    al.show();
                    break;
            }
        }
    };
}
