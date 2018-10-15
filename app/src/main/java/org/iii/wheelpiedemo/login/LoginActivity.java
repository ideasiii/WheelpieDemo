package org.iii.wheelpiedemo.login;
import org.iii.wheelpiedemo.dashboard.DashboardActivity;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
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

import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;

import org.iii.more.restapiclient.RestApiClient;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private Button loginPopupBtn;
    private ImageButton closeLoginPopupBtn;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private String userFbAccessToken;
    private String userToken;
    private AlertDialog al;

    private static RestApiClient restApiClient = new RestApiClient();
    private static final String mstrURL = "https://dsicoach.win/api/user/auth/facebook";
    private String LOG_TAG = "LoginActivity";

    private View.OnClickListener loginPopupBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            initPopup(view);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        al = new AlertDialog.Builder(this).create();

        setContentView(R.layout.login_main);
        loginPopupBtn = findViewById(R.id.login_popup_btn);

        loginPopupBtn.setOnClickListener(loginPopupBtnOnClickListener);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // get fb access token
                        userFbAccessToken = loginResult.getAccessToken().getToken();
                        Log.d(LOG_TAG, userFbAccessToken);

//                        // Send Post to api service to get user token
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
                        // App code
                        Log.d(LOG_TAG, "onCancelled...");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
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
                    JSONObject dataObj = new JSONObject(jsonObject.getString("data"));
                    userToken = dataObj.getString("token");
                    Log.d(LOG_TAG, "verification succeed with user token :"+ userToken);
                    // write the user token into shared preference for other activities to access
                    SharedPreferences sharedPref = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userToken", userToken);
                    editor.commit();

                    // switch to course activity
                    startActivity(new Intent(LoginActivity.this, CourseActivity.class));
//                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));

                    // Log out user from fb
                    LoginManager.getInstance().logOut();
                }else{
                    Log.d(LOG_TAG, "verification failed due to /auth/facebook API Response not 200");
                    LoginManager.getInstance().logOut();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    };
}
