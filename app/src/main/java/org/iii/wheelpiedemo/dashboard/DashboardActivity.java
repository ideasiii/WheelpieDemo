package org.iii.wheelpiedemo.dashboard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.iii.wheelpiedemo.R;
import android.content.SharedPreferences;
import android.content.Context;

public class DashboardActivity extends AppCompatActivity{

    private WebView webview;
    private ProgressBar spinner;
    private final String LOG_TAG = "DashboardActivity";
    private Boolean onPageStart = Boolean.FALSE;
    private Boolean isTokenInjected = Boolean.FALSE;
    private String userToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Retrieve userToken from shared preference.
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userToken = sharedPref.getString("userToken", null);

        webview =(WebView)findViewById(R.id.webview1);
        spinner = (ProgressBar)findViewById(R.id.progressBar2);
        webview.setWebViewClient(new CustomWebViewClient());
        webview.setWebChromeClient(new CustomWebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        //For testing environment
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.loadUrl("https://coachbot.win/dashboard/");
//        webview.loadUrl("https://32946ea8.ngrok.io/app/");
    }

    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.i(LOG_TAG, "DEBUG: progress : " + newProgress);
            if(onPageStart){
                if(newProgress == 100){
                    Log.i(LOG_TAG, "DEBUG: onPageFinished at progress : " + newProgress);
                    view.loadUrl(
                            "javascript:(function() { " +
                                    "let evt = new CustomEvent('tokenInject', {detail:'"+userToken+"'});"
                                    +
                                    "window.dispatchEvent(evt);"
                                    +
                                    "})()");
                    Log.i(LOG_TAG, "DEBUG: token is injected : " + userToken);
                }
            }else{
                onPageStart = Boolean.TRUE;
                Log.i(LOG_TAG, "DEBUG: onPageStarted at progress : " + newProgress);
            }
        }
    }

    // This allows for a splash screen
    // (and hide elements once the page loads)
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            webview.setVisibility(webview.INVISIBLE);
        }

        @Override
        public void onPageFinished(WebView webview, String url) {

            spinner.setVisibility(View.GONE);

            webview.setVisibility(webview.VISIBLE);
            super.onPageFinished(webview, url);
        }
    }
}
