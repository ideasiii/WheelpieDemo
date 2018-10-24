package org.iii.wheelpiedemo.common;

import org.iii.more.eventlistener.EventListener;
import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.more.restapiclient.RestApiClient;
import org.json.JSONObject;

import java.util.HashMap;

public class RestApiHeaderClient {
    private static int msnSerialNUm = 0;
    private RestApiHeaderClient.ResponseListener responseListener = null;
    private EventListener.Callback callback = new EventListener.Callback() {
        public void onEvent(JSONObject jsonObject) {
            System.out.println("[RestApiHeaderClient] EventListener.Callback: " + jsonObject.toString());
            if (null != RestApiHeaderClient.this.responseListener) {
                RestApiHeaderClient.this.responseListener.onResponse(jsonObject);
            }
        }
    };

    public RestApiHeaderClient() {
    }

    public void setResponseListener(RestApiHeaderClient.ResponseListener listener) {
        this.responseListener = listener;
    }

    public String toString() {
        return "RestApiHeaderClient";
    }

    public int HttpsGet(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response) {
        return HttpsGet(httpsURL, http_data_type, parameters, response, null);
    }

    public int HttpsGet(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new RestApiHeaderClient.HttpsGetRunnable(httpsURL, http_data_type, parameters, response, headers));
        thread.start();
        return response.Id;
    }

    public int HttpGet(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response) {
        return HttpGet(httpsURL, http_data_type, parameters, response, null);
    }

    public int HttpGet(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new RestApiHeaderClient.HttpGetRunnable(httpsURL, http_data_type, parameters, response, headers));
        thread.start();
        return response.Id;
    }

    private class HttpGetRunnable implements Runnable {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        private HashMap<String, String> mHeaders;

        HttpGetRunnable(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
            this.mstrHttpsURL = httpsURL;
            this.mHttp_data_type = http_data_type;
            this.mParameters = parameters;
            this.mResponse = response;
            this.mHeaders = headers;
        }

        public void run() {
            Http.setResponseListener(RestApiHeaderClient.this.callback);
            Http.GET(this.mstrHttpsURL, this.mHttp_data_type, this.mParameters, this.mResponse, this.mHeaders);
        }
    }

    private class HttpsGetRunnable implements Runnable {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        private HashMap<String, String> mHeaders;

        HttpsGetRunnable(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
            this.mstrHttpsURL = httpsURL;
            this.mHttp_data_type = http_data_type;
            this.mParameters = parameters;
            this.mResponse = response;
            this.mHeaders = headers;
        }

        public void run() {
            Https.setResponseListener(RestApiHeaderClient.this.callback);
            Https.GET(this.mstrHttpsURL, this.mHttp_data_type, this.mParameters, this.mResponse, this.mHeaders);
        }
    }

    public int HttpsPost(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response) {
        return HttpsPost(httpsURL, http_data_type, parameters, response, null);
    }

    public int HttpPost(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response) {
        return HttpPost(httpsURL, http_data_type, parameters, response, null);
    }

    public int HttpsPost(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new RestApiHeaderClient.HttpsPostRunnable(httpsURL, http_data_type, parameters, response, headers));
        thread.start();
        return response.Id;
    }

    public int HttpPost(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
        response.Id = ++msnSerialNUm;
        Thread thread = new Thread(new RestApiHeaderClient.HttpPostRunnable(httpsURL, http_data_type, parameters, response, headers));
        thread.start();
        return response.Id;
    }

    private class HttpPostRunnable implements Runnable {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        private HashMap<String, String> mHeaders;

        HttpPostRunnable(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
            this.mstrHttpsURL = httpsURL;
            this.mHttp_data_type = http_data_type;
            this.mParameters = parameters;
            this.mResponse = response;
            this.mHeaders = headers;
        }

        public void run() {
            Http.setResponseListener(RestApiHeaderClient.this.callback);
            Http.POST(this.mstrHttpsURL, this.mHttp_data_type, this.mParameters, this.mResponse, this.mHeaders);
        }
    }

    private class HttpsPostRunnable implements Runnable {
        private String mstrHttpsURL;
        private Config.HTTP_DATA_TYPE mHttp_data_type;
        private HashMap<String, String> mParameters;
        private Response mResponse;
        private HashMap<String, String> mHeaders;

        HttpsPostRunnable(String httpsURL, Config.HTTP_DATA_TYPE http_data_type, HashMap<String, String> parameters, Response response, HashMap<String, String> headers) {
            this.mstrHttpsURL = httpsURL;
            this.mHttp_data_type = http_data_type;
            this.mParameters = parameters;
            this.mResponse = response;
            this.mHeaders = headers;
        }

        public void run() {
            Https.setResponseListener(RestApiHeaderClient.this.callback);
            Https.POST(this.mstrHttpsURL, this.mHttp_data_type, this.mParameters, this.mResponse, this.mHeaders);
        }
    }

    public interface ResponseListener {
        void onResponse(JSONObject var1);
    }
}
